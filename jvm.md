#6 类文件结构

以8字节为基础单位，中间无任何分隔符，复杂结构用类似于 C 结构体，只有两种数据类型，无符号数和表，u1, u2, u4, u4表示1、2、4、8字节的无符号数，表以无符号数或者字表构成，下面是总表。

| 类型            | 名称                | 数量                  | 说明                                                         |
| --------------- | ------------------- | --------------------- | ------------------------------------------------------------ |
| u4              | magic               | 1                     | 魔数：确定一个文件是否是Class文件                            |
| u2              | minor_version       | 1                     | Class文件的次版本号                                          |
| u2              | major_version       | 1                     | Class文件的主版本号：一个JVM实例只能支持特定范围内版本号的Class文件（可以向下兼容）。 |
| u2              | constant_pool_count | 1                     | 常量表数量，唯一从1开始，便于定位                            |
| cp_info         | constant_pool       | constant_pool_count-1 | 常量池:以理解为Class文件的资源仓库，后面的其他数据项可以引用常量池内容。 |
| u2              | access_flags        | 1                     | 类的访问标志信息：用于表示这个类或者接口的访问权限及基础属性。 |
| u2              | this_class          | 1                     | 指向当前类的常量索引：用来确定这个类的的全限定名。           |
| u2              | super_class         | 1                     | 指向父类的常量的索引：用来确定这个类的父类的全限定名。       |
| u2              | interfaces_count    | 1                     | 接口的数量                                                   |
| u2              | interfaces          | interfaces_count      | 指向接口的常量索引：用来描述这个类实现了哪些接口。           |
| u2              | fields_count        | 1                     | 字段表数量                                                   |
| field_info      | fields              | fields_count          | 字段表集合：描述当前类或接口声明的所有字段。                 |
| u2              | methods_count       | 1                     | 方法表数量                                                   |
| method_info     | methods             | methods_count         | 方法表集合：只描述当前类或接口中声明的方法，不包括从父类或父接口继承的方法。 |
| u2              | attributes_count    | 1                     | 属性表数量                                                   |
| attributes_info | attributes          | attributes_count      | 属性表集合：用于描述某些场景专有的信息，如字节码的指令信息等等。 |

## 6.1 常量池

常量池存放：字面量 和 符号引用

int、long、flout、double、String等字面量和类，接口，方法，字段引用和 utf-8 编码字符串。为了记录这些常量，jvm定义了一组基本表，并为每个基本表编号 。

| 类型                             | 标志(tag) | 描述                   |
| -------------------------------- | --------- | ---------------------- |
| CONSTANT_utf8_info               | 1         | UTF-8编码的字符串      |
| CONSTANT_Integer_info            | 3         | 整形字面量             |
| CONSTANT_Float_info              | 4         | 浮点型字面量           |
| CONSTANT_Long_info               | ５        | 长整型字面量           |
| CONSTANT_Double_info             | ６        | 双精度浮点型字面量     |
| CONSTANT_Class_info              | ７        | 类或接口的符号引用     |
| CONSTANT_String_info             | ８        | 字符串类型字面量       |
| CONSTANT_Fieldref_info           | ９        | 字段的符号引用         |
| CONSTANT_Methodref_info          | 10        | 类中方法的符号引用     |
| CONSTANT_InterfaceMethodref_info | 11        | 接口中方法的符号引用   |
| CONSTANT_NameAndType_info        | 12        | 字段或方法的符号引用   |
| CONSTANT_MothodType_info         | 13        | 标志方法类型           |
| CONSTANT_MethodHandle_info       | 14        | 表示方法句柄           |
| CONSTANT_InvokeDynamic_info      | 15        | 表示一个动态方法调用点 |

**表结构**：

