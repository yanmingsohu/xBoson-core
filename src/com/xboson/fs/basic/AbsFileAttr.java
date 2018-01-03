////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-3 下午12:57
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/basic/AbsFileAttr.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.basic;

import com.xboson.been.XBosonException;
import com.xboson.fs.redis.RedisFileAttr;

import java.io.Serializable;


/**
 * 预定义文件属性, 只有三个属性: 路径, 类型, 修改时间
 */
public class AbsFileAttr implements Serializable, IFileAttribute {

  public final static int T_FILE = 1;
  public final static int T_DIR  = 2;

  public final String path;
  public final int type;
  public final long lastModify;


  protected AbsFileAttr(String path, int type, long lastModify) {
    if (path == null)
      throw new XBosonException.NullParamException("String path");

    this.path = path;
    this.type = type;
    this.lastModify = lastModify;
  }


  protected AbsFileAttr(AbsFileAttr other) {
    this(other.path, other.type, other.lastModify);
  }


  public boolean isDir() {
    return type == T_DIR;
  }


  public boolean isFile() {
    return type == T_FILE;
  }


  @Override
  public final String toString() {
    return "[" + path +", "+ (type == T_DIR ? "DIR":"FILE") + "]";
  }


  @Override
  public final int hashCode() {
    return (int)(path.hashCode() + type + lastModify);
  }


  @Override
  public final boolean equals(Object o) {
    if (o == this)
      return true;

    if (o == null || o instanceof RedisFileAttr == false)
      return false;

    AbsFileAttr other = (AbsFileAttr) o;
    return other.path.equals(path)
            && other.type == type
            && other.lastModify == lastModify;
  }


  /**
   * 当需要一个目录而给出一个文件, 或相反, 或路径格式错误, 抛出的异常.
   */
  public class BadPath extends XBosonException.IOError {
    public BadPath(String why) {
      super(why, path);
    }
  }
}
