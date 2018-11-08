# 0 常用

## 0.1 POSIX

**POSIX**：表示[可移植操作系统接口](https://baike.baidu.com/item/%E5%8F%AF%E7%A7%BB%E6%A4%8D%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F%E6%8E%A5%E5%8F%A3/12718298)（Portable Operating System Interface of UNIX，缩写为 POSIX ）

**POSIX标准**：定义了操作系统应该为应用程序提供的接口标准，为要在各种UNIX操作系统上运行的**软件**而定义的一系列API标准的总称，是 IEEE 组织制定了它，其正式称呼为IEEE 1003，而国际标准名称为ISO/IEC 9945。

Windows 系统不能直接支持新版POSIX接口，仅支持第一版标准。故 Windows 系统不能直接创建 **符合POSIX接口标准** 的线程和窗体、套接字。所以微软公司提供POSIX兼容层软件包（Windows Services for UNIX）以支持新版POSIX接口，Windows 系统还可以运行其他POSIX兼容层例如 Cygwin。也就是说，安装了 Cygwin 的 Windows 可以运行基于 POSIX 编写的应用程序。

# 1. 基础

![Unix体系结构](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/unix%E4%BD%93%E7%B3%BB%E7%BB%93%E6%9E%84.png)

## 1.1 内核态，用户态

​	内核从本质上看是一种软件——控制计算机的硬件资源，并提供上层应用程序运行的环境。用户态即上层应用程序的活动空间，应用程序的执行必须依托于内核提供的资源，包括CPU资源、存储资源、I/O资源等。为了使上层应用能够访问到这些资源，内核必须为上层应用提供访问的接口：即**系统调用**。 

![内核功能](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/%E5%86%85%E6%A0%B8%E5%8A%9F%E8%83%BD.png)

​	系统调用各种版本的Unix实现都提供了不同数量的系统调用，Linux的不同版本提供了240-260个系统调用，FreeBSD大约提供了320个（reference：UNIX环境高级编程）。**库函数**的实现是对系统调用的封装，对于简单的操作，我们可以直接调用系统调用来访问资源，对于复杂操作，我们借助于库函数来实现，这样的库函数依据不同的标准也可以有不同的实现版本，如 **ISO C** 标准库，**POSIX** 标准库等。

　　**Shell**是一个特殊的应用程序，本质上是一个命令解释器，它下通系统调用，上通各种应用。每一个 shell  命令是一个系统进程，shell 实际上就是一系列小工具。shell 可编程。

​        shell 跟库函数的区别是，库函数不能单独运行，而 shell 是可单独运行的进程。shell 与库函数没有交集。这就好比如，我们编程使用 c 函数库或者 c++ 函数库，但是我们不会去使用 cmd来编程，我们只会写 cmd 或者 shell 脚本。应用本身可以直接进行系统调用，但是十分麻烦，所以一般不这么做。

​	用户态的应用程序可以通过三种方式来访问内核态的资源：

- 系统调用
- 库函数
- Shell脚本

​        因为系统资源有限，Linux系统采用 **0** 和 **3** 两个特权级，分别对应**内核态**和**用户态**。**内核态** cpu可以访问内存的所有数据，包括外围设备，例如硬盘，网卡，cpu也可以将自己从一个程序切换到另一个程序。 只能受限的访问内存，且不允许访问外围设备，占用cpu的能力被剥夺，cpu资源可以被其他程序获取。 很多程序开始时运行于用户态，但在执行的过程中，一些操作需要在内核权限下才能执行，这就涉及到一个从用户态切换到内核态的过程。比如C函数库中的内存分配函数malloc()，它具体是使用sbrk()系统调用来分配内存，当malloc调用sbrk()的时候就涉及一次从用户态到内核态的切换，类似的函数还有printf()，调用的是wirte()系统调用来输出字符串，等等。由于需要限制不同的程序之间的访问能力, 防止他们获取别的程序的内存数据, 或者获取外围设备的数据, 并发送到网络, CPU划分出两个权限等级 -- 用户态和内核态。

### **用户态与内核态的切换**

所有用户程序都是运行在用户态的, 但是有时候程序确实需要做一些内核态的事情, 例如从硬盘读取数据, 或者从键盘获取输入等. 而唯一可以做这些事情的就是操作系统, 所以此时程序就需要先操作系统请求以程序的名义来执行这些操作.

这时需要一个这样的机制: 用户态程序切换到内核态, 但是不能控制在内核态中执行的指令

这种机制叫**系统调用**, 在CPU中的实现称之为**陷阱指令**(Trap Instruction)

他们的工作流程如下:

1. 用户态程序将一些数据值放在寄存器中, 或者使用参数创建一个堆栈(stack frame), 以此表明需要操作系统提供的服务.
2. 用户态程序执行陷阱指令
3. CPU切换到内核态, 并跳到位于内存指定位置的指令, 这些指令是操作系统的一部分, 他们具有内存保护, 不可被用户态程序访问
4. 这些指令称之为陷阱(trap)或者系统调用处理器(system call handler). 他们会读取程序放入内存的数据参数, 并执行程序请求的服务
5. 系统调用完成后, 操作系统会重置CPU为用户态并返回系统调用的结果

当一个任务（进程）执行系统调用而陷入内核代码中执行时，我们就称进程处于内核运行态（或简称为内核态）。此时处理器处于特权级最高的（0级）内核代码中执行。当进程处于内核态时，执行的内核代码会使用当前进程的内核栈。每个进程都有自己的内核栈。当进程在执行用户自己的代码时，则称其处于用户运行态（用户态）。即此时处理器在特权级最低的（3级）用户代码中运行。当正在执行用户程序而突然被中断程序中断时，此时用户程序也可以象征性地称为处于进程的内核态。因为中断处理程序将使用当前进程的内核栈。这与处于内核态的进程的状态有些类似。 
内核态与用户态是操作系统的两种运行级别,跟intel cpu没有必然的联系, intel cpu提供Ring0-Ring3三种级别的运行模式，Ring0级别最高，Ring3最低。Linux使用了Ring3级别运行用户态，Ring0作为 内核态，没有使用Ring1和Ring2。Ring3状态不能访问Ring0的地址空间，包括代码和数据。Linux进程的4GB地址空间，3G-4G部 分大家是共享的，是内核态的地址空间，这里存放在整个内核的代码和所有的内核模块，以及内核所维护的数据。用户运行一个程序，该程序所创建的进程开始是运 行在用户态的，如果要执行文件操作，网络数据发送等操作，必须通过write，send等系统调用，这些系统调用会调用内核中的代码来完成操作，这时，必 须切换到Ring0，然后进入3GB-4GB中的内核地址空间去执行这些代码完成操作，完成后，切换回Ring3，回到用户态。这样，用户态的程序就不能 随意操作内核地址空间，具有一定的安全保护作用。
至于说保护模式，是说通过内存页表操作等机制，保证进程间的地址空间不会互相冲突，一个进程的操作不会修改另一个进程的地址空间中的数据。

 用户态和内核态的概念区别

究竟什么是用户态，什么是内核态，这两个基本概念以前一直理解得不是很清楚，根本原因个人觉得是在于因为大部分时候我们在写程序时关注的重点和着眼的角度放在了实现的功能和代码的逻辑性上，先看一个例子：

1）例子

```c
void testfork()
{  
	if(0 = = fork())
	{  
		printf(“create new process success!\n”);  
	}  
	printf(“testfork ok\n”);  
}
```

这段代码很简单，从功能的角度来看，就是实际执行了一个fork()，生成一个新的进程，从逻辑的角度看，就是判断了如果fork()返回的是0则打印相关语句，然后函数最后再打印一句表示执行完整个testfork()函数。代码的执行逻辑和功能上看就是如此简单，一共四行代码，从上到下一句一句执行而已，完全看不出来哪里有体现出用户态和进程态的概念。

如果说前面两种是静态观察的角度看的话，我们还可以从动态的角度来看这段代码，即它被转换成CPU执行的指令后加载执行的过程，这时这段程序就是一个动态执行的指令序列。而究竟加载了哪些代码，如何加载就是和操作系统密切相关了。

2）特权级

熟悉Unix/Linux系统的人都知道，fork的工作实际上是以系统调用的方式完成相应功能的，具体的工作是由sys_fork负责实施。其实无论是不是Unix或者Linux，对于任何操作系统来说，创建一个新的进程都是属于核心功能，因为它要做很多底层细致地工作，消耗系统的物理资源，比如分配物理内存，从父进程拷贝相关信息，拷贝设置页目录页表等等，这些显然不能随便让哪个程序就能去做，于是就自然引出特权级别的概念，显然，最关键性的权力必须由高特权级的程序来执行，这样才可以做到集中管理，减少有限资源的访问和使用冲突。

特权级显然是非常有效的管理和控制程序执行的手段，因此在硬件上对特权级做了很多支持，就Intel x86架构的CPU来说一共有0~3四个特权级，0级最高，3级最低，硬件上在执行每条指令时都会对指令所具有的特权级做相应的检查，相关的概念有CPL、DPL和RPL，这里不再过多阐述。硬件已经提供了一套特权级使用的相关机制，软件自然就是好好利用的问题，这属于操作系统要做的事情，对于Unix/Linux来说，只使用了0级特权级和3级特权级。也就是说在Unix/Linux系统中，一条工作在0级特权级的指令具有了CPU能提供的最高权力，而一条工作在3级特权级的指令具有CPU提供的最低或者说最基本权力。

3）用户态和内核态

现在我们从特权级的调度来理解用户态和内核态就比较好理解了，当程序运行在3级特权级上时，就可以称之为运行在用户态，因为这是最低特权级，是普通的用户进程运行的特权级，大部分用户直接面对的程序都是运行在用户态；反之，当程序运行在0级特权级上时，就可以称之为运行在内核态。

虽然用户态下和内核态下工作的程序有很多差别，但最重要的差别就在于特权级的不同，即权力的不同。运行在用户态下的程序不能直接访问操作系统内核数据结构和程序，比如上面例子中的testfork()就不能直接调用sys_fork()，因为前者是工作在用户态，属于用户态程序，而sys_fork()是工作在内核态，属于内核态程序。

当我们在系统中执行一个程序时，大部分时间是运行在用户态下的，在其需要操作系统帮助完成某些它没有权力和能力完成的工作时就会切换到内核态，比如testfork()最初运行在用户态进程下，当它调用fork()最终触发sys_fork()的执行时，就切换到了内核态。

\2. 用户态和内核态的转换

1）用户态切换到内核态的3种方式

a. 系统调用

这是用户态进程主动要求切换到内核态的一种方式，用户态进程通过系统调用申请使用操作系统提供的服务程序完成工作，比如前例中fork()实际上就是执行了一个创建新进程的系统调用。而系统调用的机制其核心还是使用了操作系统为用户特别开放的一个中断来实现，例如Linux的int 80h中断。

b. 异常

当CPU在执行运行在用户态下的程序时，发生了某些事先不可知的异常，这时会触发由当前运行进程切换到处理此异常的内核相关程序中，也就转到了内核态，比如缺页异常。

c. 外围设备的中断

当外围设备完成用户请求的操作后，会向CPU发出相应的中断信号，这时CPU会暂停执行下一条即将要执行的指令转而去执行与中断信号对应的处理程序，如果先前执行的指令是用户态下的程序，那么这个转换的过程自然也就发生了由用户态到内核态的切换。比如硬盘读写操作完成，系统会切换到硬盘读写的中断处理程序中执行后续操作等。

这3种方式是系统在运行时由用户态转到内核态的最主要方式，其中系统调用可以认为是用户进程主动发起的，异常和外围设备中断则是被动的。

2）具体的切换操作

从触发方式上看，可以认为存在前述3种不同的类型，但是从最终实际完成由用户态到内核态的切换操作上来说，涉及的关键步骤是完全一致的，没有任何区别，都相当于执行了一个中断响应的过程，因为系统调用实际上最终是中断机制实现的，而异常和中断的处理机制基本上也是一致的，关于它们的具体区别这里不再赘述。关于中断处理机制的细节和步骤这里也不做过多分析，涉及到由用户态切换到内核态的步骤主要包括：

[1] 从当前进程的描述符中提取其内核栈的ss0及esp0信息。

[2] 使用ss0和esp0指向的内核栈将当前进程的cs,eip,eflags,ss,esp信息保存起来，这个

过程也完成了由用户栈到内核栈的切换过程，同时保存了被暂停执行的程序的下一

条指令。

[3] 将先前由中断向量检索得到的中断处理程序的cs,eip信息装入相应的寄存器，开始

执行中断处理程序，这时就转到了内核态的程序执行了。 

在内核中，存在一个数组称为中断向量表，该表中第n项对应第n号中断号相应的中断处理程序的指针。当中断来临时，CPU会暂停当前执行的代码，根据中断的中断号，在中断向量表中找到对应的中断处理程序，并调用它。

在LINUX中，进程由**用户态切换到内核态时，需要切换栈**，内核态与用户态使用不同的栈，两者互不干扰，互不相关，且每个进程都有自己的内核栈。在系统调用完成后，则进行栈切换，从内核栈切换回用户栈，回到用户态。

## 1.2. 中断

### 1.2.1 中断处理

| 类别 | 原因             | 异步/同步 | 返回行为             |
| ---- | ---------------- | --------- | -------------------- |
| 中断 | 来自设备的信号   | 异步      | 总是返回到下一条指令 |
| 陷阱 | 异常，系统调用   | 同步      | 总是返回到下一条指令 |
| 故障 | 潜在可恢复的错误 | 同步      | 返回到当前指令       |
| 终止 | 不可恢复的错误   | 同步      | 不会返回             |

除了系统的中断处理程序外，设备驱动在注册的时候，也会顺带注册中断处理程序。

**硬中断**
1.由与系统相连的外设(比如网卡、硬盘)自动产生的。主要是用来通知操作系统系统外设状态的变化。比如当网卡收到数据包
的时候，就会发出一个中断。我们通常所说的中断指的是硬中断(hardirq)。

1. 硬中断是外部设备对CPU的中断；
2. 硬中断是由硬件产生的，比如，像磁盘，网卡，键盘，时钟等。每个设备或设备集都有它自己的IRQ（中断请求）。基于IRQ，CPU可以将相应的请求分发到对应的硬件驱动上（注：硬件驱动通常是内核中的一个子程序，而不是一个独立的进程）。
3. 处理中断的驱动是需要运行在CPU上的，因此，当中断产生的时候，CPU会中断当前正在运行的任务，来处理中断。在有多核心的系统上，一个中断通常只能中断一颗CPU（也有一种特殊的情况，就是在大型主机上是有硬件通道的，它可以在没有主CPU的支持下，可以同时处理多个中断。）。
4. 硬中断可以直接中断CPU。它会引起内核中相关的代码被触发。对于那些需要花费一些时间去处理的进程，中断代码本身也可以被其他的硬中断中断。
5. 对于时钟中断，内核调度代码会将当前正在运行的进程挂起，从而让其他的进程来运行。它的存在是为了让调度代码（或称为调度器）可以调度多任务。

**软中断**
1.通常是硬中断服务程序对内核的中断；
2.为了满足实时系统的要求，中断处理应该是越快越好。linux为了实现这个特点，当中断发生的时候，硬中断处理那些短时间就可以完成的工作，而将那些处理事件比较长的工作，放到中断之后来完成，也就是软中断(softirq)来完成。

1. 软中断的处理非常像硬中断。然而，它们仅仅是由当前正在运行的进程所产生的。
2. 通常，软中断是一些对I/O的请求。这些请求会调用内核中可以调度I/O发生的程序。对于某些设备，I/O请求需要被立即处理，而磁盘I/O请求通常可以排队并且可以稍后处理。根据I/O模型的不同，进程或许会被挂起直到I/O完成，此时内核调度器就会选择另一个进程去运行。I/O可以在进程之间产生并且调度过程通常和磁盘I/O的方式是相同。
3. 软中断仅与内核相联系。而内核主要负责对需要运行的任何其他的进程进行调度。一些内核允许设备驱动的一些部分存在于用户空间，并且当需要的时候内核也会调度这个进程去运行。
4. 软中断并不会直接中断CPU。也只有当前正在运行的代码（或进程）才会产生软中断。这种中断是一种需要内核为正在运行的进程去做一些事情（通常为I/O）的请求。有一个特殊的软中断是Yield调用，它的作用是请求内核调度器去查看是否有一些其他的进程可以运行。

**差别**
①硬中断是由外部事件引起的因此具有随机性和突发性；
软中断是执行中断指令产生的，无面外部施加中断请求信号，因此中断的发生不是随机的而是由程序安排好的。
②硬中断的中断响应周期，CPU需要发中断回合信号（NMI不需要）；
软中断的中断响应周期，CPU不需发中断回合信号。
③硬中断的中断号是由中断控制器提供的（NMI硬中断中断号系统指定为02H）；
软中断的中断号由指令直接给出，无需使用中断控制器。直接送到寄存器，硬中断需要中断处理器发信号到cpu针脚，将中断号送到寄存器
④硬中断是可屏蔽的（NMI硬中断不可屏蔽）；
软中断不可屏蔽。硬中断中断处理器不发信号是完全可以的。

## 1.2 中断类型

### 1.2.1 interrupt

硬件连中断控制器，由中断控制器发信号，软中断也即exception不需要。

### 1.2.2 exception

- fault返回current instruction

- trap陷入next instruction

- abort直接返回


##1.3 Unix体系

### 1.3.1 Linux文件系统

![虚拟文件系统](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/%E8%99%9A%E6%8B%9F%E6%96%87%E4%BB%B6%E7%B3%BB%E7%BB%9F.png)

虚拟文件系统（VFS）是文件系统的一部分，提供了一个通用的接口抽象，对上提供了诸如 open、close、read 和 write 之类的系统调用。

Linux文件系统层之下是缓冲区缓存，它为文件系统层提供了一个通用函数集（与具体文件系统无关）。这个缓存层通过将数据保留一段时间（或者随即预先读取数据以便在需要是就可用）优化了对物理设备的访问。缓冲区缓存之下是设备驱动程序，它实现了特定物理设备的接口。

Linux文件系统支持多种文件系统格式，如EXT2、 EXT3、 FAT、 FAT32、 VFAT和ISO9660。注意这里的 Linux文件系统指的是 Linux 管理文件的一整套工具，而EXT2、 EXT3、 FAT等只是某一种类型的文件系统，偏重于文件的格式方面，不要混淆。

### 1.3.2 文件种类

Linux下面的文件类型主要有：
**1) 普通文件**：[C语言](http://lib.csdn.net/base/c)元代码、SHELL脚本、二进制的可执行文件等。分为纯文本和二进制。
**2) 目录文件**：目录，存储文件的唯一地方。
**3) 链接文件**：指向同一个文件或目录的的文件。
**4) 设备文件**：与系统外设相关的，通常在/dev下面。分为块设备和字符设备。

