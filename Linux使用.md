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
​        主引导分区（MBR）很重要，因为当系统开机的时候会主动去读取这个区域内容，这样系统才会知道你的程序放在哪里，且该如何开机； 
​        分区表的64bytes中，总共分为**4组**记录区（最多容纳4个分区），每组记录区都记录了该区段的起始和结束的柱面号码，这4个分区被分为主（Primary）或扩展分区（Extended）。 

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
  注意：没有hda3和hda4是因为hda[1-4]留给磁盘默认的四个分区了，这里我们值分出了2个分区P1和P2，所以hda3和hda4被空出来，因此逻辑分区名称是直接从hda5开始。 
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

启动之后，通过 blkid 可以看到 /dev/sr0这个设备，这就是光盘设备，使用挂载命令

```
创建挂载目录: mkdir /data/cdrom
将设别文件系统挂载到目录：mount -t iso9660 /dev/sr0 /data/cdrom
查看/data/cdrom下挂载的文件系统： df /data/cdrom
卸载挂载点下的设备:umount /data/cdrom
```

![](https://github.com/wutongtongshu/doc/raw/master/TCP_IP/Debian%E6%8C%82%E8%BD%BD%E5%85%89%E7%9B%98.png)

### 1.4.4 LVM

![逻辑卷构成](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/LVM.png)

PE：LVM 默认使用4MB的PE区块，而LVM的LV最多仅能含有65534个PE (lvm1 的格式)，因此默认的LVM的LV最大容量为4M*65534/(1024M/G)=256G。PE是整个LVM 最小的储存区块，也就是说，其实我们的资料都是由写入PE 来处理的。简单的说，这个PE 就有点像文件系统里面的block 大小。所以调整PE 会影响到LVM 的最大容量！不过，在 CentOS 6.x 以后，由于直接使用 lvm2 的各项格式功能，因此这个限制已经不存在了。

PV：物理卷（physical volume），物理卷就是指硬盘分区或与磁盘分区具有同样功能的设备(如RAID)，与基本的物理存储介质（如分区、磁盘等）相同，多了 LVM 相关的管理参数。

VG：卷组（Volume Group），LVM卷组由一个或多个物理卷组成。

LV：逻辑卷（logical volume），卷组集合提供了一个逻辑存储空间，这个存储空间可以被分成多个部分，每个部分可以被单独命名，称为一个逻辑卷。

LE（logical extent）：逻辑卷也被划分为被称为LE(Logical Extents) 的可被寻址的基本单位。在同一个卷组中，LE的大小和PE是相同的，并且一一对应。存在一个映射。

**注意**：LVM 只是逻辑存储空间，创建文件系统、挂载之后才能使用

```shell
//创建 ext4 文件系统
mkfs.ext4 /dev/vggroup/lv

//将文件系统挂载到 /mnt 目录上
mount -t ext4 /dev/vggroup/lv /mnt
```



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

## 1.7 命令别名

```
alias ttt="ls -l"
unalias ttt
```

##1.8 >&1和 >&2  

在 shell 程式中，最常使用的 FD (file descriptor) 大概有三个, 分别是:

**0 是一个文件描述符，表示标准输入(stdin)**
**1 是一个文件描述符，表示标准输出(stdout)**

**2 是一个文件描述符，表示标准错误(stderr)**

在标准情况下, 这些FD分别跟如下设备关联: 
stdin(0): keyboard 键盘输入,并返回在前端 
stdout(1): monitor 正确返回值 输出到前端 
stderr(2): monitor 错误返回值 输出到前端

**举例说明吧:**

当前目录只有一个文件 a.txt. 
[root@redhat box]# ls 
a.txt 
[root@redhat box]# ls a.txt b.txt 
ls: b.txt: No such file or directory 由于没有b.txt这个文件, 于是返回错误值, 这就是所谓的2输出 
a.txt 而这个就是所谓的1输出

**再接着看:**

## 1.9/bin;/sbin;/usr/bin;/usr/sbin;/usr/local/bin;/usr/local/sbin

上述文件夹都是软件的安装目录：

bin：一般存放系统必须的软件

sbin：一般存放 root 用户需要使用的软件

/usr/bin：非系统必须的，工具类软件

/usr/sbin：root 用户使用的， 管理网络类软件

/usr/local/bin：用户自己装的软件，一般不用对外提供服务的

/usr/local/sbin：root 用户自己装的，一般不用提供对外服务的

## 1.10 linux目录结构

![](https://github.com/wutongtongshu/doc/raw/master/TCP_IP/linux%E7%9B%AE%E5%BD%95%E7%BB%93%E6%9E%84.png)



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
​    ```

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

## 4.0 进程管理 ps

Linux是一个多用户，多任务的系统，可以同时运行多个用户的多个程序，就必然会产生很多的进程，而每个进程会有不同的状态。  在下文将对进程的

R、S、D、T、Z、X 六种状态做个说明。

 

PROCESS STATE CODES

​       Here are the different values that the s, stat and state output specifiers (header "STAT" or "S") will display to describe the state of a process.

​       D    Uninterruptible sleep (usually IO)

​       R    Running or runnable (on run queue)

​       S    Interruptible sleep (waiting for an event to complete)

​       T    Stopped, either by a job control signal or because it is being traced.

​       W    paging (not valid since the 2.6.xx kernel)

​       X    dead (should never be seen)

​       Z    Defunct ("zombie") process, terminated but not

​            reaped by its parent.

 

​       For BSD formats and when the stat keyword is used,additional characters may be displayed:

​       <    high-priority (not nice to other users)

​       N    low-priority (nice to other users)

​       L    has pages locked into memory (for real-time and custom IO)

​       s    is a session leader

​       l    is multi-threaded (using CLONE_THREAD, like NPTL pthreads do)

​       \+    is in the foreground process group

 

 

一. 查看进程的状态

1.1 使用PS命令

[root@localhost]# ps -a -o pid,ppid,stat,command -u oracle

  PID  PPID STAT COMMAND

  637     1 Ss   oracleXEZF (LOCAL=NO)

  729     1 Ss   oracleXEZF (LOCAL=NO)

 1144  1103 S+   top

 1230     1 Ss   oracleXEZF (LOCAL=NO)

 1289  1145 S+   vmstat 10

 1699     1 Ss   oracleXEZF (LOCAL=NO)

 1827  1294 R+   ps -a -o pid,ppid,stat,command -u oracle

 3410     1 Ss   ora_pmon_XEZF

 3412     1 Ss   ora_psp0_XEZF

 3414     1 Ss   ora_mman_XEZF

 3416     1 Ss   ora_dbw0_XEZF

 3418     1 Ss   ora_lgwr_XEZF

 3420     1 Ss   ora_ckpt_XEZF

 3422     1 Ss   ora_smon_XEZF

 3424     1 Ss   ora_reco_XEZF

 3426     1 Ss   ora_mmon_XEZF

 3428     1 Ss   ora_mmnl_XEZF

 3430     1 Ss   ora_d000_XEZF

 3432     1 Ss   ora_d001_XEZF

 3434     1 Ss   ora_s000_XEZF

 3436     1 Ss   ora_s001_XEZF

 3438     1 Ss   ora_s002_XEZF

 3488     1 Ssl  /home/oracle_app/bin/tnslsnr LISTENER -inherit

11167     1 Ss   oracleXEZF (LOCAL=NO)

11423     1 Ss   oracleXEZF (LOCAL=NO)

11425     1 Ss   oracleXEZF (LOCAL=NO)

11429     1 Ss   oracleXEZF (LOCAL=NO)

14867     1 Ss   oracleXEZF (LOCAL=NO)

19323     1 Ss   oracleXEZF (LOCAL=NO)

 

用ps 的 – l 选项,得到更详细的进程信息：

（1）F(Flag)：一系列数字的和，表示进程的当前状态。这些数字的含义为：

​       00：若单独显示，表示此进程已被终止。

​       01：进程是核心进程的一部分，常驻于系统主存。如：sched，vhand，bdflush。

​       02：Parent is tracing process.

​       04 ：Tracing parent's signal has stopped the process; the parent　is waiting ( ptrace(S)).

​       10：进程在优先级低于或等于25时，进入休眠状态，而且不能用信号唤醒，例如在等待一个inode被创建时。

​       20：进程被装入主存（primary memory）

​       40：进程被锁在主存，在事务完成前不能被置换。

 

（2） 进程状态：S(state)

​       O：进程正在处理器运行,这个状态从来木见过.

​       S：休眠状态（sleeping）

​       R：等待运行（runable）R Running or runnable (on run queue) 进程处于运行或就绪状态

​       I：空闲状态（idle）

​       Z：僵尸状态（zombie）　　　

​       T：跟踪状态（Traced）

​       B：进程正在等待更多的内存页

​       D:不可中断的深度睡眠，一般由IO引起，同步IO在做读或写操作时，cpu不能做其它事情，只能等待，这时进程处于这种状态，如果程序采用异步IO，这种状态应该就很少见到了

 

（3）C(cpu usage)：cpu利用率的估算值

 

 

1.2 使用Top命令中的S 字段

pid user      pr  ni  virt  res  shr s %cpu %mem    time+  command                                

11423 oracle    16   0  627m 170m 168m R   32  9.0   4110:21 oracle                                

 3416 oracle    15   0  650m 158m 138m S    0  8.4   0:07.12 oracle                                 

11167 oracle    15   0  626m 151m 149m S    0  8.0 400:20.77 oracle                                

11429 oracle    15   0  626m 148m 147m S    0  7.9 812:05.71 oracle                                

 3422 oracle    18   0  627m 140m 137m S    0  7.4   1:12.23 oracle                                

 1230 oracle    15   0  639m 107m  96m S    0  5.7   0:10.00 oracle                                

  637 oracle    15   0  629m  76m  73m S    0  4.0   0:04.31 oracle                     

 

 

二.  进程状态说明

2.1  R (task_running) : 可执行状态

​       只有在该状态的进程才可能在CPU上运行。而同一时刻可能有多个进程处于可执行状态，这些进程的task_struct结构（进程控制块）被放入对应CPU的可执行队列中（一个进程最多只能出现在一个CPU的可执行队列中）。进程调度器的任务就是从各个CPU的可执行队列中分别选择一个进程在该CPU上运行。

​       很多操作系统教科书将正在CPU上执行的进程定义为RUNNING状态、而将可执行但是尚未被调度执行的进程定义为READY状态，这两种状态在linux下统一为 TASK_RUNNING状态。

 

2.2  S (task_interruptible): 可中断的睡眠状态

​       处于这个状态的进程因为等待某某事件的发生（比如等待socket连接、等待信号量），而被挂起。这些进程的task_struct结构被放入对应事件的等待队列中。当这些事件发生时（由外部中断触发、或由其他进程触发），对应的等待队列中的一个或多个进程将被唤醒。

​       通过ps命令我们会看到，一般情况下，进程列表中的绝大多数进程都处于task_interruptible状态（除非机器的负载很高）。毕竟CPU就这么一两个，进程动辄几十上百个，如果不是绝大多数进程都在睡眠，CPU又怎么响应得过来。

 

2.3  D (task_uninterruptible): 不可中断的睡眠状态

​       与task_interruptible状态类似，进程处于睡眠状态，但是此刻进程是不可中断的。不可中断，指的并不是CPU不响应外部硬件的中断，而是指进程不响应异步信号。
​       绝大多数情况下，进程处在睡眠状态时，总是应该能够响应异步信号的。但是uninterruptible sleep 状态的进程不接受外来的任何信号，因此无法用kill杀掉这些处于D状态的进程，无论是”kill”, “kill -9″还是”kill -15″，这种情况下，一个可选的方法就是reboot。

 

​       处于uninterruptible sleep状态的进程通常是在等待IO，比如磁盘IO，网络IO，其他外设IO，如果进程正在等待的IO在较长的时间内都没有响应，那么就被ps看到了，同时也就意味着很有可能有IO出了问题，可能是外设本身出了故障，也可能是比如挂载的远程文件系统已经不可访问了.

 

​       而task_uninterruptible状态存在的意义就在于，内核的某些处理流程是不能被打断的。如果响应异步信号，程序的执行流程中就会被插入一段用于处理异步信号的流程（这个插入的流程可能只存在于内核态，也可能延伸到用户态），于是原有的流程就被中断了。

​       在进程对某些硬件进行操作时（比如进程调用read系统调用对某个设备文件进行读操作，而read系统调用最终执行到对应设备驱动的代码，并与对应的物理设备进行交互），可能需要使用task_uninterruptible状态对进程进行保护，以避免进程与设备交互的过程被打断，造成设备陷入不可控的状态。这种情况下的task_uninterruptible状态总是非常短暂的，通过ps命令基本上不可能捕捉到。

 

​       我们通过vmstat 命令中procs下的b 可以来查看是否有处于uninterruptible 状态的进程。 该命令只能显示数量。

 

​       In computer operating systems terminology, a sleeping process can either be interruptible (woken via signals) or uninterruptible (woken explicitly). An uninterruptible sleep state is a sleep state that cannot handle a signal (such as waiting for disk or network IO (input/output)).

 

​       When the process is sleeping uninterruptibly, the signal will be noticed when the process returns from the system call or trap.

​       -- 这句是关键。 当处于uninterruptibly sleep 状态时，只有当进程从system 调用返回时，才通知signal。

 

​       A process which ends up in “D” state for any measurable length of time is trapped in the midst of a system call (usually an I/O operation on a device — thus the initial in the ps output).

 

​       Such a process cannot be killed — it would risk leaving the kernel in an inconsistent state, leading to a panic. In general you can consider this to be a bug in the device driver that the process is accessing.

 

2.4  T(task_stopped or task_traced)：暂停状态或跟踪状态

​       向进程发送一个sigstop信号，它就会因响应该信号而进入task_stopped状态（除非该进程本身处于task_uninterruptible状态而不响应信号）。（sigstop与sigkill信号一样，是非常强制的。不允许用户进程通过signal系列的系统调用重新设置对应的信号处理函数。）
​       向进程发送一个sigcont信号，可以让其从task_stopped状态恢复到task_running状态。

​       当进程正在被跟踪时，它处于task_traced这个特殊的状态。“正在被跟踪”指的是进程暂停下来，等待跟踪它的进程对它进行操作。比如在gdb中对被跟踪的进程下一个断点，进程在断点处停下来的时候就处于task_traced状态。而在其他时候，被跟踪的进程还是处于前面提到的那些状态。

​      

​       对于进程本身来说，task_stopped和task_traced状态很类似，都是表示进程暂停下来。
​       而task_traced状态相当于在task_stopped之上多了一层保护，处于task_traced状态的进程不能响应sigcont信号而被唤醒。只能等到调试进程通过ptrace系统调用执行ptrace_cont、ptrace_detach等操作（通过ptrace系统调用的参数指定操作），或调试进程退出，被调试的进程才能恢复task_running状态。

 

 

2.5 Z (task_dead - exit_zombie)：退出状态，进程成为僵尸进程

​       在Linux进程的状态中，僵尸进程是非常特殊的一种，它是已经结束了的进程，但是没有从进程表中删除。太多了会导致进程表里面条目满了，进而导致系统崩溃，倒是不占用其他系统资源。    

​       它已经放弃了几乎所有内存空间，没有任何可执行代码，也不能被调度，仅仅在进程列表中保留一个位置，记载该进程的退出状态等信息供其他进程收集，除此之外，僵尸进程不再占有任何内存空间。

​      

​       进程在退出的过程中，处于TASK_DEAD状态。在这个退出过程中，进程占有的所有资源将被回收，除了task_struct结构（以及少数资源）以外。于是进程就只剩下task_struct这么个空壳，故称为僵尸。

 

​       之所以保留task_struct，是因为task_struct里面保存了进程的退出码、以及一些统计信息。而其父进程很可能会关心这些信息。比如在shell中，$?变量就保存了最后一个退出的前台进程的退出码，而这个退出码往往被作为if语句的判断条件。
​       当然，内核也可以将这些信息保存在别的地方，而将task_struct结构释放掉，以节省一些空间。但是使用task_struct结构更为方便，因为在内核中已经建立了从pid到task_struct查找关系，还有进程间的父子关系。释放掉task_struct，则需要建立一些新的数据结构，以便让父进程找到它的子进程的退出信息。

 

​       子进程在退出的过程中，内核会给其父进程发送一个信号，通知父进程来“收尸”。 父进程可以通过wait系列的系统调用（如wait4、waitid）来等待某个或某些子进程的退出，并获取它的退出信息。然后wait系列的系统调用会顺便将子进程的尸体（task_struct）也释放掉。

​       这个信号默认是SIGCHLD，但是在通过clone系统调用创建子进程时，可以设置这个信号。

​       如果他的父进程没安装SIGCHLD信号处理函数调用wait或waitpid()等待子进程结束，又没有显式忽略该信号，那么它就一直保持僵尸状态，子进程的尸体（task_struct）也就无法释放掉。

 

​       如果这时父进程结束了，那么init进程自动会接手这个子进程，为它收尸，它还是能被清除的。但是如果如果父进程是一个循环，不会结束，那么子进程就会一直保持僵尸状态，这就是为什么系统中有时会有很多的僵尸进程。

 

​       当进程退出的时候，会将它的所有子进程都托管给别的进程（使之成为别的进程的子进程）。托管的进程可能是退出进程所在进程组的下一个进程（如果存在的话），或者是1号进程。所以每个进程、每时每刻都有父进程存在。除非它是1号进程。1号进程，pid为1的进程，又称init进程。


linux系统启动后，第一个被创建的用户态进程就是init进程。它有两项使命：
​       1、执行系统初始化脚本，创建一系列的进程（它们都是init进程的子孙）；
​       2、在一个死循环中等待其子进程的退出事件，并调用waitid系统调用来完成“收尸”工作；



​       init进程不会被暂停、也不会被杀死（这是由内核来保证的）。它在等待子进程退出的过程中处于task_interruptible状态，“收尸”过程中则处于task_running状态。

 

Unix/Linux 处理僵尸进程的方法：

​       找出父进程号，然后kill 父进程，之后子进程（僵尸进程）会被托管到其他进程，如init进程，然后由init进程将子进程的尸体（task_struct）释放掉。

 

除了通过ps 的状态来查看Zombi进程，还可以用如下命令查看：

[oracle@rac1 ~]$ ps -ef|grep defun

oracle   13526 12825  0 16:48 pts/1    00:00:00 grep defun

oracle   28330 28275  0 May18 ?        00:00:00 [Xsession] <defunct>



 

僵尸进程解决办法：

（1）改写父进程，在子进程死后要为它收尸。

​       具体做法是接管SIGCHLD信号。子进程死后，会发送SIGCHLD信号给父进程，父进程收到此信号后，执行 waitpid()函数为子进程收尸。这是基于这样的原理：就算父进程没有调用wait，内核也会向它发送SIGCHLD消息，尽管对的默认处理是忽略，如果想响应这个消息，可以设置一个处理函数。

（2）把父进程杀掉。

​       父进程死后，僵尸进程成为"孤儿进程"，过继给1号进程init，init始终会负责清理僵尸进程．它产生的所有僵尸进程也跟着消失。如：

​       kill -9 `ps -ef | grep "Process Name" | awk '{ print $3 }'`
​       其中，“Process Name”为处于zombie状态的进程名。

（3）杀父进程不行的话，就尝试用skill -t TTY关闭相应终端，TTY是进程相应的tty号(终端号)。但是，ps可能会查不到特定进程的tty号，这时就需要自己判断了。
（4）重启系统，这也是最常用到方法之一。

 

 

2.6 X (task_dead - exit_dead)：退出状态，进程即将被销毁

​       进程在退出过程中也可能不会保留它的task_struct。比如这个进程是多线程程序中被detach过的进程。或者父进程通过设置sigchld信号的handler为sig_ign，显式的忽略了sigchld信号。（这是posix的规定，尽管子进程的退出信号可以被设置为sigchld以外的其他信号。）
​       此时，进程将被置于exit_dead退出状态，这意味着接下来的代码立即就会将该进程彻底释放。所以exit_dead状态是非常短暂的，几乎不可能通过ps命令捕捉到。

 

三. 进程状态变化说明

3.1 进程的初始状态

​       进程是通过fork系列的系统调用（fork、clone、vfork）来创建的，内核（或内核模块）也可以通过kernel_thread函数创建内核进程。这些创建子进程的函数本质上都完成了相同的功能——将调用进程复制一份，得到子进程。（可以通过选项参数来决定各种资源是共享、还是私有。）
​       那么既然调用进程处于task_running状态（否则，它若不是正在运行，又怎么进行调用？），则子进程默认也处于task_running状态。
​       另外，在系统调用调用clone和内核函数kernel_thread也接受clone_stopped选项，从而将子进程的初始状态置为 task_stopped。

 

3.2 进程状态变迁

​       进程自创建以后，状态可能发生一系列的变化，直到进程退出。而尽管进程状态有好几种，但是进程状态的变迁却只有两个方向——从task_running状态变为非task_running状态、或者从非task_running状态变为task_running状态。
​       也就是说，如果给一个task_interruptible状态的进程发送sigkill信号，这个进程将先被唤醒（进入task_running状态），然后再响应sigkill信号而退出（变为task_dead状态）。并不会从task_interruptible状态直接退出。

​       进程从非task_running状态变为task_running状态，是由别的进程（也可能是中断处理程序）执行唤醒操作来实现的。执行唤醒的进程设置被唤醒进程的状态为task_running，然后将其task_struct结构加入到某个cpu的可执行队列中。于是被唤醒的进程将有机会被调度执行。

 

而进程从task_running状态变为非task_running状态，则有两种途径：
​       1、响应信号而进入task_stoped状态、或task_dead状态；
​       2、执行系统调用主动进入task_interruptible状态（如nanosleep系统调用）、或task_dead状态（如exit系统调用）；或由于执行系统调用需要的资源得不到满足，而进入task_interruptible状态或task_uninterruptible状态（如select系统调用）。
显然，这两种情况都只能发生在进程正在cpu上执行的情况下。

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

## 4.3 DHCP dynamic host configuration protoca

一般来说，DHCP服务器必须与服务器在同一个网段

- 分配网卡 IP 地址、子网掩码
- 网络地址（网段第一个地址）、广播地址（网段最后一个地址）
- 默认网关
- DNS 服务器地址
- 引导文件、TFTP服务器地址，用得非常少

DHCP工作流程：

DHCP客户端广播（客户端不知道局域网内谁是DHCP服务器）、linux时间依次递增或过几分钟反复请求。windows系统如果找不到会连续在局域网内广播，但是WIN7以后就不再请求了，会给自己分配一个假的IP。局域网内会可能出现多个 DHCP 服务器，客户端会选择第一个响应 DHCP 服务器。随后客户端发广播消息通知全部DHCP服务器，表示已经选中并且将首次分配的 IP 广播出去，如果证实没有被其它客户端占用，则DHCP正式将IP、掩码、等信息发给客户端。

续租：

租约可能是 7200 s，超过租约。超过一半时间，就会续租请求，直到时间耗完 。重发 DHCP 请求。

## 4.4 FTP服务器

1. 将安装盘挂载到

   mount  -t  iso9660  -o loop  centos7.iso  /media/cdrom

```shell
# etc/yum.repos.d 文件夹 CentOS-Media.repo 配置了 yum 源
baseurl=file:///media/CentOS/
        file:///media/cdrom/
        file:///media/cdrecorder/
```

2. 修改如下两个配置项

```shell
enable=1
gpgcheck=0
```

3. 重建yum缓存，这个必须的，搭建完毕

```shell
yum clean all
yum makecache
```

4. 安装 ftp 服务器端，选择 vsftpd 这款

```shell
yum install vsftpd
```

5. 配置ftp权限

　　目前 FTP 服务登陆允许匿名登陆。

　　vsftpd 的配置目录为 /etc/vsftpd，包含下列的配置文件：

- vsftpd.conf 为主要配置文件
- ftpusers 配置禁止访问 FTP 服务器的用户列表
- user_list 配置用户访问控制

编辑 /etc/vsftpd/vsftpd.conf并修改

```
# 禁用匿名用户
anonymous_enable=NO

# 禁止切换根目录
chroot_local_user=YES
```

重新启动 FTP 服务

```
service vsftpd restart
```

创建一个ftp用户

```
useradd ftpuser
passwd ftpuser
```

6. 禁止用户通过 shell 登陆。虽然不可以通过 shell 登陆，但是可以通过 ftp 、smba登陆

```
usermod -s /sbin/nologin ftpuser
```

7. 为用户分配主目录

`　　　　/data/ftp` 为主目录, 该目录不可上传文件

`　　　　/data/ftp/pub` 文件只能上传到该目录下

```
mkdir -p /data/ftp/pub
```

创建登录欢迎文件 ，非必须

```
echo "Welcome to use FTP service." > /data/ftp/welcome.txt
```

8. 设置访问权限，这里a-w导致ftpuser这个用户没有写目录权限，也即不能在/data/ftp目录创建子目录。pub 目录是全部权限，可进行各种操作。通过ftp登陆后，默认进入 /data/ftp 这个目录，而这个目录是只读的，所以要求 pub 这个目录必须存在，否则啥也干不了。vsftpd增强了安全检查，如果用户被限定在了其主目录下，则该用户的主目录不能再具有写权限了！如果检查发现还有写权限，就会错误。如果在/data/ftp目录有写权限，加上**allow_writeable_chroot=YES**这个配置项

```
chmod a-w /data/ftp && chmod 777 -R /data/ftp/pub
```

9. 设置为用户的主目录：

```
usermod -d /data/ftp ftpuser
```

10. 访问FTP

```
ftp://ftpuser:Password@pub
```

11. 开机自启动：

```shell
chkconfig vsftpd on
```



# 5 shell脚本

系统支持的shell在 /etc/shells文件中可以查看，linux默认的 bash 。Linux典藏大系，shell从入门到精通是一本好书

## 5.1 脚本执行

新建立的脚本，一般没有执行权限，可通过三种方式执行。

- 改权限
- bash 脚本名或者 sh 脚本名，这里 bash 执行使用的是/bin/bash 而 sh执行使用的是 /bin/sh使用的不是同一个shell。sh是bash的子集
- source 脚本名
- . 脚本名

区别是，bash , sh 或者./脚本名的方式执行的脚本，会重新开启一个 shell ，不是在当前 shell 中执行，那么脚本呢执行完成之后，脚本中的变量都失效。而 source 和 . 的方式，变量在当前 shell 依然有效。

## 5.2 变量

可以在配置文件中配置 /etc/profile /etc/bash_profile /etc/bashrc等文件

### 5.2.1 用户变量

\# name=value 等于号两边不能有空格

### 5.2.2 环境变量

1. export name=value
2. declare -x name=value
3. name-value; export name

### 5.2.3 取消环境变量

unset name

## 5.3 环境配置文件加载

- login shell： 取得 bash 是需要完整的登入操作，就称为 login shell。例如，要在 tty1~tty6 登入，需要输入用户的账号与密码，此时取得的 bash 就称为 login shell

- `non-login shell`：取得 bash 接口的方法不需要登入的操作。例如，①你以 X widow 登入 Linux后，在以 X 图形化接口启动终端机，此时那个终端并没有需要再次输入账号和密码，那个 bash 的环境就称 non-login shell 。② 你原本的 bash 环境下再次下达 bash 这个指令，同样的也没有输入账号和密码，那第二个 bash（子程序）也是 non-login shell。

- 交互式 shell，命令行提示符来输入命令。

- 非交互式shell 

  系统执行脚本时所用，没有命令行提示符。

###5.3.1  系统级

1. /etc/environment: 是系统在登录时读取的第一个文件，该文件设置的是整个系统的环境，只要启动系统就会读取该文件，用于为所有进程设置环境变量。系统使用此文件时并不是执行此文件中的命令，而是根据而是根据 KEY=VALUE 模式的代码，对KEY赋值以 VALUE 因此文件中如果要定义 PATH 环境变量，只需加入一行形如 PATH=$PATH:/xxx/bin 的代码即可（debian 中没找到）
2. /etc/profile: 此文件是系统登录时执行的第二个文件。 为系统的每个用户设置环境信息，当用户第一次登录时，该文件被执行。并从 /etc/profile.d **目录**的配置文件中搜集 shell 的设置。（/etc/profile可以用于设定针对全系统所有用户的环境变量，环境变量周期是永久性）
3. /etc/bashrc: 是针对所有用户的 bash 初始化文件，在此中设定的环境变量将应用于所有用户的 shell 中，此文件会在用户每次打开 shell 时执行一次。（即每次新开一个终端，都会执行 /etc/bashrc ）。

### 5.3.2 用户级 

1. ~/.profile: 对应当前登录用户的 profile 文件，用于定制当前用户的个人工作环境(变量是永久性)，每个用户都可使用该文件输入专用于自己使用的 shell 信息,当用户登录时,该文件仅仅执行一次!默认情况下,他设置一些环境变量,执行用户的 .bashrc 文件。**这里是推荐放置个人设置的地方**
2. ~/.bashrc:该文件包含专用于你的bash shell的bash信息，当登录时以及每次打开新的shell时，该文件被读取。(~/.bashrc只针对当前用户，变量的生命周期是永久的)。不推荐放到这儿，因为每开一个shell，这个文件会读取一次，效率肯定有影响。
3. ~/.bash_profile、~./bash_login、~/.bash_profile是交互式login 方式进入 bash 运行的，~/.bashrc 是交互式 non-login 方式进入 bash 运行的通常二者设置大致相同，所以通常前者会调用后者

### 5.3.3 执行顺序

```
1. /etc/profile
      1.1 /etc/profile.d/*.sh
      1.2 /etc/profile.d/lang.sh
      1.3 /etc/sysconfig/i18n
2. ~/.bash_profile | ~/.bash_login | ~/.profile 只会执行三者中的一个，从左边优先级最高
3. ~/.bashrc 由步骤 2 中的一个启动
4. /etc/bashrc
5. ~/.bash_logout
```

####/etc/profile

```
  1 # /etc/profile: system-wide .profile file for the Bourne shell (sh(1))  
  2 # and Bourne compatible shells (bash(1), ksh(1), ash(1), ...).
  全部 Bourne 类型的 shell 登陆都会加载这个文件
  3
  4 if [ "`id -u`" -eq 0 ]; then
  5   PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" 超级用户 path
  6 else
  7   PATH="/usr/local/bin:/usr/bin:/bin:/usr/local/games:/usr/games"  普通用户 path
  8 fi
  9 export PATH 导出成为全局变量
 10 
 11 if [ "$PS1" ]; then  如果设置了 PS1 这个变量 
 12   if [ "$BASH" ] && [ "$BASH" != "/bin/sh" ]; then 如果设置了 BASH 这个变量，不是 /bin/sh
 13     # The file bash.bashrc already sets the default PS1.
 14     # PS1='\h:\w\$ '
 15     if [ -f /etc/bash.bashrc ]; then  如果bash.bashrc存在，先导入/etc/bash.bashrc
 16       . /etc/bash.bashrc
 17     fi
 18   else
 19     if [ "`id -u`" -eq 0 ]; then 没有 BASH PS1 比较简单
 20       PS1='# '
 21     else
 22       PS1='$ '
 23     fi
 24   fi
 25 fi
```

## 5.4 获取指令（程序运行结果）

- ``运算符

  ls 命令返回 h1  testln.tet，令变量 test1 接收这个返回值

  ```
  wudeyun@www:~$ ls
  h1  testln.tet
  wudeyun@www:~$ test1=`ls`
  wudeyun@www:~$ echo $test1 
  h1 testln.tet
  ```

- \$\(\) 符号

  ```
  wudeyun@www:~$ test2=$(ls)
  wudeyun@www:~$ echo $test2
  h1 testln.tet
  ```

## 5.5 括号、引号

双引号和单引号，是为了解决 linux 中的空格问题。单引号和双引号与字符串无关，linux默认类型为字符串，即使不写引号，依然可以识别为字符串。只是，双引号内的 $ 和\`\`符号，是可以生效的，还有特殊字符也是可以生效的，而单引号不管是啥，原样输出。

<font color=red>\[</font>  ：是 linux 的内建命令，用于测试

- \[ 后要跟个空格，不然识别不了
- 内部操作符与操作变量之间要有空格：如  [  “a”  =  “b”  ]
- 字符串比较中 \>  需要写成 \\> 进行转义
- \[ \]中字符串或者${}变量尽量使用"" 双引号扩住，避免值未定义引用而出错的好办法
- \[ 中可以使用 –a ! –o 进行逻辑运算

<font color=red>\[\[</font>  ：提供了 <font color=red>\[</font> 功能的扩展，但是它不是 shell 的内建命令，就是说它不是一个程序，只是一个shell关键词

- 字符串比较中，可以直接使用 \> \< 无需转义
- [\[ 中字符串或者${}变量尽量如未使用"" 双引号扩住的话，会进行模式和元字符匹配

```shell
[root@localhostkuohao]# [[ "ab" = a* ]] && echo "ok"
  ok
```

- \[\[ 内部可以使用 &&  || 进行逻辑运算

```shell

[  exp1  -a exp2  ] = [[  exp1 && exp2 ]] = [  exp1  ]&& [  exp2  ] = [[ exp1  ]] && [[  exp2 ]]
[  exp1  -o exp2  ] = [[  exp1 || exp2 ]] = [  exp1  ]|| [  exp2  ] = [[ exp1  ]] || [[  exp2 ]]

[root@localhost ~]# if [[ "a" == "a" && 2 -gt1 ]] ;then echo "ok" ;fi
ok
[root@localhost ~]# if [[ "a" == "a" ]] && [[2 -gt 1 ]] ;then echo "ok" ;fi
ok
[root@localhost ~]# if [[ "a" == "a" ]] || [[ 2 -gt 1]] ;then echo "ok" ;fi
ok
[root@localhost ~]# if [[ "a" == "a" ]] || [[ 2 -gt10 ]] ;then echo "ok" ;fi
ok
[root@localhost ~]# if [[ "a" == "a"  || 2 -gt 10 ]] ;then echo "ok" ;fi
ok
```

- 其他用法都和 \[ 一样

<font color=red>\(\(\)\)</font> ：只要符合 C 中算术运算都可以用

## 5.6 变量作用域

1. 函数内部定义变量，也是全局变量，函数内部变量可以先使用，再定义

   ```shell
     1 #! /bin/bash
     2 
     3 func()
     4 {
     5   echo "$v1"  未定义，这个地方是函数申明
     6   v1=200
     7 }
     8 
     9 v1=100
    10 func    函数调用
    11 echo "$v1"
   ```

2. 局部变量

   ```
     1 #! /bin/bash
     2 
     3 func()
     4 {
     5   echo "global variable v1 is $v1"
     6   local v1=2
     7   echo "local variable v1 is $v1"
     8 }
     9 
    10 v1=1
    11 func
    12 echo "global variable v1 is $v1"
   ```

## 5.7 条件测试

字符串条件测试 = 、!= 、-n、-z、[ 'abc' = 'abc' ] 

```shell
  1 #! /bin/bash
  2 
  3 x='abc'
  4 
  5 #非空测试
  6 echo "empty test"
  7 test $x
  8 echo $?
  9 
 10 #不等测试
 11 echo "not equal test"
 12 test 'abc' != $x
 13 echo $?
 14 
 15 #等号两边有空格，赋值没有
 16 echo "equal test"
 17 test 'abc' = $x
 18 echo $?
 19 
 20 echo "not empty test"
 21 test -n $x
 22 echo $?
 23 [ "a" = "b" ]; echo $?
```

数字测试 [ ]、eq 、ne、gt、lt、le、ge。无论是字符测试还是数字测试，要想让运算符发挥作用，变量与运算符之间都要留空格，还有各种括号，也算是运算符，都要留空格。我猜在条件测试的时候，linux还是把这些东西都当作字符串来处理了，注意，测试的时候，不能使用数学上的 > 、< 等。

文件测试也一样，

## 5.8 if else

如果写在一行，要用 ; 隔开。

```shell
if 语句
  then 语句
fi
```

## 5.9 case

;; 表示 case 结束，前面都部匹配，最终要执行 *）后面的语句

```shell
case var in
  condition1)
  语句
  ;;
  condition2)
  语句
  ;;
  condition3)
  语句
  ;;
  *)
  最终匹配的语句
 esac
```

##5.10 数学运算赋值

shell 只会处理文字，所以要让 ()跟数学上的范围统一，必须要转义。\$(())和\$[]是告诉shell我现在进行数学运算

```shell
x=`expr 10 - \( 4 - 7 \)`
```

\$\(\(\...)\) 这个里面随便写

```shell
x=$(((2+10)/4*20))
```

\$\[...\] 括号里面随便写

```shell
x=$[1+(2*5)/6+3*2]
```

let 是赋值用的命令，不用空格

```shell
ww:~$ let "x=4"
```

## 5.11 循环

### 5.11.1 for

- {1..2} 表范围

```shell
#! /bin/bash
 
for var in {1..2}
do
echo "the number is $var"
done
```

```shell
wudeyun@www:~$ bash st10.sh 
the number is 1
the number is 2
```

- {1..20..5} start end step

```shell
#! /bin/bash

for var in {1..20..5} 
 do
   echo "the number is $var"
 done
```

```shell
wudeyun@www:~$ bash st10.sh 
the number is 1
the number is 6
the number is 11
the number is 16
```

- 遍历字符串序列

```shell
for x in str1 str2 str3
do
    echo $x
done
```

- 遍历数组

```shell
array=(1 2 3 4 5 6)
for x in ${array[@]}
do
    echo $x
done

for x in ${array[*]}
do
    echo $x
done
```

- 遍历序列，输出 1 到 10

```shell
for x in `seq 1 10`
do
    echo $x
done
```

### 5.11.2 until

```shell
#! /bin/shell

i=1

until [ $i -gt 5 ]
do
 let "square = i*i"
 echo "$i*$i = $square"
 let "i=i+1"
done
```

```shell
wudeyun@www:~$ bash st11.sh 
1*1 = 1
2*2 = 4
3*3 = 9
4*4 = 16
5*5 = 25
```

### 5.11.3 while

```shell
#! /bin/shell

i=1

while [ $i -lt 5 ]
do
  let "square = i * i"
  echo "$i*$i=$square"
  let "i = i + 1"
done
```

```shell
wudeyun@www:~$ bash st12.sh 
1*1=1
2*2=4
3*3=9
4*4=16
```

## 5.12 函数

### 5.12.1 函数定义

函数要先定义，后调用，否则， linux 会把调用语句看成命令。导致出错

```shell
function funname(){
    语句
}

funname
```

### 5.12.2 return

跟 exit 作用一样，只能返回 0 - 255

```shell
#! /bin/bash

sum(){
  let "z = $1 + $2"
  return $z
}

sum 22 4
echo $?
```

运行

```shell
wudeyun@www:~$ bash sum.sh 
26
```

### 5.12.3 shift

shift n 会将参数左移 n 个。比如

shift 3 会将 \$4 左移 3 个，变成 \$1 

```shell
#! /bin/bash

until [ $# -eq 0 ]
do
  echo $1
  shift
done
```

### 5.12.4 getopts

```shell
#! /bin/bash

func(){
  while getopts "a:b:c" arg
  do
    case "$arg" in
    a)
      echo "a's argument is $OPTARG"
      ;;
    b)
      echo "b's argument is $OPTARG"
      ;;
    c)
      echo "c's argument is $OPTARG"
      ;;
    ?)
      echo "unkown argument."
      exit 1
      ;;
    esac
  done
}

func -a hello -b world -c wudeyun
```

可以接收 a、b、c 三个参数，但是，只有 后面跟 ： 的参数有 $OPTARG 值，参数 c 后面没有 : ，所以取不到值

```shell
wudeyun@www:~$ bash funopt.sh 
a's argument is hello
b's argument is world
c's argument is
```

## 5.13 数组

### 5.13.1定义

array=(v0 v1 v2 v3)

array=([0]=value0 [1]=vlaue1 [2]=value2 ... [n]=valuen)

### 5.13.2使用

${#array[@]} 计数

${array[n]}第 n 个

${array[@]} 全部

### 5.13.3删除数组元素

unset array[n]

unset array

# 6 文本编辑

## 6.1 sed

**1.简介**

sed是非交互式的编辑器。它不会修改文件，除非使用shell重定向来保存结果。默认情况下，所有的输出行都被打印到屏幕上。

sed编辑器逐行处理文件（或输入），并将结果发送到屏幕。具体过程如下：首先sed把当前正在处理的行保存在一个临时缓存区中（也称为模式空间），然后处理临时缓冲区中的行，完成后把该行发送到屏幕上。sed每处理完一行就将其从临时缓冲区删除，然后将下一行读入，进行处理和显示。处理完输入文件的最后一行后，sed便结束运行。sed把每一行都存在临时缓冲区中，对这个副本进行编辑，所以不会修改原文件。

**2.定址**

定址用于决定对哪些行进行编辑。地址的形式可以是数字、正则表达式、或二者的结合。如果没有指定地址，sed将处理输入文件的所有行。

地址是一个数字，则表示行号；是“$"符号，则表示最后一行。例如： 

```
`sed -n '3p' datafile只打印第三行`
```

 只显示指定行范围的文件内容，例如：

\# 只查看文件的第100行到第200行
sed -n '100,200p' mysql_slow_query.log

地址是逗号分隔的，那么需要处理的地址是这两行之间的范围（包括这两行在内）。范围可以用数字、正则表达式、或二者的组合表示。例如：

```
`sed '2,5d' datafile#删除第二到第五行sed '/My/,/You/d' datafile#删除包含"My"的行到包含"You"的行之间的行sed '/My/,10d' datafile#删除包含"My"的行到第十行的内容`
```

**3.命令与选项**

sed命令告诉sed如何处理由地址指定的各输入行，如果没有指定地址则处理所有的输入行。

**3.1 sed命令**

| 命令 | 功能                                                         |
| ---- | ------------------------------------------------------------ |
| a\   | 在当前行后添加一行或多行。多行时除最后一行外，每行末尾需用“\”续行 |
| c\   | 用此符号后的新文本替换当前行中的文本。多行时除最后一行外，每行末尾需用"\"续行 |
| i\   | 在当前行之前插入文本。多行时除最后一行外，每行末尾需用"\"续行 |
| d    | 删除行                                                       |
| h    | 把模式空间里的内容复制到暂存缓冲区                           |
| H    | 把模式空间里的内容追加到暂存缓冲区                           |
| g    | 把暂存缓冲区里的内容复制到模式空间，覆盖原有的内容           |
| G    | 把暂存缓冲区的内容追加到模式空间里，追加在原有内容的后面     |
| l    | 列出非打印字符                                               |
| p    | 打印行                                                       |
| n    | 读入下一输入行，并从下一条命令而不是第一条命令开始对其的处理 |
| q    | 结束或退出sed                                                |
| r    | 从文件中读取输入行                                           |
| !    | 对所选行以外的所有行应用命令                                 |
| s    | 用一个字符串替换另一个                                       |
| g    | 在行内进行全局替换                                           |
|      |                                                              |
| w    | 将所选的行写入文件                                           |
| x    | 交换暂存缓冲区与模式空间的内容                               |
| y    | 将字符替换为另一字符（不能对正则表达式使用y命令）            |

**3.2 sed选项**

| 选项 | 功能                                          |
| ---- | --------------------------------------------- |
| -e   | 进行多项编辑，即对输入行应用多条sed命令时使用 |
| -n   | 取消默认的输出                                |
| -f   | 指定sed脚本的文件名                           |

**4.退出状态**

sed不向grep一样，不管是否找到指定的模式，它的退出状态都是0。只有当命令存在语法错误时，sed的退出状态才不是0。

**5.正则表达式元字符**

 与grep一样，sed也支持特殊元字符，来进行模式查找、替换。不同的是，sed使用的正则表达式是括在斜杠线"/"之间的模式。

如果要把正则表达式分隔符"/"改为另一个字符，比如o，只要在这个字符前加一个反斜线，在字符后跟上正则表达式，再跟上这个字符即可。例如：sed -n '\o^Myop' datafile

| 元字符   | 功能                           | 示例                                                         |
| -------- | ------------------------------ | ------------------------------------------------------------ |
| ^        | 行首定位符                     | /^my/  匹配所有以my开头的行                                  |
| $        | 行尾定位符                     | /my$/  匹配所有以my结尾的行                                  |
| .        | 匹配除换行符以外的单个字符     | /m..y/  匹配包含字母m，后跟两个任意字符，再跟字母y的行       |
| *        | 匹配零个或多个前导字符         | /my*/  匹配包含字母m,后跟零个或多个y字母的行                 |
| []       | 匹配指定字符组内的任一字符     | /[Mm]y/  匹配包含My或my的行                                  |
| [^]      | 匹配不在指定字符组内的任一字符 | /[^Mm]y/  匹配包含y，但y之前的那个字符不是M或m的行           |
| \(..\)   | 保存已匹配的字符               | 1,20s/\(you\)self/\1r/  标记元字符之间的模式，并将其保存为标签1，之后可以使用\1来引用它。最多可以定义9个标签，从左边开始编号，最左边的是第一个。此例中，对第1到第20行进行处理，you被保存为标签1，如果发现youself，则替换为your。 |
| &        | 保存查找串以便在替换串中引用   | s/my/**&**/  符号&代表查找串。my将被替换为**my**             |
| \<       | 词首定位符                     | /\<my/  匹配包含以my开头的单词的行                           |
| \>       | 词尾定位符                     | /my\>/  匹配包含以my结尾的单词的行                           |
| x\{m\}   | 连续m个x                       | /9\{5\}/ 匹配包含连续5个9的行                                |
| x\{m,\}  | 至少m个x                       | /9\{5,\}/  匹配包含至少连续5个9的行                          |
| x\{m,n\} | 至少m个，但不超过n个x          | /9\{5,7\}/  匹配包含连续5到7个9的行                          |

**6.范例**

**6.1p命令**

命令p用于显示模式空间的内容。默认情况下，sed把输入行打印在屏幕上，选项-n用于取消默认的打印操作。当选项-n和命令p同时出现时,sed可打印选定的内容。

```
`sed '/my/p' datafile#默认情况下，sed把所有输入行都打印在标准输出上。如果某行匹配模式my，p命令将把该行另外打印一遍。``sed -n '/my/p' datafile#选项-n取消sed默认的打印，p命令把匹配模式my的行打印一遍。`
```

**6.2 d命令**

命令d用于删除输入行。sed先将输入行从文件复制到模式空间里，然后对该行执行sed命令，最后将模式空间里的内容显示在屏幕上。如果发出的是命令d，当前模式空间里的输入行会被删除，不被显示。

```
`sed '$d' datafile#删除最后一行，其余的都被显示sed '/my/d' datafile#删除包含my的行，其余的都被显示`
```

**6.3 s命令**

```
`sed 's/^My/You/g' datafile#命令末端的g表示在行内进行全局替换，也就是说如果某行出现多个My，所有的My都被替换为You。sed -n '1,20s/My$/You/gp' datafile#取消默认输出，处理1到20行里匹配以My结尾的行，把行内所有的My替换为You，并打印到屏幕上。`
```

```
`sed 's#My#Your#g' datafile#紧跟在s命令后的字符就是查找串和替换串之间的分隔符。分隔符默认为正斜杠，但可以改变。无论什么字符（换行符、反斜线除外），只要紧跟s命令，就成了新的串分隔符。`
```

**6.4 e选项**

-e是编辑命令，用于sed执行多个编辑任务的情况下。在下一行开始编辑前，所有的编辑动作将应用到模式缓冲区中的行上。

```
`sed -e '1,10d' -e 's/My/Your/g' datafile``#选项-e用于进行多重编辑。第一重编辑删除第1-3行。第二重编辑将出现的所有My替换为Your。因为是逐行进行这两项编辑（即这两个命令都在模式空间的当前行上执行），所以编辑命令的顺序会影响结果。`
```

**6.5 r命令**

r命令是读命令。sed使用该命令将一个文本文件中的内容加到当前文件的特定位置上。

```
`sed '/My/r introduce.txt' datafile#如果在文件datafile的某一行匹配到模式My，就在该行后读入文件introduce.txt的内容。如果出现My的行不止一行，则在出现My的各行后都读入introduce.txt文件的内容。`
```

**6.6 w命令**

```
`sed -n '/hrwang/w me.txt' datafile`
```

**6.7 a\ 命令**

a\ 命令是追加命令，追加将添加新文本到文件中当前行（即读入模式缓冲区中的行）的后面。所追加的文本行位于sed命令的下方另起一行。如果要追加的内容超过一行，则每一行都必须以反斜线结束，最后一行除外。最后一行将以引号和文件名结束。

```
`sed '/^hrwang/a\>hrwang and mjfan are husband\>and wife' datafile#如果在datafile文件中发现匹配以hrwang开头的行，则在该行下面追加hrwang and mjfan are husband and wife` 
```

**6.8 i\ 命令**

i\ 命令是在当前行的前面插入新的文本。

**6.9 c\ 命令**

sed使用该命令将已有文本修改成新的文本。

**6.10 n命令**

sed使用该命令获取输入文件的下一行，并将其读入到模式缓冲区中，任何sed命令都将应用到匹配行紧接着的下一行上。

```
`sed '/hrwang/{n;s/My/Your/;}' datafile`
```

注：如果需要使用多条命令，或者需要在某个地址范围内嵌套地址，就必须用花括号将命令括起来，每行只写一条命令，或这用分号分割同一行中的多条命令。

**6.11 y命令**

该命令与UNIX/Linux中的tr命令类似，字符按照一对一的方式从左到右进行转换。例如，y/abc/ABC/将把所有小写的a转换成A，小写的b转换成B，小写的c转换成C。

```
`sed '1,20y/hrwang12/HRWANG^$/' datafile#将1到20行内，所有的小写hrwang转换成大写，将1转换成^,将2转换成$。#正则表达式元字符对y命令不起作用。与s命令的分隔符一样，斜线可以被替换成其它的字符。`
```

**6.12 q命令**

q命令将导致sed程序退出，不再进行其它的处理。

```
`sed '/hrwang/{s/hrwang/HRWANG/;q;}' datafile`
```

**6.13 h命令和g命令**

```
`#cat datafile``My name is hrwang.``Your name is mjfan.``hrwang is mjfan's husband.``mjfan is hrwang's wife.`` ` `sed -e '/hrwang/h' -e '$G' datafile``sed -e '/hrwang/H' -e '$G' datafile``#通过上面两条命令，你会发现h会把原来暂存缓冲区的内容清除，只保存最近一次执行h时保存进去的模式空间的内容。而H命令则把每次匹配hrwnag的行都追加保存在暂存缓冲区。``sed -e '/hrwang/H' -e '$g' datafile``sed -e '/hrwang/H' -e '$G' datafile``#通过上面两条命令，你会发现g把暂存缓冲区中的内容替换掉了模式空间中当前行的内容，此处即替换了最后一行。而G命令则把暂存缓冲区的内容追加到了模式空间的当前行后。此处即追加到了末尾。`
```

**7. sed脚本**

sed脚本就是写在文件中的一列sed命令。脚本中，要求命令的末尾不能有任何多余的空格或文本。如果在一行中有多个命令，要用分号分隔。执行脚本时，sed先将输入文件中第一行复制到模式缓冲区，然后对其执行脚本中所有的命令。每一行处理完毕后，sed再复制文件中下一行到模式缓冲区，对其执行脚本中所有命令。使用sed脚本时，不再用引号来确保sed命令不被shell解释。例如sed脚本script：`

## 6.2 AWK

awk  '<font color=red>pattern   </font> {  <font color=blue>commands</font>}'  files

awk的用法1：

```shell
`awk `/La/` dataf3       #显示含La的行。`
```

awk的用法2:

```shell
`awk -F ``":"` `'{print $1,$2}'` `/etc/passwd　　#以“:”为分割,显示/etc/passwd每一行的第1和第2个字段。$1代表第1个字段，$2代表第2个字段，其他类推.`
```

awk的用法3：

```shell
`awk ``'/La/{ print $1,$2 }'` `dataf3  #将含有La关键字的数据行的第1及第2个字段显示出来.默认使用空格分割.`
```

awk的用法4：

```shell
`awk -F : ``'/^www/{print $3,$4}'` `/etc/passwd  # 使用选项 -F，指定：为分隔符，账号www的uid（第3个字段）及gid（第4个字段）显示出来.`
```

awk的用法5：

```shell
`[root@localhost~]# awk -F : ``'/^r/{print $1}'` `/etc/passwd        #显示以r开头的行的第一个字段``root``rpc``rpcuser`
```

awk的用法6：

```shell
`[root@localhost~]# awk -F : ``'$3>=500{print $1,$3}'` `/etc/passwd   #找出$3这个字段的id大于等于500的行，并显示1、3列``www 500``cacti 501``nagios 502``vsftpd 503`
```

awk的用法7：

```shell
`[root@localhost~]# awk -F : ``'$7~"bash"{print $1,$7}'` `/etc/passwd        #匹配出$7是bash的行，如果为真则打印出来``root /bin/bash``mysql /bin/bash``www /bin/bash``cacti /bin/bash``nagios /bin/bash`
```

awk的用法8：

```shell
`[root@localhost~]# awk -F : ``'$7!~"bash"{print $1,$7}'` `/etc/passwd       #取出$7不是bash的行并打印出来``bin /sbin/nologin``daemon /sbin/nologin``adm /sbin/nologin``lp /sbin/nologin``sync /bin/sync``shutdown /sbin/shutdown`
```

## 6.3 tr 字符替换

1. 不带参数将SET2替换SET1替换，且SET1长度大于SET2

```shell
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr 'abc' '12'
11AA122BB222CC3
```

<font color=blue>a被替换成1，b被替换成2，c被替换成2</font>

2. 不带参数将SET2替换SET1替换，且SET1长度小于SET2

```shell
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr 'ab' '123'
11AA122BB2ccCC3
```

<font color=blue>a被替换成1，b被替换成2</font>

3. -t参数

```shell
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -t 'abc' '12'
11AA122BB2ccCC3
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -t 'ab' '123'
11AA122BB2ccCC3
```

<font color=blue>都是a被替换成1，b被替换成2</font>

4.删除指定字符,-d

```shell
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -d 'a-z' 
AA1BB2CC3
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -d -c 'a-z\n'  
aabbcc
```

注意 -c 的作用是取反\( complement \)的意思

```shell
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -d '[:lower:]' 
AA1BB2CC3
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -d -c '[:lower:]\n'
aabbcc
```

5.替换连续字符，-s

```shell
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -s 'a-zA-Z'
aA1bB2cC3
[root@localhost ~]# echo "aaAA1bbBB2ccCC3" | tr -s '[:alnum:]\n'
aA1bB2cC3
```

上面两种方法都是将重复的多个字符替换成单个字符

6.-c操作 

```shell
[root@localhost test]# echo "name" |tr -d -c 'a \n'
a
```

上述操作是删除标准输入中除“a”，空格 "\n"之外的字符

# 8 Unix环境高级编程

## 8.1 系统调用

调用内核的接口被称为系统调用，内核控制计算机硬件资源
