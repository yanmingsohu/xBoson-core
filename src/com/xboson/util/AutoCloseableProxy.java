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
// 文件创建日期: 17-11-14 下午5:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/AutoCloseableProxy.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.log.LogFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * 代理一个自动关闭对象, 当被代理对象的 close 被调用后, 调用实现的 doClose 方法,
 * 用于数据库连接池/TCP 池等; 当对象被 GC 回收前, 没有调用过 close 方法,
 * 则使用 finalize 机制最后一次调用 doClose().
 * @param <T>
 */
public abstract class AutoCloseableProxy<T extends AutoCloseable>
        implements InvocationHandler {

  public static final String CLOSE_NAME = "close";

  private String classname;
  protected T original;


  /**
   * 创建代理句柄
   * @param original 原始对象
   */
  public AutoCloseableProxy(T original) {
    if (original == null) {
      throw new XBosonException.NullParamException("original");
    }
    this.original = original;
    this.classname = original.getClass().getName();
  }


  public T getProxy() {
    Class c = original.getClass();
    Class[] interfaces = c.getInterfaces();
    interfaces = appendInterfaces(interfaces);

    Object obj = Proxy.newProxyInstance(c.getClassLoader(),
            interfaces, this);
    return (T) obj;
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
    if (original == null) {
      throw new ClosedException(classname);
    }

    if (CLOSE_NAME.equals(method.getName()) && args  == null) {
      callClose(proxy);
      return null;
    }

    try {
      return method.invoke(original, args);
    } catch(InvocationTargetException ite) {
      XBosonException.throwCause(ite);
      return null;
    }
  }


  private void callClose(Object proxy) {
    try {
      if (original != null) {
        doClose(original, proxy);
      }
    } catch(Throwable e) {
      LogFactory.create().error("Call doClose() Fail:", Tool.allStack(e));
    } finally {
      original = null;
    }
  }


  @Override
  protected final void finalize() throws Throwable {
    callClose(null);
  }

  @Override
  public String toString() {
    return "Proxy="+ this.hashCode() +","+ original.toString();
  }

  /**
   * 当返回的代理类的 close() 被调用后该方法被触发,
   * 必须立即处理 original, 之后调用生成的代理对象所有方法都会抛出异常
   *
   * @param original 原始对象
   * @param proxy 生成的代理对象, 可以为 null
   * @throws Exception 如果实现抛出异常, 方法返回后对象仍然会进入关闭状态.
   */
  protected abstract void doClose(T original, Object proxy) throws Exception;


  /**
   * 附加新接口给代理类, 默认实现直接返回 interfaces.
   */
  protected Class[] appendInterfaces(Class[] interfaces) {
    return interfaces;
  }


  /**
   * 在已经关闭的对象上调用任何方法都会抛出该异常
   */
  static public class ClosedException extends XBosonException {
    public ClosedException(String classname) {
      super("Object " + classname + " is closed");
    }
  }


  /**
   * what ??
   */
  public static AutoCloseable wrap(AutoCloseable out) {
    return out;
  }
}
