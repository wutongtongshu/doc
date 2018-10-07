# 1 log4j

 三个组件 *loggers*, *appenders* and *layouts*.

## 1.1 loggers

logger对象用一个与logger对象的名字相关的目条目记录着，名字是大小写敏感的，名字遵循如下规则：

名字后跟着一个点号，则前面是父，后面是子For example, the logger named `"com.foo"` is a parent of the logger named `"com.foo.Bar"`. Similarly, `"java"` is a parent of `"java.util"` and an ancestor of `"java.util.Vector"`.

根logger是logger继承体系的顶层，两个特性

- it always exists,
- 不能通过名字检索

通过 Logger.getRootLogger 方法检索出根 logger，通过 Logger.getLogger 方法实例化其它的 logger。

```java
 package org.apache.log4j;

  public class Logger {

    // Creation & retrieval methods:
    public static Logger getRootLogger();
    public static Logger getLogger(String name);

    // printing methods:
    public void trace(Object message);
    public void debug(Object message);
    public void info(Object message);
    public void warn(Object message);
    public void error(Object message);
    public void fatal(Object message);

    // generic printing method:
    public void log(Level l, Object message);
}
```

如上，logger 对象提供了各种级别的方法，根据我们配置的级别，调用相应级别的方法进行日志输出，日志级别如下

```java
TRACE,
DEBUG,
INFO,
WARN,
ERROR,
FATAL
```

### 1.1.1 日志输出级别的继承

如果 logger 对象没有指定输出级别，那就会继承离它最近的祖先的级别。下面是几个例子

Example 1

| Logger name | 设置的级别 | 通过继承获取的级别 |
| ----------- | ---------- | ------------------ |
| root        | Proot      | Proot              |
| X           | none       | Proot              |
| X.Y         | none       | Proot              |
| X.Y.Z       | none       | Proot              |

logger 对象 X 、X.Y、X.Y.Z 都没有指定级别，那么就只能继承 root 的级别



Example 2

| Logger name | Assigned level | Inherited level |
| ----------- | -------------- | --------------- |
| root        | Proot          | Proot           |
| X           | Px             | Px              |
| X.Y         | Pxy            | Pxy             |
| X.Y.Z       | Pxyz           | Pxyz            |

In example 2, all loggers have an assigned level value. There is no need for level inheritence.



Example 3

| Logger n | 设置的级别 | 通过继承获取的级别 |
| -------- | ---------- | ------------------ |
| root     | Proot      | Proot              |
| X        | Px         | Px                 |
| X.Y      | none       | Px                 |
| X.Y.Z    | Pxyz       | Pxyz               |

In example 3, the loggers `root`, `X` and `X.Y.Z` are assigned the levels `Proot`, `Px` and `Pxyz` respectively. The logger `X.Y` inherits its level value from its parent `X`.

| Logger name | 设置的级别 | 通过继承获取的级别 |
| ----------- | ---------- | ------------------ |
| root        | Proot      | Proot              |
| X           | Px         | Px                 |
| X.Y         | none       | Px                 |
| X.Y.Z       | none       | Px                 |

In example 4, the loggers `root` and `X` and are assigned the levels `Proot` and `Px` respectively. The loggers `X.Y` and `X.Y.Z` inherits their level value from their nearest parent `X` having an assigned level..

### 1.1.2 日志输出的条件

```mathematica
logger类设置的级别是 q，要让改该类输出日志，那么指定的输出级别 p 必须 p >= q.

DEBUG < INFO < WARN < ERROR < FATAL.
```

##1.2 appenders

 Log4 允许日志调用输出到多个 destinations.  an output destination is called an *appender*. 目前支持的appender 有  [console](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/ConsoleAppender.html),  [files](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/FileAppender.html),  GUI components, [remote socket](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/net/SocketAppender.html) servers,[JMS](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/net/JMSAppender.html), [NT Event Loggers](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/nt/NTEventLogAppender.html), and remote UNIX [Syslog](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/net/SyslogAppender.html) daemons. 也支持异步日志输出 [asynchronously](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/AsyncAppender.html).

一个 logger 类可以配置多个 appender，组成这个类的 appenders。

### 1.2.1 appender继承规则

