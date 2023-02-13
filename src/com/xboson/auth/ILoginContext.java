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
// 文件创建日期: 17-11-18 上午7:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/ILoginContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

/**
 * 登录上下文, 将登入主体保持在程序中, 当检查权限时, 可以不提供主体对象.
 * 上下文被 AuthFactory 管理, 每个线程只有一个登录上下文.
 * 需要保证线程安全, 可重入.
 */
public interface ILoginContext {

  /**
   * 将主体登入当前上下文, 每个上下文只允许一个主体登入
   * @param who
   */
  void login(IAWho who);


  /**
   * 将主体登出线程上下文; 登出的主体必须和当前主体相同.
   * @param who
   */
  void logout(IAWho who);


  /**
   * 返回登录到当前上下文的主体, 如果没有主体登入, 则抛出安全异常
   * @return
   */
  IAWho whois();


  /**
   * 初始化上下文, 不同的实现需要传递不同的对象
   * @param contextData
   */
  void contextIn(Object contextData);


  /**
   * 切出上下文
   */
  void contextOut(Object contextData);


  /**
   * 返回上下文的名字, 作为缓冲池的 key
   */
  String contextName();
}
