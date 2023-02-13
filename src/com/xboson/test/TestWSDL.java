/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 18-6-6 下午6:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestWSDL.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.app.lib.ModuleHandleContext;
import com.xboson.app.lib.WebService;
import com.xboson.app.lib.XmlImpl;
import com.xboson.db.DbmsFactory;
import com.xboson.script.lib.JsOutputStream;
import com.xboson.util.CloseableSet;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;


public class TestWSDL extends Test {

  /**
   * 中国股票行情分时走势预览缩略图 WEB 服务
   */
  static String url1 = "http://www.webxml.com.cn/webservices/ChinaStockSmallImageWS.asmx?wsdl";

  /**
   * 天气预报
   */
  static String url2 = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";

  /**
   * IP 地址搜索
   */
  static String url3 = "http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx?wsdl";

  static final String key = "0000";

  /**
   * WSDL 文档参考:
   *   https://www.ibm.com/developerworks/cn/java/j-jws20/index.html
   *   http://www.w3school.com.cn/wsdl/index.asp
   *   替换: http://cxf.apache.org/
   */
  public static void main(String[] as) throws Exception {
    new TestWSDL();
  }


  @Override
  public void test() throws Throwable {
    new ModuleHandleContext().init();
    ModuleHandleContext.register(ModuleHandleContext.CLOSE, new CloseableSet());

    wsdl(url3);
    call2();
    callkey();
  }


  public void wsdl(String url) throws Exception {
    sub("Parse WSDL from URL", url);
    WebService ws = new WebService();
    Map o = ws.wsdl(url);
    msg(Tool.beautifyJson(Object.class, o));
    save(o);
  }


  public void save(Map o) throws Exception {
    sub("Save WSDL function setting");

    Map mod  = (Map) ((Map) o.get("modules")).get("IpAddressSearchWebServiceSoap");
    Map func = (Map) ((Map) mod.get("functions")).get("getCountryCityByIp");

    String funcSetting = Tool.beautifyJson(Object.class, func);
    msg(funcSetting);

    String sql = "Insert Into sys_webservice (" +
            " wsid, wsname, wsnote, ws_mod_name, ws_func_name, " +
            " ws_uri, ws_config_json, createdt, updatedt) " +
            " values (?,?,?,?,?,?,?, now(), now())" +
            " ON DUPLICATE KEY UPdate updatedt = values(updatedt)";

    try (Connection conn =
      DbmsFactory.me().open(SysConfig.me().readConfig().db)) {
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, key);
      ps.setString(2, "测试");
      ps.setString(3, func.get("doc").toString());
      ps.setString(4, mod.get("name").toString());
      ps.setString(5, func.get("name").toString());
      ps.setString(6, func.get("curl").toString());
      ps.setString(7, funcSetting);
      ps.execute();
    }
  }


  public void callkey() throws Exception {
    sub("Call SOAP Function from KEY");
    WebService ws = new WebService();
    WebService.WSConnection conn = ws.connection(key);
    conn.connect();

    XmlImpl.XmlTagWriter func = conn.buildFunctionCall();
    func.tag("theIpAddress").text("182.201.178.62");

    XmlImpl.TagStruct ret = new XmlImpl().parse(conn.openInput());
    msg("XML:", new XmlImpl().stringify(ret));
  }


  public void call2() throws Exception {
    sub("Call SOAP Function");
    WebService.DEBUG = true;
    WebService.WSConnection conn = new WebService().connection(
            "http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx",
            "getCountryCityByIp",
            "http://WebXml.com.cn/");

    // conn.setVersion(10);
    JsOutputStream out = conn.connect();
    XmlImpl.XmlTagWriter func = conn.buildFunctionCall();
    func.tag("theIpAddress").text("182.201.178.62");

    XmlImpl.TagStruct ret = new XmlImpl().parse(conn.openInput());
    msg("Return Message:", Tool.beautifyJson(Object.class, ret));
    msg("XML:", new XmlImpl().stringify(ret));
  }

}
