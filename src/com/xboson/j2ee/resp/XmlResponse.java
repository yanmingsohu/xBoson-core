////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午7:47
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/resp/XmlResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.resp;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.xboson.been.ResponseRoot;
import com.xboson.j2ee.container.IXResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;


/**
 * 已经开启了自动注解检查, 在做数据转换到 xml 时, 对象/属性上的注解会起作用
 *
 * @see com.thoughtworks.xstream.annotations.XStreamAlias 类别名 / 属性别名
 * @see com.thoughtworks.xstream.annotations.XStreamAsAttribute 转换为标签属性
 * @see com.thoughtworks.xstream.annotations.XStreamImplicit 集合类型展开
 */
public class XmlResponse implements IXResponse {

  public static final String MIME_XML = "application/xml; charset=utf-8";
  public static final String XML_HEAD =
          "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

  private final XStream xs;


  public XmlResponse() {
    xs = new XStream();
    xs.autodetectAnnotations(true);
    xs.registerConverter(new XmlResponse.MapEntryConverter());
  }


  @Override
  public void response(HttpServletRequest request, HttpServletResponse response,
                       Map<String, Object> ret_root) throws IOException {

    response.setHeader("content-type", MIME_XML);
    Writer out = response.getWriter();
    out.write(XML_HEAD);

    xs.toXML(ret_root, out);
  }


  public static class MapEntryConverter implements Converter {

    public boolean canConvert(Class clazz) {
      return ResponseRoot.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {

      Map map = (Map) value;
      for (Object obj : map.entrySet()) {
        Map.Entry entry = (Map.Entry) obj;
        writer.startNode(entry.getKey().toString());
        context.convertAnother(entry.getValue());
        writer.endNode();
      }
    }

    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {

      Map<String, String> map = new HashMap<>();
      while(reader.hasMoreChildren()) {
        reader.moveDown();
        map.put(reader.getNodeName(), reader.getValue());
        reader.moveUp();
      }
      return map;
    }
  }
}
