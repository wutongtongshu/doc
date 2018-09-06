#1 约束

##1.1 主键约束

- 方法一

```sql
create table tb_emp2(
	id int(11) primary key,
	name varchar(25),
	deptId int(11),
	salary float
);
```

- 方法二

```sql
create table tb_emp4(
	name varchar(25), 
	deptId int(11), 
	salary float, 
	primary key(name, deptId)
);
```

## 1.2 外键约束

```sql
[CONSTRAINT symbol] FOREIGN KEY (col_name, ...)
REFERENCES tbl_name (index_col_name, ...)
[ON DELETE {RESTRICT | CASCADE | SET NULL | NO ACTION | SET DEFAULT}]
[ON UPDATE {RESTRICT | CASCADE | SET NULL | NO ACTION | SET DEFAULT}]
该语法可以在 CREATE TABLE 和 ALTER TABLE 时使用，如果不指定CONSTRAINT symbol，MYSQL会自动生成一个名字。
ON DELETE、ON UPDATE表示事件触发限制，可设参数：
RESTRICT（限制外表中的外键改动）
CASCADE（跟随外键改动）
SET NULL（设空值）
SET DEFAULT（设默认值）
NO ACTION（无动作，默认的）
```

- 方法一

```sql
create table teacher(id int(11), city int(11), 
                     constraint fk foreign key(city) references city(id));
```

- 方法二

```sql
ALTER TABLE `article`
ADD CONSTRAINT `fk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);
```

- 删除外键

```sql
ALTER TABLE article DROP FOREIGN KEY fk_1
```

## 1.3 非空约束

字段名 数据类型 not empty

## 1.4 唯一性约束

mysql中二者没区别，sql server中二者有区别。注意唯一索引对  null 值不起作用

```sql
create table tb_dept2(id int(11) primary key, name varchar(22) unique, location varchar(50));
```

```sql
create table tb_dept3(id int(11) primary key, name varchar(22), location varchar(50), constraint sth unique(name));
```

## 1.5 默认值约束

```sql
create table tb_emp7(id int(11) primary key, name varchar(25) not null, deptId int(11) default 1111, salary float);
```

# 2 改表

## 2.1 改表名

```sql
alter table tb_dept3 rename tb_department3;
```

## 2.2改字段数据类型

```sql
alter table city modify name varchar(30);
```

## 2.3 改字段名

```sql
alter table city change name site varchar(30);
```

## 2.4 删字段

```sql
 alter table t drop location;
```

## 2.5指定字段位置

```sql
alter table t add location varchar(20) first;

mysql> describe t;
+----------+-------------+------+-----+---------+-------+
| Field    | Type        | Null | Key | Default | Extra |
+----------+-------------+------+-----+---------+-------+
| location | varchar(20) | YES  |     | NULL    |       |
| id       | int(11)     | NO   | PRI | NULL    |       |
| name     | varchar(22) | YES  | UNI | NULL    |       |
+----------+-------------+------+-----+---------+-------+

lter table t add city varchar(10) after location;

mysql> describe t;
+----------+-------------+------+-----+---------+-------+
| Field    | Type        | Null | Key | Default | Extra |
+----------+-------------+------+-----+---------+-------+
| location | varchar(20) | YES  |     | NULL    |       |
| city     | varchar(10) | YES  |     | NULL    |       |
| id       | int(11)     | NO   | PRI | NULL    |       |
| name     | varchar(22) | YES  | UNI | NULL    |       |
+----------+-------------+------+-----+---------+-------+
```

# 3 存储过程

## 3.1显示存储过程

 show procedure status where db = '数据库表名'

## 3.1@

不能直接定义临时变量

```sql
mysql> declare @x;
ERROR 1064 (42000): You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'declare @x' at line 1
```

通过 select 可以定义临时变量

```slq
mysql> select @x:= 4;
```

通过参数定义临时变量

```sql
mysql> call my_sqrt(@x, @y);
```

##3.1 demo1

hello.sql

```sql
delimiter $$
drop procedure if exists HelloWorld;
create procedure HelloWorld()
begin
	select 'Hello World!';
