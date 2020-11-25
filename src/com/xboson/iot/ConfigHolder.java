////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-25 下午12:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/ConfigHolder.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.app.lib.ConfigImpl;
import com.xboson.been.XBosonException;
import com.xboson.event.GLHandle;
import org.bson.Document;

import javax.naming.event.NamingEvent;


public final class ConfigHolder extends GLHandle implements IotConst {

  private static ConfigHolder me;
  private IotConfig config;


  public static ConfigHolder me() {
    if (me == null) {
      synchronized (ConfigHolder.class) {
        if (me == null) {
          me = new ConfigHolder();
          me.updateConfig();
        }
      }
    }
    return me;
  }


  private ConfigHolder() {
    ConfigImpl.regEventOnBus(this);
    config = new IotConfig();
  }


  @Override
  public void objectChanged(NamingEvent namingEvent) {
    if (namingEvent.getType() != ConfigImpl.DATA) {
      return;
    }
    if (! CONF_NAME.equals(namingEvent.getNewBinding().getObject())) {
      return;
    }
    updateConfig();
  }


  private void updateConfig() {
    Document c = new ConfigImpl().get(CONF_NAME);
    if (c == null) {
      throw new XBosonException("Cannot iot open config: "+ CONF_NAME);
    }
    synchronized (config) {
      config = new IotConfig(c);
    }
  }


  public IotConfig getConfig() {
    synchronized (config) {
      return config;
    }
  }
}
