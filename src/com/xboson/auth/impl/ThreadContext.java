////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 上午7:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/ThreadContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth.impl;

import com.xboson.auth.IAWho;
import com.xboson.auth.ILoginContext;
import com.xboson.auth.PermissionException;
import com.xboson.been.XBosonException;


public class ThreadContext implements ILoginContext {

  private IAWho now;


  public ThreadContext() {
  }


  /**
   *
   * 注意: Servlet 4.0 的异步模式不直接支持这种方法.
   * @param who
   */
  @Override
  public void login(IAWho who) {
    if (now != null) {
      throw new PermissionException("some one has logged in");
    }
    if (who == null) {
      throw new XBosonException.NullParamException("IAWho who");
    }
    if (who.identification() == null) {
      throw new PermissionException("bad identification");
    }
    now = who;
  }


  /**
   * 将用户登出线程上下文; 登出的主体必须和当前主体相同.
   * @param who
   */
  @Override
  public void logout(IAWho who) {
    if (who == null) {
      throw new XBosonException.NullParamException("IAWho who");
    }
    if (now == null) {
      throw new PermissionException("No login");
    }
    if (!who.identification().equals(now.identification())) {
      throw new PermissionException("bad identification");
    }
    now = null;
  }


  /**
   * 获取当前的登录主体, 若没有登录抛出异常
   */
  @Override
  public IAWho whois() {
    if (now == null) {
      throw new PermissionException("No login");
    }
    return now;
  }


  @Override
  public void contextIn(Object contextData) {
  }


  @Override
  public void contextOut(Object contextData) {
  }


  public String contextName() {
    return "thread_context";
  }
}
