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
// 文件创建日期: 17-11-24 下午3:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/XmlDataMapConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.xboson.been.XmlDataMap;
import com.xboson.util.Version;

import java.util.HashMap;
import java.util.Map;


/**
 * Map 的属性转换为 <key>Value</key> 的形式, 转为 XmlDataMap 设计
 *
 * @see XmlDataMap
 */
public class XmlDataMapConverter implements Converter {

  public boolean canConvert(Class clazz) {
    return XmlDataMap.class.isAssignableFrom(clazz);
  }


  public void marshal(Object value, HierarchicalStreamWriter writer,
                      MarshallingContext context) {
    writer.addAttribute("xboson", Version.xBoson);

    Map map = (Map) value;
    for (Object obj : map.entrySet()) {
      Map.Entry entry = (Map.Entry) obj;
      writer.startNode(entry.getKey().toString());
      context.convertAnother(entry.getValue());
      writer.endNode();
    }
  }


  /**
   * 并没有严格按照规则来创建对象 !
   */
  public Object unmarshal(HierarchicalStreamReader reader,
                          UnmarshallingContext context) {

    Map<String, String> map = new HashMap<>();
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      map.put(reader.getNodeName(), reader.getValue());
      reader.moveUp();
    }
    return map;
  }
}