| 常量                             | 项目   | 类型 | 描述                                                      |
| -------------------------------- | ------ | ---- | --------------------------------------------------------- |
|                                  | tag    | u1   | 值为1                                                     |
| CONSTANT_Utf8_info               | length | u2   | UF-8编码的字符串占用的字节数                              |
|                                  | bytes  | u1   | 长度为length的UTF-8编码的字符串                           |
|                                  | tag    | u1   | 值为3                                                     |
| CONSTANT_Integer_info            | bytes  | u4   | 按照高位在前存储的int值                                   |
|                                  | tag    | u1   | 值为4                                                     |
| CONSTANT_Float_info              | bytes  | u4   | 按照高位在前存储的float值                                 |
|                                  | tag    | u1   | 值为5                                                     |
| CONSTANT_Long_info               | bytes  | u8   | 按照高位在前存储的long值                                  |
|                                  | tag    | u1   | 值为6                                                     |
| CONSTANT_Double_info             | bytes  | u8   | 按照高位在前存储的double值                                |
|                                  | tag    | u1   | 值为7                                                     |
| CONSTANT_Class_info              | index  | u2   | 指向全限定名常量项的索引                                  |
|                                  | tag    | u1   | 值为8                                                     |
| CONSTANT_String_info             | index  | u2   | 指向字符串字面量的索引                                    |
|                                  | tag    | u1   | 值为9                                                     |
| CONSTANT_Fieldref_info           | index  | u2   | 指向声明字段的类或接口描述符CONSTANT_Class_info的索引项   |
|                                  | index  | u2   | 指向字段名称及类型描述符CONSTANT_NameAndType_info的索引项 |
|                                  | tag    | u1   | 值为10                                                    |
| CONSTANT_Methodref_info          | index  | u2   | 指向声明方法的类描述符CONSTANT_Class_info的索引项         |
|                                  | index  | u2   | 指向方法名称及类型描述符CONSTANT_NameAndType_info的索引项 |
|                                  | tag    | u1   | 值为11                                                    |
| CONSTANT_InrerfaceMethodref_info | index  | u2   | 指向声明方法的接口描述符CONSTANT_Class_info的索引项       |
|                                  | index  | u2   | 指向方法名称及类型描述符CONSTANT_NameAndType_info的索引项 |
|                                  | tag    | u1   | 值为12                                                    |
| CONSTANT_NameAndType_info        | index  | u2   | 指向字段或方法名称常量项目的索引                          |
|                                  | index  | u2   | 指向该字段或方法描述符常量项的索引                        |

### 6.1.1 类访问标志

紧跟常量池的两个字节，表示类的属性

| 标志名         | 标志值 | 标志含义                  | 针对的对像 |
| -------------- | ------ | ------------------------- | ---------- |
| ACC_PUBLIC     | 0x0001 | public类型                | 所有类型   |
| ACC_FINAL      | 0x0010 | final类型                 | 类         |
| ACC_SUPER      | 0x0020 | 使用新的invokespecial语义 | 类和接口   |
| ACC_INTERFACE  | 0x0200 | 接口类型                  | 接口       |
| ACC_ABSTRACT   | 0x0400 | 抽象类型                  | 类和接口   |
| ACC_SYNTHETIC  | 0x1000 | 该类不由用户代码生成      | 所有类型   |
| ACC_ANNOTATION | 0x2000 | 注解类型                  | 注解       |
| ACC_ENUM       | 0x4000 | 枚举类型                  | 枚举       |

### 6.1.2 类，父类，接口

用三个表，this class, super class, interfaces_count, interfaces四种表记录这组信息

### 6.1.3 code

# 7 类加载

## 7.1 代码加载顺序

**首次构造**：

父类的静态代码块
子类的静态代码块

父类的代码块
父类的构造方法

子类的代码块
子类的构造方法

**二次构造**：

父类的代码块
父类的构造方法
子类的代码块
子类的构造方法

通过子类名，调用继承的静态字段，只需要加载父类即可，并不需要把整个子类全部加载进来，当然，这个要看虚拟机实现。

## 7.2 非双亲委派模型

- 双亲委派模型发行之前，类加载器已经发布，为了向前兼容，在ClassLoader类中增加了一个函数loadclass（），目的就是个性化加载。
- 委派模型自身缺陷，接口提供者(SPI)负责实现类，上层对下层是不了解的。所有的SPI(JNDI，JDBC，JCE， JAXB，JBI)均是通过JVM 提供的线程上下文类加载器，由父类调用子类完成。
- 追求动态性，代码热替换，模块热部署，在JSR-297、JSR-277标准发布之前，OSGI是事实上的模块化标准，每个模块(bundle)都有自己的类加载器，热替换时，加载器和代码同时换掉。

# 8. 字节码执行引擎

# 8.1 运行时栈帧

- 局部变量表
- 操作数栈
- 动态链接
- 返回地址

# 9.Tomcat类加载 

/common/：公用

