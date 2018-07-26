# 1 NIO

IO分为五种类型：

 - blocking IO   

   ![](https://github.com/wutongtongshu/doc/raw/master/IO/blocking_IO.gif)  

-  nonblocking IO

   ![](https://github.com/wutongtongshu/doc/raw/master/IO/nonblocking_IO.gif)     

-  IO multiplexing     

   ![](https://github.com/wutongtongshu/doc/raw/master/IO/IO_multiplexing.gif)

-  signal driven IO    

-  asynchronous IO 

   ![](https://github.com/wutongtongshu/doc/raw/master/IO/asynchronous_IO%20.gif)

A synchronous I/O operation causes the requesting process to be blocked until that I/O operation completes;    

An asynchronous I/O operation does not cause the requesting process to be blocked

对于网络IO来说，在用户线程执行系统调用后，内核要分两步完成IO操作。首先准备数据，然后将数据拷贝到用户空间中。在内核执行期间，用户线程可以选择阻塞，也可以选择不阻塞，不完全阻塞。

## 1.1 ByteBuffer

flip()：limit=position, position=0, capacity不变。读写反转的意思

clear()：limit=capacity, position=0, capacity不变。清空，达到最大程度

rewind()：position=0，limit和capacity不变，重新执行一遍

compact()：将 [position, limit] 之间的数据移动到 buffer 最前端，(limit, capacity]之间不管有没有数据统统删除。

## 2.1 channel支持的事件 

ServerSocketChannel OP_ACCEPT 

SocketChannel OP_CONNECT, OP_READ, OP_WRITE 

DatagramChannel OP_READ, OP_WRITE 

Pipe.SourceChannel OP_READ 

Pipe.SinkChannel OP_WRITE