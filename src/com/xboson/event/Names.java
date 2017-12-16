////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-12 上午9:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/Names.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.util.Tool;


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
   * c(Exception, String classname) 系统内部错误,
   * 如果在接受这个消息的函数中又抛出一个错误, 则之前的消息会被中断, 行为无法定义.
   * classname 是抛出这个错误的对象
   * @see ErrorHandle
   */
  String inner_error = "sys.error";

  /**
   * c(String filename, null)
   * ui 文件修改消息, 由于离线的本地文件节点在上线后, 需要知道在线文件的修改记录
   * 所以该消息的离线队列由 RedisBase 相关线程维护
   *
   * @see com.xboson.j2ee.ui.RedisBase
   */
  String ui_file_change = "ui.file.change";
  
  /**
   * 文件修改事件前缀, 消息指发送给在线节点, 离线节点上线后也无法收到该消息.
   * @see OnFileChangeHandle
   */
  String volatile_file_change_prifix = "v.file.change:";

  /**
   * 全局消息总线名称
   */
  String CHANNEL_PREFIX = "/com.xboson.event.GlobalEventBus/";

}