**5）管道(FIFO)文件 :**  提供进程建通信的一种方式
**6）套接字(socket) 文件：** 该文件类型与网络通信有关

### 1.3.3 挂载

linux系统中每个分区都是一个文件系统，都有自己的目录层次结构。linux会将这些分属不同分区的、单独的文件系统按一定的方式形成一个系统的总的目录层次结构。这里所说的“按一定方式”就是指的挂载。

将一个文件系统的顶层目录挂到另一个文件系统的子目录上，使它们成为一个整体，称为挂载。把该子目录称为挂载点.

### 1.3.4 文件系统管理命令

**磁盘和文件空间 ：**fdisk df du

**文件目录与管理：** cd pwd mkdir rmdir ls cp rm mv

**查看文件内容** cat、tac、more、less、head 、tail

**文件目录与权限 ：**chmod chown chgrp umask

**文件查找：**which、whereis、locate、find、find 

# 2 高级系统编程

## 2.1 UNIX 标准之ISO C、IEEE POSIX和Single UNIX Specification

三者的关系是 ISO C是基础，规定了 C 库，大方向； POSIX 增加系统调用标准，相当于ISO的细化和扩展，SUS就更是细化和扩展了，越粗的东西越是基础。

UNIX标准用于保证不同的UNIX系统实现能提供一致的编程环境，从而使得在一个UNIX系统上开发和打包的UNIX程序也可以在其它UNIX系统上运行。UNIX标准涉及ISO C、IEEE POSIX和Single UNIX Specification这三个关系密切的标准。

