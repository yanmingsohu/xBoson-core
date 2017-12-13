////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-22 下午5:00
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/XjApi.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.fix.SourceFix;
import com.xboson.been.CallData;
import com.xboson.been.Module;
import com.xboson.been.XBosonException;
import com.xboson.db.IDict;
import com.xboson.db.SqlResult;
import com.xboson.fs.FileAttr;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * api 应该监听脚本修改事件并在脚本修改后清空缓存重新编译改变的脚本
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class XjApi implements IDict {

  private Log log;
  private XjOrg org;
  private XjApp app;
  private XjModule mod;
  private String id;
  private String name;
  private byte[] content;
  private FileAttr attr;
  private long createdt;
  private long updatedt;
  private Module jsmodule;


  XjApi(XjOrg org, XjApp app, XjModule mod, String id) {
    this.org = org;
    this.app = app;
    this.mod = mod;
    this.id  = id;
    this.log = LogFactory.create();

    init();
    log.debug("Api Success", id);
  }


  public void run(CallData cd, String path) throws IOException, ScriptException {
    if (jsmodule == null) {
      synchronized (this) {
        if (jsmodule == null) {
          jsmodule = app.buildJSModule(cd, path);
        }
      }
    }
    app.run(cd, jsmodule);
  }


  private void init() {
    Object[] parm = new Object[] { app.getID(), mod.id(), id };

    try (SqlResult res = org.query("open_api.sql", parm)) {
      ResultSet rs = res.getResult();
      if (rs.next()) {
        if (! ZR001_ENABLE.equals(rs.getString("status")) ) {
          throw new XBosonException("API 已经禁用");
        }
        name     = rs.getString("apinm");
        createdt = rs.getDate("createdt").getTime();
        updatedt = rs.getDate("updatedt").getTime();
        decode(rs.getString("content"));
      } else {
        throw new XBosonException("找不到 API " + id);
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
  }


  /**
   * 解码脚本, 在必要时对代码进行修正.
   */
  private void decode(String str) {
    content = ApiEncryption.decryptApi(str);
    if (SourceFix.fixBeginEnd(content)) {
      content = SourceFix.fixFor(content);
      content = SourceFix.fixJavaCall(content);
    }
  }


  public byte[] getCode() {
    return content;
  }


  public FileAttr getApiAttr() {
    if (attr == null) {
      synchronized (this) {
        if (attr == null) {
          attr = new FileAttr();
          attr.fileSize   = content.length;
          attr.fileName   = id;
          attr.pathName   = '/' + mod.id();
          attr.fullPath   = '/' + mod.id() + '/' + id;
          attr.isfile     = true;
          attr.isdir      = false;
          attr.createTime = createdt;
          attr.modifyTime = updatedt;
        }
      }
    }
    return attr;
  }
}
