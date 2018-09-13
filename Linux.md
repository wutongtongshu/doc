# 1 系统知识

## 1.1 命令路径

/bin；/usr/bin 一般所有用户可用

/sbin；/usr/sbin 系统用户可用

## 1.2 查看命令路径

which ls：查看ls路径和别名

whereis ls：查看ls 命令绝对路径和帮助文档位置

## 1.3 帮助

man ls：查看ls命令，man命令是more和less命令合体，列出的是详细信息。--help是列出大部分参数的

man passwd：给出密码配置的帮助信息，配置文件文档的帮助是5，命令的文档只1

whatis ls：查看ls的简短信息

shell内置命令，使用man，whereis，whichis均得不到帮助信息，必须使用help命令

## 1.4 磁盘分区

**典型设备**：	

Linux中，设备也是文件，所以每个设备都有一个文件名，简称设备文件名。

| 设备               | 设备文件名                                |
| ------------------ | ----------------------------------------- |
| IDE硬盘            | /dev/hd[a-d]                              |
| SCSI/STAT/USB硬盘  | /dev/sd[a-p]                              |
| U盘                | dev/sd[a-p] (与STAT相同)                  |
| 软驱               | dev/fd[0-1]                               |
| 打印机             | 25针: /dev/lp[0-2] USB: /dev/usb/lp[0-15] |
| 鼠标               | PS2: /dev/psaux USB: /dev/usb/mouse[0-15] |
| 当前CD ROM/DVD ROM | /dev/cdrom                                |
| 当前鼠标           | /dev/mouse                                |

​        常见磁盘接口有两种：IDE接口和SATA接口，目前主流是SATA接口。以IDE接口来说，一个IDE扁平线缆可以连接2个IDE设备(分别为Master主设备和从设备Slave)，一台主机都会提供2个IDE接口（IDE1、IDE2），所以一台主机最多可以连接4个IDE设备。一台主机的IDE接口命名如下：

| IDE接口 | Master(主设备) | Slave(从设备) |
| ------- | -------------- | ------------- |
| IDE1    | /dev/hda       | /dev/hdb      |
| IDE2    | /dev/hdc       | /dev/hdd      |

**这只是四块IDE硬盘，跟分区扯不上任何关系，真正的分区是对于单块硬盘来说的**。

​        每个盘片可分成N个扇区，但是第一块扇区特别重要，它主要记录了两个主要信息，分别是： 
**主引导分区**（Master Boot Record， MBR）：可以安装引导加载程序的地方，有446bytes; 
**分区表**（partition table）: 记录整块磁盘分区的状态，有64bytes; 
        主引导分区（MBR）很重要，因为当系统开机的时候会主动去读取这个区域内容，这样系统才会知道你的程序放在哪里，且该如何开机； 
        分区表的64bytes中，总共分为**4组**记录区（最多容纳4个分区），每组记录区都记录了该区段的起始和结束的柱面号码，这4个分区被分为主（Primary）或扩展分区（Extended）。 

- 其实所谓的“分区”只是针对那64个字节的分区表进行设置； 

- 硬盘默认的分区表只能写入4组分区信息， 每组16字节，描述的是分区的开始和结束位置。这4组分区我们称为主（primary）或者扩展分区（Extended），例如，对于硬盘/dev/hda来说，它的3个主分区和1个扩展分区的文件名为/dev/hda1 、/dev/hda2、/dev/hda3、/dev/hda4。IDE硬盘最大64个分区，SDA最多16个分区。扩展分区是不能直接使用的，必须将扩展分区分成一个个逻辑分区才能使用，所以逻辑分区的编号是从5开始的，1-4是主分区和扩展分区编号，即使只有一个主分区，逻辑分区也必须从5开始编号。

