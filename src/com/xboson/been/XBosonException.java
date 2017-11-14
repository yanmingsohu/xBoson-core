////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-13 下午4:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/XBosonException.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.sql.SQLException;


/**
 * 异常基础类, 不需要特别捕获
 */
public class XBosonException extends RuntimeException implements IBean, IXBosonException {

  public XBosonException() {
    super();
  }

  public XBosonException(String s) {
    super(s);
  }

  public XBosonException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public XBosonException(Throwable throwable) {
    super(throwable);
  }

  protected XBosonException(String s, Throwable throwable, boolean b, boolean b1) {
    super(s, throwable, b, b1);
  }


  /**
   * 参数为空则抛出异常
   */
  static public class NullParamException extends XBosonException {
    /**
     * @param paramName 参数的完成名字, 如: "String paramName"
     */
    public NullParamException(String paramName) {
      super("The Function parameter \"" + paramName + "\" can not be NULL");
    }
  }


  /**
   * 方法暂时没有实现, 抛出这个异常
   */
  static public class NotImplements extends XBosonException {
    public NotImplements() {
      super("The Function/Method is not implemented yet");
    }
    /**
     * @param fname 函数的完整名字
     */
    public NotImplements(String fname) {
      super("The " + fname + "() is not implemented yet");
    }
  }


  /**
   * sql 执行错误抛出的异常
   */
  static public class XSqlException extends XBosonException {
    public XSqlException(SQLException sqle) {
      super(sqle);
    }
    /**
     * @param whatDoing 正在做什么导致的错误
     */
    public XSqlException(String whatDoing, SQLException sqle) {
      super(whatDoing, sqle);
    }
    public XSqlException(String whatDoing, Throwable sqle) {
      super(whatDoing, sqle);
    }
  }


  /**
   * 当寻找的资源/对象不存在时抛出的异常
   */
  static public class NotExist extends XBosonException {
    /**
     * @param whatThing 什么东西找不到
     */
    public NotExist(String whatThing, SQLException sqle) {
      super(whatThing, sqle);
    }
    /**
     * @param whatThing 什么东西找不到
     */
    public NotExist(String whatThing) {
      super(whatThing);
    }
  }
}
