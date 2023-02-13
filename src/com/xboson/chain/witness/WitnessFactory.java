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
// 文件创建日期: 18-8-13 下午3:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/WitnessFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.been.Witness;
import com.xboson.been.XBosonException;
import com.xboson.chain.DBFunctions;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;
import com.xboson.util.WeakMemCache;

import javax.naming.event.NamingEvent;
import java.util.HashSet;
import java.util.Set;


public class WitnessFactory {

  private static WitnessFactory instance;

  private WeakMemCache<String, Witness> witnessPool;
  private Set<String> skipDeliver;
  private Log witconn;


  private WitnessFactory() {
    this.witnessPool = new WeakMemCache<>(new CreateWitness());
    this.witconn     = LogFactory.create("witness-connect");
    this.skipDeliver = new HashSet<>();
    GlobalEventBus.me().on(Names.witness_update, new OnUpdate());
  }


  public static WitnessFactory me() {
    if (instance == null) {
      synchronized (WitnessFactory.class) {
        if (instance == null) {
          instance = new WitnessFactory();
        }
      }
    }
    return instance;
  }


  public Witness get(String witness_id) {
    if (Tool.isNulStr(witness_id))
      throw new XBosonException.NullParamException("String witnessId");

    return witnessPool.getOrCreate(witness_id);
  }


  public Log getWitnessLog() {
    return witconn;
  }


  /**
   * 当见证者的 deliver 被标记为无效返回 true
   */
  public boolean isSkipDeliver(String id) {
    return skipDeliver.contains(id);
  }


  /**
   * 标记见证者的 deliver 方法无效
   */
  public void setSkipDeliver(String id) {
    skipDeliver.add(id);
  }


  /**
   * 更新见证者数据, 当主机地址变更时调用
   */
  public void update(String witnessId) {
    GlobalEventBus.me().emit(Names.witness_update, witnessId);
  }


  /**
   * 根据 id 返回见证者链接, 该方法有缓存优化.
   */
  public WitnessConnect openConnection(String witnessId) {
    Witness wit = get(witnessId);
    return new WitnessConnect(wit);
  }


  private class CreateWitness implements WeakMemCache.ICreator<String, Witness> {
    @Override
    public Witness create(String wid) {
      Witness ret = DBFunctions.me().getWitness(wid);
      if (ret == null)
        throw new XBosonException.NotExist("Witness from id:"+ wid);

      return ret;
    }
  }


  private class OnUpdate extends GLHandle {
    @Override
    public void objectChanged(NamingEvent e) {
      String id = (String) e.getNewBinding().getObject();
      witnessPool.remove(id);
      skipDeliver.remove(id);
    }
  }
}
