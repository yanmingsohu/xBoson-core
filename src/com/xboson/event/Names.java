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
// 文件创建日期: 17-11-12 上午9:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/Names.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;


/**
 * 已有的事件列表, 带有 sys 开头的消息不会在集群中路由
 *
 * 说明: e([bind-name,] data, info), bind-name 总是事件名称
 *    bind-name : NamingEvent.getNewBinding.getName() 可以获得,
 *    data      : NamingEvent.getNewBinding.getObject() 可以获得,
 *    info      : NamingEvent.getChangeInfo() 可以获得,
 *
 * @see javax.naming.event.NamingEvent
 */
public interface Names {

  /**
   * e(null, null) 容器销毁前发出
   * @see OnExitHandle
   */
  String exit = "sys.exit";

  /**
   * c(Config, null) 配置文件读取完成后发出
   */
  String config = "sys.config_success";

  /**
   * c(null, null) 系统开始初始化之前发出
   * @see com.xboson.been.Config
   */
  String initialization = "sys.initialization";

  /**
   * c(null, null) Touch 完成所有子模块初始化后发出
   */
  String already_started = "sys.already_started";

  /**
   * c(Exception, String classname) 系统内部错误,
   * 如果在接受这个消息的函数中又抛出一个错误, 则之前的消息会被中断, 行为无法定义.
   * classname 是抛出这个错误的对象
   * @see ErrorHandle
   */
  String inner_error = "sys.error";


  String host_update = "host.update";

  
  /**
   * 文件修改事件前缀, 消息只发送给在线节点, 离线节点上线后也无法收到该消息.
   * @see OnFileChangeHandle
   */
  String volatile_file_change_prifix = "v.file.change:";

  /**
   * 全局消息总线名称
   */
  String CHANNEL_PREFIX = "/com.xboson.event.GlobalEventBus/";

  /**
   * 区块链消息
   */
  String chain_sync = "sys.chain.sync";

  /**
   * 见证者更新
   */
  String witness_update = "sys.chain.witness.update";

  /**
   * 智能配置更新
   */
  String iconfig_update = "sys.app.iconfig.cache.update";

}