- 分区的最小单位为“柱面”；

  这样硬盘分好区后，还要挂载到文件系统，否则无法使用。这个其实不难理解，分区只是把硬盘的逻辑结构写进了分区表，但是操作系统不知道硬盘的路径，而挂载就是要给设备在文件系统中分配位置，这个过程也叫分配盘符，只是不再是windows中的ABCD这些个盘符了。

- 例如：一块硬盘有400个柱面，我们把其中1-100分为第一个分区P1，也是主分区，101-400分到P2，也就是扩展分区，扩展分区可以随意有我们来继续分区，这样分出来的分区被称为逻辑分区，比如我们可以将101-160分为L1，161-220分为L2，221-280分为L3，281-340分为L4，341-400分为L5，那么我们有6个分区，这6个分区名称为： 
  P1: /dev/hda1 
  P2: /dev/hda2 
  L1: /dev/hda5 
  L2: /dev/hda6 
  L3: /dev/hda7 
  L4: /dev/hda8 
  L5: /dev/hda9 
  注意：没有hda3和hda4是因为hda[1-4]留给磁盘默认的四个分区了，这里我们值分出了2个分区P1和P2，所以1、hda3和hda4被空出来，因此逻辑分区名称是直接从hda5开始。 
  2、主分区和扩展分区最多可以有4个（硬盘限制）； 
  3、扩展分区最多只能有1个（操作系统限制）； 
  4、逻辑分区是有扩展分区继续切割出来的分区； 
  5、能够被格式化后作为数据访问的分区为主分区与逻辑分区，扩展分区无法被格式化； 
  6、逻辑分区的数量依据操作系统而不同，在Linux中IDE硬盘最多有59个逻辑分区（5-63号），SATA硬盘则有11个逻辑分区（5-15）；

### 1.4.1 查看磁盘文件系统df,du

df显示系统中的文件系统，注意，有些文件系统并没有存在磁盘中，而是存在于内存中

### 1.4.2块设备管理 lsblk

查看磁盘信息，注意，磁盘不等于文件系统

### 1.4.3 Debian磁盘VMWare



## 1.5 关机

shutdown -h now

shutdown -r now

shutdown -c 取消前一个关机命令

## 1.6 系统运行级别init

- 0关机

- 1 单用户，只启动核心，甚至不能联网

- 2不完全多用户，无NFS（网络服务）

- 3完全多用户

- 4未分配

- 5图形界面

- 6重启

  init 0：关机

  init 6：重启

# 2. 文件管理

## 2.1. 压缩文件

.zip可以在windows和linux通用

## 2.1.1. gzip

不可以压缩文件夹

gzip test.txt，将test.txt变成压缩文件格式

gunzip test.txt.gz将文件从压缩格式还原，或者gzip -d也可以解压

## 2.1.2. tar

-z参数要放在-f参数前面

- tar -c打包  -v是详细信息 -f指定文件名 -z打包并且压缩 -x解包

- tar -cvf japan.tar japan：将japan文件夹打包成japan.tar，也可以再压缩

- gzip japan.jar，这样会生成japan.jar.gz

- 解包只要添加-x这个命令

  tar -zcvf japan.tar.gz可以解压出japan这个文件夹，注意 z 可能要写在最前面

## 2.1.3. zip

zip japan.zip japan

unzip japan.zip

## 2.1.4. bzip2

产生.bz2 文件

## 2.2. 挂载

# 3.用户管理

## 3.1. 用户增删

### 3.1.1. useradd

​        useradd username，这只是添加了一个用户，还必须加一个密码

​        passwd username，这个命令可以更改密码，管理员可以改所有人的密码，用户只能改自己的密码

​        **-u**，指定 uid；**-d** 指定家目录；**-c** 注释；**-g** 指定初始组名；**-G**指定附加组 ,逗号分隔；**-s** 指定shell

**eg**：

```
 # useradd -u 1001 -G root -c "test user add function" \ (这是一个换行标识)
 > -d /home/wudeyun2 -s /bin/shell wudeyun2
```

