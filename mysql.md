# 1 常用管理命令

## 1.1 连接Mysql

格式： mysql -h 主机地址 -u 用户名 －p 用户密码

1、进入 mysql.exe 所在目录，打开命令行工具键入命令 mysql -u username -p ，回车后提示你输密码.注意用户名前可以有空格也可以没有空格，但是密码前必须没有空格，否则让你重新输入密码。成功进入可以看到提示符 

mysql>

2、连接到远程主机上的MYSQL。假设远程主机的IP为：110.110.110.110，用户名为root,密码为abcd123。

键入以下命令
​    mysql -h110.110.110.110 -u username-p 123;（注:u与username之间可以不用加空格，其它也一样）

## 1.2 命令行

```mysql
?         (\?) Synonym for `help'.
clear     (\c) Clear the current input statement.
connect   (\r) Reconnect to the server. Optional arguments are db and host.
delimiter (\d) Set statement delimiter.
ego       (\G) Send command to mysql server, display result vertically.
exit      (\q) Exit mysql. Same as quit.
go        (\g) Send command to mysql server.
help      (\h) Display this help.
print     (\p) Print current command.
prompt    (\R) Change your mysql prompt.
quit      (\q) Quit mysql.
rehash    (\#) Rebuild completion hash.
source    (\.) Execute an SQL script file. Takes a file name as an argument.
status    (\s) Get status information from the server.
tee       (\T) Set outfile [to_outfile]. Append everything into given outfile.
notee     (\t) Don't write into outfile.
use       (\u) Use another database. Takes database name as argument.
charset   (\C) Switch to another charset. Might be needed for processing binlog with multi-byte charsets.
warnings  (\W) Show warnings after every statement.
nowarning (\w) Don't show warnings after every statement.
```

## 1.3 修改密码

格式：mysqladmin -u 用户名 -p 旧密码 password 新密码

修改密码使用的是 mysqladmin  命令。初始的root可能没有密码，可以给root加个密码

mysqladmin -u root -password ab12

对于存在密码的用户，修改密码的命令如下：

mysqladmin -u username -p ab12 password djg345

## 1.4 用户以及权限管理

**用户权限管理主要有以下作用**： 

1. 可以限制用户访问哪些库、哪些表 (访问控制)
2. 可以限制用户对哪些表执行SELECT、CREATE、DELETE、DELETE、ALTER等操作 （操作控制）
3. 可以限制用户登录的IP或域名 （登陆地控制）
4. 可以限制用户自己的权限是否可以授权给别的用户（权限转移）

###1.4.1 新建用户

1. 直接操作用户表，用户表字段很多，很难插入成功，不建议使用

   ```sql
   insert into mysql.user -- 直接操作 user 表，麻烦--
   ```

2. 使用 create 命令，创建用户 test1 和 test2，密码都是123456

   ```sql
   create user `test1`@`172.22.3.160` identified by '123456';
   create user `test2`@`%` identified by '123456';
   
   -- 将 name 和 host分开，但是 @ 要紧挨着 host--
   create user test1 @localhost identified by '123456';
   ```

   @ 符号后面的 IP 指定了用户登陆的 IP，用户只能使用该指定 IP 来连接 Mysql 数据库。反撇号使 name 

   和 ip 可以区分，若不使用反撇号也可以使用引号，绝对不能啥也不加，这样 Mysql 会把他们看成是用户

   名，并且使用默认 IP —— %，这样用户登陆 IP 不受限制。

   新创建的用户没有任何权限。

###1.4.2 为用户授权

为用户授权 grant 命令，命令行输入 help grant 查看帮助

```sql
grant all privileges on test.* to test1 @localhost;
```

查看权限：

show grants for root@'localhost';

###1.4.3 删除用户

 　　@>mysql -u root -p

　　@>密码

 　　mysql>Delete FROM user Where User='test' and Host='localhost';

 　　mysql>flush privileges;

 　　mysql>drop database testDB; //删除用户的数据库

删除账户及权限：

>drop user 用户名@'%';

> drop user 用户名@ localhost; 

###1.4.4 修改指定用户密码

@>mysql -u root -p

  　　@>密码

  　　mysql>update mysql.user set password=password('新密码') where User="test" and Host="localhost";

  　　mysql>flush privileges;

### 1.4.5 查看用户权限

mysql> grant select,create,drop,update,alter on *.* to 'yangxin'@'localhost' identified by 'yangxin0917' with grant option;
mysql> show grants for 'yangxin'@'localhost';

### 1.4.6 回收权限

mysql> revoke all privileges on st1.* from 'user1'@'localhost'
mysql> flush privileges;

### 1.4.7 重命名用户

shell> rename user 'test3'@'%' to 'test1'@'%'; 

### 1.4.8 忘记密码

## 忘记密码

1> 添加登录跳过权限检查配置

修改my.cnf，在mysqld配置节点添加skip-grant-tables配置

```
[mysqld]
skip-grant-tables
```

2> 重新启动mysql服务

```
shell> service mysqld restart
```

3> 修改密码

此时在终端用mysql命令登录时不需要用户密码，然后按照修改密码的第一种方式将密码修改即可。 注意：mysql库的user表，5.7以下版本密码字段为password，5.7以上版本密码字段为authentication_string

4> 还原登录权限跳过检查配置

将my.cnf中mysqld节点的skip-grant-tables配置删除，然后重新启动服务即可。

Mysql 有多个个权限？经常记不住，今天总结一下，看后都能牢牢的记在心里啦！

## 1.5 创建数据库

注意：创建数据库之前要先连接Mysql服务器

命令：create database <数据库名>

例1：建立一个名为xhkdb的数据库
   mysql> create database xhkdb;

例2：创建数据库并分配用户

①CREATE DATABASE 数据库名;

②GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,ALTER ON 数据库名.* TO 数据库名@localhost IDENTIFIED BY '密码';

③SET PASSWORD FOR '数据库名'@'localhost' = OLD_PASSWORD('密码');

依次执行3个命令完成数据库创建。注意：中文 “密码”和“数据库”是户自己需要设置的。

## 1.6 显示数据库

命令：show databases （注意：最后有个s）
mysql> show databases;

注意：为了不再显示的时候乱码，要修改数据库默认编码。以下以GBK编码页面为例进行说明：

1、修改MYSQL的配置文件：my.ini里面修改default-character-set=gbk
2、代码运行时修改：
   ①Java代码：jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=gbk
   ②PHP代码：header("Content-Type:text/html;charset=gb2312");
   ③C语言代码：int mysql_set_character_set( MYSQL * mysql, char * csname)；
该函数用于为当前连接设置默认的字符集。字符串csname指定了1个有效的字符集名称。连接校对成为字符集的默认校对。该函数的工作方式与SET NAMES语句类似，但它还能设置mysql- > charset的值，从而影响了由mysql_real_escape_string() 设置的字符集。

## 1.7 删除数据库

命令：drop database <数据库名>
例如：删除名为 xhkdb的数据库
mysql> drop database xhkdb;

例子1：删除一个已经确定存在的数据库
   mysql> drop database drop_database;
   Query OK, 0 rows affected (0.00 sec)

例子2：删除一个不确定存在的数据库
   mysql> drop database if exists drop_database;

## 1.8 使用数据库

命令： use <数据库名>

例如：如果xhkdb数据库存在，尝试存取它：
   mysql> use xhkdb;
屏幕提示：Database changed

##1.9 查看数据库

命令：mysql> select database();

MySQL中SELECT命令类似于其他编程语言里的print或者write，你可以用它来显示一个字符串、数字、数学表达式的结果等等。如何使用MySQL中SELECT命令的特殊功能？

###1.9.1 显示MYSQL的版本

mysql> select version(); 
+-----------------------+ 
| version()             | 
+-----------------------+ 
| 6.0.4-alpha-community | 
+-----------------------+ 
1 row in set (0.02 sec) 

### 1.9.2 显示当前时间

mysql> select now(); 

- 显示年月日

SELECT DAYOFMONTH(CURRENT_DATE); 
+--------------------------+ 
| DAYOFMONTH(CURRENT_DATE) | 
+--------------------------+ 
|                       15 | 
+--------------------------+ 
1 row in set (0.01 sec) 

- SELECT MONTH(CURRENT_DATE); 

+---------------------+ 
| MONTH(CURRENT_DATE) | 
+---------------------+ 
|                   9 | 
+---------------------+ 
1 row in set (0.00 sec) 

- SELECT YEAR(CURRENT_DATE); 

+--------------------+ 
| YEAR(CURRENT_DATE) | 
+--------------------+ 
|               2009 | 
+--------------------+ 
1 row in set (0.00 sec) 

##1.10 删除数据表

命令：drop table <表名>

例如：删除表名为 MyClass 的表
   mysql> drop table MyClass;

DROP TABLE用于取消一个或多个表。您必须有每个表的DROP权限。所有的表数据和表定义会被取消，所以使用本语句要小心！

注意：对于一个带分区的表，DROP TABLE会永久性地取消表定义，取消各分区，并取消储存在这些分区中的所有数据。DROP TABLE还会取消与被取消的表有关联的分区定义（.par）文件。

对与不存在的表，使用IF EXISTS用于防止错误发生。当使用IF EXISTS时，对于每个不存在的表，会生成一个NOTE。

RESTRICT和CASCADE可以使分区更容易。目前，RESTRICT和CASCADE不起作用。

## 1.11 修改表结构

- 命令：alter table 表名 add 字段 类型 其他;

例如：在表MyClass中添加了一个字段passtest，类型为int(4)，默认值为0
   mysql> alter table MyClass add passtest int(4) default '0'

- 加索引

   mysql> alter table 表名 add index 索引名 (字段名1[，字段名2 …]);
例子： mysql> alter table employee add index emp_name (name);

- 加主关键字的索引

  mysql> alter table 表名 add primary key (字段名);
例子： mysql> alter table employee add primary key(id);

- 加唯一限制条件的索引

   mysql> alter table 表名 add unique 索引名 (字段名);
例子： mysql> alter table employee add unique emp_name2(cardnumber);

- 删除某个索引

   mysql> alter table 表名 drop index 索引名;
例子： mysql>alter table employee drop index emp_name;

- 增加字段：

mysql> ALTER TABLE table_name ADD field_name field_type;

- 修改原字段名称及类型：

mysql> ALTER TABLE table_name CHANGE old_field_name new_field_name field_type;

- 删除字段：

ALTER TABLE table_name DROP field_name;

- 修改表名

命令：rename table 原表名 to 新表名;

例如：在表MyClass名字更改为YouClass
   mysql> rename table MyClass to YouClass;

当你执行 RENAME 时，你不能有任何锁定的表或活动的事务。你同样也必须有对原初表的 ALTER 和 DROP 权限，以及对新表的 CREATE 和 INSERT 权限。

如果在多表更名中，MySQL 遭遇到任何错误，它将对所有被更名的表进行倒退更名，将每件事物退回到最初状态。

RENAME TABLE 在 MySQL 3.23.23 中被加入。

## 1.12 显示表结构

mysql> describe taa;
+-------+-------------+------+-----+---------+-------+
| Field | Type        | Null | Key | Default | Extra |
+-------+-------------+------+-----+---------+-------+
| name  | varchar(20) | YES  |     | NULL    |       |
| id    | int(11)     | NO   | PRI | NULL    |       |
| age   | int(11)     | YES  |     | NULL    |       |
| city  | varchar(20) | YES  |     | NULL    |       |
+-------+-------------+------+-----+---------+-------+

## 1.13备份数据库

命令在DOS的[url=file://\\mysql\\bin]\\mysql\\bin[/url]目录下执行

- 1.导出整个数据库

导出文件默认是存在mysql\bin目录下
​    mysqldump -u 用户名 -p 数据库名 > 导出的文件名
​    mysqldump -u user_name -p123456 database_name > outfile_name.sql

- 2.导出一个表

​    mysqldump -u 用户名 -p 数据库名 表名> 导出的文件名
​    mysqldump -u user_name -p database_name table_name > outfile_name.sql

- 3.导出一个数据库结构

​    mysqldump -u user_name -p -d –add-drop-table database_name > outfile_name.sql
​    -d 没有数据 –add-drop-table 在每个create语句之前增加一个drop table

- 4.带语言参数导出

​    mysqldump -uroot -p –default-character-set=latin1 –set-charset=gbk –skip-opt database_name > outfile_name.sql

例如，将aaa库备份到文件back_aaa中：
　　[root@test1 root]# cd　/home/data/mysql
　　[root@test1 mysql]# mysqldump -u root -p --opt aaa > back_aaa

## 1.14 执行SQL脚本

mysql> source d:/mysql.sql ;

# 2 概念

## 2.1 数据库语言

1.DDL（Data Definition Language）数据库定义语言statements are used to define the database structure or schema.   DDL是SQL语言的四大功能之一。 用于定义数据库的三级结构，包括外模式、概念模式、内模式及其相互之间的映像，定义数据的完整性、安全控制等约束 DDL不需要commit.

 CREATE ALTER DROP TRUNCATE COMMENT RENAME   

2.DML（Data Manipulation Language）数据操纵语言statements are used for managing data within schema objects.   由DBMS提供，用于让用户或程序员使用，实现对数据库中数据的操作。 DML分成交互型DML和嵌入型DML两类。 依据语言的级别，DML又可分成过程性DML和非过程性DML两种。 需要commit. 

增删改

3.DQL查询语言

4.DCL（Data Control Language）数据库控制语言  授权，角色控制等 GRANT 授权 REVOKE 取消授权   5.TCL（Transaction Control Language）事务控制语言 SAVEPOINT 设置保存点 ROLLBACK  回滚 SET TRANSACTION 

4和5可以看作DCL，控制，包括权限，事务等。

## 2.2 访问数据库

### 2.2.1 ODBC 

直接访问DB文件，不用通过DBMS(数据库管理系统)访问数据文件，提供了ODBC驱动的数据库，windows平台上的应用都是可以直接访问的。

### 2.2.2 JDBC

# 3 数据类型

## 3.1 数值型

TINYINT、SMALLINT、MEDIUMINT、INT、BIGINT长度分别为1，2，3，4，8

FLOAT、DOUBLE分别为4、8。其中FLOAT 可保证8位精度，DOUBLE可保证15位精度，意思是说，如果小数位数不超过8，都是准确的，当然可能只有一位小数。他俩都是浮点数

DECIMAL(M, N) 以字符串存放，占用 M + 2 个字节。M 表示位数，N 表示小数位数。这个是定点数。

## 3.2 时间与日期

- YEAR 

  1个字节，

- DATE

  3个字节

- TIME

  3个字节

- DATETIME

  8字节，1001年到9999年 

  \1. 占用8个字节

  \4. 与时区无关（It has nothing to deal with the TIMEZONE and Conversion.）

- TIMESTAMP

  4字节，1970到2038年 

  \1. 占用4个字节

  \2. TIMESTAMP值不能早于1970或晚于2037。这说明一个日期，例如'1968-01-01'，虽然对于DATETIME或DATE值是有效的，但对于TIMESTAMP值却无效，如果分配给这样一个对象将被转换为0。

  4.值以UTC格式保存（ it stores the number of milliseconds）


## 3.3 字符类型 

- char 数据净载255字节空间

- varchar 65535字节空间，数据净载65532字节

- tinytext 数据净载255字节（1字节）tinyblob

- text数据净载65535净载（2字节）blob

- mediumtext 24位长度（3字节）mediumblob

- longtext 32位长度(4字节) longblob

- enum 1或者2字节

- set 1，2，4，8字节

- bit(M) 比特位(M + 7)/8

- binary(M) 跟char是一类的，不过一种是二进制编码，一种是字符编码，净载255个字节

- varbinary(M) 净载65532字节，占用65535字节空间

  

  MySQL记录

  如果有任何一个字段允许为空,系统将自动从整个记录中保留一个字节来存储NULL(弱想释放NULL所占用的字节:必须保证所有的字段都不允许为空)

  

  MySQL 数据库的varchar类型在4.1以下的版本中的最大长度限制为255字节，其数据范围可以是0~255字节或1~255字节（根据不同版本数据库来定）。在 MySQL5.0以上的版本中，varchar数据类型的长度支持到了65535字节，起始位和结束位共占用3个字 节，因此数据净载为65532个字节。

  a) 存储限制

  varchar 字段是将实际内容单独存储在聚簇索引之外，内容开头用1到2个字节表示实际长度（长度超过255时需要2个字节），结束字符 1 个字节。

  b) 编码长度限制

  在 latin1 字符集下，做大字符数为 65532 字符

  在utf8字符集下最大长度 : 21844 21844 * 3 + 2 = 65534

  在GBK字符集下最大长度 : 32766 32766 * 2 + 2 = 65534

  navicat 在字段长度那一列会根据字符集检查长度设置，若长度不符合，不能创建DDL

  c) 行长度限制

  导致实际应用中varchar长度限制的是一个行定义的长度。 MySQL要求一个行的定义长度不能超过65535。若定义的表长度超过这个值，则提示

  ERROR 1118 (42000): Row size too large. The maximum row size for the used table type, not counting BLOBs, is 65535. You have to change some columns to TEXT or BLOBs。

# 4 存储过程

## 4.1 delimiter

在用命令行时，由于默认的delimiter是 分号 ; ，而 sql 语句的结束标志也是分号。这就导致命令行无法准确判断存储过程的结束位置，一般在使用命令行时都要重新设定delimiter。在脚本中不存在这种情况，不需要delimiter。 
