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
        <h1>版权信息</h1>
      </div>

      <div id='right'>
        <h2>xBoson 大数据平台, 版权信息</h2>

        <section>
          Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
          本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
          必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
          的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
          由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
        </section>

        <form method="POST">
          <input type="hidden" name="next" value="1"/>

          <h6>接受协议中的条款</h6>

          <section>
            <input type='checkbox' name="copyright" value='yes'/>我接受本条款, 并受到法律约束.
            <br/><br/>
            <input type='submit' value='下一步'/>
            <span class="red"><%=request.getAttribute("msg") %></span>
          </section>
        </form>
      </div>

    </div>
  </body>
</html>