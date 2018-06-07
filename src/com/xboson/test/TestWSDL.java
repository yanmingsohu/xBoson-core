////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-6-6 下午6:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestWSDL.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.app.lib.WebService;
import com.xboson.app.lib.XmlImpl;
import com.xboson.script.lib.JsOutputStream;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.*;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestWSDL {

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

  /**
   * WSDL 文档参考:
   *   https://www.ibm.com/developerworks/cn/java/j-jws20/index.html
   *   http://www.w3school.com.cn/wsdl/index.asp
   *   替换: http://cxf.apache.org/
   */
  public static void main(String[] as) throws Exception {
//    wsdl(url3);
//    call("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx");
    WebService ws = new WebService();
    Object o = ws.wsdl(url3);
    Tool.pl(o);

    call2();
  }


  public static void call2() throws Exception {
    WebService.DEBUG = true;
    WebService.WSConnection conn = new WebService().$test(
            "http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx",
            "getCountryCityByIp",
            "http://WebXml.com.cn/");

//    conn.setVersion(10);
    JsOutputStream out = conn.connect();
    XmlImpl.XmlTag func = conn.buildFunctionCall();
    func.tag("theIpAddress").text("182.201.178.62");
    conn.end();
    Tool.pl(out);

    InputStreamReader r = new InputStreamReader(conn.openInput(), IConstant.CHARSET);
    LineNumberReader line = new LineNumberReader(r);

    String str;
    while (null != (str = line.readLine())) {
      Tool.pl(line.getLineNumber(), str);
    }
  }


  static void call(String url) throws Exception {
    URL u = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
    conn.setRequestMethod("POST");
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
    conn.connect();

    OutputStream out = conn.getOutputStream();
    OutputStreamWriter w = new OutputStreamWriter(out, IConstant.CHARSET);
    w.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "     xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
            "  <soap12:Body>\n" +
            //"  <IpAddressSearchWebServiceSoap xmlns=\"http://WebXml.com.cn/\">"+
            "    <getCountryCityByIp xmlns=\"http://WebXml.com.cn/\">\n" +
            "      <theIpAddress>182.201.178.62</theIpAddress>\n" +
            "    </getCountryCityByIp>" +
            //"  </IpAddressSearchWebServiceHttpGet>\n"+
            "  </soap12:Body>\n" +
            "</soap12:Envelope>");
    w.flush();

    InputStream  in  = conn.getInputStream();
    InputStreamReader r = new InputStreamReader(in, IConstant.CHARSET);
    LineNumberReader line = new LineNumberReader(r);
    String str;
    while (null != (str = line.readLine())) {
      Tool.pl(line.getLineNumber(), str);
    }
  }


  static void wsdl(String url) throws Exception {
    Tool.pl("Running ...");
    WSDLFactory fact = WSDLFactory.newInstance();
    WSDLReader reader = fact.newWSDLReader();
    Definition def = reader.readWSDL(url, "");
    Map<Object, String> parsedTypes = types(def);

    Tool.pl("Name spaces:");
    nameSpace(def);
    Tool.pl("DOC URI:", def.getDocumentBaseURI());
    Tool.pl("\nDOC:", getDoc(def));
    Tool.pl(parsedTypes);

    //Tool.pl(def.getService(null));

    //
    // PortTypes 是模块
    //
    Map<QName, PortType> modules = def.getPortTypes();
    for (QName name : modules.keySet()) {
      PortType mod =  modules.get(name);
      Tool.pl(Test.line);
      Tool.pl("\nModule", name.getLocalPart());

      Binding desc = def.getBinding(name);
      Tool.pl("\t URI: ", findInvokeURI(mod, def));
      Tool.pl("\t NS:  ", mod.getQName().getNamespaceURI());
      Tool.pl("\t DOC: ", getDoc(mod));

      //
      // Opertaions 是函数
      //
      List<Operation> oplist = mod.getOperations();
      for (Operation func : oplist) {
        Tool.pl("\n\t\t---------------------------------------------------------------");
        Tool.pl("\t\t Function", func.getName(), "(");

        //
        // Message 是参数集合
        //
        Message inputParams = func.getInput().getMessage();
        printParameter(inputParams, parsedTypes);

        Tool.pl("\t\t ) \n\n\t\t Return:");
        Output ret = func.getOutput();
        printParameter(ret.getMessage(), parsedTypes);

        Tool.pl("\n\t\t DOC:", getDoc(func));
      }

      Tool.pl();
    }
  }


  static void nameSpace(Definition def) {
    Map m = def.getNamespaces();
    for (Object a : m.keySet()) {
      Object b = m.get(a);
      Tool.pl(a, b);
    }
  }


  static String findInvokeURI(PortType mod, Definition def) {
    String modName = mod.getQName().getLocalPart();

    for (Object o : def.getServices().values()) {
      Service srv = (Service) o;
      Port port = (Port) srv.getPorts().get(modName);

      if (port != null) {
        List ex = port.getExtensibilityElements();
        for (Object x : ex) {
          if (x instanceof HTTPAddress) {
            return "<HTTP> "+ ((HTTPAddress) x).getLocationURI();
          }
          if (x instanceof SOAPAddress) {
            return "<SOAP> "+ ((SOAPAddress) x).getLocationURI();
          }
          return "<UNKNOW> "+ x;
        }
      }
    }
    return "<NOT-FOUND>";
  }


  static void printParameter(Message msg, Map<Object, String> types) {
    Map<String, Part> params = msg.getParts();
    //
    // Part 是参数
    //
    for (Part parm : params.values()) {
      QName type = parm.getTypeName();
      if (type == null) {
        type = parm.getElementName();
      }
      String typeDesc = types.get(type.getLocalPart());
      Tool.pl("\t\t\t", parm.getName(), ":", typeDesc);
    }
  }


  static Map<Object, String> types(Definition def) {
    Types types = def.getTypes();
    List sub = types.getExtensibilityElements();
    Map<Object, String> ret = new HashMap<>();

    for (Object o : sub) {
      if (o instanceof Schema) {
        Schema s = (Schema) o;
        NodeList list = s.getElement().getChildNodes();
        types(list, ret, null);
      }
    }
    return ret;
  }


  /**
   * 生成的 map:
   *  [名称]: [类型]
   *  [名称]= 字符串
   *  [类型]= [描述][.]
   *  [描述]= \[字符串\] : 原始类型 | 字符串 : 变量名 | <字符串> : 组合类型
   */
  static void types(NodeList list, Map<Object, String> map, StringBuilder buf) {
    for (int i=0; i<list.getLength(); ++i) {
      Node node = list.item(i);
      if ("#text".equals(node.getNodeName()))
        continue;

      String nodeName = '<'+ node.getLocalName() +'>';
      String pname = getAttr(node, "name");
      String ptype = getAttr(node, "type");

      if (map != null) {
        buf = new StringBuilder();
        NodeList list0 = node.getChildNodes();
        if (list0 != null && list0.getLength() > 0) {
          types(list0, null, buf);
        } else {
          buf.append("["+ ptype +"]");
        }

        map.put(pname!= null ? pname : nodeName, buf.toString());
      }
      else if (buf != null) {
        String typeName;
        if (pname == null) {
          typeName = nodeName;
        } else {
          typeName = pname;
        }
        if (ptype != null) {
          typeName += "["+ ptype +"]";
        }
        buf.append(typeName);
        NodeList list0 = node.getChildNodes();

        if (list0 != null && list0.getLength() > 0) {
          buf.append(".");
          types(list0, null, buf);
        }
      }
      else {
        throw new NullPointerException("not both null");
      }
    }
  }


  static String getAttr(Node n, String name) {
    NamedNodeMap map = n.getAttributes();
    if (map == null) return null;
    Node namen = map.getNamedItem(name);
    if (namen == null) return null;
    return namen.getNodeValue();
  }


  static Object getDoc(WSDLElement e) {
    Element d = e.getDocumentationElement();
    if (d == null) return "NO DOC";

    NodeList l = d.getChildNodes();
    StringBuilder buf = new StringBuilder();
    for (int i=0; i<l.getLength(); ++i) {
      buf.append(l.item(i).getNodeValue());
    }
    return buf;
  }
}
