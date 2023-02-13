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
import org.bson.BsonString;
import org.bson.Document;

import javax.naming.event.NamingEvent;


public final class ConfigHolder extends GLHandle implements IotConst {

  private static ConfigHolder me;
  private IotConfig config;
  private boolean nullConfig;

  /** 配置文件名前面被 类ConfigImpl 加入前缀  */
  private static final String CONF_WITH_PREFIX = ":"+ CONF_NAME;


  public static ConfigHolder me() {
    if (me == null) {
      synchronized (ConfigHolder.class) {
        if (me == null) {
          me = new ConfigHolder();
        }
      }
    }
    return me;
  }


  private ConfigHolder() {
    config = new IotConfig();
    nullConfig = true;
    ConfigImpl.regEventOnBus(this);
  }


  @Override
  public void objectChanged(NamingEvent namingEvent) {
    if (namingEvent.getType() != ConfigImpl.DATA) {
      return;
    }
    BsonString name = (BsonString) namingEvent.getNewBinding().getObject();
    if (! name.getValue().endsWith(CONF_WITH_PREFIX)) {
      return;
    }
    updateConfig();
  }


  /**
   * 在不同的上下文中, 配置文件可能不同, 配置文件模式默认全局配置,
   * 否则可能导致 IOT 无法正确随系统启动,
   *
   * 如果读取配置文件失败, 会抛出异常.
   */
  public void updateConfig() {
    Document c = new ConfigImpl().get(CONF_NAME);
    if (c == null) {
      throw new XBosonException("Cannot open IoT config: "+ CONF_NAME);
    }
    updateConfig(c);
  }


  public void updateConfig(Document c) {
    synchronized (config) {
      config = new IotConfig(c);
      nullConfig = false;
    }
  }


  public boolean needInitConfig() {
    return nullConfig;
  }


  public IotConfig getConfig() {
    synchronized (config) {
      return config;
    }
  }
}
