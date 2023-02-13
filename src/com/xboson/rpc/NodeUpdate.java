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
// 文件创建日期: 18-1-31 上午9:02
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/NodeUpdate.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;

import javax.naming.event.NamingEvent;


/**
 * 节点更新事件辅助对象
 *
 * @see Names#host_update 事件名称
 */
public abstract class NodeUpdate extends GLHandle {


  public NodeUpdate() {
    GlobalEventBus.me().on(Names.host_update, this);
  }


  public void objectChanged(NamingEvent namingEvent) {
    String host = (String) namingEvent.getNewBinding().getObject();
    onChange(host);
  }


  /**
   * 接受更新消息后该方法被调用
   * @param nodeID 更新的节点 id
   */
  protected abstract void onChange(String nodeID);


  /**
   * 发送全局消息, 通知整个集群节点信息已经更新
   */
  public static void emit(String nodeID) {
    GlobalEventBus.me().emit(Names.host_update, nodeID);
  }


  /**
   * 结束监听消息, 从全局事件移除自身.
   */
  public void removeFileListener() {
    GlobalEventBus.me().off(Names.host_update, this);
  }
}
