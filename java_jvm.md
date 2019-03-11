# 1. jvm参数列表

1. -XXaltjvm配置client，server

2. -XX:+/-UseTLAB支持Thread local

3. | -Xms                        | 初始堆大小                                                 | 物理内存的1/64(<1GB) | 默认(MinHeapFreeRatio参数可以调整)空余堆内存小于40%时，JVM就会增大堆直到-Xmx的最大限制. |
   | --------------------------- | ---------------------------------------------------------- | -------------------- | ------------------------------------------------------------ |
   | -Xmx                        | 最大堆大小                                                 | 物理内存的1/4(<1GB)  | 默认(MaxHeapFreeRatio参数可以调整)空余堆内存大于70%时，JVM会减少堆直到 -Xms的最小限制 |
   | -Xmn                        | 年轻代大小(1.4or lator)                                    |                      | **注意**：此处的大小是（eden+ 2 survivor space).与jmap -heap中显示的New gen是不同的。 整个堆大小=年轻代大小 + 年老代大小 + 持久代大小. 增大年轻代后,将会减小年老代大小.此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8 |
   | -XX:NewSize                 | 设置年轻代大小(for 1.3/1.4)                                |                      |                                                              |
   | -XX:MaxNewSize              | 年轻代最大值(for 1.3/1.4)                                  |                      |                                                              |
   | -XX:PermSize                | 设置持久代(perm gen)初始值                                 | 物理内存的1/64       |                                                              |
   | -XX:MaxPermSize             | 设置持久代最大值                                           | 物理内存的1/4        |                                                              |
   | -Xss                        | 每个线程的堆栈大小                                         |                      | JDK5.0以后每个线程堆栈大小为1M,以前每个线程堆栈大小为256K.更具应用的线程所需内存大小进行 调整.在相同物理内存下,减小这个值能生成更多的线程.但是操作系统对一个进程内的线程数还是有限制的,不能无限生成,经验值在3000~5000左右 一般小的应用， 如果栈不是很深， 应该是128k够用的 大的应用建议使用256k。这个选项对性能影响比较大，需要严格的测试。（校长） 和threadstacksize选项解释很类似,官方文档似乎没有解释,在论坛中有这样一句话:"” -Xss is translated in a VM flag named ThreadStackSize” 一般设置这个值就可以了。 |
   | -*XX:ThreadStackSize*       | Thread Stack Size                                          |                      | (0 means use default stack size) [Sparc: 512; Solaris x86: 320 (was 256 prior in 5.0 and earlier); Sparc 64 bit: 1024; Linux amd64: 1024 (was 0 in 5.0 and earlier); all others 0.] |
   | -XX:NewRatio                | 年轻代(包括Eden和两个Survivor区)与年老代的比值(除去持久代) |                      | -XX:NewRatio=4表示年轻代与年老代所占比值为1:4,年轻代占整个堆栈的1/5 Xms=Xmx并且设置了Xmn的情况下，该参数不需要进行设置。 |
   | -XX:SurvivorRatio           | Eden区与Survivor区的大小比值                               |                      | 设置为8,则两个Survivor区与一个Eden区的比值为2:8,一个Survivor区占整个年轻代的1/10 |
   | -XX:LargePageSizeInBytes    | 内存页的大小不可设置过大， 会影响Perm的大小                |                      | =128m                                                        |
   | -XX:+UseFastAccessorMethods | 原始类型的快速优化                                         |                      |                                                              |
   | -XX:+DisableExplicitGC      | 关闭System.gc()                                            |                      | 这个参数需要严格的测试                                       |
   | -XX:MaxTenuringThreshold    | 垃圾最大年龄                                               |                      | 如果设置为0的话,则年轻代对象不经过Survivor区,直接进入年老代. 对于年老代比较多的应用,可以提高效率.如果将此值设置为一个较大值,则年轻代对象会在Survivor区进行多次复制,这样可以增加对象再年轻代的存活 时间,增加在年轻代即被回收的概率 该参数只有在串行GC时才有效. |
   | -XX:+AggressiveOpts         | 加快编译                                                   |                      |                                                              |
   | -XX:+UseBiasedLocking       | 锁机制的性能改善                                           |                      |                                                              |
   | -Xnoclassgc                 | 禁用垃圾回收                                               |                      |                                                              |
   | -XX:SoftRefLRUPolicyMSPerMB | 每兆堆空闲空间中SoftReference的存活时间                    | 1s                   | softly reachable objects will remain alive for some amount of time after the last time they were referenced. The default value is one second of lifetime per free megabyte in the heap |
   | -XX:PretenureSizeThreshold  | 对象超过多大是直接在旧生代分配                             | 0                    | 单位字节 新生代采用Parallel Scavenge GC时无效 另一种直接在旧生代分配的情况是大的数组对象,且数组中无外部引用对象. |
   | -XX:TLABWasteTargetPercent  | TLAB占eden区的百分比                                       | 1%                   |                                                              |
   | -XX:+*CollectGen0First*     | FullGC时是否先YGC                                          | false                |                                                              |

   **并行收集器相关参数**

   | -XX:+UseParallelGC          | Full GC采用parallel MSC (此项待验证)              |      | 选择垃圾收集器为并行收集器.此配置仅对年轻代有效.即上述配置下,年轻代使用并发收集,而年老代仍旧使用串行收集.(此项待验证) |
   | --------------------------- | ------------------------------------------------- | ---- | ------------------------------------------------------------ |
   | -XX:+UseParNewGC            | 设置年轻代为并行收集                              |      | 可与CMS收集同时使用 JDK5.0以上,JVM会根据系统配置自行设置,所以无需再设置此值 |
   | -XX:ParallelGCThreads       | 并行收集器的线程数                                |      | 此值最好配置与处理器数目相等 同样适用于CMS                   |
   | -XX:+UseParallelOldGC       | 年老代垃圾收集方式为并行收集(Parallel Compacting) |      | 这个是JAVA 6出现的参数选项                                   |
   | -XX:MaxGCPauseMillis        | 每次年轻代垃圾回收的最长时间(最大暂停时间)        |      | 如果无法满足此时间,JVM会自动调整年轻代大小,以满足此值.       |
   | -XX:+UseAdaptiveSizePolicy  | 自动选择年轻代区大小和相应的Survivor区比例        |      | 设置此选项后,并行收集器会自动选择年轻代区大小和相应的Survivor区比例,以达到目标系统规定的最低相应时间或者收集频率等,此值建议使用并行收集器时,一直打开. |
   | -XX:GCTimeRatio             | 设置垃圾回收时间占程序运行时间的百分比            |      | 公式为1/(1+n)                                                |
   | -XX:+*ScavengeBeforeFullGC* | Full GC前调用YGC                                  | true | Do young generation GC prior to a full GC. (Introduced in 1.4.1.) |

   **CMS相关参数**

   | -XX:+UseConcMarkSweepGC                | 使用CMS内存收集                           |      | 测试中配置这个以后,-XX:NewRatio=4的配置失效了,原因不明.所以,此时年轻代大小最好用-Xmn设置.??? |
   | -------------------------------------- | ----------------------------------------- | ---- | ------------------------------------------------------------ |
   | -XX:+AggressiveHeap                    |                                           |      | 试图是使用大量的物理内存 长时间大内存使用的优化，能检查计算资源（内存， 处理器数量） 至少需要256MB内存 大量的CPU／内存， （在1.4.1在4CPU的机器上已经显示有提升） |
   | -XX:CMSFullGCsBeforeCompaction         | 多少次后进行内存压缩                      |      | 由于并发收集器不对内存空间进行压缩,整理,所以运行一段时间以后会产生"碎片",使得运行效率降低.此值设置运行多少次GC以后对内存空间进行压缩,整理. |
   | -XX:+CMSParallelRemarkEnabled          | 降低标记停顿                              |      |                                                              |
   | -XX+UseCMSCompactAtFullCollection      | 在FULL GC的时候， 对年老代的压缩          |      | CMS是不会移动内存的， 因此， 这个非常容易产生碎片， 导致内存不够用， 因此， 内存的压缩这个时候就会被启用。 增加这个参数是个好习惯。 可能会影响性能,但是可以消除碎片 |
   | -XX:+UseCMSInitiatingOccupancyOnly     | 使用手动定义初始化定义开始CMS收集         |      | 禁止hostspot自行触发CMS GC                                   |
   | -XX:CMSInitiatingOccupancyFraction=70  | 使用cms作为垃圾回收 使用70％后开始CMS收集 | 92   | 为了保证不出现promotion failed(见下面介绍)错误,该值的设置需要满足以下公式**CMSInitiatingOccupancyFraction计算公式** |
   | -XX:CMSInitiatingPermOccupancyFraction | 设置Perm Gen使用到达多少比率时触发        | 92   |                                                              |
   | -XX:+CMSIncrementalMode                | 设置为增量模式                            |      | 用于单CPU情况                                                |
   | -XX:+CMSClassUnloadingEnabled          |                                           |      |                                                              |

   **辅助信息**

   | -XX:+PrintGC                          |                                                          |      | 输出形式:[GC 118250K->113543K(130112K), 0.0094143 secs] [Full GC 121376K->10414K(130112K), 0.0650971 secs] |
   | ------------------------------------- | -------------------------------------------------------- | ---- | ------------------------------------------------------------ |
   | -XX:+PrintGCDetails                   |                                                          |      | 输出形式:[GC [DefNew: 8614K->781K(9088K), 0.0123035 secs] 118250K->113543K(130112K), 0.0124633 secs] [GC [DefNew: 8614K->8614K(9088K), 0.0000665 secs][Tenured: 112761K->10414K(121024K), 0.0433488 secs] 121376K->10414K(130112K), 0.0436268 secs] |
   | -XX:+PrintGCTimeStamps                |                                                          |      |                                                              |
   | -XX:+PrintGC:PrintGCTimeStamps        |                                                          |      | 可与-XX:+PrintGC -XX:+PrintGCDetails混合使用 输出形式:11.851: [GC 98328K->93620K(130112K), 0.0082960 secs] |
   | -XX:+PrintGCApplicationStoppedTime    | 打印垃圾回收期间程序暂停的时间.可与上面混合使用          |      | 输出形式:Total time for which application threads were stopped: 0.0468229 seconds |
   | -XX:+PrintGCApplicationConcurrentTime | 打印每次垃圾回收前,程序未中断的执行时间.可与上面混合使用 |      | 输出形式:Application time: 0.5291524 seconds                 |
   | -XX:+PrintHeapAtGC                    | 打印GC前后的详细堆栈信息                                 |      |                                                              |
   | -Xloggc:filename                      | 把相关日志信息记录到文件以便分析. 与上面几个配合使用     |      |                                                              |
   | -XX:+PrintClassHistogram              | garbage collects before printing the histogram.          |      |                                                              |
   | -XX:+PrintTLAB                        | 查看TLAB空间的使用情况                                   |      |                                                              |
   | XX:+PrintTenuringDistribution         | 查看每次minor GC后新的存活周期的阈值                     |      | Desired survivor size 1048576 bytes, new threshold 7 (max 15) new threshold 7即标识新的存活周期的阈值为7。 |

