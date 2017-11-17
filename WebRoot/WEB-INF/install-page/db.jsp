<%@ page contentType="text/html;charset=UTF8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta content="text/html; charset=utf-8" http-equiv="content-type" />
    <title>xBoson Install</title>
    <style type="text/css">
      <%@ include file="install.css" %>
    </style>
    <script><%@ include file="jquery-2.1.4.min.js" %></script>
    <script><%@ include file="install.js" %></script>
    <%! com.xboson.been.Config c; %>
    <% c =  (com.xboson.been.Config) request.getAttribute("config"); %>
  </head>

  <body>
    <div id='contentdiv'>
      <div id='left'>
        <h1>数据库</h1>
      </div>

      <div id='right'>
        <h2>xBoson 大数据平台</h2>

        <section>
          平台需要一个 Mysql 数据库作为内核存储库, 请设置一个长期稳定的 Mysql 服务器连接.
          Mysql 版本在 5.0 以上.
        </section>

        <form method="POST">
          <input type="hidden" name="next" value="1"/>
          <h6>Core DBMS</h6>

          <table>
          <tr>
            <td>DB地址:</td><td><input name="host" value="${ param.host }" /></td>
          </tr><tr>
            <td> DB端口: </td>
            <td><input name="port" value="${ param.port }" /> (3306)</td>
          </tr><tr>
            <td> DB类型: </td><td>
              <select name='dbname'>
                <option>mysql</option>  
              </select>
              </td>
          </tr><tr>
            <td> DB用户: </td><td><input name="username" value="${ param.username }"/></td>
          </tr><tr>
            <td> DB密码: </td><td><input name="password" 
              value="${ param.password }" type="password" /></td>
          </tr>
          <tr>
            <td> scheme: </td><td><input name="database" value="${ param.database }"/></td>
          </tr>
          </tr><tr><td></td>
            <td> <input type='submit' value='确定' />
              <span class="red"><%=request.getAttribute("msg") %></span>
            </td>
          </tr>
          </table>
        </form>
      </div>

    </div>
  </body>
</html>