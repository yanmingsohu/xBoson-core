////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-17 下午12:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/RedisFileAttr.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.been.XBosonException;
import com.xboson.fs.basic.IFileAttribute;
import com.xboson.script.lib.Path;
import com.xboson.util.IConstant;

import java.io.Serializable;
import java.util.*;


/**
 * 虽然也是文件属性类, 但是为 UI 存储优化.
 * file_content 可以能为 null.
 */
public class RedisFileAttr implements Serializable, IFileAttribute {

  public final static int T_FILE = 1;
  public final static int T_DIR  = 2;

  public final String path;
  public final int type;
  public final long lastModify;

  private Set<String> dir_contain;
  private transient byte[] file_content;
  private transient boolean need_synchronization;


  private RedisFileAttr(String path, int type, long lastModify) {
    if (path == null)
      throw new XBosonException.NullParamException("String path");

    this.path = path;
    this.type = type;
    this.lastModify = lastModify;
  }


  protected RedisFileAttr(RedisFileAttr fs) {
    this(fs.path, fs.type, fs.lastModify);
    this.dir_contain = fs.dir_contain;
    this.file_content = fs.file_content;
    this.need_synchronization = fs.need_synchronization;
  }


  public static RedisFileAttr createFile(String path,
                                         long lastModify,
                                         byte[] content)
  {
    RedisFileAttr fs = new RedisFileAttr(path, T_FILE, lastModify);
    fs.file_content = content;
    return fs;
  }


  public static RedisFileAttr createDir(String path) {
    RedisFileAttr fs = new RedisFileAttr(path, T_DIR, 0);
    fs.dir_contain = new HashSet<>();
    return fs;
  }


  public boolean isDir() {
    return type == T_DIR;
  }


  public boolean isFile() {
    return type == T_FILE;
  }


  /**
   * 当前对象必须是目录, full 必须是当前目录子节点路径,
   * 返回去掉当前路径的子路径字符串. 当 full 不符合条件时抛出异常.
   */
  private String childPath(String full) {
    if (type != T_DIR) {
      throw new BadPath("Is not directory");
    }
    if (! full.startsWith(path)) {
      throw new BadPath("Is not child: '" + full + "'");
    }
    return full.substring(path.length());
  }


  /**
   * 将文件或目录加入当前目录中作为子节点,
   * 保存时将去掉当前目录前缀, 只保留子节点名称 (有前置 '/')
   * @param fileOrDir 完整路径
   */
  public void addChildStruct(String fileOrDir) {
    String ch = childPath(fileOrDir);
    dir_contain.add(ch);
  }


  /**
   * 删除当前目录中的子节点
   * @param fileOrDir 完整路径
   */
  public void removeChild(String fileOrDir) {
    String ch = childPath(fileOrDir);
    if (! dir_contain.remove(ch)) {
      throw new BadPath("Not in this dir:" + fileOrDir);
    }
  }


  /**
   * 返回所有子节点路径列表, 路径中不包含当前目录的路径.
   */
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


  public String getContentToString() {
    return new String(file_content, IConstant.CHARSET);
  }


  public void setFileContent(byte[] c) {
    file_content = c;
  }


  /**
   * 返回父路径字符串, 如果已经是父路径返回 null
   */
  public String parentPath() {
    return Path.me.dirname(path);
  }


  public class BadPath extends XBosonException.IOError {
    BadPath(String why) {
      super(why, path);
    }
  }


  @Override
  public String toString() {
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

    RedisFileAttr other = (RedisFileAttr) o;
    return other.path.equals(path)
            && other.type == type
            && other.lastModify == lastModify;
  }


  /**
   * 复制所有属性, 除了 path 只保留文件名部分,
   * 返回的对象中, 内容属性将指向同一个可变对象.
   */
  public final RedisFileAttr cloneBaseName() {
    String basename = Path.me.basename(path);
    RedisFileAttr fs = new RedisFileAttr(basename, type, lastModify);
    fs.dir_contain = dir_contain;
    fs.file_content = file_content;
    fs.need_synchronization = need_synchronization;
    return fs;
  }


  /**
   * 克隆所有属性除了 path 使用 newPath
   */
  public final RedisFileAttr cloneWithName(String newPath) {
    RedisFileAttr fs = new RedisFileAttr(newPath, type, lastModify);
    fs.dir_contain = dir_contain;
    fs.file_content = file_content;
    fs.need_synchronization = need_synchronization;
    return fs;
  }


  /**
   * 返回创建该对象的 IRedisFileSystemProvider 实例 id 值, 可由子类重写
   */
  public int mappingID() {
    return RedisFileMapping.ID;
  }


  public boolean needSynchronization() {
    return need_synchronization;
  }


  public void setSynchronization(boolean need) {
    need_synchronization = need;
  }
}