additivity flag  控制 appender 的继承，如果设置为  true ，那么要继承。如果设置为 false ，继承被截断。



| Logger Name     | Added Appenders | Additivity Flag | Output Targets         | Comment                                                      |
| --------------- | --------------- | --------------- | ---------------------- | ------------------------------------------------------------ |
| root            | A1              | not applicable  | A1                     | The root logger is anonymous but can be accessed with the Logger.getRootLogger() method. There is no default appender attached to root. |
| x               | A-x1, A-x2      | true            | A1, A-x1, A-x2         | Appenders of "x" and root.                                   |
| x.y             | none            | true            | A1, A-x1, A-x2         | Appenders of "x" and root.                                   |
| x.y.z           | A-xyz1          | true            | A1, A-x1, A-x2, A-xyz1 | Appenders in "x.y.z", "x" and root.                          |
| security        | A-sec           | false           | A-sec                  | No appender accumulation since the additivity flag is set to `false`. |
| security.access | none            | true            | A-sec                  | Only appenders of "security" because the additivity flag in "security" is set to `false`. |

### 1.2.2 最简单的配置

直接输出到控制台

```java 
import org.apache.log4j.Logger;
 import org.apache.log4j.BasicConfigurator;

 public class MyApp {

   // Define a static logger variable so that it references the
   // Logger instance named "MyApp".
   static Logger logger = Logger.getLogger(MyApp.class);

   public static void main(String[] args) {

     // Set up a simple configuration that logs on the console.
     BasicConfigurator.configure();

     logger.info("Entering application.");
     Bar bar = new Bar();
     bar.doIt();
     logger.info("Exiting application.");
   }
 }
```

## 1.3 layout

