////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-15 下午3:13
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/PermissionSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

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
   * @see #apply(Class, IAResource) 更简单的调用方法
   */
  public static void apply(IAWho who, IAWhere where, IAResource res) {
    if (who == null)
      throw new XBosonException.NullParamException("IAWho who");
    if (res == null)
      throw new XBosonException.NullParamException("IAResource res");
    if (where == null)
      throw new XBosonException.NullParamException("IAWhere where");

    if (where.apply(who, res) == false) {
      throw new PermissionException(who, where, res);
    }
  }


  /**
   * IAWhere 会从缓冲区中获取
   *
   * @see #apply(IAWho, IAWhere, IAResource) 完整的调用
   * @see #apply(Class, IAResource) 更简单的调用方法
   */
  public static void apply(IAWho who, Class<? extends IAWhere> where, IAResource res) {
    IAWhere iaw = AuthFactory.me().get(where);
    apply(who, iaw, res);
  }


  /**
   * 在需要做权限检查的地方调用这个函数;
   * IAWho 主体从当前上下文中取得, 动作从缓冲区创建.
   *
   * @param where 检查权限的位置对象的类类型, 该对象会被缓冲
   * @param res 主体请求的资源
   *
   * @see #apply(IAWho, IAWhere, IAResource) 完整的调用
   * @see AuthFactory#initWhoContext(String, Object) 初始化上下文
   */
  public static void apply(Class<? extends IAWhere> where, IAResource res) {
    IAWho who = AuthFactory.me().whoContext().whois();
    apply(who, where, res);
  }

}
