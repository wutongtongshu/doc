# 1. jvm参数列表

1. -XXaltjvm配置client，server
2. -XX:+/-UseTLAB支持Thread local

## 1.1 jvm的client和server模式

[Java](http://lib.csdn.net/base/java)HotSpot Client VM(-client)，为在客户端环境中减少启动时间而优化；

[java](http://lib.csdn.net/base/java)HotSpot Server VM(-server)，为在服务器环境中最大化程序执行速度而设计。

1. 32位，java -XXaltjvm=Client -version 或/lib/i386/jvm.cfg下修改配置
2. 64位不允许设置，必须运行在server模式或者lib/amd64/jvm.cfg下改配置

## 1.2 支持ThreadLocal

-XX:+/-UseTLAB (thread local allocator buffer)

# 2. jvm内存结构



 ## 2.1. 内存划分

一下说的是概念模型，并不是真正的实现，内存分如下几块

### 2.1.1. 方法区

- 已加载类信息，常量，静态变量，会抛出outofmemory
- 运行时常量池：是方法区一部分，运行时常量池在运行时也是动态的，运行时可以加入常量。

### 2.1.2. 堆

### 2.1.3. 虚拟机栈

线程私有。栈深度大于虚拟机允许栈深度，会发生 stackoverflow 。对于支持动态扩展的 jvm ，当没有空间扩展时，发生outofmemoryError。

### 2.1.4. 本地方法栈

一般直接跟系统栈合二为一

### 2.1.5. 程序计数器

程序解释器通过程序计数器获取将要执行的字节码指令，为满足各线程独立性，程序计数器为线程私有。native方法不由虚拟机解释执行，故程序计数器为 undefined 。是虚拟机中唯一不会outofmemory (内存溢出)的区域。它跟 PC 寄存器的作用是一样的。

### 2.1.6. 本地方法区

不属于 jvm 内存结构，但是也会发生outofmemory 

## 2.2 jvm 对象

### 2.2.1 普通java对象加载

除数组和Class对象外，普通java 对象创建。遇到new，到常量池找到符号引用，然后判断是否已加载，然后执行加载，并分配内存，有两种，一种是"指针碰撞"，另一张是“空闲列表”。然后设置对象的markword信息，然后才能初始化。

### 2.2.2 对象的访问定位

- 句柄
- 直接记录地址，hotspot使用这个

这两种都是可以的实现方式

# 3 jdk工具

## 3.1 jps

- jps -q，查java进程pid
- jps -l, pid和全类名
- jps -v，详细信息
- jps -m，主类，详细信息

## 3.2 jstat

格式：jstat -option vmid 时间间隔 次数

**类加载统计：**
C:\Users\Administrator>jstat -class 2060

Loaded  Bytes  Unloaded  Bytes     Time
 15756 17355.6        0     0.0      11.29

Loaded:加载class的数量
Bytes：所占用空间大小
Unloaded：未加载数量
Bytes:未加载占用空间
Time：时间

**编译统计:**
C:\Users\Administrator>jstat -compiler 2060

Compiled     Failed    Invalid    Time         FailedType                                            FailedMethod
    9142             1             0          5.01                 1                                  org/apache/felix/resolver/ResolverImpl 

Compiled：编译数量。
Failed：失败数量
Invalid：不可用数量
Time：时间
FailedType：失败类型
FailedMethod：失败的方法

**垃圾回收统计:**
C:\Users\Administrator>jstat -gc 2060
 S0C    S1C    S0U    S1U      EC       EU        OC         OU          MC     MU    CCSC      CCSU   YGC     YGCT    FGC    FGCT     GCT
20480.0 20480.0  0.0   13115.3 163840.0 113334.2  614400.0   436045.7  63872.0 61266.5  0.0    0.0      149    3.440   8      0.295    3.735

S0C：第一个幸存区的大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
OC：老年代大小
OU：老年代使用大小
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

**堆内存统计:**
C:\Users\Administrator>jstat -gccapacity 2060
 NGCMN    NGCMX     NGC     S0C     S1C       EC      OGCMN      OGCMX       OGC         OC          MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
204800.0 204800.0 204800.0 20480.0 20480.0 163840.0   614400.0   614400.0   614400.0   614400.0      0.0    63872.0  63872.0      0.0      0.0      0.0    149     8

NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0C：第一个幸存区大小
S1C：第二个幸存区的大小
EC：伊甸园区的大小
OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC:当前老年代大小
MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代gc次数
FGC：老年代GC次数

**新生代垃圾回收统计:**
C:\Users\Administrator>jstat -gcnew 7172
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
40960.0 40960.0 25443.1    0.0 15  15 20480.0 327680.0 222697.8     12    0.736

S0C：第一个幸存区大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
TT:对象在新生代存活的次数
MTT:对象在新生代存活的最大次数
DSS:期望的幸存区大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间

**新生代内存统计:**
C:\Users\Administrator>jstat -gcnewcapacity 7172
  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC
  409600.0   409600.0   409600.0  40960.0  40960.0  40960.0  40960.0   327680.0   327680.0    12     0

NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0CMX：最大幸存1区大小
S0C：当前幸存1区大小
S1CMX：最大幸存2区大小
S1C：当前幸存2区大小
ECMX：最大伊甸园区大小
EC：当前伊甸园区大小
YGC：年轻代垃圾回收次数
FGC：老年代回收次数

**老年代垃圾回收统计:**
C:\Users\Administrator>jstat -gcold 7172
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT
 33152.0  31720.8      0.0      0.0    638976.0    184173.0     12     0    0.000    0.736

MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
OC：老年代大小
OU：老年代使用大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

**老年代内存统计:**
C:\Users\Administrator>jstat -gcoldcapacity 7172
   OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT
   638976.0    638976.0    638976.0    638976.0    12     0    0.000    0.736

OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC：老年代大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

**元数据空间统计:**
C:\Users\Administrator>jstat -gcmetacapacity 7172
   MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT
   0.0    33152.0    33152.0        0.0        0.0        0.0    12     0    0.000    0.736

MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

**总结垃圾回收统计:**
C:\Users\Administrator>jstat -gcutil 7172
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
 62.12   0.00  81.36  28.82  95.68      -     12    0.736     0    0.000    0.736

S0：幸存1区当前使用比例
S1：幸存2区当前使用比例
E：伊甸园区使用比例
O：老年代使用比例
M：元数据区使用比例
CCS：压缩使用比例
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

**JVM编译方法统计:**
C:\Users\Administrator>jstat -printcompilation 7172
Compiled  Size  Type Method
    4608     16    1 org/eclipse/emf/common/util/SegmentSequence$SegmentSequencePool$SegmentsAccessUnit reset

Compiled：最近编译方法的数量
Size：最近编译方法的字节码数量
Type：最近编译方法的编译类型。
Method：方法名标识。

## 3.3 jmap

获取堆转存快照

- jmap -dump:fromat=b,file=xxxx.xxx 14060

  然后使用jhat进行分析

- jmap -heap 14060

  分析堆信息

## 3.4 jstack

分析进程栈

## 3.5 hsdis

hotspot dis：运行时反会汇编

# 名词解释

###### JIT：

英文写作Just-In-Time Compiler，中文意思是即时编译器 