一、ISO C标准
C语言是一种在恰当的时间出现的恰当的语言，统治了操作系统编程。 ISO C标准的目的在于提高C程序在不同操作系统之间的移植性，既包括UNIX系统，也包括非UNIX系统。
１、ISO C标准化历程
（1）1989年，ANSI Standard X3.159-1989标准通过，并在之后进一步上升为International Standard ISO/IEC9899:1990标准。
其中，ANSI（the American National Standards Institute）是International Organization for Standardization (ISO)中代表美国的成员，而IEC指的是the International Electrotechnical Commission；
（2）1999年，ISO C标准更新为ISO/IEC 9899:1999，主要增强了对于数值处理应用的支持；
（3）1999年之后，分别于2001、2004和2007年发布了3个对ISO C标准的技术勘误。
目前，ISO C标准由the ISO/IEC international standardization working group for the C programming language, known as ISO/IEC JTC1/SC22/WG14, or WG14 for short负责维护。
２、ISO C标准的内容主要包括如下两块：C语言的语法和语义；C语言标准库。其中，C语言标准库可以按照其头文件划分为24个区（POSIX.1标准完全包含了这些头文件），如下图所示：

![ISO C](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/ISO%20C%E5%A4%B4%E6%96%87%E4%BB%B6.jpg)

二、IEEE POSIX标准
POSIX（Portable Operating System Interface）是一组最初由IEEE（Institute of Electrical and Electronics Engineers）开发形成的标准族，原本指是the IEEE Standard 1003.1-1988（操作系统接口），但是后来进一步扩展为包含很多带1003标识的标准和草案（如shell和utilities (1003.2)）。下面主要关注与UNIX环境编程密切相关的the 1003.1 operating system interface standard，并用POSIX.1指代the IEEE Standard 1003.1-1988标准的各种版本。
与ISO C标准的目的一样，POSIX标准的目的在于提高应用程序在不同UNIX操作系统之间的移植性，它规定了符合POSIX标准的操作系统必须提供的服务。实际上，除了UNIX和UNIX-like系统外，很多其它的操作系统也遵循该标准。
１、POSIX.1标准化历程
（１）1988年，IEEE将the IEEE Standard 1003.1-1988标准提交给ISO。这个IEEE标准被更新为IEEE
Standard 1003.1-1990 [IEEE 1990]后，成为International Standard ISO/IEC 9945-1:1990标准。此后，该标准经历了多次修改和扩展；
（２）ISO于2008年接受了IEEE提交的POSIX.1最近修改版本，并于2009年将它发布为International Standard ISO/IEC 9945:2009标准；
（３）经过20多年的发展，POSIX.1标准已经相对成熟和稳定，现在由the Austin Group（http://www.opengroup.org/austin）负责维护。
２、POSIX.1标准的内容
值得注意的是，the 1003.1 standard指定的是接口而不涉及具体实现，所有的例程都称为函数，因而没有区分系统调用和库函数。换句话说，POSIX规定那些库函数是一个符合标准规范的系统必须提供的（参数、功能、返回值），但是并没有提到系统调用——虽然大多数库函数会引发系统调用，但也有一些可以在系统内核之外实现。
（１）POSIX.1接口分为必需头文件和可选头文件。注意，POSIX.1标准完全包含了ISO C标准库函数，因而它的必需头文件包括ISO C标准库函数的所有头文件；
（２）必需头文件如下图所示：

![POSIX](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/POSIX%20%E5%A4%B4%E6%96%87%E4%BB%B6.jpg)

