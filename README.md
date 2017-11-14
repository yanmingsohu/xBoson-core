# 大数据平台 v2

Java JDK 8, Tomcat 9


# JAR 依赖

* (解析/封装 JSON - moshi-1.5.0.jar)[https://github.com/square/moshi]  
		-- (moshi-kotlin-1.5.jar)  
		-- (okio-1.13.0.jar)  
		
* (控制台颜色输出 - jansi-1.16.jar)[https://github.com/fusesource/jansi]  

* (Redis 客户端 - jedis-2.9.0.jar)[https://github.com/xetorthio/jedis]  
    -- (commons-pool2-2.4.3.jar)  
    
* (数据库驱动)
    -- (mysql-connector-java-5.1.44-bin.jar)[https://dev.mysql.com/downloads/connector/j/]
    -- (db2jcc4.jar)[http://www-01.ibm.com/support/docview.wss?uid=swg21363866]
    -- (mssql-jdbc-6.2.2.jre8.jar)[https://docs.microsoft.com/en-us/sql/connect/sql-connection-libraries]
    -- (ojdbc7.jar)[http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html]
    -- (mpp-connector-java-8.3.81.51-build-53.2-bin.jar)[华三数据库]
    -- (inspur11-jdbc.jar)[浪潮数据库]
    -- (h2-1.4.196.jar)[http://h2database.com/html/main.html]
		
# URL 映射约定

get 请求使用标准的 http url 参数.

post 请求忽略 url 参数, 解析 body 中存放的 json.


## /app

脚本接口

`/app/APP ID/机构ID/模块ID/API?参数`

当请求参数中有 jsonp=functionname, 则返回 jsonp 格式的数据, 
并使用 functionname 来作为回调函数.


# 配置文件 

系统配置文件将生成在:

  `/HOME/xBoson-config/config.json`
  
日志级别配置文件生成在:

  `/HOME/xBoson-config/log.level.properties`
  
  
## 日志级别配置文件:

  日志级别配置使用 properties 属性表, 每个配置一行.
  每行表示一个类或命名服务的日志级别, 允许单独配置每个类的级别,
  日志的最终级别是全局级别与类级别中最小的.

```properties
#LogFactory Config From xBoson.
#Tue Nov 14 21:13:11 CST 2017

com.xboson.event.GlobalEvent=ALL
Event\:\:sys.error=ALL
com.xboson.j2ee.container.UrlMapping=ALL
/check-env.js=ALL
```
  
  
## 系统配置字段说明:

  配置文件使用 json 格式, 注意字符串使用引号包围, 最后元素的结尾不能有 '.' 符号,
  一下的注释使用 /* */ 包围, 但是 json 格式并不支持注释的使用, 所以不要复制到配置文件中.
  
```json
{
    /* 配置文件版本号, 随着系统升级而升级 */
  "configVersion": "1.3",
  
    /* 日志过滤级别: ALL | OFF | DEBUG | INFO | WARN | ERROR | FATAL */
  "logLevel": "all",
    /* 日志输出类型, 可选的: ConsoleOut | FileOut | FileAndConsoleOut */
  "loggerWriterType": "ConsoleOut",
    /* redis 所在主机 */
  "redis_host": "localhost",
    /* session 密钥, 集群中所有节点必须相同 */
  "sessionPassword": "/gWMJ+cbYOixLiuElBV7Vxll3sM",
    /* session 超时时间, 分钟 */
  "sessionTimeout": 30,
    /* HTTP 服务接口, 是否输出错误堆栈 */
  "debugService": false,
  
  /* 数据库连接池的设置 */
  "dbpool": {
      /* 默认值 true ,当资源耗尽时,是否阻塞等待获取资源 */
    "blockWhenExhausted": true,
    "evictionPolicyClassName": "org.apache.commons.pool2.impl.DefaultEvictionPolicy",
    "evictorShutdownTimeoutMillis": 10000,
    "fairness": false,
    "jmxEnabled": true,
    "jmxNamePrefix": "pool",
    "lifo": true,
    "maxIdlePerKey": 8,
      /* 允许创建资源的最大数量,默认值 8,-1 代表无数量限制 */
    "maxTotal": 2000, 
    "maxTotalPerKey": 8,
      /* 获取资源时的等待时间,单位毫秒.当 blockWhenExhausted 配置为 true 时,
         此值有效. -1 代表无时间限制,一直阻塞直到有可用的资源. */
    "maxWaitMillis": 3000,
    "minEvictableIdleTimeMillis": 1800000,
    "minIdlePerKey": 0,
      /* 资源回收线程执行一次回收操作,回收资源的数量.默认值 3 */
    "numTestsPerEvictionRun": 99,
    "softMinEvictableIdleTimeMillis": -1,
      /* 默认值 false ,当设置为true时, 每次从池中获取资源时都会调用
         validateObject() 方法*/
    "testOnBorrow": true,
    "testOnCreate": false,
    "testOnReturn": false,
      /* 设置为 true 时, 当回收策略返回 false,
         则调用 activateObject() 和 validateObject() */
    "testWhileIdle": true,
      /* 回收资源线程的执行周期,单位毫秒.默认值 -1 ,-1 表示不启用线程回收资源 */
    "timeBetweenEvictionRunsMillis": 3600000
  },
  
  /* redis 连接池的设置, 部分设置与 dbpool 相同 */
  "jedispool": {
    "blockWhenExhausted": true,
    "evictionPolicyClassName": "org.apache.commons.pool2.impl.DefaultEvictionPolicy",
    "evictorShutdownTimeoutMillis": 10000,
    "fairness": false,
    "jmxEnabled": true,
    "jmxNamePrefix": "pool",
    "lifo": true,
    "maxIdle": 10,
    "maxTotal": 200,
    "maxWaitMillis": -1,
    "minEvictableIdleTimeMillis": 60000,
    "minIdle": 0,
    "numTestsPerEvictionRun": -1,
    "softMinEvictableIdleTimeMillis": -1,
    "testOnBorrow": false,
    "testOnCreate": false,
    "testOnReturn": false,
    "testWhileIdle": true,
    "timeBetweenEvictionRunsMillis": 30000
  }
}
```
