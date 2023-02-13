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
// 文件创建日期: 17-11-22 上午10:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/XmlDataMap.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.util.HashMap;
import java.util.Map;


/**
 * 行为与 HashMap 完全相同, 但是当转换为 xml 时将与 Map 的行为不同.
 * 默认转换器已经注册到 Tool 中.
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
 *
 * @see com.xboson.util.converter.XmlDataMapConverter
 * @see com.xboson.util.Tool#createXmlStream
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


}