（３）可选头文件按照功能的不同进一步划分为40个分区，每个分区用由2~3个字母缩写构成的选项码来标识。每个分区包含多个接口，而这些接口依赖于特定选项的支持（很多选项用于处理实时扩展）。
其中，包含未被废弃接口的分区有24个，如下图所示：

![](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/%E6%8E%A5%E5%8F%A3%E5%88%86%E5%8C%BA.jpg)

特别地，其中的XSI分区所包含的头文件如下图所示：

![](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/XSI%E5%88%86%E5%8C%BA%E5%A4%B4%E6%96%87%E4%BB%B6.jpg)

三、Single UNIX Specification标准
１、Single UNIX Specification标准化历程
Single UNIX Specification标准的第一个版本由X/Open于1994年发布，此后经过多次更新，于2010年由Open Group发布第四个版本（SUSv4）。其中，Open Group是由两个工业社团——X/Open和Open Software Foundation (OSF)于1996年合并而构成的。
２、Single UNIX Specification标准与UNIX系统
（１）Open Group拥有UNIX商标，使用Single UNIX Specification标准来判断一个系统能否被称为UNIX系统。系统提供商必须以文件形式提供符合性声明，通过验证符合性的测试后，才能得到使用UNIX商标的许可证；
（２）Single UNIX Specification标准以POSIX.1作为其基本规范部分（即Single UNIX Specification标准是POSIX.1标准的超集），并额外定义了一些接口来扩展功能。
特别地，所有UNIX系统都是遵循XSI（The X/Open System Interfaces）的实现。XSI是POSIX.1标准中可选接口头文件中的一个功能分区（如上所述）。XSI除描述了一些可选的POSIX.1接口外，还定义了遵循XSI的实现所必须支持的可选POSIX.1接口。这些必须被支持的接口包括file synchronization、thread stack address and size attributes、thread process-shared synchronization和 the _XOPEN_UNIX symbolic constant。

四、典型UNIX系统
UNIX系统的各种版本和变体都起源于PDP-11上运行的UNIX分时系统的第9（1976）和第7版本（1979），这两个系统是在贝尔实验室外首先得到广泛应用的UNIX系统，演进出了如下3个分支：
（１）AT&T分支开发出System III 和 System V（被称为UNIX商用版本）；
（２）加州伯克利分校分支开发出4.xBSD；
（３）AT&T贝尔实验室的计算科学研究中心推出的UNIX研究版本，开发出UNIX分时系统第8和第9版本，并终止于1990年的第10版本。
典型的UNIX系统包括 FreeBSD、 Linux、 Mac OS X和Solaris等，虽然其中只有 Mac OS X和Solaris可以称自己为UNIX系统，但是所有这4个系统都提供了相似的编程环境，因为它们都在某种程度上符合POSIX标准。
１、FreeBSD基于4.4BSD-Lite操作系统，由FreeBSD project发布。FreeBSD project开发的所有软件（包括其二进制代码和源代码）都是可以免费使用的。
２、Linux由Linus Torvalds于1991年参考MINIX这个类UNIX系统开发而成。Linux系统相比于其它系统的一个显著特点是：它经常是支持新硬件的第一个操作系统。Linux另一个独特之处在于它的商业模式：自由软件——可以从互联网上的很多站点中下载到。尽管Linux是自由的，但是它有一个GPL许可（GNU公共许可）。
３、Mac OS X的核心操作系统（称为“Darwin”）基于Mach kernel、FreeBSD、an object-oriented framework for drivers和其它内核扩展等的集合。

４、Solaris由Sun Microsystems（现为Oracle）开发，基于System V Release 4（SVR4），是唯一个在商业上取得成功的SVR4后裔。为了建立围绕Solaris的外部开发人员社区，Sun Microsystems于2005年将Solaris操作系统的大部分源代码开放给公众。

## 2.2 基本系统数据类型

caddr_t 内存地址（ 1 2 . 9节）
clock_t 时钟滴答计数器（进程时间）
comp_t 压缩的时钟滴答
dev_t 设备号（主和次）
fdse_t 文件描述符集
fpos_t 文件位置
gid_t 数值组ID
ino_t i节点编号
mode_t 文件类型，文件创建方式
nlink_t 目录项的连接计数
off_t 文件长度和位移量（带符号的）（lseek）
pid_t 进程I D和进程组I D（带符号的）
ptrdiff_t 两个指针相减的结果（带符号的）
rlim_t 资源限制
sigatomic_t 能原子地存取的数据类型
sigset_t 信号集
size_t 对象（例如字符串）长度（不带符号的）
ssize_t 返回字节计数的函数（带符号的）（read, write）
time_t 日历时间的秒计数器
uid_t 数值用户ID
wchar_t 能表示所有不同的字符码

## 2.3 文件IO

### 2.3.1 文件描述符

对内核而言，所有打开的文件都通过文件描述符引用。当打开或者创建一个文件时，内核返回一个文件描述符。不带缓冲（nobuffered）IO中的 open、close、read、write、lseek将介绍。

<font color=blue>STDIN_FILENO、STDOUT_FILENO、STDERR_FILENO</font>是标注输入、输出、错误的宏，值为 0、1、2

### 2.3.2 无缓存IO函数定义

```c
int open(const char* path, int oflag, ... /*mode_t mode*/);
int openat(int fd, const char* path, int oflag, ... /*mode_t mode*/);
int creat(const char* path, mode_t mode);
int close(int fd);
off_t lseek(int fd, off_t offset, int whence);
ssize_t read(int fd, void* buf, size_t nbytes);
ssize_t write(int fd, const void* buf, size_t nbytes);
```

注意，负数的返回值是可能的，但是 -1 就绝对标识着出错。包括偏移量，都可以是负数，但是不能是 -1.

### 2.3.3 缓存

标准库I/O函数是带缓冲的，可以使用setbuf关闭缓冲。标准输出文件、标准输入文件、标准错误文件跟缓冲扯不上任何关系，它们只是个文件。看例子

```c
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>

int glob = 6;
char buf[] = "anonymalias\n";

int main()
{
    int var;
    pid_t pid;
    var = 8;
    if(write(STDOUT_FILENO, buf,sizeof(buf) - 1) != sizeof(buf) - 1)
    {
        fprintf(stderr, "write error");
        return 0;
    }
    printf("before fork()...\n");
    if((pid = fork()) == -1)
    {
        fprintf(stderr, "fork error");
        return 0;
     }
     if(pid == 0)
    {
        glob++;
        var++;
     }
    else
    {
        sleep(2);
    }
    printf("parent process id:%d ", getppid());
    printf("process id:%d, glob:%d, var:%d\n", getpid(), glob, var);
    return 0;
}
```

输出

```shell
anonymalias                                             //这个是write函数输出的
before fork()...                                        //这个是printf函数输出的
parent process id:7056 process id:7057, glob:7, var:9
parent process id:6747 process id:7056, glob:6, var:8
```

printf函数是标准库函数，有缓冲。当前的标准输出文件是屏幕，所以是行缓冲的。遇到换行符直接输出。如果讲结果重定向到文件，`before fork()...             `输出两遍，原因是 printf函数不会立即输出，它是全缓冲的，fork时将这个缓冲区也fork了一份，输出的时候输出两遍。

### 2.3.4 文件锁

功能描述：根据文件描述词来操作文件的特性。

\#include <unistd.h>
\#include <fcntl.h> 
int fcntl(int fd, int cmd); 
int fcntl(int fd, int cmd, long arg); 
int fcntl(int fd, int cmd, struct flock *lock);

[描述]
fcntl()针对(文件)描述符提供控制。参数fd是被参数cmd操作(如下面的描述)的描述符。针对cmd的值，fcntl能够接受第三个参数int arg。

[返回值]
fcntl()的返回值与命令有关。如果出错，所有命令都返回－1，如果成功则返回某个其他值。下列三个命令有特定返回值：F_DUPFD , F_GETFD , F_GETFL以及F_GETOWN。
​    F_DUPFD   返回新的文件描述符
​    F_GETFD   返回相应标志
​    F_GETFL , F_GETOWN   返回一个正的进程ID或负的进程组ID

 

