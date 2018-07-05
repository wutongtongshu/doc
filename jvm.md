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