[详解可以看这篇博客](http://www.blogjava.net/DLevin/archive/2012/07/04/382131.html)



| **格式字符** | **结果**                                                     |
| ------------ | ------------------------------------------------------------ |
| **c**        | 显示logger name，可以配置精度，如**%c{2}**，从后开始截取。   |
| **C**        | 显示日志写入接口的雷鸣，可以配置精度，如**%C{1}**，从后开始截取。**注：**会影响性能，慎用。 |
| **d**        | 显示时间信息，后可定义格式，如**%d{HH:mm:ss,SSS}****，**或Log4J中定义的格式，如**%d{ISO8601}**，**%d{ABSOLUTE}**，Log4J中定义的时间格式有更好的性能。 |
| **F**        | 显示文件名，会影响性能，慎用。                               |
| **l**        | 显示日志打印是的详细位置信息，一般格式为full.qualified.caller.class.method(filename:lineNumber)。**注：**该参数会极大的影响性能，慎用。 |
| **L**        | 显示日志打印所在源文件的行号。**注：**该参数会极大的影响性能，慎用。 |
| **m**        | 显示渲染后的日志消息。                                       |
| **M**        | 显示打印日志所在的方法名。**注：**该参数会极大的影响性能，慎用。 |
| **n**        | 输出平台相关的换行符。                                       |
| **p**        | 显示日志Level                                                |
| **r**        | 显示相对时间，即从程序开始（实际上是初始化LoggingEvent类）到日志打印的时间间隔，以毫秒为单位。 |
| **t**        | 显示打印日志对应的线程名称。                                 |
| **x**        | 显示与当前线程相关联的NDC（Nested Diagnostic Context）信息。 |
| **X**        | 显示和当前想成相关联的MDC（Mapped Diagnostic Context）信息。 |
| **%**        | %%表达显示%字符                                              |

上述输出规则仿照 C 语言

---

%a             浮点数、十六进制数字和p-记数法（Ｃ９９）
%A　　　　浮点数、十六进制数字和p-记法（Ｃ９９）
%c　　　　 一个字符(char)

%C           一个ISO宽字符

%d　　　　有符号十进制整数(int)（%e　　　　浮点数、e-记数法
%E　　　　浮点数、Ｅ-记数法
%f　　　　 单精度浮点数(默认float)、十进制记数法（%.nf  这里n表示精确到小数位后n位.十进制计数）

%g　　　　根据数值不同自动选择％f或％e．
%G　　　　根据数值不同自动选择％f或％e.
%i              有符号十进制数（与％d相同）
%o　　　　无符号八进制整数
%p　　　   指针
%s　　　　 对应字符串char*（%S             对应宽字符串WCAHR*（%u　　　   无符号十进制整数(unsigned int)
 %x　　　　使用十六进制数字０f的无符号十六进制整数　
 %X　　　   使用十六进制数字０f的无符号十六进制整数
 %%　　　  打印一个百分号
 

%I64d 用于INT64 或者 long long

unsigned long long



①%：表示格式说明的起始符号，不可缺少。
②-：有-表示左对齐输出，如省略表示右对齐输出。
③0：有0表示指定空位填0,如省略表示指定空位不填。
④m.n：m指域宽，即对应的输出项在输出设备上所占的字符数。n指精度。用于说明输出的实型数的小数位数。为指定n时，隐含的精度为n=6位。
⑤l或h:l对整型指long型，对实型指double型。h用于将整型的格式字符修正为short型。

－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
**格式字符**
格式字符用以指定输出项的数据类型和输出格式。
 ①d格式：用来输出十进制整数。有以下几种用法：
%d：按整型数据的实际长度输出。
%md：m为指定的输出字段的宽度。如果数据的位数小于m，则左端补以空格，若大于m，则按实际位数输出。

②o格式：以无符号八进制形式输出整数。对长整型可以用"%lo"格式输出。同样也可以指定字段宽度用“%mo”格式输出。
例：
   main()
   { int a = -1;
​     printf("%d, %o", a, a);
   }
  运行结果：-1,177777
  程序解析：-1在内存单元中（以补码形式存放）为(1111111111111111)2，转换为八进制数为(177777)8。
③x格式：以无符号十六进制形式输出整数。对长整型可以用"%lx"格式输出。同样也可以指定字段宽度用"%mx"格式输出。
④u格式：以无符号十进制形式输出整数。对长整型可以用"%lu"格式输出。同样也可以指定字段宽度用“%mu”格式输出。
⑤c格式：输出一个字符。
⑥s格式：用来输出一个串。有几中用法
%s：例如:printf("%s", "CHINA")输出"CHINA"字符串（不包括双引号）
%ms：输出的字符串占m列，如果字符串本身长度大于m，则突破获m的限制,将字符串全部输出。若串长小于m，则左补空格。
%-ms：如果串长小于m，则在m列范围内，字符串向左靠，右补空格。
%m.ns：输出占m列，但只取字符串中左端n个字符。这n个字符输出在m列的右侧，左补空格。
%-m.ns：其中m、n含义同上，n个字符输出在m列范围的左侧，右补空格。如果n>m，则自动取n值，即保证n个字符正常输出。
⑦f格式：用来输出实数（包括单、双精度），以小数形式输出。有以下几种用法：
%f：不指定宽度，整数部分全部输出并输出6位小数。
%m.nf：输出共占m列，其中有n位小数，若数值宽度小于m左端补空格。 
%-m.nf：输出共占m列，其中有n位小数，若数值宽度小于m右端补空格。
⑧e格式：以指数形式输出实数。可用以下形式：
%e：数字部分（又称尾数）输出6位小数，指数部分占5位或4位。
%m.ne和%-m.ne：m、n和”-”字符含义与前相同。此处n指数据的数字部分的小数位数，m表示整个输出数据所占的宽度。
⑨g格式：自动选f格式或e格式中较短的一种输出，且不输出无意义的零。

－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
关于printf函数的进一步说明：
如果想输出字符"%",则应该在“格式控制”字符串中用连续两个%表示，如:
printf("%f%%", 1.0/3);
输出0.333333%。

－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－


对于单精度数，使用%f格式符输出时，仅前7位是有效数字，小数6位．
对于双精度数，使用%lf格式符输出时，前16位是有效数字，小数6位．



-----------------------------可变宽度参数
对于m.n的格式还可以用如下方法表示（例）
char ch[20];
printf("%*.*s\n",m,n,ch);
前边的*定义的是总的宽度，后边的定义的是输出的个数。分别对应外面的参数m和n 。我想这种方法的好处是可以在语句之外对参数m和n赋值，从而控制输出格式。

---

## 1.4 使用示例

**1. 配置文件**
Log4J配置文件的基本格式如下： 

![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)#配置根Logger
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.rootLogger  =   [ level ]   ,  appenderName1 ,  appenderName2 ,  …
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)#配置日志信息输出目的地Appender
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.appenderName  =  fully.qualified.name.of.appender.class 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　log4j.appender.appenderName.option1  =  value1 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　… 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　log4j.appender.appenderName.optionN  =  valueN 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)#配置日志信息的格式（布局）
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.appenderName.layout  =  fully.qualified.name.of.layout.class 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　log4j.appender.appenderName.layout.option1  =  value1 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　… 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　log4j.appender.appenderName.layout.optionN  =  valueN  

 
其中 **[level]** 是日志输出级别，共有5级：
 

