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



# 5 插入数据

```sql
insert into students values(41048101, 'Lucy Green', '1', '1990-02-14');
```