end $$
```

```sql
mysql> source hello.sql
mysql> call Helloworld()$$
```

\$\$分隔符里面的语句，不必使用 \$\$，如果使用在 **source hello.sql** 这句会报错。

## 3.2 SET

```sql
delimiter $$
drop procedure if exists my_sqrt;
create procedure my_sqrt(input_num int)
begin
 declare l_sqrt float;
 set l_sqrt=sqrt(input_num);
 select l_sqrt;
end $$
delimiter ;
```

```sql
mysql> my_sqrt hello.sql
mysql> call my_sqrt(4);
```

## 3.3 IN OUT INOUT

```sql
delimiter $$
drop procedure if exists my_sqrt;
create procedure my_sqrt(input_num int, OUT out_num float)
begin
 set out_num=sqrt(input_num);
end $$
delimiter ;
```

```sql
mysql> select @x:= 4;
mysql> call my_sqrt(@x, @y);
mysql> select @y;
```

## 3.4 作用域

###3.4.1 内外部均申明

内部和外部均申明了 my_variables ，外部无法进入内部，内部无法溢出，互不影响

```sql
drop procedure if exists nested_block3;
create procedure nested_block3()
begin
	declare my_variables varchar(50);
	set my_variables = 'This value was set in out block';
	begin
		declare my_variables varchar(50);
		set my_variables = 'This value was set in inner block';
		select my_variables innertest;
	end;
	select my_variables as outtest;
end
```

结果显示内外部互不干扰

```sql
mysql> call nested_block3();
+-----------------------------------+
| innertest                         |
+-----------------------------------+
| This value was set in inner block |
+-----------------------------------+
1 row in set (0.00 sec)

+---------------------------------+
| outtest                         |
+---------------------------------+
| This value was set in out block |
+---------------------------------+
1 row in set (0.00 sec)
```

### 3.4.2 外部申明,内部不申明

外部进入内部，值被内部修改

```sql
drop procedure if exists nested_block3;
create procedure nested_block3()
begin
	declare my_variables varchar(50);
	set my_variables = 'This value was set in out block';
	begin
		set my_variables = 'This value was set in inner block';
		select my_variables innertest;
	end;
	select my_variables as outtest;
end

call nested_block3();
```

```sql
mysql> call nested_block3();
+-----------------------------------+
| innertest                         |
+-----------------------------------+
| This value was set in inner block |
+-----------------------------------+
1 row in set (0.00 sec)

+-----------------------------------+
| outtest                           |
+-----------------------------------+
| This value was set in inner block |
+-----------------------------------+
1 row in set (0.01 sec)
```

###3.4.3 内部申明，外部不申明

外部报错，内部事务完成

```sql
drop procedure if exists nested_block3;
create procedure nested_block3()
begin
	begin
		declare my_variables varchar(50);
		set my_variables = 'This value was set in inner block';
		select my_variables innertest;
	end;
	select my_variables as outtest;
end
```

```sql
mysql> call nested_block3();
+-----------------------------------+
| innertest                         |
+-----------------------------------+
| This value was set in inner block |
+-----------------------------------+
1 row in set (0.00 sec)

ERROR 1054 (42S22): Unknown column 'my_variables' in 'field list'
```

### 3.4.4 带if

if的条件不能带括号，否则报错。也可以加上括号，但是必须重新定义分隔符delimiter

```sql
DROP PROCEDURE IF EXISTS nested_blocks5;
CREATE PROCEDURE nested_blocks5( )
outer_block: BEGIN
	DECLARE l_status int;
	SET l_status=1;
	inner_block: BEGIN
		IF l_status=1 THEN
			LEAVE inner_block;
		END IF;
		SELECT 'This statement will never be executed';
	END inner_block;
	SELECT 'End of program';
