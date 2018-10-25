# 1 离线安装Cloudera Manager 5和CDH5

CDH (Cloudera's Distribution, including Apache Hadoop)，是Hadoop众多分支中的一种，由Cloudera维护，基于稳定版本的Apache Hadoop构建，并集成了很多补丁，可直接用于生产环境。

Cloudera Manager则是为了便于在集群中进行Hadoop等大数据处理相关的服务安装和监控管理的组件，对集群中主机、Hadoop、Hive、Spark等服务的安装配置管理做了极大简化。

##1. 1 系统环境

- 实验环境：Mac下VMware虚拟机

- 操作系统：CentOS 6.5 x64 (至少内存2G以上，这里内存不够的同学建议还是整几台真机配置比较好，将CDH的所有组件全部安装会占用很多内存，我已开始设置的虚拟机内存是1G，安装过程中直接卡死了)

- Cloudera Manager：5.1.3

- CDH: 5.1.3

**安装说明**

**官方参考文档：**
 http://www.cloudera.com/content/cloudera/en/documentation/cloudera-manager/v5-latest/Cloudera-Manager-Installation-Guide/cm5ig_install_path_C.html

官方共给出了3中安装方式，采用第三种离线的方式来安装。相关包的下载地址：

Cloudera Manager下载地址：
 <http://archive.cloudera.com/cm5/cm/5/cloudera-manager-el6-cm5.1.3_x86_64.tar.gz>，
 下载信息：
 <http://www.cloudera.com/content/cloudera/en/documentation/cloudera-manager/v5-latest/Cloudera-Manager-Version-and-Download-Information/Cloudera-Manager-Version-and-Download-Information.html#cmvd_topic_1>

CDH安装包地址：<http://archive.cloudera.com/cdh5/parcels/latest/>，由于我们的操作系统为CentOS6.5，需要下载以下文件：

·         CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel

·         CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel.sha1

·         manifest.json

**注意**：与CDH4的不同，原来安装CDH4的时候还需要下载IMPALA、Cloudera Search(SOLR)，CDH5中将他们包含在一起了，所以只需要下载一个CDH5的包就可以了。

# 2 系统环境搭建

<font color=red size=4>要在 root 用户下操作</font>

假设现在有三台主机，如下

| ip            | 名称  |
| ------------- | ----- |
| 192.168.1.107 | 主机1 |
| 192.168.1.108 | 主机2 |
| 192.168.1.109 | 主机3 |

## 2.1 配置 hostname

修改上述三台主机的配置配置文件 `/etc/sysconfig/network` ，文件内容分别为

```
NETWORKING=yes
HOSTNAME=n1
```

```
NETWORKING=yes
HOSTNAME=n2
```

```
NETWORKING=yes
HOSTNAME=n3
```

将上述三台主机的 `/etc/hosts` 文件内容配置成一样的，内容如下

```
192.168.1.106   n1
192.168.1.107   n2
192.168.1.108   n3
```

注意，有几台主机就配置几个，有一台就配置一个，不能不配置。

## 2.2 设置ssh无密码登陆（所有节点）

在主节点上执行`ssh-keygen -t rsa`一路回车，生成无密码的密钥对。

将公钥添加到认证文件中：`cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys`，并设置authorized_keys的访问权限：`chmod 600 ~/.ssh/authorized_keys`。

scp命令，讲 authorized_keys 这个文件拷贝到其它主机上去

```
scp ~/.ssh/authorized_keys root@n2:~/.ssh/
```

**测试：**在主节点上ssh n2，正常情况下，不需要密码就能直接登陆进去了。

## 2.3 安装 JDK

CentOS，自带OpenJdk。不过运行CDH5需要使用Oracle的Jdk，所以要安装 Oracle JDK

## 2.4 安装配置MySql（主节点）

安装并设置 root用户

```shell
yum install mysql-server  #安装mysql服务器
chkconfig mysqld on #设置开机自启动
service mysqld start #启动mysql服务器
mysqladmin -u root password '123456' #将root用户的密码设置为 123456
```

创建如下两个数据库

```sql
#hive
create database hive DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
#activity monitor
create database amon DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
```

授予 root 用户对这两个数据库全部操作权限

```sql
grant all privileges on *.* to 'root'@'n1' identified by '123456' with grant option;
flush privileges;
```

## 2.5 关闭防火墙和SELinux

**注意：** 需要在所有的节点上执行，因为涉及到的端口太多了，临时关闭防火墙是为了安装起来更方便，安装完毕后可以根据需要设置防火墙策略，保证集群安全。

关闭防火墙：

```
service iptables stop （临时关闭）  
chkconfig iptables off （重启后生效）
```

关闭SELINUX（实际安装过程中发现没有关闭也是可以的，不知道会不会有问题，还需进一步进行验证）:

```
setenforce 0 （临时生效）  
修改 /etc/selinux/config 下的 SELINUX=disabled （重启后永久生效）
```

## 2.6 所有节点配置NTP服务

集群中所有主机必须保持时间同步，如果时间相差较大会引起各种问题。 具体思路如下：

master节点作为ntp服务器与外界对时中心同步时间，随后对所有datanode节点提供时间同步服务。

所有datanode节点以master节点为基础同步时间。

所有节点安装相关组件：`yum install ntp`。完成后，配置开机启动：`chkconfig ntpd on`,检查是否设置成功：`chkconfig --list ntpd`其中2-5为on状态就代表成功。

**主节点配置**

在配置之前，先使用ntpdate手动同步一下时间，免得本机与对时中心时间差距太大，使得ntpd不能正常同步。这里选用65.55.56.206作为对时中心,`ntpdate -u 65.55.56.206`。

ntp服务只有一个配置文件，配置好了就OK。 这里只给出有用的配置，不需要的配置都用#注掉，这里就不在给出：

```
driftfile /var/lib/ntp/drift
restrict 127.0.0.1
restrict -6 ::1
restrict default nomodify notrap 
server 65.55.56.206 prefer
includefile /etc/ntp/crypto/pw
keys /etc/ntp/keys
```

配置文件完成，保存退出，启动服务，执行如下命令：`service ntpd start`

检查是否成功，用ntpstat命令查看同步状态，出现以下状态代表启动成功：

```
synchronised to NTP server () at stratum 2
time correct to within 74 ms
polling server every 128 s
```

如果出现异常请等待几分钟，一般等待5-10分钟才能同步。

**配置ntp客户端（所有datanode节点）**

```
driftfile /var/lib/ntp/drift
restrict 127.0.0.1
restrict -6 ::1
restrict default kod nomodify notrap nopeer noquery
restrict -6 default kod nomodify notrap nopeer noquery
#这里是主节点的主机名或者ip
server n1
includefile /etc/ntp/crypto/pw
keys /etc/ntp/keys
```

ok保存退出，请求服务器前，请先使用ntpdate手动同步一下时间：`ntpdate -u n1` (主节点ntp服务器)

这里可能出现同步失败的情况，请不要着急，一般是本地的ntp服务器还没有正常启动，一般需要等待5-10分钟才可以正常同步。启动服务：`service ntpd start`

因为是连接内网，这次启动等待的时间会比master节点快一些，但是也需要耐心等待一会儿。

## 3 安装 CM

cloudera manager的目录默认位置在/opt下，解压：`tar xzvf cloudera-manager*.tar.gz`将解压后的cm-5.1.3和cloudera目录放到/opt目录下。

**为Cloudera Manager 5建立数据库**

首先需要去MySql的官网下载JDBC驱动，<http://dev.mysql.com/downloads/connector/j/>，解压后，找到

<font color=red>mysql-connector-java-5.1.33-bin.jar，放到/opt/cm-5.1.3/share/cmf/lib/中</font>

在主节点初始化CM5的数据库：

```
/opt/cm-5.1.3/share/cmf/schema/scm_prepare_database.sh mysql cm -hlocalhost -uroot -pxxxx --scm-host localhost scm scm scm
```

**Agent配置**

修改/opt/cm-5.1.3/etc/cloudera-scm-agent/config.ini中的server_host为主节点的主机名。

**同步Agent到其他节点**

```
scp -r /opt/cm-5.1.3 root@n2:/opt/
```

**在所有节点创建cloudera-scm用户**

```
useradd --system --home=/opt/cm-5.1.3/run/cloudera-scm-server/ --no-create-home --shell=/bin/false --comment "Cloudera SCM User" cloudera-scm
```

**准备Parcels，用以安装CDH5**

将CHD5相关的Parcel包放到主节点的/opt/cloudera/parcel-repo/目录中（parcel-repo需要手动创建）。

相关的文件如下：

- CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel
- CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel.sha1
- manifest.json

最后将CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel.sha1，重命名为CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel.sha，这点必须注意，否则，系统会重新下载CDH-5.1.3-1.cdh5.1.3.p0.12-el6.parcel文件。

**相关启动脚本**

通过`/opt/cm-5.1.3/etc/init.d/cloudera-scm-server start`启动服务端。

通过`/opt/cm-5.1.3/etc/init.d/cloudera-scm-agent start`启动Agent服务。

我们启动的其实是个service脚本，需要停止服务将以上的start参数改为stop就可以了，重启是restart。