​ /etc/passwd中存的：

    ```
wudeyun2:x:1001:1001:test user add function:/home/wudeyun2:/bin/bash
    ```

useradd的默认值，在/etc/default/useradd和/etc/login.defs共同指定的，前者主要指定基本信息，后面是密码有效期，过期等等。

### 3.1.2. password

**passwd -l wudeyun**：锁定wudeyun

**passwd -u wudeyun**：解锁

### 3.1.3. usermod

**usermod -L wudeyun**：锁定

**usermod -U wudeun**：解锁

### 3.1.4. userdel

## 3.1.5. su

- su - root这个 - 必须有

- su - root -c "userdel uone" 普通用户，执行一次root用户命令

  debian里面不用加 '-'，centenos里面不加，环境变量不能改变

## 3.2. who

查看登陆用户

## 3.3. w

查看用户登录详细信息

## 3.4. uptime

查看登陆详细信息，查看负载均衡

## 3.5. /etc/passwd

man 5 passwd

root x:0:0:comment:/root:/bin/bash

- root用户名

- x密码标志

- 第一个0，用户ID

  - 0：超级用户
  - 1-499：伪用户，系统使用
  - 500-65535：普通用户

- 第二个0，组ID

  用户组分为**初始组**和**附加组**，用户创建时，指定默认组，用户离开组不能活，每个用户只能有一个**初始组**，但是可以改。但是可以有多个**附加组**。

- comment：用户说明、注释

- /root：home目录；普通用户一般是在/home/wudeyun这种

- /bin/bash：登录后的命令解释器，这个改成nologin之后，用户被禁用。

## 3.6. /etc/shadow

wudeyun : SHA512mima : 17684 : 0 : 90 : 7 : 5 : 30 :

- wudeyun：用户
- SHA512mima 加密的密码，即使密码相同，只要用户不用，算出的串依然不同，若最前面加  **!** 则密码失效。 
- 17684，这个是距离1970年的日期，是密码设置的时间戳天数
- 0 成功修改密码的最小时间间隔下限天
- 90 修改密码的时间上限，天
- 7 提前通知改密码的时间，在第83天的时候，会提醒。
- 5宽限5天，0到期立即失效，-1永不失效
- 30超过30天密码失效，虽然90天密码要修改，但是还没到90天，账号就失效了。

## 3.7. /etc/group

每个用户，一般都有一个同名的Group，在用户创建的时间系统自动添加

wudeyun : x : 1000 : ？

- wudeyun：组名
- x：密码标志
- 1000：GID
- 组中附加用户，组中初始用户是看不到的。只能通过结合passwd和group文件来判断。

## 3.8. /etc/gshadow

## 3.9. 普通用户改超级用户

必须到/etc/passwd中将用户的userid改为 0，而如果把Gid改成 0，却能使普通用户变成超级用户

# 4. 网络管理

## 4.1 本机ip配置

### 4.1.1配置一张虚拟网卡

查看本机网卡，查到两张网卡，eth0、lo回环网卡

```
root@www:/home/wudeyun# ifconfig
eth0      Link encap:Ethernet（以太网）  HWaddr 00:0c:29:2a:63:2c  
          inet addr:172.22.10.138  Bcast:172.22.255.255  Mask:255.255.0.0
          inet6 addr: fe80::20c:29ff:fe2a:632c/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:16872 errors:0 dropped:0 overruns:0 frame:0   发包数
          TX packets:88 errors:0 dropped:0 overruns:0 carrier:0    收包数
          collisions:0 txqueuelen:1000 
          RX bytes:1083560 (1.0 MiB)  TX bytes:11018 (10.7 KiB)    发包，收包流量

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)
```

配置一张虚拟网卡

```sh
ifconfig eth0:0 172.22.10.137
```

