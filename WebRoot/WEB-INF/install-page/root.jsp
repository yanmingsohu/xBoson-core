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
        <h1>超级用户</h1>
      </div>

      <div id='right'>
        <h2>xBoson 大数据平台</h2>

        <section>
          设置超级用户权限, 该用户不受权限任何约束, 初始系统的第一个用户;
          您将自己对任何因密码强度不够导致数据泄露, 或引起的商业损失而负责.
        </section>

        <form method="POST">
          <input type="hidden" name="next" value="1"/>
          <h6>超级用户权限</h6>

          <table>
            <tr>
            <td>用户:</td><td> <input name="rootUserName" 
              value="${ param.rootUserName }"/> (最短4个字符) </td>
            </tr>
            <tr>
              <td>密码: </td><td><input name="rootPassword" type="password" 
              value="${ param.rootPassword }"/> (最短6个字符) </td>
            </tr>
            <tr>
              <td>密码: </td><td><input name="again" type="password" /> (重复输入)</td>
            </tr>
            <tr><td></td><td>
              <input type='submit' value='确定' />
              <span class="red"><%=request.getAttribute("msg") %></span>
            </td>
            </tr>
          </table>
        </form>
      </div>

    </div>
  </body>
</html>