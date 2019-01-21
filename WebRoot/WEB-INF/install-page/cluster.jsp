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
        <h1>集群设置</h1>
      </div>

      <div id='right'>
        <h2>xBoson 大数据平台</h2>

        <section>
          <p>同一个集群中的运算节点 ID 不能重复, 有效值为 0~1023, 默认为 0.</p>
          <p>集群中所有会话密钥必须统一, 否则登录失败/RPC无法工作.</p>
          <p>如果运算节点位于网关后面, 本机地址应该设置为网关的地址,<br/>
             多个本机 IP 地址用 ',' 逗号分隔.</p>
             RPC 需要动态创建随机的 TCP 端口, 必要时需启用 UPnP 支持; <br/>
        </section>

        <form method="POST">
          <input type="hidden" name="next" value="1"/>
          <h6>Core DBMS</h6>

          <table>
          <tr>
            <td>运算节点ID:</td><td>
              <input name="clusterNodeID" value="${ param.clusterNodeID }" />
              <a href='#' val='0' setto='[name=clusterNodeID]'>(单机)</a></td>
          </tr>

          <tr>
            <td>集群会话密钥:</td><td>
              <input name="sessionPassword" value="${ sessionPassword }" />
            </td>
          </tr>

          <tr>
            <td>RPC 端口:</td><td>
              <input name="rpcPort" value="${ param.rpcPort }" />
            </td>
          </tr>

          <tr>
            <td>本机地址:</td><td>
              <input name="rpcIp" value="${ param.rpcIp }" />
            </td>
          </tr>

          <tr>
            <td>RPC-UPnP 支持:</td><td>
              <select name="rpcUpnp" value="${ param.rpcUpnp }">
                <option value='0'>禁用</option>
                <option value='1'>启用</option>
              </select>
            </td>
          </tr>

          <tr><td></td>
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