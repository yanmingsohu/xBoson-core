////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-14 上午9:36
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/AuthFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

import com.xboson.auth.impl.ThreadContext;
import com.xboson.been.XBosonException;

import java.util.HashMap;
import java.util.Map;


/**
 * 与权限相关的操作, 自身并不检查权限; 权限检查使用 PermissionSystem.
 * @see PermissionSystem
 */
public class AuthFactory {

  private static AuthFactory instance;
  public static AuthFactory me() {
    if (instance == null) {
      instance = new AuthFactory();
    }
    return instance;
  }


  private Map<Class, IAWhere> whereMap;
  private Map<String, Class> who_context_types;
  private ThreadLocal<ILoginContext> context_thread;
  private String default_who_context;


  private AuthFactory() {
    whereMap = new HashMap<>();
    who_context_types = new HashMap<>();
    context_thread = new ThreadLocal<>();

    //
    // 登入上下文初始化列表
    //
    reg(ThreadContext.class);
  }


  public void reg(Class<? extends ILoginContext> lc) {
    try {
      ILoginContext c = lc.newInstance();
      who_context_types.put(c.contextName(), lc);
      setDefaultWhoContext(c.contextName());
    } catch (Exception e) {
      System.err.println("REG ILoginContext " + e);
    }
  }


  /**
   * 获取 IAWhere 实例, 该对象会被缓冲.
   */
  public IAWhere get(Class<? extends IAWhere> where) {
    IAWhere ret = whereMap.get(where);
    if (ret == null) {
      synchronized (whereMap) {
        ret = whereMap.get(where);
        if (ret == null) {
          try {
            ret = where.newInstance();
            whereMap.put(where, ret);
          } catch (Exception e) {
            throw new PermissionException(e);
          }
        }
      }
    }
    return ret;
  }


  /**
   * 初始化一个当前线程的 who 上下文
   * @param data
   */
  public void initWhoContext(String context_name, Object data) {
    try {
      Class cl = who_context_types.get(context_name);
      ILoginContext context = (ILoginContext) cl.newInstance();
      context.contextIn(data);
      context_thread.set(context);
    } catch(Exception e) {
      throw new XBosonException("initWhoContext()", e);
    }
  }


  public void initWhoContext(Object data) {
    initWhoContext(default_who_context, data);
  }


  /**
   * 退出当前 who 上下文
   * @param data
   */
  public void outWhoContext(Object data) {
    ILoginContext context = context_thread.get();
    context.contextOut(data);
  }


  /**
   * 使用名称设置默认上下文类型
   */
  public void setDefaultWhoContext(String name) {
    if (!who_context_types.containsKey(name)) {
      throw new XBosonException("Not a valid context name: " + name);
    }
    default_who_context = name;
  }


  /**
   * 返回当前上下文
   */
  public ILoginContext whoContext() {
    ILoginContext whoc = context_thread.get();
    if (whoc == null) {
      throw new PermissionException("not init context on this thread");
    }
    return whoc;
  }
}
