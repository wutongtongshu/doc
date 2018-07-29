# 两个关键概念

project和task是gradle的两个关键概念。一个gradle的project由多个task组成，task是可以单独被执行

gradle taskName

# gradlew

提供的windwos下的工具，是对gradle命令的封装，使用 gradle或者gradle warpper命令后，会产生wapper文件夹，下面有两个文件gradle-wrapper.properties是对gradle的封装。

distributionBase=GRADLE_USER_HOME  解压后基础目录
distributionPath=wrapper/dists 解压后目录
distributionUrl=file:///D:/tools/gradle-4.9-bin.zip  gradle的下载地址，这里配置的是本地
zipStoreBase=GRADLE_USER_HOME  下载时的基础目录
zipStorePath=wrapper/dists  下载后的解压目录