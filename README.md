# 大数据平台 v2

Java JDK 8, Tomcat 9, Servlet 4.0.


# 注意:

* Docker: 平台访问整个操作系统的资源, 应该管理 Docker 而非被 Docker 管理.
* 当平台没有完全初始化完成调用文件模块(fs) 的 open 方法会抛出 null 错误, 稍等即可.
* sys_mdm001.url 字段与平台服务接口绑定.


# 需要做:

* blob 保存时直接写入db, 而不在内存堆积.
* 接管 Tomcat.
* 区块链api
* 云盘系统(api已经完成)
* 多语言
* SqlServer 分页, oracle 查询全面测试.
* 程序授权许可系统
* 系统交叉登录(补充到计划任务调用api时的权限)


# 2.0 新特性

* 图形化的安装.
* 模块化可扩展的日志系统.
* 多数据源连接池
* 脚本可处理二进制数据
* 完整的 js es5 脚本.
* 可安装/调用 npm 模块, 并脱离 nodejs 运行.
* ui 目录映射到虚拟文件系统中.
* node 模块目录映射到虚拟文件系统中.
* 临时文件抽象为虚拟文件系统.
* 虚拟文件系统通过 api 开放给脚本.
* ui 服务使用平台脚本重写, 并加入修改日志. (不再依赖 nodejs 服务).
* 集群不再依赖操作系统的网络文件系统.
* 安全的启动/退出机制.
* 针对平台开发的 j2ee 超轻量高速框架.
* 快如闪电的 json/xml 转换器.
* 集群使用通用消息总线来发送通知/数据.
* MongoDB 驱动.
* 新的 API 进程模块, 集群模块, 计划任务模块.


# JAR 依赖

* (解析/封装 JSON)
    -- (moshi-1.5.0.jar)[https://github.com/square/moshi]  
		-- (moshi-kotlin-1.5.jar)  
		-- (okio-1.13.0.jar)  
		
* (控制台颜色输出)
    -- (jansi-1.16.jar)[https://github.com/fusesource/jansi]  

* (Redis 客户端)
    -- (jedis-2.9.0.jar)[https://github.com/xetorthio/jedis]  
    -- (commons-pool2-2.4.3.jar)  
    
* (数据库驱动)
    -- (mysql-connector-java-5.1.44-bin.jar)[https://dev.mysql.com/downloads/connector/j/]
    -- (db2jcc4.jar)[http://www-01.ibm.com/support/docview.wss?uid=swg21363866]
    -- (mssql-jdbc-6.2.2.jre8.jar)[https://docs.microsoft.com/en-us/sql/connect/sql-connection-libraries]
    -- (ojdbc7.jar)[http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html]
    -- (mpp-connector-java-8.3.81.51-build-53.2-bin.jar)[华三数据库]
    -- (inspur11-jdbc.jar)[浪潮数据库]
    -- (h2-1.4.196.jar)[http://h2database.com/html/main.html]
    
* (XML 转换)
    -- (xstream-1.4.10-java7.jar)[http://hmkcode.com/xstream-java-object-xml/]
    -- (xmlpull-1.1.3.1.jar)
    -- (xpp3_min-1.1.4c.jar)
    
* (CSV 转换)
    -- (super-csv-2.4.0.jar)[https://github.com/super-csv/super-csv]
    
* (HTTP 客户端)
    -- (okhttp-3.9.1.jar)[http://square.github.io/okhttp/]
    
* (MongoDB 客户端)
    -- (bson-3.6.1.jar)[http://bsonspec.org/]
    -- (mongodb-driver-3.6.1.jar)[http://mongodb.github.io/mongo-java-driver/]
    -- (mongodb-driver-core-3.6.1.jar)
    
* (Microsoft Excel 报表)
    -- (poi-3.17.jar)[https://poi.apache.org/]
    
* (YAML 转换)
    -- (yamlbeans-1.13.jar)[https://github.com/EsotericSoftware/yamlbeans/]
    
* (Hyperledger-Fabric 区块链)[http://hyperledger-fabric.readthedocs.io/en/release/]
    -- (?)

		
# URL 映射约定

get 请求使用标准的 http url 参数.


## /app

脚本接口

`/app/机构ID/APP ID/模块ID/API?参数`

当请求参数中有 jsonp=functionname, 则返回 jsonp 格式的数据, 
并使用 functionname 来作为回调函数.


关于脚本兼容:

  1. "use strict" 为 js 严格模式, 严格模式完全遵守 js 语法规范.
  2. 非严格模式中, `for (v in list)` v 是对象/数组中的元素值, v__index 是索引(附加变量).
  3. 非严格模式中, `@list.add(...)` 相当于调用 Java 中 List.add(). 
  4. 非严格模式中, `list.~size` 调用预定义的虚拟属性. 
  5. 严格模式必须把 "use strict" 声明在第一行.
  6. date 模块中的函数可以在 sys 中直接调用.


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
#LogFactory Config From xBoson.
#Tue Nov 14 21:13:11 CST 2017

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
  
  
# 参考

* (JVM 垃圾收集器调优)[http://www.oracle.com/technetwork/java/javase/gc-tuning-6-140523.html]
* (Nashorn 上下文等)[https://wiki.openjdk.java.net/display/Nashorn/Nashorn+jsr223+engine+notes]
* (Java 与区块链)[https://www.ibm.com/developerworks/cn/java/j-chaincode-for-java-developers/index.html]
* (hyperledger 实现)[http://hyperledger-fabric.readthedocs.io/en/latest/blockchain.html]