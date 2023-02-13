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
// 文件创建日期: 17-12-22 上午8:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/redis/IFileSystemConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

/**
 * 通过继承该接口来配置文件系统中的参数.
 * 这些参数保证不同的文件系统不会冲突, 所谓名称就是 redis 保存数据中的 key.
 */
public interface IFileSystemConfig {

  /**
   * 返回文件修改消息队列的名称;
   * 文件修改通知首先发送到队列中, 再由消息迁移线程发送到订阅通知中.
   * 该队列维护的消息保证节点离线后再次上线也可以接受到文件修改通知.
   * 例如 "XB.UI.File.ChangeQueue"
   */
  String configQueueName();


  /**
   * 返回保存文件结构信息的名称, 文件结构包括文件属性和目录结构
   * 例如: "XB.UI.File.Struct"
   */
  String configStructName();


  /**
   * 返回保存文件内容的名称
   * 例如: "XB.UI.File.CONTENT"
   */
  String configContentName();


  /**
   * 文件修改通知 '订阅名称'
   * 例如: "ui.file.change"
   */
  String configFileChangeEventName();


  /**
   * 返回本地文件存储的目录
   */
  String configLocalPath();


  /**
   * 该方法起动事件迁移队列线程, 是否单例由实现决定
   */
  void startMigrationThread();

}
