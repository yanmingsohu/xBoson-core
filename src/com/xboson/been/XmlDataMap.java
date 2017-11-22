////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-22 上午10:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/XmlDataMap.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashMap;
import java.util.Map;


/**
 * 行为与 HashMap 完全相同, 但是当转换为 xml 时将与 Map 的行为不同.
 * 注册转换器: XStream.registerConverter(new MapEntryConverter());
 *
 * Map 转换后的样式:
 * <map>
 *   <entry>
 *   <string>code</string>
 *   <int>0</int>
 *   </entry>
 *   <entry>
 *   <string>message</string>
 *   <string>ok</string>
 *   </entry>
 * </map>
 *
 * XmlDataMap 转换后的样式:
 * <map>
 *   <code>0</code>
 *   <message>ok</message>
 * </map>
 */
public class XmlDataMap<K,V> extends HashMap<K,V> {

  public XmlDataMap(int i, float v) {
    super(i, v);
  }


  public XmlDataMap(int i) {
    super(i);
  }


  public XmlDataMap() {
    super();
  }


  public XmlDataMap(Map<? extends K, ? extends V> map) {
    super(map);
  }


  /**
   * Map 的属性转换为 <key>Value</key> 的形式
   */
  public static class MapEntryConverter implements Converter {

    public boolean canConvert(Class clazz) {
      return XmlDataMap.class.isAssignableFrom(clazz);
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