```
root@www:/home/wudeyun# ifconfig
eth0      Link encap:Ethernet  HWaddr 00:0c:29:2a:63:2c  
          inet addr:172.22.10.138  Bcast:172.22.255.255  Mask:255.255.0.0
          inet6 addr: fe80::20c:29ff:fe2a:632c/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:26091 errors:0 dropped:0 overruns:0 frame:0
          TX packets:203 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:1667918 (1.5 MiB)  TX bytes:21936 (21.4 KiB)

eth0:0    Link encap:Ethernet  HWaddr 00:0c:29:2a:63:2c  
          inet addr:172.22.10.137  Bcast:172.22.255.255  Mask:255.255.0.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

```

再配置两张虚拟网卡

```sh
ifconfig eth0:1 172.22.10.137
ifconfig eth0:2 172.22.10.137
```

卸载虚拟网卡

```
查看两张网卡
root@www:/home/wudeyun# ifconfig
eth0      Link encap:Ethernet  HWaddr 00:0c:29:2a:63:2c  
          ......

lo        Link encap:Local Loopback  
          ......

配置一张虚拟网卡
root@www:/home/wudeyun# ifconfig eth0:0 192.168.1.103
配置后查看三张网卡
root@www:/home/wudeyun# ifconfig
eth0      Link encap:Ethernet  HWaddr 00:0c:29:2a:63:2c  
          ......

eth0:0    Link encap:Ethernet  HWaddr 00:0c:29:2a:63:2c  
          inet addr:192.168.1.103  Bcast:192.168.1.255  Mask:255.255.255.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1

lo        Link encap:Local Loopback  
          ......
卸载虚拟网卡eth0:0
root@www:/home/wudeyun# ifconfig eth0:0 down
卸载后查看，还是两张网卡
root@www:/home/wudeyun# ifconfig
eth0      Link encap:Ethernet  HWaddr 00:0c:29:2a:63:2c  
          ......
lo        Link encap:Local Loopback  
```

重新加载虚拟网卡，无法成功，但是如果把回环网卡卸载，还可以再次加载

ifconfig eht0:0

```
root@www:/home/wudeyun# ifconfig eth0:0 up
SIOCSIFFLAGS: Cannot assign requested address
```

### 4.1.2 永久配置网卡

打开网卡配置文件 vim /etc/network/interface

配置网卡eth0的IP地址

```
auto eth0 表示网卡随系统自动请
iface eth0 inet static 表示网卡为静态ip地址
address 192.168.2.147 表示设置ip地址
netmask 255.255.255.0 表示子网掩码
gateway 192.168.2.1 表示网关，不为了上网，可以不填，只用于局域网通信
```

配置动态可以从 dhcp 服务器自动获取，必须联网，网址什么的都不用配置了

```
iface eth0 inet dhcp
      dns-nameservers 10.112.18.1多个的时候，空格隔开
```

或者修改 dhcp 配置文件   /etc/resolv.conf ，往里面多加几行

```
 nameserver 211.161.122.206
 nameserver 211.161.191.230
```


上述中文在实际配置中需要全部删除


指定网卡重启
ifdown eth0
ifup eth0


网络重启
/etc/init.d/networking restart
service networking restart


当有两个网卡，一个是无线网卡，一个是有线网卡，一定要将无线网卡的详细配置写入，例如设置静态地址，SSID，密码等等

1、搜索附近的无线网络，获取要连接网络的 SSID

root@linaro-alip:/# iwlist scan

2、配置无线网络
把下面文件中的 ssid 和 passwd 换成无线网络的 ssid 和密码。

root@linaro-alip:/# vim /etc/network/interfaces

打开这个文件，把其中倒数第2行的 ssid 替换成你要用的无线网络名称，password 替换成该无线网络的密码

auto wlan0

iface wlan0 inet dhcp

pre-up ip link set wlan0 up

pre-up iwconfig wlan0 essid ssid

wpa-ssid ssid

wpa-psk password

举个例子，我的无线网络名称叫 Caesar-AP，密码是 test0000，那我的配置文件应该写成：

