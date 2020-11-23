////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/TopicInf.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.app.lib.IOTImpl;
import com.xboson.been.XBosonException;


public class TopicInf {

  public String scenes;
  public String product;
  public String device;


  public TopicInf(String topic) {
    if (topic.charAt(0) != '/') {
      throw new XBosonException.BadParameter(
              "Topic name", "not begin '/'");
    }
    int st = 0;
    int a = 1;

    for (int i=a; i<topic.length(); ++i) {
      char c = topic.charAt(i);
      if (c == '/') {
        if (a == i) {
          throw new XBosonException.BadParameter(
                  "Topic name", "no char in '/'");
        }
        switch (st) {
          case 0:
            scenes = topic.substring(a, i);
            break;
          case 1:
            product = topic.substring(a, i);
            break;
          case 2:
            device = topic.substring(a, i);
            return;
        }
        a = i+1;
        ++st;
      }
    }
    if (st == 2) {
      device = topic.substring(a);
    } else {
      throw new XBosonException.BadParameter(
              "Topic name", "bad ending");
    }
  }


  /**
   * 生成完整设备 id, 包括场景/产品前缀
   */
  public String genDeviceID() {
    return Util.id(scenes, product, device);
  }


  public String genProductID() {
    return Util.id(scenes, product);
  }


  public String toString() {
    return "scenes="+ scenes +", product="+ product +", device="+ device;
  }


  public String genTopic(String type) {
    return '/'+ scenes +'/'+ product +'/'+ device +'/'+ type;
  }
}