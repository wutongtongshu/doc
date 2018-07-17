### windows10单机 zookeeper集群搭建

本集群包含3个**Server**实例，下文简称**Server1**、**Server2**、**Server3**。

##### 1. 修改配置文件

 * 分别创建**Server1**、**Server2**、**Server3**的配置文件,zoo1.cfg，zoo2.cfg, zoo3.cfg，内容如下：

   ``` properties
   #心跳时间
   tickTime=2000
   #定义初始化的时间限制，总时间10*tickTime = 20s
   initLimit=10
   #定义同步的时间限制，5*tickTime = 10s  
   syncLimit=5
   #数据目录
   dataDir=D:\kafkaData\zookeeper\z1\data
   #日志目录
   dataLogDir=D:\kafkaData\zookeeper\z1\log
   #zookeeper端口
   clientPort=2181
   #Server1、Server2、Server3配置，2888这一列是server之间通信端口,2889这一列
   #是选举leader时使用的
   server.1=localhost:2888:2889
   server.2=localhost:2890:2891
   server.3=localhost:2892:2893
   ```

   上述可作为zoo1.cfg，对于zoo2.cfg, zoo3.cfg要对如下属性值进行修改，分别改为：

   ```properties
   dataDir=D:\kafkaData\zookeeper\z2\data
   dataLogDir=D:\kafkaData\zookeeper\z2\log
   clientPort=2182
   ```

   和

   ``` properties
   dataDir=D:\kafkaData\zookeeper\z3\data
   dataLogDir=D:\kafkaData\zookeeper\z3\log
   clientPort=2182
   ```

* 创建dataDir、dataLogDir对应的目录

  ``` c
  mkdir D:\kafkaData\zookeeper\z1\data
  mkdir D:\kafkaData\zookeeper\z2\data
  mkdir D:\kafkaData\zookeeper\z3\data
  mkdir D:\kafkaData\zookeeper\z1\log
  mkdir D:\kafkaData\zookeeper\z2\log
  mkdir D:\kafkaData\zookeeper\z3\log
  ```

* 创建myid文件

  ```c
  echo 1>>D:\kafkaData\zookeeper\z1\data\myid
  echo 2>>D:\kafkaData\zookeeper\z2\data\myid
  echo 3>>D:\kafkaData\zookeeper\z3\data\myid
  ```

  用文本编辑器打开这三个文件，确保其中只有1、2、3。**Server1**、**Server2**、**Server3**在启动的时候，会读取myid中的值，作为server的id标志，注意这个文件中的空格和空行，统统都删掉

##### 2. 创建**Server1**、**Server2**、**Server3**的启动脚本 

复制**zkServer.cmd**，分别为**zkServer1.cmd**、**zkServer2.cmd**、**zkServer3.cmd**，关键步骤是在这三个脚本

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain这句下面加入下面这个设置：

``` 
set ZOOCFG=%ZOOCFGDIR%\zoo1.cfg
```

```
set ZOOCFG=%ZOOCFGDIR%\zoo2.cfg
```

```
set ZOOCFG=%ZOOCFGDIR%\zoo3.cfg
```

截图如下：

其作用是在启动的时候，分别加载自己的配置zoo1.cfg，zoo2.cfg, zoo3.cfg。

##### 3. 启动 

- **zkServer1.cmd**、**zkServer2.cmd**、**zkServer3.cmd**三个脚本至少启动两个，否则会一直报connect faild!，原因是集群要求至少一半的服务器启动成功

- 客户端以随机顺序连接到连接串中的服务器，利于负载均衡

  zkCli.sh -server 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183

##### 4. 原理

###### 4.1 锁实现

当 N 个进程执行时，进程 P 尝试创建 /lock 节点，为防止 P 崩溃锁死，/lock 节点必须为临时节点。若创建成功则表示进程 P 获取到了锁，可以执行后续操作。

###### 4.2 主节点角色 

一个进程成为 ZooKeeper 的主节点后必须锁定管理权。为此，进程需要创建一个临时znode，名为 /master

```c
create -e /master "master1.example.com:2223"
```

参数 -e 表明当前节点为临时节点

![1527266135046](C:\Users\wudey\AppData\Local\Temp\1527266135046.png)

分别创建master、workers、tasks、assign节点，并且监听这些节点

###### 4.3 监视节点 

``` c
stat /msater true
```

stat：列出节点信息

true：设置 wather

###### 4.4 给重节点配置角色

目前有localhost:2181,localhost:2812,localhost:2183这三个节点，其中localhost:2181是主节点，下面配置节点2为worker，角色标志为wk.com

```c
 create -e /workers/wk.com "localhost:2182"
```

配置wk.com这个角色接受任务能力

```c
 create /assign/wk.com ""
```

由于localhost:2182这个节点已经是一个角色，这里不需要再写节点信息了

创建一个任务, **-s**参数用来创建顺寻节点，**-e**节点用来创建临时节点

```c
create -s /tasks/task- "cmd"
WATCHER::Created /tasks/task-0000000000
```

表明顺序节点 task-0000000000 创建成功，也表示任务创建了。斜面将该任务分配给角色

```c
create /assign/wk.com/task-0000000000 ""
```

任务执行完毕，刷新任务状态

```c
 create /tasks/task-0000000000/status "done"
```

###### 4.5 启动时实际执行命令 

```java
java -cp xxx1.jar;xxx2.jar Test.class 这是Test.class依赖前面的jar
------ 
    
"D:\Program Files\Java\jdk1.8.0_162"\bin\java          // java工具
"-Dzookeeper.log.dir=D:\tool\zookeeper-3.4.10\bin\.."  //设置环境变量
"-Dzookeeper.root.logger=INFO,CONSOLE" -cp 
"D:\tool\zookeeper-3.4.10\bin\..\build\classes;         //依赖的路径，下全部包都可以用
D:\tool\zookeeper-3.4.10\bin\..\build\lib\*;
D:\tool\zookeeper-3.4.10\bin\..\*;
D:\tool\zookeeper-3.4.10\bin\..\lib\*;
D:\tool\zookeeper-3.4.10\bin\..\conf" 
org.apache.zookeeper.server.quorum.QuorumPeerMain       //主类
"D:\tool\zookeeper-3.4.10\bin\..\conf\zoo.cfg"          //配置文件
```

这是zkserver.cmd中命令

```c
set CLASSPATH=%ZOOCFGDIR%
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\*;%CLASSPATH%
SET CLASSPATH=%~dp0..\build\classes;%~dp0..\build\lib\*;%CLASSPATH%
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*
```

这里**%~dp0**表示脚本存放目录，**%cd%**表示主调程序路径