**![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)FATAL       0  ![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)ERROR      3  ![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)WARN       4  ![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)INFO         6  ![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)DEBUG      7 ![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)**

 
Appender 

为日志输出目的地，Log4j提供的appender有以下几种：

![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.ConsoleAppender（控制台），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.FileAppender（文件），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方） 

**Layout**：日志输出格式，Log4j提供的layout有以下几种：

![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.HTMLLayout（以HTML表格形式布局），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.PatternLayout（可以灵活地指定布局模式），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.SimpleLayout（包含日志信息的级别和信息字符串），
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)org.apache.log4j.TTCCLayout（包含日志产生的时间、线程、类别等等信息） 

 
**打印参数:** Log4J采用类似C语言中的printf函数的打印格式格式化日志信息，如下:

![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　  **%m**   输出代码中指定的消息
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%p**   输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%r**   输出自应用启动到输出该log信息耗费的毫秒数 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%c**   输出所属的类目，通常就是所在类的全名 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%t**   输出产生该日志事件的线程名 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%n**   输出一个回车换行符，Windows平台为“/r/n”，Unix平台为“/n” 
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%d**   输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy MMM dd HH:mm:ss , SSS}，输出类似：2002年10月18日  22 ： 10 ： 28 ， 921  
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)　　**%l**   输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。举例：Testlog4.main(TestLog4.java: 10 ) ![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif) 

 
**2. 在代码中初始化Logger:** 
1）在程序中调用**BasicConfigurator.configure()**方法：给根记录器增加一个ConsoleAppender，输出格式通过PatternLayout设为**"%-4r [%t] %-5p %c %x - %m%n"**，还有根记录器的默认级别是**Level.DEBUG**. 
2）配置放在文件里，通过命令行参数传递文件名字，通过**PropertyConfigurator.configure(args[x])**解析并配置；
3）配置放在文件里，通过环境变量传递文件名等信息，利用log4j默认的初始化过程解析并配置；
4）配置放在文件里，通过应用服务器配置传递文件名等信息，利用一个特殊的servlet来完成配置。

**3. 为不同的 Appender 设置日志输出级别：**
当调试系统时，我们往往注意的只是异常级别的日志输出，但是通常所有级别的输出都是放在一个文件里的，如果日志输出的级别是BUG！？那就慢慢去找吧。
这时我们也许会想要是能把异常信息单独输出到一个文件里该多好啊。当然可以，Log4j已经提供了这样的功能，我们只需要在配置中修改**Appender**的**Threshold** 就能实现,比如下面的例子：

**[配置文件]**
 

![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)### set log levels ###
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.rootLogger = debug ,  stdout ,  D ,  E
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)### 输出到控制台 ###
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.stdout = org.apache.log4j.ConsoleAppender
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.stdout.Target = System.out
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.stdout.layout.ConversionPattern =  %d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)### 输出到日志文件 ###
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.File = logs/log.log
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.Append = true
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.**Threshold** = DEBUG **## 输出DEBUG级别以上的日志**
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.layout = org.apache.log4j.PatternLayout
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)### 保存异常信息到单独文件 ###
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.File = logs/error.log ## 异常日志文件名
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.Append = true
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.**Threshold** = ERROR **## 只输出ERROR级别以上的日志!!!**![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.layout = org.apache.log4j.PatternLayout
![img](http://www.blogjava.net/Images/OutliningIndicators/None.gif)log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

  ---------------------  本文来自 azheng270 的CSDN 博客 ，全文地址请点击：https://blog.csdn.net/azheng270/article/details/2173430?utm_source=copy 