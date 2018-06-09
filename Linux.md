# 1. 系统知识

###### 命令路径

/bin；/usr/bin 一般所有用户可用

/sbin；/usr/sbin 系统用户可用

###### 查看命令路径命令which/where

which ls：查看ls路径和别名

whereis ls：查看ls 命令绝对路径和帮助文档位置

###### 查看命令帮助

man ls：查看ls命令，man命令是more和less命令合体，列出的是详细信息。--help是列出大部分参数的

###### 查看配置帮助

man passwd：给出密码配置的帮助信息，配置文件文档的帮助是5，命令的文档只1

###### 查看命令简短介绍whatis

whatis ls：查看ls的简短信息

###### shell内置命令帮助

shell内置命令，使用man，whereis，whichis均得不到帮助信息，必须使用help命令

###### 磁盘分区

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

###### 关机

shutdown -h now

shutdown -r now

shutdown -c 取消前一个关机命令

###### 系统运行级别init

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

##### 2.1. 压缩文件

.zip可以在windows和linux通用

###### 2.1.1. gzip

不可以压缩文件夹

gzip test.txt，将test.txt变成压缩文件格式

gunzip test.txt.gz将文件从压缩格式还原，或者gzip -d也可以解压

###### 2.1.2. tar

-z参数要放在-f参数前面

- tar -c打包  -v像是详细信息 -f指定文件名 -z打包并且压缩 -x解包

- tar -cvf japan.tar japan：将japan文件夹打包成japan.tar，也可以再压缩

- gzip japan.jar，这样会生成japan.jar.gz

- 解包只要添加-x这个命令

  tar -zcvf japan.tar.gz可以解压出japan这个文件夹，注意

###### 2.1.3. zip

zip japan.zip japan

unzip japan.zip

###### 2.1.4. bzip2

产生.bz2 文件

##### 2.2. 挂载











# 3.用户管理

##### 3.1. 添加用户

###### 3.1.1. useradd

​        useradd uone，这只是添加了一个用户，还必须加一个密码

​        passwd uone，这个命令可以更改密码，管理员可以改所有人的密码，用户只能改自己的密码

​        **-u**，uid；**-d** 家目录；**-c** 用户的说明；**-g** 初始组名；**-G**附加组 ,逗号分隔；**-s** shell

**eg**：

​        useradd -u 666 -G root,bin -c "test comment" -d /home/utwo -s /bin/shell utwo

​       /etc/passwd中存的：

​        utwo x: 666: 1002 : test comment : /home/utwo:  /bin/shell

useradd的默认值，在/etc/default/useradd和/etc/login.defs共同指定的，前者主要指定基本信息，后面是密码有效期，过期等等。

###### 3.1.2. password

**passwd -l wudeyun**：锁定wudeyun

**passwd -u wudeyun**：解锁

###### 3.1.3. usermod

**usermod -L wudeyun**：锁定

**usermod -U wudeun**：解锁

###### 3.1.4. userdel

###### 3.1.5. su

- su - root这个 - 必须有
- su - root -c "userdel uone" 普通用户，执行一次root用户命令







##### 3.2. who

查看登陆用户

##### 3.3. w

查看用户登录详细信息

##### 3.4. uptime

查看登陆详细信息，查看负载均衡

##### 3.5. /etc/passwd

man 5 passwd

​root x:0:0:root:/root:/bin/bash

- root用户名

- x密码标志

- 第一个0，用户ID

  - 0：超级用户
  - 1-499：伪用户，系统使用
  - 500-65535：普通用户

- 第二个0，组ID

  用户组分为**初始组**和**附加组**，用户创建时，指定默认组，用户离开组不能活，每个用户只能有一个**初始组**，但是可以改。但是可以有多个**附加组**。

- root：用户说明

- /root：家目录；普通用户一般是在/home/wudeyun这种

- /bin/bash：登录后的命令解释器，这个改成nologin之后，用户被禁用。

##### 3.6. /etc/shadow

wudeyun : SHA512mima : 17684 : 0 : 90 : 7 : 5 : 30 :

- wudeyun：用户
- SHA512mima 加密的密码，即使密码相同，只要用户不用，算出的串依然不同，若最前面加  **!** 则密码失效。 
- 17684，这个是距离1970年的日期，是密码设置的时间戳天数
- 0 成功修改密码的最小时间间隔下限天
- 90 修改密码的时间上限，天
- 7 提前通知改密码的时间，在第83天的时候，会提醒。
- 5宽限5天，0到期立即失效，-1永不失效
- 30超过30天密码失效，虽然90天密码要修改，但是还没到90天，账号就失效了。

##### 3.7. /etc/group

每个用户，一般都有一个同名的Group，在用户创建的时间系统自动添加

wudeyun : x : 1000 : ？

- wudeyun：组名
- x：密码标志
- 1000：GID
- 组中附加用户，组中初始用户是看不到的。只能通过结合passwd和group文件来判断。

##### 3.8. /etc/gshadow

##### 3.9. 普通用户改超级用户

必须到/etc/passwd中将用户的userid改为 0，而如果把Gid改成 0，却能使普通用户变成超级用户







# 4. 网络管理

###### 给用户发消息

- write 用户名，给指定用户发信息
- wall 消息体，向所有用户广播

###### ping

ping -c 3 url：指定ping的次数

###### ifconfig

###### 用户登录信息

- last
- lastlog

###### 路由追踪

traceroute www.baidu.com

##### netstat

-t，tcp；-u，udp；-l 监听；-r，路由；-n，显示IP和端口

- netstat -rn：查看本机路由表
- netstat -an：查看本机链接









