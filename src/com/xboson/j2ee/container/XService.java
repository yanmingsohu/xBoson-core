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
// 文件创建日期: 2017年11月2日 下午3:22:04
// 原始文件路径: xBoson/src/com/xboson/j2ee/container/XService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import com.xboson.been.CallData;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.log.ILogName;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 可以多线程重入服务实现, 默认总是需要验证.
 *
 * @see #subService 方便制作子服务
 */
public abstract class XService implements ILogName {

  private static final Class[] PARMS_TYPE = new Class[] { CallData.class };


  /**
   * 输出日志
   */
  protected final Log log = LogFactory.create(this);


  /**
   * 子类实现该方法, 当服务被调用, 进入该方法中
   */
  public abstract void service(CallData data) throws Exception;


  /**
   * 子类重写该方法, 当服务器终止时调用
   */
  public void destroy() {
    log.info("default destory().");
  }


  /**
   * 需要登录验证返回 true
   */
  public boolean needLogin() {
    return true;
  }


  /**
   * 调用检查登录状态, 无登录则抛出异常
   */
  public void checkLoging(CallData cd) {
    if (cd.sess.login_user == null) {
      throw new XBosonException("please login", 1000);
    }
    if (cd.sess.login_user.pid == null) {
      throw new XBosonException("invaild login state", 1006);
    }
  }


  /**
   * 将本类的函数映射为子服务.
   *
   * 调用该方法, 将请求路径再次拆分, 后一级路径作为函数名, 并使用 data 调用这个函数,
   * 函数必须是 public 且函数签名和 service 一致.
   *
   * @param msg 拆分路径错误抛出的消息
   * @throws Exception
   * @throws XBosonException.NoService
   */
  protected void subService(CallData data, String msg) throws Exception {
    UrlSplit sp = data.url.clone();
    sp.setErrorMessage(msg);
    sp.withoutSlash(true);
    String sub = sp.next();

    try {
      Method sub_service = this.getClass().getMethod(sub, PARMS_TYPE);
      sub_service.invoke(this, data);

    } catch(NoSuchMethodException e) {
      throw new XBosonException.NoService(sub);

    } catch(InvocationTargetException e) {
      throw (Exception) e.getCause();
    }
  }
}