fcntl函数有5种功能： 
1. 复制一个现有的描述符(cmd=F_DUPFD). 
2. 获得／设置文件描述符标记(cmd=F_GETFD或F_SETFD). 
3. 获得／设置文件状态标记(cmd=F_GETFL或F_SETFL). 
4. 获得／设置异步I/O所有权(cmd=F_GETOWN或F_SETOWN). 
5. 获得／设置记录锁(cmd=F_GETLK , F_SETLK或F_SETLKW).

**1. cmd值的F_DUPFD ：** 
F_DUPFD    返回一个如下描述的(文件)描述符：
​        ·最小的大于或等于arg的一个可用的描述符
​        ·与原始操作符一样的某对象的引用
​        ·如果对象是文件(file)的话，则返回一个新的描述符，这个描述符与arg共享相同的偏移量(offset)
​        ·相同的访问模式(读，写或读/写)
​        ·相同的文件状态标志(如：两个文件描述符共享相同的状态标志)
​        ·与新的文件描述符结合在一起的close-on-exec标志被设置成交叉式访问execve(2)的系统调用

实际上调用dup(oldfd)；
等效于
​        fcntl(oldfd, F_DUPFD, 0);

而调用dup2(oldfd, newfd)；
等效于
​        close(oldfd)；
​        fcntl(oldfd, F_DUPFD, newfd)；

**2. cmd值的F_GETFD和F_SETFD：**      
F_GETFD    取得与文件描述符fd联合的close-on-exec标志，类似FD_CLOEXEC。如果返回值和FD_CLOEXEC进行与运算结果是0的话，文件保持交叉式访问exec()，否则如果通过exec运行的话，文件将被关闭(arg 被忽略)        
F_SETFD    设置close-on-exec标志，该标志以参数arg的FD_CLOEXEC位决定，应当了解很多现存的涉及文件描述符标志的程序并不使用常数 FD_CLOEXEC，而是将此标志设置为0(系统默认，在exec时不关闭)或1(在exec时关闭)    

在修改文件描述符标志或文件状态标志时必须谨慎，先要取得现在的标志值，然后按照希望修改它，最后设置新标志值。不能只是执行F_SETFD或F_SETFL命令，这样会关闭以前设置的标志位。 

**3. cmd值的F_GETFL和F_SETFL：**   
F_GETFL    取得fd的文件状态标志，如同下面的描述一样(arg被忽略)，在说明open函数时，已说明
了文件状态标志。不幸的是，三个存取方式标志 (O_RDONLY , O_WRONLY , 以及O_RDWR)并不各占1位。(这三种标志的值各是0 , 1和2，由于历史原因，这三种值互斥 — 一个文件只能有这三种值之一。) 因此首先必须用屏蔽字O_ACCMODE相与取得存取方式位，然后将结果与这三种值相比较。       
F_SETFL    设置给arg描述符状态标志，可以更改的几个标志是：O_APPEND，O_NONBLOCK，O_SYNC 和 O_ASYNC。而fcntl的文件状态标志总共有7个：O_RDONLY , O_WRONLY , O_RDWR , O_APPEND , O_NONBLOCK , O_SYNC和O_ASYNC

可更改的几个标志如下面的描述：
​    O_NONBLOCK   非阻塞I/O，如果read(2)调用没有可读取的数据，或者如果write(2)操作将阻塞，则read或write调用将返回-1和EAGAIN错误
​    O_APPEND     强制每次写(write)操作都添加在文件大的末尾，相当于open(2)的O_APPEND标志
​    O_DIRECT     最小化或去掉reading和writing的缓存影响。系统将企图避免缓存你的读或写的数据。如果不能够避免缓存，那么它将最小化已经被缓存了的数据造成的影响。如果这个标志用的不够好，将大大的降低性能
​    O_ASYNC      当I/O可用的时候，允许SIGIO信号发送到进程组，例如：当有数据可以读的时候

**4. cmd值的F_GETOWN和F_SETOWN：**   
F_GETOWN   取得当前正在接收SIGIO或者SIGURG信号的进程id或进程组id，进程组id返回的是负值(arg被忽略)     
F_SETOWN   设置将接收SIGIO和SIGURG信号的进程id或进程组id，进程组id通过提供负值的arg来说明(arg绝对值的一个进程组ID)，否则arg将被认为是进程id

 **5. cmd值的F_GETLK, F_SETLK或F_SETLKW：** 获得／设置记录锁的功能，成功则返回0，若有错误则返回-1，错误原因存于errno。
F_GETLK    通过第三个参数arg(一个指向flock的结构体)取得第一个阻塞lock description指向的锁。取得的信息将覆盖传到fcntl()的flock结构的信息。如果没有发现能够阻止本次锁(flock)生成的锁，这个结构将不被改变，除非锁的类型被设置成F_UNLCK    
F_SETLK    按照指向结构体flock的指针的第三个参数arg所描述的锁的信息设置或者清除一个文件的segment锁。F_SETLK被用来实现共享(或读)锁(F_RDLCK)或独占(写)锁(F_WRLCK)，同样可以去掉这两种锁(F_UNLCK)。如果共享锁或独占锁不能被设置，fcntl()将立即返回EAGAIN     
F_SETLKW   除了共享锁或独占锁被其他的锁阻塞这种情况外，这个命令和F_SETLK是一样的。如果共享锁或独占锁被其他的锁阻塞，进程将等待直到这个请求能够完成。当fcntl()正在等待文件的某个区域的时候捕捉到一个信号，如果这个信号没有被指定SA_RESTART, fcntl将被中断

当一个共享锁被set到一个文件的某段的时候，其他的进程可以set共享锁到这个段或这个段的一部分。共享锁阻止任何其他进程set独占锁到这段保护区域的任何部分。如果文件描述符没有以读的访问方式打开的话，共享锁的设置请求会失败。

独占锁阻止任何其他的进程在这段保护区域任何位置设置共享锁或独占锁。如果文件描述符不是以写的访问方式打开的话，独占锁的请求会失败。

结构体flock的指针：
struct flcok 
{ 
short int l_type; /* 锁定的状态*/

//以下的三个参数用于分段对文件加锁，若对整个文件加锁，则：l_whence=SEEK_SET, l_start=0, l_len=0
short int l_whence; /*决定l_start位置*/ 
off_t l_start; /*锁定区域的开头位置*/ 
off_t l_len; /*锁定区域的大小*/

pid_t l_pid; /*锁定动作的进程*/ 
};

l_type 有三种状态： 
F_RDLCK   建立一个供读取用的锁定 
F_WRLCK   建立一个供写入用的锁定 
F_UNLCK   删除之前建立的锁定

l_whence 也有三种方式： 
SEEK_SET   以文件开头为锁定的起始位置 
SEEK_CUR   以目前文件读写位置为锁定的起始位置 
SEEK_END   以文件结尾为锁定的起始位置


fcntl文件锁有两种类型：建议性锁和强制性锁
建议性锁是这样规定的：每个使用上锁文件的进程都要检查是否有锁存在，当然还得尊重已有的锁。内核和系统总体上都坚持不使用建议性锁，它们依靠程序员遵守这个规定。
强制性锁是由内核执行的：当文件被上锁来进行写入操作时，在锁定该文件的进程释放该锁之前，内核会阻止任何对该文件的读或写访问，每次读或写访问都得检查锁是否存在。

系统默认fcntl都是建议性锁，强制性锁是非POSIX标准的。如果要使用强制性锁，要使整个系统可以使用强制性锁，那么得需要重新挂载文件系统，mount使用参数 -0 mand 打开强制性锁，或者关闭已加锁文件的组执行权限并且打开该文件的set-GID权限位。
建议性锁只在cooperating processes之间才有用。对cooperating process的理解是最重要的，它指的是会影响其它进程的进程或被别的进程所影响的进程，举两个例子：
(1) 我们可以同时在两个窗口中运行同一个命令，对同一个文件进行操作，那么这两个进程就是cooperating  processes
(2) cat file | sort，那么cat和sort产生的进程就是使用了pipe的cooperating processes

