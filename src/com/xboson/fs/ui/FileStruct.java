////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-17 下午12:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/FileStruct.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.been.XBosonException;
import com.xboson.script.lib.Path;

import java.io.Serializable;
import java.util.*;


/**
 * 虽然也是文件属性类, 但是为 UI 存储优化.
 * file_content 可以能为 null.
 */
public class FileStruct implements Serializable {

  public final static int T_FILE = 1;
  public final static int T_DIR  = 2;

  public final String path;
  public final int type;
  public final long lastModify;

  private Set<String> dir_contain;
  transient private byte[] file_content;


  private FileStruct(String path, int type, long lastModify) {
    if (path == null)
      throw new XBosonException.NullParamException("String path");

    this.path = path;
    this.type = type;
    this.lastModify = lastModify;
  }


  public static FileStruct createFile(String path,
                                      long lastModify,
                                      byte[] content)
  {
    FileStruct fs = new FileStruct(path, T_FILE, lastModify);
    fs.file_content = content;
    return fs;
  }


  public static FileStruct createDir(String path) {
    FileStruct fs = new FileStruct(path, T_DIR, 0);
    fs.dir_contain = new HashSet<>();
    return fs;
  }


  public boolean isDir() {
    return type == T_DIR;
  }


  public boolean isFile() {
    return type == T_FILE;
  }


  private String childPath(String full) {
    if (type != T_DIR) {
      throw new BadPath("Is not directory");
    }
    if (! full.startsWith(path)) {
      throw new BadPath("Is not child " + full);
    }
    return full.substring(path.length());
  }


  public void addChildStruct(String fileOrDir) {
    String ch = childPath(fileOrDir);
    dir_contain.add(ch);
  }


  public void removeChild(String fileOrDir) {
    String ch = childPath(fileOrDir);
    if (! dir_contain.remove(ch)) {
      throw new BadPath("Not in this dir:" + fileOrDir);
    }
  }


  public Set<String> containFiles() {
    if (type != T_DIR) {
      throw new BadPath("Is not directory");
    }
    return dir_contain;
  }


  public byte[] getFileContent() {
    if (type != T_FILE) {
      throw new BadPath("Is not file");
    }
    return file_content;
  }


  public void setFileContent(byte[] c) {
    file_content = c;
  }


  /**
   * 返回父路径字符串, 如果已经是父路径返回 null
   */
  public String parentPath() {
    return Path.dirname(path);
  }


  public class BadPath extends XBosonException {
    BadPath(String why) {
      super("["+ path +"] " + why);
    }
  }


  @Override
  public String toString() {
    return "[" + path +", "+ (type == T_DIR ? "DIR":"FILE") + "]";
  }


  @Override
  public int hashCode() {
    return (int)(path.hashCode() + type + lastModify);
  }


  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;

    if (o == null || o instanceof FileStruct == false)
      return false;

    FileStruct other = (FileStruct) o;
    return other.path.equals(path)
            && other.type == type
            && other.lastModify == lastModify;
  }


  /**
   * 复制所有属性, 除了 path 只保留文件名部分,
   * 返回的对象中, 内容属性将指向同一个可变对象.
   */
  public FileStruct cloneBaseName() {
    String basename = Path.basename(path);
    FileStruct fs = new FileStruct(basename, type, lastModify);
    fs.dir_contain = dir_contain;
    fs.file_content = file_content;
    return fs;
  }
}