*/server/*：tomcat独用

/shared/：web程序共用，tomcat不可用

/web-inf/:app自用

CommonClassLoader、CatalinaClassLoader、SharedClassLoader、WebappClassLoader 。

# 10 并发

## 10.1 内存交互

### 10.1.1 交互动作

1. lock：主内存操作，锁定变量，标识其为线程独占的状态。

2. unlock：主内存操作，解锁变量，将其从线程独占的状态中释放出来。

3. read：主内存操作，读取变量到工作内存。

   

4. load：工作内存操作，将读取到的变量赋值给工作内存中的变量副本。

5. use：工作内存操作，将变量值传递给执行引擎以供操作。

6. assign：工作内存操作，将执行引擎操作后的值赋给工作内存中的变量。

7. store：工作内存操作，将工作内存中的变量传递给主内存。

   

8. write：主内存变量，将store得到的值写入主内存中的变量。

### 10.1.2 交互规则

- read, load不用连续执行，中间可执行其它指令，store，write也一样。
- 工作内存读，必须经过 read , load；工作内存写，必须经过 store，write。
- 不允许 read load或者 store，write只执行一种
- 不允许工作线程丢弃最近的assign，必须同步回主线程
- 不允许将未经assign的值，同步回主线程。
-  变量在某时刻至多一个线程对其进行lock操作，可lock多次，并unlock同样次数释放。
- 变量被lock时，工作内存中该变量清空，重新lock，assign。
- 没lock得变量，不允许unlock，线程也不许unlock其它线程lock的变量
- 线程unlock一个变量，必须将变量同步回主线程

### 10.1.3 volatile

可保证一致性，工作A同步到主线程，再同步到工作B。但是不是原子操作。

### 10.1.4 long和double的原子性

jvm可以不保证long和double的原子性。jvm内存模型允许long和double读写不是原子操作，但是强烈建议jvm实现为原子操作，一般确实是这么做的。

## 10.2 并发三大特性

原子性，可见性，有序性

## 10.3 线程安全

**不可变**：最安全

**绝对线程安全**：不管运行时环境如何，调用者都不需要任何额外的同步措施。

**相对线程安全**：保证对这个对象单独的操作是线程安全的，我们在调用的时候不需要做额外的保障措施，但是对于一些特定顺序的连续调用，就可能需要在调用端使用额外的同步手段来保证调用的正确性。 在Java语言中，大部分的线程安全类都属于这种类型，例如Vector、 HashTable、Collections的synchronizedCollection（）方法包装的集合等 

**线程兼容**：对象本身并不是线程安全的，但是可以通过在调用端正确地使用同步手段来保证对象在并发环境中可以安全地使用 

**线程对立**：安全无法保证

## 10.4 线程安全实现

- 互斥同步

临界区（Critical Section）、 互斥量（Mutex）和信号量（Semaphore）都是主要的互斥实现方式 

- 非阻塞同步

硬件保证语意只需一条机器指令完成，常用的有：

测试并设置（Test-and-Set）。
获取并增加（Fetch-and-Increment）。
交换（Swap）。
比较并交换（Compare-and-Swap，下文称CAS）。

CAS指令需要有3个操作数，分别是内存位置V、 旧的预期值（用O表示）和新值（用N表示）。 CAS指令执行时，当且仅当V符合旧预期值 O 时，处理器用新值 N 更新 V 的值，否则它就不执行更新，但是无论是否更新了 V 的值，都会返回 V 的旧值，上述的处理过程是一个原子操作。CAS指令存在漏洞，就是 O 变成中间量，再变回 O ，CAS指令无法识别。

- 无同步方案
  - 线程封闭
  - 使用ThreadLocal变量，每个线程都可以通过线程的 hashcode 找到自己的ThreadLocal变量。

## 10.5 锁优化

## 10.5.1 锁自旋

如果刚自旋成功获取了一次锁，并且占有锁的线程正在运行，那么再次获取希望很大。如果自旋指定次数，依然失败，应该挂起。如果自旋很少能获得锁，那么应该避免自旋。

## 10.6 轻量级锁

虚拟机首先在当前线程的栈帧中建立一个名为（Lock Record）的空间 , 拷贝object mark word。由于脱离了原始mark word，官方将它冠以displaced前缀，即displaced mark word(置换标记字)。 JVM做了一步交换指针的操作， 将object mark word里的轻量级锁指针指向lock record所在的stack指针，作用是让其他线程知道，该object monitor已被占用。 lock record里的owner指针指向object mark word的作用是为了在接下里的运行过程中，识别哪个对象被锁住了。 即互指。

## 10.6 重量级锁

若无争用，轻量级锁可应付。若出现多线程同时争用一个锁，那要膨胀为重量级锁，让内核来处理。

## 10.7  偏向锁

若无争用，连CAS操作都不要了，mark word中记录偏向的线程ID。有争用，根据状态恢复成无锁或者轻量级锁状态，再按照轻量级锁来处理。