使用fcntl文件锁进行I/O操作必须小心：进程在开始任何I/O操作前如何去处理锁，在对文件解锁前如何完成所有的操作，是必须考虑的。如果在设置锁之前打开文件，或者读取该锁之后关闭文件，另一个进程就可能在上锁/解锁操作和打开/关闭操作之间的几分之一秒内访问该文件。当一个进程对文件加锁后，无论它是否释放所加的锁，只要文件关闭，内核都会自动释放加在文件上的建议性锁(这也是建议性锁和强制性锁的最大区别)，所以不要想设置建议性锁来达到永久不让别的进程访问文件的目的(强制性锁才可以)；强制性锁则对所有进程起作用。

fcntl使用三个参数 F_SETLK/F_SETLKW， F_UNLCK和F_GETLK 来分别要求、释放、测试record locks。record locks是对文件一部分而不是整个文件的锁，这种细致的控制使得进程更好地协作以共享文件资源。fcntl能够用于读取锁和写入锁，read lock也叫shared lock(共享锁)， 因为多个cooperating process能够在文件的同一部分建立读取锁；write lock被称为exclusive lock(排斥锁)，因为任何时刻只能有一个cooperating process在文件的某部分上建立写入锁。如果cooperating processes对文件进行操作，那么它们可以同时对文件加read lock，在一个cooperating process加write lock之前，必须释放别的cooperating process加在该文件的read lock和wrtie lock，也就是说，对于文件只能有一个write lock存在，read lock和wrtie lock不能共存。



一：第一种类似于dup操作，在这里不做举例。（fcnlt(oldfd, F_DUPFD, 0) <==>dup2(oldfd, newfd)）

二：设置close-on-exec旗标

在此函数中创建子进程，调用execl

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 #include <stdio.h>
 2 #include <stdlib.h>
 3 #include <string.h>
 4 
 5 int main()
 6 {
 7     pid_t pid;
 8     //以追加的形式打开文件
 9     int fd = fd = open("test.txt", O_TRUNC | O_RDWR | O_APPEND | O_CREAT, 0777);
10     if(fd < 0)
11     {
12         perror("open");
13         return -1;
14     }
15     printf("fd = %d\n", fd);
16     
17     fcntl(fd, F_SETFD, 0);//关闭fd的close-on-exec标志
18 
19     write(fd, "hello c program\n", strlen("hello c program!\n"));
20 
21     pid = fork();
22     if(pid < 0)
23     {
24             perror("fork");
25             return -1;
26     }
27     if(pid == 0)
28     {
29         printf("fd = %d\n", fd);
30         
31         int ret = execl("./main", "./main", (char *)&fd, NULL);
32         if(ret < 0)
33         {
34             perror("execl");
35             exit(-1);
36         }
37         exit(0);
38     }
39 
40     wait(NULL);
41 
42     write(fd, "hello c++ program!\n", strlen("hello c++ program!\n"));
43 
44     close(fd);
45 
46     return 0;
47 }
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

main测试函数

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 int main(int argc, char *argv[])
 2 {
 3     int fd = (int)(*argv[1]);//描述符
 4     
 5     printf("fd = %d\n", fd);
 6 
 7     int ret = write(fd, "hello linux\n", strlen("hello linux\n"));
 8     if(ret < 0)
 9     {
10         perror("write");
11         return -1;
12     }
13 
14     close(fd);
15 
16     return 0;
17 }
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

执行后文件结果：

[root@centOS5 class_2]# cat test.txt 
hello c program
hello linux
hello c++ program!

 

三：用命令F_GETFL和F_SETFL设置文件标志，比如阻塞与非阻塞

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 #include <stdio.h>
 2 #include <sys/types.h>
 3 #include <unistd.h>
 4 #include <sys/stat.h>
 5 #include <fcntl.h>
 6 #include <string.h>
 7 
 8 /**********************使能非阻塞I/O********************
 9 *int flags;
10 *if(flags = fcntl(fd, F_GETFL, 0) < 0)
11 *{
12 *    perror("fcntl");
13 *    return -1;
14 *}
15 *flags |= O_NONBLOCK;
16 *if(fcntl(fd, F_SETFL, flags) < 0)
17 *{
18 *    perror("fcntl");
19 *    return -1;
20 *}
21 *******************************************************/
22 
23 /**********************关闭非阻塞I/O******************
24 flags &= ~O_NONBLOCK;
25 if(fcntl(fd, F_SETFL, flags) < 0)
26 {
27     perror("fcntl");
28     return -1;
29 }
30 *******************************************************/
31 
32 int main()
33 {
34     char buf[10] = {0};
35     int ret;
36     int flags;
37     
38     //使用非阻塞io
39     if(flags = fcntl(STDIN_FILENO, F_GETFL, 0) < 0)
40     {
41         perror("fcntl");
42         return -1;
43     }
44     flags |= O_NONBLOCK;
45     if(fcntl(STDIN_FILENO, F_SETFL, flags) < 0)
46     {
47         perror("fcntl");
48         return -1;
49     }
50 
51     while(1)
52     {
53         sleep(2);
54         ret = read(STDIN_FILENO, buf, 9);
55         if(ret == 0)
56         {
57             perror("read--no");
58         }
59         else
60         {
61             printf("read = %d\n", ret);
62         }
63         
64         write(STDOUT_FILENO, buf, 10);
65         memset(buf, 0, 10);
66     }
67 
68     return 0;
69 }
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

四：设置异步IO还没想好以后实现（呵呵呵。。。。。）

五：设置获取记录锁

结构体flock的指针：

struct flcok

{

　　 short int l_type; /* 锁定的状态*/

　　　　//这三个参数用于分段对文件加锁，若对整个文件加锁，则：l_whence=SEEK_SET,l_start=0,l_len=0;

　　 short int l_whence;/*决定l_start位置*/

　　 off_t l_start; /*锁定区域的开头位置*/

　　 off_t l_len; /*锁定区域的大小*/

　　 pid_t l_pid; /*锁定动作的进程*/

};

l_type 有三种状态:

　　 F_RDLCK 建立一个供读取用的锁定

　　 F_WRLCK 建立一个供写入用的锁定

​       F_UNLCK 删除之前建立的锁定

l_whence 也有三种方式:

　　SEEK_SET 以文件开头为锁定的起始位置。

​     SEEK_CUR 以目前文件读写位置为锁定的起始位置

