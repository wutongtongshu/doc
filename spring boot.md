# 1. spring boot入门

## 1.1. srping boot简介

### 1.1.1. 微服务概念

**martinflower**：微服务是一种构架风格，应用是小型服务，通过http方式互通。最终每个服务可独立部署，升级。

## 1.2. hello world

### 1.2.1 手工创建spring boot

只需要下面三个包，即可生成一个web程序：

``` xml
<!-- Inherit defaults from Spring Boot -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.0.BUILD-SNAPSHOT</version>
</parent>

<!-- mvc依赖 -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

<!--这个插件将工程打包为可执行jar包-->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 1.2.2 spring initializer自动导入

需要联网，生成resource文件

- static jss, js , 
- templates 可以使用模板引擎

## 1.3 spring boot启动分析

 在POM文件中添加 spring-boot-starter-parent 依赖，用来管理版本依赖。它的父项目是 spring-boot-dependencies 而在它的 POM 的 properties 标签中，定义了基本上所有的依赖包的版本信息，故导入依赖一般不需要再写版本信息了。spring-boot-starter-web 这个是支持web的starts，我们把starts叫做启动器，在 [spring官方文档][https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-starters/spring-boot-starter-web/pom.xml]上的 starts 清晰介绍了各starts的作用。

### 1.3.1. @SpringBootApplication

这个表示 spring boot 的启动类，组合

```java
@SpringBootConfiguration
@EnableAutoConfiguration
    @AutoConfigurationPackage 导入spring boot主配置类所在包及其子包
    @Import({AutoConfigurationImportSelector.class}) 选择autoconfigration
    根据这个pring-boot-autoconfigure-2.0.1.RELEASE.jar\META-INF\spring.factories文件的配置
    通过类加载器，自动加载配置类。
@ComponentScan
   
```

## 1.4. 配置

### 1.4.1. 配置文件

```json
person:
lastName: hello
age: 18
boss: false
birth: 2017/12/12
maps: {k1: v1,k2: 12}
lists:
‐ lisi
‐ zhaoliu
dog:
name: 小狗
age: 12
```

```java
@Component
@ConfigurationProperties(prefix = "person")
public class Person {
private String lastName;
private Integer age;
private Boolean boss;
private Date birth;
private Map<String,Object> maps;
private List<Object> lists;
private Dog dog;
```

**占位符**

```properties
person.last‐name=张三${random.uuid}
person.age=${random.int}
person.birth=2017/12/15
person.boss=false
person.maps.k1=v1
person.maps.k2=14
person.lists=a,b,c
person.dog.name=${person.hello:hello}_dog 若person.hello这个属性不存在，取默认值hello
person.dog.age=15
```

### 1.4.2. 导入xml 配置bean

```java
@ImportResource(location={xx.xml,yy.xml})
```

### 1.4.3. 多配置文件

```json
server:
	port: 8081
spring:
	profiles:
		active: prod #激活生产环境
#文档块1
‐‐‐
server:
	port: 8083
spring:
	profiles: dev

#文档块2
‐‐‐
server:
p	ort: 8084
spring:
	profiles: prod #指定属于哪个环境
```

### 1.4.4 多配置文件激活方式

- spring.profiles.active：yaml配置文件方式

- 在IDEA启动时，配置 Program arguments 项值为 --spring.profiles.active=xxx。或者将VM options的值设置为

  -Dspring.profiles.active=xxx

- 在cli工具下使用 java -jar xxxx.jar  --spring.profiles.active=yyy

### 1.4.5. 主配置文件位置

优先级由高到低，依次扫描，高优先级覆盖低优先级。

- 项目根目录/config

- 项目根目录

- resources/config
- resources

可以使用cli工具下使用 java -jar xxxx.jar  --spring.config.location=G:\xxx.properties

这样原包内配置也会起作用。但是location指定的配置文件优先级更高。

**命令行cli环境下，跟xxx.jar放一个文件夹**：优先级从高到低：

- jar包外部带spring.profile配置文件
- jar包内部带spring.profile配置文件
- jar包外部不带spring.profile配置文件
- jar包内部不带spring.profile配置文件

## 1.5. @condition注解匹配 

**开启自动配置报告：**

debug = true





# 2. 包作用

## 2.1 spring-boot-configuration-processor

编辑属性时，自动提示

# 3. 数据访问

## 3.1 jdbc

两个依赖分别为 spring-boot-starter-jdbc 原生jdbc整合，mysql-connector-java提供驱动。

## 3.2. mybatis 

mybatis-spring-boot-starts自己适配的，mysql-connector-java提供驱动。两就够了。

# 4. eureka 

发音：[juˈri:kə]

## 4.1. 配置

eureke:

​	instance:

​		hostname:localhost   #eureka实例名称

​	client:

​		register-with-eureka:false  #不注册，因为是server，不用注册

​		fetch-registry:false  #不用获取注册信息，因为是Server

​		service-url:

​			defaultZone: http://${euraka.instance.hostname}:${port}/euraka

# 5. run dashboard

```xml
<component name="RunDashboard">
  <option name="configurationTypes">
    <set>
      <option value="SpringBootApplicationConfigurationType" />
    </set>
  </option>
  <option name="ruleStates">
    <list>
      <RuleState>
        <option name="name" value="ConfigurationTypeDashboardGroupingRule" />
      </RuleState>
      <RuleState>
        <option name="name" value="StatusDashboardGroupingRule" />
      </RuleState>
    </list>
  </option>
</component>
```

