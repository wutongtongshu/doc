# 1 目录结构

![](https://github.com/wutongtongshu/doc/raw/master/tomcat/%E7%9B%AE%E5%BD%95%E7%BB%93%E6%9E%84.png)





# 2 容器配置

## 2.1 context

**配置属性**

- path: 访问该 web 应用的 url 入口
- docBase: 应用文件路径，可以给绝对路径。也可以是相对于host的路径，使用appBase属性来设置。web应用有两种布局，一种是开放式，另外一种是打war包，默认在部署时会解压，也可以不解压。开放式docBase是根目录，war形式是war的路径。
- className: 这是 context 的全类名
- reloadable: 对web.xml、class、lib进行监视，有变化重新加载web应用
- cachingAllowed: 静态资源缓存
- cacheMaxSize: 最大缓存容量，k为单位
- workDir：应用的工作目录，临时文件全放这里
- uppackWar: war是否解压运行

**context 文件加载位置**

- catalina_base/config/context.xml
- catalina_base/config/[enginename]/[hostname]/context.xml.default全部应用一块用
- catalina_base/config/[enginename]/[hostname]/apppath.xml，apppath是应用的入口，应用自用
- META-INF/context.xml应用自用
- catalina_base/config/server.xml

# 3 调用链

![](https://github.com/wutongtongshu/doc/raw/master/tomcat/%E8%87%AA%E5%AE%9A%E4%B9%89servlet%E8%B0%83%E7%94%A8%E6%B5%81%E7%A8%8B.png)