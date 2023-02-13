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
import com.xboson.script.lib.Path;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * 预定义文件属性, 只有三个属性: 路径, 类型, 修改时间
 */
public abstract class AbsFileAttr implements Serializable, IFileAttribute {


  /** 规范化的绝对路径 */
  public final String path;

  /** 文件类型 */
  public final int type;

  /** 修改时间, ms */
  public final long lastModify;

  /** 若是目录, 则存储目录中的元素 */
  public final Set<String> dir_contain;

  /** 不包含路径, 只有文件名 */
  public final String name;


  protected AbsFileAttr(String path, int type, long lastModify) {
    if (path == null)
      throw new XBosonException.NullParamException("String path");

    this.path = path;
    this.type = type;
    this.lastModify = lastModify;
    this.name = Path.me.basename(path);

    if (type == T_DIR) {
      dir_contain = new HashSet<>();
    } else {
      dir_contain = null;
    }
  }


  protected AbsFileAttr(AbsFileAttr other) {
    this(other.path, other.type, other.lastModify);
    if (dir_contain != null) {
      dir_contain.addAll(other.dir_contain);
    }
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


  /**
   * 当前对象必须是目录, full 必须是当前目录子节点路径,
   * 返回去掉当前路径的子路径字符串. 当 full 不符合条件时抛出异常.
   */
  protected String childPath(String full) {
    if (type != T_DIR) {
      throw new BadPath("Is not directory");
    }
    if (! full.startsWith(path)) {
      throw new BadPath("Is not child in: '" + full + "'");
    }
    return full.substring(path.length());
  }


  public boolean isDir() {
    return type == T_DIR;
  }


  public boolean isFile() {
    return type == T_FILE;
  }


  @Override
  public int type() {
    return type;
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


  @Override
  public String path() {
    return path;
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
