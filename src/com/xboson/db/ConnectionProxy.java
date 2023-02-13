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
// 文件创建日期: 17-11-14 下午6:19
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/ConnectionProxy.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

import com.xboson.been.XBosonException;
import com.xboson.event.timer.TimeFactory;
import com.xboson.util.AutoCloseableProxy;
import com.xboson.util.CloseableSet;
import com.xboson.util.ResourceLeak;
import com.xboson.util.Tool;
import org.apache.commons.pool2.KeyedObjectPool;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;


/**
 * 生成数据库连接代理, 在关闭时并不关闭连接而是返回连接池.
 * 在回收连接时, 所有在 Connection 上打开的资源都会被关闭.
 */
public class ConnectionProxy extends AutoCloseableProxy<Connection> {

  private KeyedObjectPool<ConnectConfig, Connection> pool;
  private ConnectConfig config;
  private CloseableSet closelist;
  private ResourceLeak leak;


  public ConnectionProxy(KeyedObjectPool<ConnectConfig, Connection> pool,
                         Connection original,
                         ConnectConfig config) {
    super(original);

    if (pool == null)
      throw new XBosonException.NullParamException("KeyedObjectPool pool");
    if (original == null)
      throw new XBosonException.NullParamException("ConnectConfig config");

    this.pool = pool;
    this.config = config.clone();
    this.closelist = new CloseableSet();
    //this.leak = closelist.add(new ResourceLeak(this));
  }


  @Override
  protected void doClose(Connection original, Object proxy) throws Exception {
    pool.returnObject(config, original);
    closelist.close();
  }


  /**
   * 拦截 Connection 返回的对象, 比如 Statement
   * 在关闭时一起关闭
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {

    Object ret = super.invoke(proxy, method, args);
    if (ret instanceof AutoCloseable) {
      closelist.add((AutoCloseable) ret);
    }
    return ret;
  }


  @Override
  protected Class[] appendInterfaces(Class[] interfaces) {
    for (int i=0; i<interfaces.length; ++i) {
      if (interfaces[i] == Connection.class) {
        return interfaces;
      }
    }
    Class[] ret = Arrays.copyOf(interfaces, interfaces.length + 1);
    ret[interfaces.length] = Connection.class;
    return ret;
  }
}
