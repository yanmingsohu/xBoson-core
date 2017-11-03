# 大数据平台 v2

Java 8, Tomcat 9


# 依赖

(Moshi - moshi-1.5.0.jar)[https://github.com/square/moshi] -- 解析/封装 JSON
		-- (moshi-kotlin-1.5.jar)
		-- (okio-1.13.0.jar)
		
(jansi-1.16.jar)[https://github.com/fusesource/jansi] -- 控制台颜色输出
	
		
# URL 映射约定

/应用路径/app 映射路径/


# 配置文件 

`/HOME/xBoson-config/config.json`

## loggerWriterType : String

日志输出类型, 可选的: ConsoleOut | FileOut | FileAndConsoleOut

## sessionTimeout : Int

session 超时时间, 分钟

## sessionPassword : String

session 密钥, 集群中所有节点必须相同