## 1.1 jvm的client和server模式

HotSpot Client VM(-client)，为在客户端环境中减少启动时间而优化；

HotSpot Server VM(-server)，为在服务器环境中最大化程序执行速度而设计。

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
​    9142             1             0          5.01                 1                                  org/apache/felix/resolver/ResolverImpl 

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
​    4608     16    1 org/eclipse/emf/common/util/SegmentSequence$SegmentSequencePool$SegmentsAccessUnit reset

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

# 4 高效并发

##4.1 单例模式

很多人用过单例模式，笔者面试时，偶尔谈起设计模式，大部分人都会说自己会设计模式，基本上也就是说个单例模式、工厂模式。可能笔者面试的人都层次都比较低吧(笔者所在公司是小公司，负责面的也都是两年工作经验以下的)。
很多人谈起单例模式，但并不能真正用好这个模式，也不能写出一个好点的例子，今天笔者斗胆介绍一下单例模式。

### 4.1.1 饿汉式

类加载时，处理static变量时生成时实例化单例，没有延迟加载。线程安全

```java
public class Singleton {
    private static Singleton singleton = new Singleton();

    private Singleton(){}
    
    public static Singleton getInstance(){
        return singleton;
    }
}
```

类加载时，处理静态代码块时实例化单例

```java
public class Singleton {
    private static Singleton singleton;

    private Singleton(){}
    static {
        singleton = new Singleton();
    }
    public static Singleton getInstance(){
        return singleton;
    }
}
```

