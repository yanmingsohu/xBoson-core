////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:20
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SysImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.db.ConnectConfig;

import java.util.HashMap;
import java.util.Map;


/**
 * 每次请求一个实例
 */
public class SysImpl extends RuntimeImpl {

  /**
   * 公共属性
   */
  public final RequestImpl request;

  private ConnectConfig orgdb;
  private Map<String, Object> retData;


  public SysImpl(CallData cd, ConnectConfig orgdb) {
    super(cd);
    this.orgdb = orgdb;
    this.request = new RequestImpl(cd);
    this.retData = new HashMap<>();
  }


  public void addRetData(Object o) {
    addRetData(o, "result");
  }


  public void addRetData(Object o, String key) {
    retData.put(key, o);
  }


  public void setRetData(String code, String msg, String ...parm) {
    int c = Integer.parseInt(code);
    setRetData(c, msg, parm);
  }


  public void setRetData(int code, String msg, String ...parm) {
    cd.xres.setCode(code);
    cd.xres.setMessage(msg);
    for (int i=0; i<parm.length; ++i) {
      String name = parm[i];
      cd.xres.bindResponse(name, retData.get(name));
    }
  }


  public String getUserPID() {
    return cd.sess.login_user.pid;
  }


  public String getUserPID(String ...users) {
//    orgdb
    return null; //!!!!!!!!!!!!!
  }
}