END outer_block;
```

## 3.5 loop

loop出错可能导致死循环, leave离开循环体

```sql
drop procedure if exists pr_loop;
CREATE  PROCEDURE pr_loop()
begin
declare i int;
set i = 0;
myloop: loop
	set i = i + 1;
	if i = 10 then leave myloop;
	end if;
end loop myloop;
select 'I cant count to 10';
end;
```

iterate从循环体头再次执行

```sql
drop procedure if exists pr_loop2;
create procedure pr_loop2()
begin
	declare i int;
	set i = 0;
	loop1 : loop
		set i = i + 1;
		if i >= 10 then leave loop1;
		elseif MOD(i, 2) = 0 then iterate loop1;
		end if;
		select concat(i, ' is odd num');
	end loop loop1;
end;
```

## 3.6 repeat

```sql
drop procedure if exists pr_loop3;
create procedure pr_loop3()
begin
	declare x int;
	set x = 0;
	repeat1 : repeat
	set x = x + 1;
	if MOD(x, 2) <> 0 then 
		select concat(x, ' is odd num');
	end if;
	until x > 3
	end repeat repeat1;
end;
```

```sql
mysql> call pr_loop3();
+--------------------------+
| concat(x, ' is odd num') |
+--------------------------+
| 1 is odd num             |
+--------------------------+
1 row in set (0.00 sec)

+--------------------------+
| concat(x, ' is odd num') |
+--------------------------+
| 3 is odd num             |
+--------------------------+
1 row in set (0.00 sec)
```

## 3.7过程中创建表

```sql
CREATE DEFINER=`root`@`localhost` PROCEDURE `test_table`()
begin
  declare i int default 0;
	drop table if exists test_table;
	create table test_table(
	id int primary key,
	some_data varchar(30)) engine=innodb;
	while i < 10 do 
	insert into test_table values (i, concat('record_', i));
	set i = i + 1;
	end while;
	set i = 5;
	update test_table set some_data = concat('I update row', i) where id = i;
	delete from test_table where id < i;
end
```

## 3.8 into

使用into为x,y 赋值

```sql
drop procedure if exists into_test;
create procedure into_test(in_id int)
begin
	declare x varchar(30);
	declare y varchar(30);
	
	select customer_id, city 
	into x,y
	from customers where customer_id = in_id;
	
	select x, y;
end;
```

## 3.9 游标

变量 i 申明在游标 c 之后，报错

```sql
delimiter $$
drop procedure if exists bad_cursor;
CREATE PROCEDURE bad_cursor( )
BEGIN
DECLARE c CURSOR FOR SELECT * from departments;
DECLARE i INT;
END $$
```

获取多行，loop循环，没有停止的标识，所以报错

```sql
drop procedure if exists cursor_test;
create procedure cursor_test()
begin
	declare x varchar(30);
	declare y varchar(30);
	
	declare c cursor for 
	select customer_id, city 
	from customers;
	
	open c;
	loop
	fetch c into x, y;
	select x,y;
	end loop;
	close c;
end;
```

## 3.10 异常处理

异常处理必须在 cursor 定义之后，否则会报错

```sql
drop procedure if exists cursor_test;
create procedure cursor_test()
begin
	declare x varchar(30);
	declare y varchar(30);
	declare status int default 0;

	declare c cursor for select customer_id, city from customers;
	declare continue handler for not found set status = 1;
	
	open c;
	cursor1: loop
	fetch c into x, y;
	if status = 1 then 
		select 'ersss';
		leave cursor1;
	end if;
	select x,y;
	end loop cursor1;
	close c;
end;
```

异常处理例二

```sql
	
drop procedure if exists sp_add_location;
create procedure sp_add_location(in_location varchar(30),in_address1 varchar(30),
	in_address2 varchar(30),zipcode varchar(10)) 
	begin
		declare l_status int default 0;
		declare continue handler for 1062 set l_status = 1;
		
		insert into locations values (in_location,
		in_address1, in_address2, zipcode);
		
		if l_status = 1 then
		  SELECT CONCAT('Warning: using existing definition for location ', in_location) AS warning;
		end if;
	end;
```

# 3.11 signal