### 4.1.2 懒汉式

```java
public class Singleton {
    private static Singleton singleton;

    private Singleton(){}
    
    public static Singleton getInstance(){
        if (singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }
}
```

多个线程同时判空，分别生成一个实例，不是单例，加同步一定是线程安全的

```java
public class Singleton {
    private static Singleton singleton;

    private Singleton(){}
    
    public static synchronized Singleton getInstance(){
        if (singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }
}
```

锁住该方法，所有线程在尝试获取单例对象时排队阻塞，影响性能。解决这个问题可以使用双重校验锁 DCL(Double Check Lock) 机制，

###4.1.3 DCL单例

下面虽然是一个双校验，但是仍然不是线程安全的

```java
public class Singleton {
    private static Singleton singleton;

    private Singleton(){}
    
    public static Singleton getInstance(){
        if (singleton == null){
            synchronized(Singleton.class){
                if(singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
```

- 第一次校验：为空，才去竞争锁，不为空不用竞争锁，避免每次都要去竞争锁。

- 第二次校验：多线程同时判断为空，若不判空，排队后分别生成实例。对于这个判空，也是存在问题的，问题的原因是指令重排序，一般我们认为Java获取一个对象引用的过程是：

  - 分配内存空间
  - 初始化对象
  - 将内存空间的地址赋值给对应的引用

  由于指令重排序的存在，可能的过程是：

  - 分配内存空间
  - 将内存空间的地址赋值给对应的引用
  - 初始化对象

  如下图所示：

![](https://github.com/wutongtongshu/doc/raw/master/%E7%8E%B0%E4%BB%A3%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/java%E8%8E%B7%E5%8F%96%E5%BC%95%E7%94%A8%E5%AF%B9%E8%B1%A1%E8%BF%87%E7%A8%8B%E5%9B%BE.png)

正真线程安全，还要加上`volatile`关键字，在第二次判空时，任何线程创建了实例，其它线程都会知道。

```java
public class Singleton {
    //通过volatile关键字来确保安全
    private volatile static Singleton singleton;

    private Singleton(){}

    public static Singleton getInstance(){
        if(singleton == null){
            synchronized (Singleton.class){
                if(singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
```

### 4.1.4 基于ClassLoader的实现

```java
public class Singleton {
    private static class SingletonHolder{
        public static Singleton singleton = new Singleton();
    }
    
    public static Singleton getInstance(){
        return SingletonHolder.singleton;
    }
}
```

SingletonHolder 加载时，虚拟机保证只生成一个实例，这跟饿汉模式是一样的，都是由虚拟机来保证。比饿汉的有点是有延迟加载

# 名词解释

###### JIT：

英文写作Just-In-Time Compiler，中文意思是即时编译器 

