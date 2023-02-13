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
// 文件创建日期: 17-11-15 下午3:13
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/PermissionSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

import com.xboson.app.AppContext;
import com.xboson.been.XBosonException;


/**
 * 该对象只完成一件事情: 检查权限
 * @see AuthFactory 如何初始化登录上下文
 */
public class PermissionSystem {


  /**
   * 在需要做权限检查的地方调用这个函数;
   * 参数可以通过 AuthFactory 来生成.
   *
   * @param who 谁 (用户? 用户组? 机构? 第三方应用?)
   * @param where 在哪 (函数中的函数名; 数据库操作的数据库描述)
   * @param res 目标资源 (编辑文件? 访问页面?)
   *
   * @throws PermissionException 如果禁止操作会抛出异常, 否则立即返回
   *
   * @see #applyWithApp(Class, IAResource) 更简单的调用方法
   */
  public static void apply(IAWho who, IAWhere where, IAResource res) {
    if (who == null)
      throw new XBosonException.NullParamException("IAWho who");
    if (res == null)
      throw new XBosonException.NullParamException("IAResource res");
    if (where == null)
      throw new XBosonException.NullParamException("IAWhere where");

    if (where.passAdmin() && who.isRoot())
      return;

    if (where.apply(who, res) == false) {
      throw new PermissionException(who, where, res);
    }
  }


  /**
   * IAWhere 会从缓冲区中获取
   *
   * @see #apply(IAWho, IAWhere, IAResource) 完整的调用
   * @see #applyWithApp(Class, IAResource) 应用上下文
   */
  public static void apply(IAWho who,
                           Class<? extends IAWhere> where,
                           IAResource res) {
    IAWhere iaw = AuthFactory.me().get(where);
    apply(who, iaw, res);
  }


  /**
   * 在应用上下文中检查权限
   * @see #apply(IAWho, IAWhere, IAResource)
   */
  public static void applyWithApp(Class<? extends IAWhere> where,
                                IAResource res) {
    apply(AppContext.me().who(), where, res);
  }


}
