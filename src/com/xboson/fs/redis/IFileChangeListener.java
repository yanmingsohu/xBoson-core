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
// 文件创建日期: 17-11-19 上午11:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/IFileChangeListener.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;


/**
 * 仅对 ui 模块使用的文件通知器, 当其他节点对文件有操作,
 * 会通过网络将消息发送到本地文件系统, 通过该接口接收这些消息.
 *
 * @see FileModifyHandle
 */
public interface IFileChangeListener {

  /**
   * 通知文件改动或创建
   * @param vfile 改动的文件路径
   */
  void noticeModifyContent(String vfile);


  /**
   * 通知目录被创建
   * @param vdirname 目录路径
   */
  void noticeMakeDir(String vdirname);


  /**
   * 通知文件被删除
   * @param vfile 删除的文件路径
   */
  void noticeDelete(String vfile);


  /**
   * 通知文件被移动
   * @param form 源文件名
   * @param to 目的文件名
   */
  void noticeMove(String form, String to);

}
