# 大数据平台核心 v2

该平台的设计目的是当软件项目进入开发阶段时, 首先部署平台, 然后业务人员进行持续开发, 
没有更多的部署/实施步骤, 当测试完成后, 应用立即切入生产环境, 并对新需求和错误进行快速响应.   
平台使用了内置的 javascript 引擎, 并用包装器把脚本环境设置的类似 nodejs, 
一个最简单的服务接口脚本只需要如下几行:

```
"use strict";
sql.query("Select 'Hello World !' message", null, 'ret1');
sys.setRetData(0, sys.result.ret1);
```

业务开发人员只要专注于业务, 而接口权限判定/接口日志/错误处理/DB连接/集群问题等都由平台处理.

只有一个平台核心是做不了什么的, 所以我们开发了一系列应用, 这些应用由服务接口和Web画面组成,
服务接口在导入DB后配置到平台, Web画面则在另一个项目中, 这些应用实现了最基础的功能: 用户登录/注册,
动态菜单, 权限相关, 开发IDE等, 用户也可以以这些基础功能为脚手架开发自己的基础接口和画面.


该项目由 [上海竹呗信息技术有限公司](https://xboson.net/) 提供技术支持.


# 在平台上实现并开源的(部分)项目

* 在线开发服务接口 IDE, 前端 IDE
* 数据源/数据集/元数据管理/值域代码/多维模型, 支持多种DB同时接入
* 用户权限/用户组/机构管理
* 数据透视管理器, 把接口数据可视化
* 集成 mxGraph 流程图工具.
* 基于区块链引擎实现的区块链网盘, 和用于说明原理的波子币.
* 基于物模型的物联网数据中心, 用 apache-artemis 实现数据中转.
* 分布式 OPC 节点控制中心.
* 基于 neo4j 实现的知识图谱业务开发系统.
* 基于 Vue2 的大屏展示系统, 基于 Vue2 的前端低代码开发工具.


# 2.0 新特性

* 图形化的系统安装.
* 可与区块链网络交换数据, 签订智能合约.
* 完整的 Javascript ES5 脚本, 新的脚本引擎支持通用库/通用设置的写法.
* 脚本将被编译在内存中, 直到脚本被修改后自动重新编译.
* 可安装/调用 NPM 模块, 并脱离 Nodejs 运行.
* UI 服务使用平台脚本重写, 并加入修改履历. (不再依赖 nodejs 服务).
* UI 文件不再依赖操作系统提供的网络文件系统进行集群同步, 也不依赖第三方中间件.
* 针对大数据开发的 JEE 超轻量高速框架.
* 快如闪电的 json/xml 转换器.
* 新的内核模块: API 进程管理, 集群管理, 计划任务管理, MongoDB 驱动; 全部可在脚本中调用.
* 模块化可扩展的日志系统, 慢速日志不再依赖第三方中间件.
* 智能数据源, 不再需要设置数据源微调参数.
* 集群使用通用消息总线来发送通知或数据.
* 集群间节点使用 RPC 来管理内部事务, 第三方插件可以利用 RPC 进行内核扩展.
* API 脚本中可以调用操作系统外壳脚本.
* ui 目录 / node 模块目录 / 临时文件目录, 映射到虚拟文件系统中 (支持集群).
* 虚拟文件系统通过 api 开放给脚本, 直接在脚本中操作文件.
* 第三方使用 OAuth 2.0 接入平台.
* 当内存不足, 内核会卸载已经编译的 api 来释放紧张的内存.
* 与业务紧密结合的区块链系统, 0部署, 0维护, 开箱即用.
* 可用 Hadoop 保存大数据


# 注意:

* java 版本 >= JDK 8u111
* JEE 容器必须以 UTF-8 模式启动 `set JAVA_OPTS=-Dfile.encoding=UTF-8`.
* sys_mdm001.url 字段与平台服务接口绑定.
* 需要把 `crypto.dll` 文件复制到 `java.library.path` 指向的目录中 (jdk/bin 目录).
* SqlServer 2012 之后的版本支持分页查询.
* 使用 `gradle` 构建/发布混淆后的 war 包.
* 配置文件中 `rootUserName: admin-pl`, 
  `rootPassword: 861170a039539136e605744dbbeb81e607173d1aa8e94bac1f0db9ba77ec88fc` 
  方可启用超级用户
* 若要启用区块链服务器, 必须有 0 号节点.
* RPC 为动态端口.
* 本项目的 jar 依赖在另一个项目中定义.


Java JDK, Tomcat 9, Servlet 4.0.  

经过测试的 JAVA 版本: 
  * OracleJDK 1.8.0_172, 1.8.0_162, 1.8.0_111
  * OpenJDK 1.8.0_151
  
待测试测 Java 版本:
  * OpenJDK 11.2 - 失败, javax.activation/javax.xml.bind 类已经从 jdk 移除.


# 需要做:

* 分布式投票算法 (http://www.cnblogs.com/smartloli/p/7190360.html)(https://raft.github.io/)
* 多语言
* js 字典模块
* org 参数只作为 mysql schema 选择条件, 并从 api 加载流程中分离; app 直接作为根选择条件,
  因为 app 是不会重复的 (XjOrg 和 XjApp 解绑, 表 sys_apps 没有和机构做关联).
* 机构自己的模板目录
* 升级 js 引擎到 [graaljs](https://github.com/graalvm/graaljs) 
* 统计分析模块 -api完成 +wiki +画面
* docker 管理模块.
* 打印模块


# JAR 依赖

## 解析/封装 JSON
  
  1. [moshi-1.5.0.jar](https://github.com/square/moshi)  
  1. [moshi-kotlin-1.5.jar]  
  1. [okio-1.13.0.jar]  
      
## 控制台颜色输出
  
  1. [jansi-1.16.jar](https://github.com/fusesource/jansi)  

## Redis 客户端
  
  1. [jedis-2.9.0.jar](https://github.com/xetorthio/jedis)  
  1. [commons-pool2-2.4.3.jar]  
    
## 数据库驱动
  
  1. [mysql-connector-java-5.1.44-bin.jar](https://dev.mysql.com/downloads/connector/j/)
  1. [db2jcc4.jar](http://www-01.ibm.com/support/docview.wss?uid=swg21363866)
  1. [mssql-jdbc-6.2.2.jre8.jar](https://docs.microsoft.com/en-us/sql/connect/sql-connection-libraries)
  1. [ojdbc7.jar](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html)
  1. [mpp-connector-java-8.3.81.51-build-53.2-bin.jar](华三数据库)
  1. [inspur11-jdbc.jar](浪潮数据库)
  1. [h2-1.4.196.jar](http://h2database.com/html/main.html)
      
## XML 转换

  1. [xstream-1.4.10-java7.jar](http://hmkcode.com/xstream-java-object-xml/)
  1. [xmlpull-1.1.3.1.jar]
  1. [xpp3_min-1.1.4c.jar]
    
## CSV 转换

  1. [super-csv-2.4.0.jar](https://github.com/super-csv/super-csv)
    
## HTTP 客户端

  1. [okhttp-3.9.1.jar](http://square.github.io/okhttp/)
    
## MongoDB 客户端
  
  1. [bson-3.6.1.jar](http://bsonspec.org/)
  1. [mongodb-driver-3.6.1.jar](http://mongodb.github.io/mongo-java-driver/)
  1. [mongodb-driver-core-3.6.1.jar]
      
## Microsoft Excel 报表

  1. [poi-3.17.jar](https://poi.apache.org/)
    
## YAML 转换

  1. [yamlbeans-1.13.jar](https://github.com/EsotericSoftware/yamlbeans/)
    
## 软件授权许可

  1. [xBoson-crypto.jar]
    
## [Hyperledger-Fabric 区块链](http://hyperledger-fabric.readthedocs.io/en/release/)

  1. [fabric-sdk-java-*-with-dependencies.jar] sdk 及若干依赖库集合
    
## 文件上传解析

  1. [commons-fileupload-1.3.3.jar](http://commons.apache.org/proper/commons-fileupload/]
    
## WebService 组件
  
  1. [wsdl4j.jar]
  1. [qname.jar]
    
## [MapDB 存储区块的数据库](https://github.com/jankotek/mapdb)

  1. [mapdb-3.0.8-SNAPSHOT-jar-with-dependencies.jar) 依赖库集合
    
## 编译依赖/脱离 servlet 容器运行库
  
  1. [jetty-all-9.4.14.v20181114-editor.jar]
  1. [org.eclipse.jdt.ecj-3.14.0.jar]
  1. [org.eclipse.jetty.apache-jsp-9.4.14.v20181114.jar]
  1. [org.mortbay.jasper.apache-el-8.5.33.1.jar]
  1. [org.mortbay.jasper.apache-jsp-8.5.33.1.jar]
  1. [jsp-api.jar]
  1. [servlet-api.jar]
    
## UPnP 支持

  1. [weupnp-0.1.4.jar](https://github.com/bitletorg/weupnp)
    
## Docker 客户端

> 作为参考 - 对接 docker 完成后删除

  1. [docker-client-8.14.5.jar](https://github.com/spotify/docker-client)
  1. [Java API 文档](https://github.com/spotify/docker-client/blob/master/docs/user_manual.md)
  1. [docker 原生 API](https://docs.docker.com/engine/api/v1.39/)
  1. [生成安全连接](https://docs.docker.com/engine/security/https/)
      
## Hadoop

  1. [hadoop-common-3.1.3-with-dependencies.jar] 合并了 
      hadoop-3.1.3\share\hadoop\common 目录中的 jar 文件 和其下 lib 中的文件.
      
## Kafka (TODO)

  1. [中文文档](https://kafka.apachecn.org/intro.html), [英文文档](http://kafka.apache.org/documentation/#topicconfigs), [API](http://kafka.apache.org/26/javadoc/index.html), [控制mianb](https://github.com/yahoo/CMAK)
  
## 线性代数

  1. [首页](http://jblas.org/)
  1. [源码](https://github.com/jblas-project/jblas)
  1. [BLAS](https://www.netlib.org/blas/)
  
linux 需要:

  `sudo yum install libgfortran.x86_64`


# URL 映射约定

get 请求使用标准的 http url 参数.


## /app

脚本接口

`/app/机构ID/APP ID/模块ID/API?参数`

当请求参数中有 `$format=jsonp cb=functionname`, 则返回 jsonp 格式的数据, 
并使用 functionname 来作为回调函数.


关于脚本兼容:

  1. "use strict" 为 js 严格模式, 严格模式完全遵守 js 语法规范.
  2. 非严格模式中, `for (v in list)` v 是对象/数组中的元素值, v__index 是索引(附加变量).
  3. 非严格模式中, `@list.add(...)` 相当于调用 Java 中 List.add(). 
  4. 非严格模式中, `list.~size` 调用预定义的虚拟属性. 
  5. 严格模式必须把 "use strict" 声明在第一行.
  6. date 模块中的函数可以在 sys 中直接调用.
  7. java 内部类必须是公共的, 脚本中才能访问其中方法; 接口默认实现函数不能在脚本中调用.


### 特殊参数, "$format"

平台接口接收该参数来改变输出数据的格式. 

  * 'json' 返回 json 格式的参数, 默认值.
  * 'xml' 返回 xml 格式的参数.
  * 'jsonp' 同时必须提供 cb 参数.
  
  
## /face/*

画面接口, GET 读取文件, url 其他部分为文件的访问路径, 映射到本地文件系统.


## /files/[sub path]

上传下载接口.

路径生成规则:

* 当用户登录一级目录为用户 id, 否则为 temporary;
* 二级目录为 servlet 服务路径之后的路径字符串 [sub path], 
* 最终路径 = /[一级目录]/[二级目录]/[文件]
 
## POST 上传文件

  上传时使用 `multipart/form-data` 的表单, 可以上传多个文件, 返回文件的完整路径和名称.
  对请求参数没有要求, 
  
## GET 下载文件

  下载时需要提供 HTTP 参数: file_name 文件名; 路径使用 `路径生成规则` 生成,
  如果设置了 page=1 则忽略 file_name 参数, 并返回一个测试页, 用于上传文件用; 
  
  返回时, 将文件类型放在 Content-Type 的 http 头域, 整个文件放在 http body 中返回; 
  找不到文件返回 http 404 错误, 失败返回 500 body 放入失败的 json 应答.


# 配置文件 

系统配置文件将生成在:

  `/HOME/xBoson-config/config.json`
  
日志级别配置文件生成在:

  `/HOME/xBoson-config/log.level.properties`
  
mysql 配置:
  
  如果出现中文变成 '???', 增加 mysql 配置文件项目到配置文件中:
  `character_set_server=utf8`
  
  如果登录页面抛出 sql 执行异常, 修正配置文件:
  `sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES`
  
  
## 日志级别配置文件:

  日志级别配置使用 properties 属性表, 每个配置一行.
  每行表示一个类或命名服务的日志级别, 允许单独配置每个类的级别,
  如果是继承的, 则使用全局级别, 否则使用日志自己的级别.

```properties
#
#LogFactory Config From xBoson.
#Tue Nov 14 21:13:11 CST 2017
#
com.xboson.event.GlobalEventBus=ALL
Event\:\:sys.error=OFF
com.xboson.j2ee.container.UrlMapping=DEBUG
/check-env.js=INHERIT
```

日志级别可选项:

  * INHERIT | 该级别不能配置给全局, 给日志实例配置该属性, 则使用全局设置的级别.
  * ALL     | 显示全部日志
  * OFF     | 禁止全部日志
  * DEBUG   | 以下日志都启用
  * INFO    | 以下日志都启用
  * WARN    | 以下日志都启用
  * ERROR   | 以下日志都启用
  * FATAL   | 以下日志都启用
  
  
# Mysql 默认配置文件

该文件的完整配置在 `xboson-docker` 项目中.

```ini
[mysqld]
character-set-server = utf8mb4 
join_buffer_size = 512M
sort_buffer_size = 20M
read_rnd_buffer_size = 20M 
sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES
max_connect_errors=1000

# 若上传大文件报错, 则增大这个设置 
max_allowed_packet=100M
# 账户永不过期
default_password_lifetime=0
```

登录帐户过期, 在 sql 控制台登录后执行 `SET GLOBAL default_password_lifetime = 0;`, 这会使安全降级.


# 相关项目

* [xBoson前端WEB, 包含Wiki](https://github.com/yanmingsohu/xBoson-web-basic)
* [xBoson基础数据库](https://github.com/yanmingsohu/xBoson-db-basic)
* [xBoson前端文件渲染节点](https://github.com/yanmingsohu/xBoson-ui-ext)
* [xBoson见证者节点](https://github.com/yanmingsohu/xBoson-witness-enterprise)
* [xBosonIOT数据接入点](https://github.com/yanmingsohu/xBoson-artemis)
* [xBosonJAR依赖](https://github.com/yanmingsohu/xBoson-core-library)
* [ETL/ESB中心控制节点](https://github.com/yanmingsohu/xBoson-conductor)
* [ETL/ESB边缘运算节点](https://github.com/yanmingsohu/xBoson-virtuoso)
  
  
# 外部参考

* [JVM 垃圾收集器调优](http://www.oracle.com/technetwork/java/javase/gc-tuning-6-140523.html)
* [Nashorn 上下文等](https://wiki.openjdk.java.net/display/Nashorn/Nashorn+jsr223+engine+notes)
* [Java 与区块链](https://www.ibm.com/developerworks/cn/java/j-chaincode-for-java-developers/index.html)
* [hyperledger 实现](http://hyperledger-fabric.readthedocs.io/en/latest/blockchain.html)
* [OLTU WIKI](https://cwiki.apache.org/confluence/display/OLTU/Index)
* [OAuth2 规范文档](https://oauth.net/2/)
* [Paxos 的共识算法](https://www.zhihu.com/question/19787937)
* [Raft 共识算法](https://github.com/maemual/raft-zh_cn/blob/master/raft-zh_cn.md)
* [Raft 可视化](https://raft.github.io/)
* [向量标量子程序](https://www.ibm.com/support/knowledgecenter/en/SSFHY8_6.1/reference/am5gr_vecsca.html)
* [MQTT 服务器文档](https://activemq.apache.org/components/artemis/documentation/latest/)
* [K8s 文档](https://kubernetes.io/zh/docs/concepts/overview/)
* [K8s Java 客户端](https://github.com/kubernetes-client/java)
* [延迟和容错库, 用于 api 代理](https://github.com/Netflix/Hystrix)
* [api网关](https://github.com/Kong/kong)
* [MQTT 客户端](https://github.com/eclipse/paho.mqtt.java), [文档](https://www.eclipse.org/paho/files/javadoc/index.html)
* [知识图谱数据库](https://neo4j.com/developer/cypher/)

