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
// 文件创建日期: 20-11-23 上午7:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/TopicInf.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.been.XBosonException;


public class TopicInf {

  /** 场景 id 片段 */
  public String scenes;
  /** 产品 id 片段 */
  public String product;
  /** 设备 id 片段 */
  public String device;


  private TopicInf() {
  }


  public TopicInf(String topic) {
    parse(this, topic, '/');
  }


  private static void parse(TopicInf inf, final String topic, final char delimiter) {
    if (topic.charAt(0) != delimiter) {
      throw new XBosonException.BadParameter(
              "Topic name", "Not begin '"+ delimiter +"'");
    }
    int st = 0;
    int a = 1;

    for (int i=a; i<topic.length(); ++i) {
      char c = topic.charAt(i);
      if (c == delimiter) {
        if (a == i) {
          throw new XBosonException.BadParameter(
                  "Topic name", "No character between '"+ delimiter +"'");
        }
        switch (st) {
          case 0:
            inf.scenes = topic.substring(a, i);
            break;
          case 1:
            inf.product = topic.substring(a, i);
            break;
          case 2:
            inf.device = topic.substring(a, i);
            return;
        }
        a = i+1;
        ++st;
      }
    }

    if (st == 1) {
      inf.product = topic.substring(a);
    } else if (st == 2) {
      inf.device = topic.substring(a);
    } else {
      throw new XBosonException.BadParameter(
              "Topic name", "bad ending");
    }
  }


  /**
   * 解析完整 id
   */
  public static TopicInf parseID(String id) {
    TopicInf inf = new TopicInf();
    parse(inf, id, '.');
    return inf;
  }


  /**
   * 生成完整设备 id, 包括场景/产品前缀
   */
  public String genDeviceID() {
    return Util.id(scenes, product, device);
  }


  /**
   * 生成完整产品 id, 包括场景前缀
   */
  public String genProductID() {
    return Util.id(scenes, product);
  }


  public String toString() {
    return "scenes="+ scenes +", product="+ product +", device="+ device;
  }


  /**
   * 生成 mqtt 主题格式字符串
   */
  public String genTopic(String type) {
    return Util.toTopic(scenes, product, device, type);
  }
}