​     SEEK_END 以文件结尾为锁定的起始位置。

 

 

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
 1 #include "filelock.h"
 2 
 3 /* 设置一把读锁 */
 4 int readLock(int fd, short start, short whence, short len) 
 5 {
 6     struct flock lock;
 7     lock.l_type = F_RDLCK;
 8     lock.l_start = start;
 9     lock.l_whence = whence;//SEEK_CUR,SEEK_SET,SEEK_END
10     lock.l_len = len;
11     lock.l_pid = getpid();
12 //  阻塞方式加锁
13     if(fcntl(fd, F_SETLKW, &lock) == 0)
14         return 1;
15     
16     return 0;
17 }
18 
19 /* 设置一把读锁 , 不等待 */
20 int readLocknw(int fd, short start, short whence, short len) 
21 {
22     struct flock lock;
23     lock.l_type = F_RDLCK;
24     lock.l_start = start;
25     lock.l_whence = whence;//SEEK_CUR,SEEK_SET,SEEK_END
26     lock.l_len = len;
27     lock.l_pid = getpid();
28 //  非阻塞方式加锁
29     if(fcntl(fd, F_SETLK, &lock) == 0)
30         return 1;
31     
32     return 0;
33 }
34 /* 设置一把写锁 */
35 int writeLock(int fd, short start, short whence, short len) 
36 {
37     struct flock lock;
38     lock.l_type = F_WRLCK;
39     lock.l_start = start;
40     lock.l_whence = whence;
41     lock.l_len = len;
42     lock.l_pid = getpid();
43 
44     //阻塞方式加锁
45     if(fcntl(fd, F_SETLKW, &lock) == 0)
46         return 1;
47     
48     return 0;
49 }
50 
51 /* 设置一把写锁 */
52 int writeLocknw(int fd, short start, short whence, short len) 
53 {
54     struct flock lock;
55     lock.l_type = F_WRLCK;
56     lock.l_start = start;
57     lock.l_whence = whence;
58     lock.l_len = len;
59     lock.l_pid = getpid();
60 
61     //非阻塞方式加锁
62     if(fcntl(fd, F_SETLK, &lock) == 0)
63         return 1;
64     
65     return 0;
66 }
67 
68 /* 解锁 */
69 int unlock(int fd, short start, short whence, short len) 
70 {
71     struct flock lock;
72     lock.l_type = F_UNLCK;
73     lock.l_start = start;
74     lock.l_whence = whence;
75     lock.l_len = len;
76     lock.l_pid = getpid();
77 
78     if(fcntl(fd, F_SETLKW, &lock) == 0)
79         return 1;
80 
81     return 0;
82 }
```

## 2.3 进程控制

### 2.3.1 BIOS

它是一组固化到计算机上一个ROM上的程序，它保存着计算机最重要的基本输入输出的程序、开机后自检程序和系统自启动程序，其主要功能是为计算机提供最底层的、最直接的硬件设置和控制。

BIOS设置程序是储存在BIOS芯片中的，[BIOS芯片](https://baike.baidu.com/item/BIOS%E8%8A%AF%E7%89%87)是主板上一块长方形或正方形芯片，只有在开机时才可以进行设置。（一般在计算机启动时按F2或者Delete进入BIOS进行设置，一些特殊机型按F1、Esc、F12等进行设置）。BIOS设置程序主要对计算机的基本输入输出系统进行管理和设置，使系统运行在最好状态下，使用BIOS设置程序还可以排除系统故障或者诊断系统问题。

从奔腾时代开始，现代的电脑主板都使用NORFlash来作为BIOS的存储芯片。除了容量比ROM更大外，主要是NORFlash具有写入功能 ，运行电脑通过软件的方式进行BIOS的更新，而无需额外的硬件支持（通常EEPROM的擦写需要不同的电压和条件），且写入速度快。

**bios有三大功能，如下**

- 用于电脑刚接通电源时对硬件部分的检测，也叫做加电自检（Power On Self Test，简称POST），功能是检查电脑是否良好，通常完整的POST自检将包括对CPU，640K[基本内存](https://baike.baidu.com/item/%E5%9F%BA%E6%9C%AC%E5%86%85%E5%AD%98)，1M以上的[扩展内存](https://baike.baidu.com/item/%E6%89%A9%E5%B1%95%E5%86%85%E5%AD%98)，ROM，主板，CMOS[存储器](https://baike.baidu.com/item/%E5%AD%98%E5%82%A8%E5%99%A8)，[串并口](https://baike.baidu.com/item/%E4%B8%B2%E5%B9%B6%E5%8F%A3)，显示卡，软硬盘子系统及键盘进行测试，一旦在自检中发现问题，系统将给出提示信息或鸣笛警告。自检中如发现有错误，将按两种情况处理：对于严重故障（致命性故障）则停机，此时由于各种初始化操作还没完成，不能给出任何提示或信号；对于非严重故障则给出提示或声音报警信号，等待用户处理。

- 初始化，包括创建[中断向量](https://baike.baidu.com/item/%E4%B8%AD%E6%96%AD%E5%90%91%E9%87%8F)、设置[寄存器](https://baike.baidu.com/item/%E5%AF%84%E5%AD%98%E5%99%A8)、对一些[外部设备](https://baike.baidu.com/item/%E5%A4%96%E9%83%A8%E8%AE%BE%E5%A4%87)进行初始化和检测等，其中很重要的一部分是BIOS设置，主要是对硬件设置的一些参数，当电脑启动时会读取这些参数，并和实际硬件设置进行比较，如果不符合，会影响系统的启动。

- 引导程序，功能是引导[DOS](https://baike.baidu.com/item/DOS)或其他操作系统。BIOS先从[软盘](https://baike.baidu.com/item/%E8%BD%AF%E7%9B%98)或硬盘的开始[扇区](https://baike.baidu.com/item/%E6%89%87%E5%8C%BA)读取[引导记录](https://baike.baidu.com/item/%E5%BC%95%E5%AF%BC%E8%AE%B0%E5%BD%95)，如果没有找到，则会在显示器上显示没有引导设备，如果找到引导记录会把电脑的控制权转给引导程序，bios结束。

###2.3.1 系统启动引导管理器

计算机启动后运行的第一个程序，他是用来负责加载、传输控制到操作系统的内核，一旦把内核挂载，系统引导管理器的任务就算完成退出，系统引导的其它部份，比如系统的初始化及启动过程则完全由内核来控制完成；

在X86 架构处理器的机器中，Linux、BSD 或其它Unix类的操作系统中GRUB、LILO 是大家最为常用，应该说是主流；

Windows也有类似的工具NTLOADER；比如我们在机器中安装了Windows 98后，我们再安装一个Windows XP ，在机器启动的时候有一个菜单让我们选择进入是进入Windows 98 还是进入Windows XP。NTLOADER就是一个多系统启动引导管理器，NTLOADER 同样也能引导Linux，只是极为麻烦罢了；

在Powerpc 架构处理器的机器中，如果安装了Linux的Powerpc 版本，大多是用yaboot 多重引导管理器，比如Apple机目前用的是IBM Powerpc处理器，所以如果想在Apple机上，安装Macos 和Linux Powerpc 版本，大多是用yaboot来引导多个操作系统；

因为目前X86架构的机器仍是主流， 所以目前GRUB和LILO 仍然是我们最常用的多重操作系统引导管理器；

### 2.3.3 内核初始化

Linux下有3个特殊的进程，<font color=red>idle</font> 进程(PID = 0)，<font color=red>init</font> 进程(PID = 1)和 <font color=red>kthreadd</font> (PID = 2)

简单的说idle是一个进程，其pid号为 0。其前身是系统创建的第一个进程，也是唯一一个没有通过fork()产生的进程。在smp系统中，每个处理器单元有独立的一个运行队列，而每个运行队
列上又有一个idle进程，即有多少处理器单元，就有多少idle进程。系统的空闲时间，其实就是指idle进程的"运行时间"。既然是idle是进程，那我们来看看idle是如何被创建，又具体做了哪些事情？

**idle的创建** 
我们知道系统是从BIOS加电自检，载入MBR中的引导程序(LILO/GRUB),再加载linux内核开始运行的，一直到指定shell开始运行告一段落，这时用户开始操作Linux。而大致是在vmlinux的入口**startup_32(head.S)**中为pid号为0的原始进程设置了执行环境，然后这个原始进程开始执行**start_kernel()**完成Linux内核的初始化工作。包括初**始化页表，初始化中断向量表，初始化系统时间等**。继而调用 fork(),创建第一个用户进程: 

```c
kernel_thread(kernel_init, NULL, CLONE_FS | CLONE_SIGHAND); 
```

这个进程就是着名的pid为1的**init**进程，**init**继续完成剩下的初始化工作，然后**execve(/sbin/init)**, 成为系统中的其他所有进程的祖先。pid=0的原始进程，在创建了init进程后，调用 cpu_idle()演变成了idle进程。

```c
current_thread_info()->status |= TS_POLLING; 
```

在 smp系统中，除了上面刚才我们讲的主处理器(执行初始化工作的处理器)上idle进程的创建，还有从处理器(被主处理器activate的处理器)上的 idle进程，他们又是怎么创建的呢？接着看init进程，init在演变成/sbin/init之前，会执行一部分初始化工作，其中一个就是 smp_prepare_cpus()，初始化SMP处理器，在这过程中会在处理每个从处理器时调用 

```c
task = copy_process(CLONE_VM, 0, idle_regs(&regs), 0, NULL, NULL, 0); 　　
init_idle(task, cpu); 　
```

即从init中复制出一个进程，并把它初始化为idle进程(pid仍然为0)。从处理器上的idle进程会进行一些Activate工作，然后执行cpu_idle()。 整个过程简单的说就是，原始进程(pid=0)创建init进程(pid=1),然后演化成idle进程(pid=0)。init进程为每个从处理器(运行队列)创建出一个idle进程(pid=0)，然后演化成
/sbin/init。 　

idle 进程优先级为MAX_PRIO，即最低优先级。早先版本中，idle是参与调度的，所以将其优先级设为最低，当没有其他进程可以运行时，才会调度执行 idle。而目前的版本中
idle并不在运行队列中参与调度，而是在运行队列结构中含idle指针，指向idle进程，在调度器发现运行队列为空的时候运行，调入运行。

从上面的分析我们可以看出，idle在系统没有其他就绪的进程可执行的时候才会被调度。不管是主处理器，还是从处理器，最后都是执行的cpu_idle()函数。所以cpu_idle做了什么事情。 因为idle进程中并不执行什么有意义的任务，所以通常考虑的是两点：1.节能，2.低退出延迟。 

```c
void cpu_idle(void) 
{ 
    int cpu = smp_processor_id(); 
    current_thread_info()->status |= TS_POLLING; 
    while (1) 
    { 
        tick_nohz_stop_sched_tick(1);
        while (!need_resched()) 
        { 
            check_pgt_cache(); 
            rmb();
            if (rcu_pending(cpu)) 
                rcu_check_callbacks(cpu, 0); 
            if (cpu_is_offline(cpu)) 
                play_dead(); 
            local_irq_disable();
            __get_cpu_var(irq_stat).idle_timestamp = jiffies; 
            stop_critical_timings(); 
            pm_idle(); 
            start_critical_timings(); 
        } 
        tick_nohz_restart_sched_tick(); 
        preempt_enable_no_resched(); 
        schedule(); 
        preempt_disable(); 
    } 
} 　　
```

循环判断need_resched以降低退出延迟，用idle()来节能。 　　
默认的idle实现是hlt指令，hlt指令使CPU处于暂停状态，等待硬件中断发生的时候恢复，从而达到节能的目的。即从处理器C0态变到 C1态(见 ACPI标准)。这也是早些年
windows平台上各种"处理器降温"工具的主要手段。当然idle也可以是在别的ACPI或者APM模块中定义的，甚至是自定义的一个idle(比如说nop)。 　　
<font color=red>小结</font>：

- idle是一个进程，其pid为0。 　　
- 主处理器上的idle由原始进程(pid=0)演变而来。从处理器上的idle由init进程fork得到，但是它们的pid都为0。 3.
- Idle进程为最低优先级，且不参与调度，只是在运行队列为空的时候才被调度。 　　
- Idle循环等待need_resched置位。默认使用hlt节能。

### 2.3.4 僵尸进程

父进程 wait子进程，子进程执行完毕，没人管了。

### 2.3.5  孤儿进程

父进程没有，会被 init 进程收养

### 2.3.6 进程状态

S      睡眠。通常是在等待某个事件的发生，如一个信号或有输入可用
R      运行。 严格来说，应是“可运行”，即在运行队列中，处于正在执行或即将运行状态
D      不可中断的睡眠（等待）。通常是在等待输入或输出完成
T    （terminate）停止。通常是被shell作业控制所停止，或者进程正处于调试器的控制之下
Z    （zombie)僵尸进程，通常是该进程已经死亡，但父进程没有调用wait类函数来释放该进程的资源
N   （nice)低优先级任务
s       进程是会话期首进程

\+     进程属于前台进程组
l      进程是多线程的
<     高先级任务

## 2.4 进程关系

###2.4.1 终端 虚拟终端

终端，是init进程创建的子进程，共六个，终端是确定的。

虚拟终端，通过网络连接，由于事先不知道是否有虚拟终端，会开一个线程等待，并保持网络通信。

## 2.5 进程组

返回调用进程的进程组 ID

```c
pid_t getpgrp()
pid_t getpgrp(pid_t pid)    
```

每个进程组有一个组长 ID，该组长ID 与 进程组 ID 相等。进程组生命周期不受组长进程影响。

## 2.6 会话

一个或者多个进程组集合。setsid可以创建一个会话，如果是进程组ID，报错。不是的话，主调进程成为会话首进程。

## 2.5 线程

进程，可以看成线程，只有一个控制线程。

进程ID唯一，线程ID只在其进程上下文中有意义。进程ID类型为**pid_t**，线程ID类型为**pthread_t**类型，不同的操作系统，**pthread_t**的实现不同。所以使用函数来比较线程的ID。

### 2.5.1 锁

<font color=red>定义：</font>锁是计算机协调多个进程或线程并发访问某一资源的机制。

从定义可以看出，锁是一种机制。锁的作用是协调程序对资源的访问，注意资源包括硬件（如CPU、RAM、I/O等）和数据。同步技术可以用来实现锁，但是它们不是锁，锁是一种机制。

### 2.5.1 同步技术

四种常用同步技术，如下：

- 临界区: 临界区是一个代码段，同一时刻，只有一个线程能够执行该代码段。如果有多个线程试图同时访问临界区，那么在有一个线程进入后，其他所有试图访问此临界区的线程将被挂起，并一直持续到进入临界区的线程离开。临界区在被释放后，其他线程可以继续抢占

- 互斥量: 一个具有两种状态的对象，在访问共享共享资源前

- 信号量: 信号量允许多个线程同时使用共享资源 ，这与操作系统中的PV操作相同。它指出了同时访问共享资源的线程最大数目。它允许多个线程在同一时刻访问同一资源，但是需要限制在同一时刻访问此资源的最大线程数目。在创建信号量时即要同时指出允许的最大资源计数和当前可用资源计数。一般是将当前可用资源计数设置为最大资源计数，每增加一个线程对共享资源的访问，当前可用资源计数 就会减1，只要当前可用资源计数是大于0的，就<font color=red>可以发出信号量信号</font>。但是当前可用计数减小到0时则说明当前占用资源的线程数已经达到了所允许的最大数目， 不能在允许其他线程的进入，此时的<font color=red>信号量信号将无法发出</font>。线程在处理完共享资源后，应在离开的同时当前可用资源计数加1。在任何时候当前可用资源计数决不可能大于最大资源计数。

  <font color=red>PV操作及信号量</font>的概念都是由荷兰科学家E.W.Dijkstra提出的。信号量S是一个整数，S大于等于零时代表可供并发进程使用的资源实体数，但S小于零时则表示正在等待使用共享资源的进程数。
  <font color=red>P</font>操作申请资源：

  (1) S减1；

  (2) 若S减1后仍大于等于零，则进程继续执行；

  (3) 若S减1后小于零，则该进程被阻塞后进入与该信号相对应的队列中，然后转入进程调度。
  <font color=red>V</font>操作释放资源：

  (1) S加1；

  (2) 若相加结果大于零，则进程继续执行；

  (3) 若相加结果小于等于零，则从该信号的等待队列中唤醒一个等待进程，然后再返回原进程继续执行或转入进程调度。

  信号量包含的几个操作原语：

  ```c
  CreateSemaphore()   // 创建一个信号量
  OpenSemaphore()     // 打开一个信号量
  ReleaseSemaphore()  // 释放信号量
  WaitForSingleObject()   // 等待信号量
  ```

- 事 件: 用来通知线程有一些事件已发生，从而启动后继任务的开始。



# 3 网络

## 3.1  差错控制编码

- 检错重发，发送码元序列增加差错控制码，错误重发
- 前向纠错，发送码元序列增加差错控制码，纠正错误，比检错重发难。
- 反馈校验，发过去的再送回来
- 检错删除，接收端发现错误，删除。有大量多余码元时可用，比如遥感测绘信息

差错控制码，又叫纠错编码，不同的纠错编码，纠错能力不同。

## 3.2 TCP/IP卷一

### 3.2.1  WAN广域网

wide area network 

### 3.2.2 LAN局域网

Local area network

### 3.2.3 TCP/IP协议族

![](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/TCP_IP%E5%8D%8F%E8%AE%AE%E6%97%8F.png)





