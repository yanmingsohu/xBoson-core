# 大数据平台 v2

Java JDK 8, Tomcat 9


# 依赖

* (解析/封装 JSON - moshi-1.5.0.jar)[https://github.com/square/moshi]  
		-- (moshi-kotlin-1.5.jar)  
		-- (okio-1.13.0.jar)  
		
* (控制台颜色输出 - jansi-1.16.jar)[https://github.com/fusesource/jansi]  

* (Redis 客户端 - jedis-2.9.0.jar)[https://github.com/xetorthio/jedis]  
    -- (commons-pool2-2.4.3.jar)  
	
		
# URL 映射约定

get 请求使用标准的 http url 参数.

post 请求忽略 url 参数, 解析 body 中存放的 json.


## /app

脚本接口

`/app/APP ID/机构ID/模块ID/API?参数`

当请求参数中有 jsonp=functionname, 则返回 jsonp 格式的数据, 并使用 functionname 来作为回调函数.


# 配置文件 

`/HOME/xBoson-config/config.json`

## loggerWriterType : String

日志输出类型, 可选的: ConsoleOut | FileOut | FileAndConsoleOut

## sessionTimeout : Int

session 超时时间, 分钟

## sessionPassword : String

session 密钥, 集群中所有节点必须相同

## logLevel : String

日志过滤级别: ALL | OFF | DEBUG | INFO | WARN | ERROR | FATAL