iface wlan0 inet dhcp

pre-up ip link set wlan0 up

pre-up iwconfig wlan0 essid ssid

wpa-ssid Caesar-AP

wpa-psk test0000

3、启用无线网线，执行系列命令，无线网卡会按照我们配置文件里面的信息自动连接该无线网络，并且每次重启电脑后仍然可以自动连接。

root@linaro-alip:/# ifup wlan0

重启后每次也可以自动链接上.

注意：
auto：开机启动，设置了2个网卡都开机启动
ip地址获取方式，static是静态IP地址，dhcp是DHCP获取地址。
设置两个网卡同时启动的时候，需要将一个网卡的网管置空，否则需要手动修改路由，删除其中的一条默认路由。

------------------------------------------------------------------------------
auto与allow-hotplug的区别


/etc/network/interfaces文件中一般用auto或者allow-hotplug来定义接口的启动行为。


auto


语法：
auto <interface_name>
含义：
在系统启动的时候启动网络接口,无论网络接口有无连接(插入网线),如果该接口配置了DHCP,则无论有无网线,系统都会去执行DHCP,如果没有插入网线,则等该接口超时后才会继续。


allow-hotplug


语法:
allow-hotplug <interface_name>


含义：
只有当内核从该接口检测到热插拔事件后才启动该接口。如果系统开机时该接口没有插入网线,则系统不会启动该接口,系统启动后,如果插入网线,系统会自动启动该接口。也就是将网络接口设置为热插拔模式。


手动重新启动网络


一般修改了网络配置文件后,会用以下命令重新启动网络
### 4.1.3 给用户发消息

- write 用户名，给指定用户发信息
- wall 消息体，向所有用户广播

### 4.1.4 ping

ping -c 3 url：指定ping的次数

### 4.1.6 用户登录信息

- last
- lastlog

### 4.1.7 路由追踪

很多节点禁止ping，所以无法追踪的节点很多

traceroute www.baidu.com

### 4.1.8 netstat

-t，tcp；-u，udp；-l 监听；-r，路由；-n，以数字显示ip

- netstat -rn：查看本机路由表
- netstat -an：查看本机链接

```
root@www:/home/wudeyun# netstat -tuln  查看tcp、udp服务列表
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State      
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN     
tcp        0      0 127.0.0.1:6011          0.0.0.0:*               LISTEN     
tcp6       0      0 :::22                   :::*                    LISTEN     
tcp6       0      0 ::1:6011                :::*                    LISTEN     
udp        0      0 0.0.0.0:68              0.0.0.0:*                          
udp        0      0 0.0.0.0:12897           0.0.0.0:*                          
udp6       0      0 :::52563                :::*       
```

### 4.1.9 域名解析命令

 debian 默认没有安装工具

```shell
apt-get install dnsutils
```

安装完了之后，试试解析百度。它的原理是访问 dns 服务器得到，所以不联网是获取不到的，得到了百度的两个 IP 119.75.216.20和119.75.213.61。server的值是 211.161.122.206端口可能是 53

```
root@www:/home/wudeyun# nslookup www.baidu.com
Server:		211.161.122.206
Address:	211.161.122.206#53

Non-authoritative answer:
www.baidu.com	canonical name = www.a.shifen.com.
Name:	www.a.shifen.com
Address: 119.75.216.20
Name:	www.a.shifen.com
Address: 119.75.213.61
```

### 4.1.10下载命令

wget 文件全路径

证书没安装会报错，系统中没有安装ca证书包导致的 

```
ERROR: The certificate of ‘cn.wordpress.org’ is not trusted.
```

```
apt-get install ca-certificates
```

下载

```
wget http://cn.wordpress.org/wordpress-3.1-zh_CN.zip 
```

查看

```
-rw-r--r-- 1 root root 3448957 Feb 24  2011 wordpress-3.1-zh_CN.zip
```

## 4.2 ssh服务










