<%@ page contentType="text/html;charset=UTF8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta content="text/html; charset=utf-8" http-equiv="content-type" />
    <title>xBoson Install</title>
    <style type="text/css">
      <%@ include file="install.css" %>

      .configlist * {
        border-bottom: 1px solid #aaa;
      }
    </style>
    <script><%@ include file="jquery-2.1.4.min.js" %></script>
    <script><%@ include file="install.js" %></script>
    <%! com.xboson.been.Config c; %>
    <% c =  (com.xboson.been.Config) request.getAttribute("config"); %>
  </head>

  <body>
    <div id='contentdiv'>
      <div id='left'>
        <h1>配置完成</h1>
      </div>

      <div id='right'>
        <h2>配置文件</h2>

        <section class='configlist'>
          <div>文件路径: <%=c.configFile %></div>
          <div>ROOT 帐号: <%=c.rootUserName %></div>
          <div>DB: <%=c.db.getDbname() %> 
            / <%=c.db.getHost() %> / <%=c.db.getDatabase() %></div>
          <div>Redis: <%=c.redis.getHost() %> / <%=c.redis.getPort() %></div>
          <div>Log Level: <%=c.logLevel %> </div>
          <div>UI 目录: <%=c.uiUrl %> </div>
          <div>UI 目录列表: <%=c.uiListDir %> </div>
        </section>

        <form method="POST">
          <input type="hidden" name="next" value="1"/>
          <h6>配置已经就绪, 准备重置服务器</h6>

          <section>
            <p>
              <input type="checkbox" name="act" value="restart"/>应用配置并重启服务器
              <br/>  
              <input type="checkbox" name="act" value="reconfig"/>重新配置
            </p>
            <input type='submit' value='确定' />
                <span class="red"><%=request.getAttribute("msg") %></span>
          </section>
        </form>
      </div>

    </div>
  </body>
</html>