////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午8:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/ui/RedisFileMapping.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * redis 文件模式.
 *
 * 读取: 从 redis 缓存中读取文件, 如果文件不存在则返回 404, 即使本地文件存在.
 * 写入: 判断 redis 中保存的文件修改时间, 条件允许则保存文件; 写入结束后,
 *      将修改记录加入消息队列
 */
public class RedisFileMapping implements IUIFileProvider {

  private RedisBase rb;
  private Log log;


  public RedisFileMapping() {
    this(new RedisBase());
    log = LogFactory.create();
  }


  public RedisFileMapping(RedisBase rb) {
    this.rb = rb;
  }


  @Override
  public byte[] readFile(String path) throws IOException {
    FileStruct fs = rb.getStruct(path);
    rb.getContent(fs);
    return fs.getFileContent();
  }


  @Override
  public long modifyTime(String path) {
    return rb.getStruct(path).lastModify;
  }


  @Override
  public void makeDir(String path) throws IOException {
    try (RedisBase.JedisSession jsession = rb.openSession()) {
      makeDirWithoutNotice(path);
      rb.sendCreateDirNotice(path);
    }
  }


  public void makeDirWithoutNotice(String path) {
    try (RedisBase.JedisSession jsession = rb.openSession()) {
      FileStruct dir = rb.getStruct(path);
      if (dir == null) {
        dir = FileStruct.createDir(path);
        makeStructUntilRoot(dir);
      }
    }
  }


  @Override
  public void writeFile(String path, byte[] bytes) throws IOException {
    writeFile(path, bytes, System.currentTimeMillis());
  }


  public void writeFile(String path, byte[] bytes, long modify)
          throws IOException {
    try (RedisBase.JedisSession jsession = rb.openSession()) {
      FileStruct file = FileStruct.createFile(path, modify, bytes);
      writeFileWithoutNotice(file);
      rb.sendModifyNotice(file.path);
    }
  }


  public void writeFileWithoutNotice(FileStruct file) {
    makeStructUntilRoot(file);
    rb.setContent(file);
  }


  private void removeAndUpdateParent(FileStruct fs) {
    FileStruct parent = rb.getStruct(fs.parentPath());
    parent.removeChild(fs.path);
    rb.removeStruct(fs);
    rb.saveStruct(parent);
  }


  @Override
  public void deleteFile(String file) {
    try (RedisBase.JedisSession jsession = rb.openSession()) {
      FileStruct fs = rb.getStruct(file);
      if (fs == null) return;

      if (fs.isFile()) {
        rb.delContent(fs);
        removeAndUpdateParent(fs);
      }
      else /* Is dir */ {
        for (String child : fs.containFiles()) {
          deleteFile(child);
        }
        removeAndUpdateParent(fs);
      }
      rb.sendDeleteNotice(file);
    }
  }


  @Override
  public Set<FileStruct> readDir(final String path) {
    try (RedisBase.JedisSession jsession = rb.openSession()) {
      FileStruct fs = rb.getStruct(path);
      if (! fs.isDir()) {
        throw new XBosonException.IOError("Is not dir: " + path);
      }
      Set<String> names = fs.containFiles();
      Set<FileStruct> ret = new HashSet<>(names.size());

      for (String name : names) {
        fs = rb.getStruct(path + name);
        if (fs != null) {
          ret.add(fs.cloneBaseName());
        } else {
          log.warn("Redis file system may bad, Cannot found:",
                  name, ", In dir:", path, ", But recorded.");
        }
      }
      return ret;
    }
  }


  /**
   * 从 fs 的父节点开始创建目录, 这会检查一直到目录根节点之前的路径是否都是目录,
   * 最后创建 fs 定义的节点. 在任意一部上失败都会抛出异常.
   * 该方法会直接复制路径上的 struct, 如果是文件没问题, 是目录需要检查子节点问题.
   * 该方法不发送集群消息, 本地文件系统只要收到最深层目录即可自动创建上层目录.
   */
  public void makeStructUntilRoot(FileStruct fs) {
    try (RedisBase.JedisSession jsession = rb.openSession()) {
      List<String> need_create_dir = new ArrayList<>();
      FileStruct direct_parent = null;

      String pp = fs.parentPath();
      while (pp != null) {
        FileStruct check_fs = rb.getStruct(pp);
        if (check_fs == null) {
          //
          // 创建不存在的目录
          //
          need_create_dir.add(pp);
        } else if (check_fs.isFile()) {
          //
          // 不能在文件路径上创建子目录
          //
          throw new XBosonException("Contain files in " + fs.path);
        } else if (check_fs.isDir()) {
          //
          // 当前向搜索到一个目录则不再继续搜索,
          // 这个目录结构的正确由创建该目录的时候保证.
          //
          direct_parent = check_fs;
          break;
        }

        pp = Path.dirname(pp);
      }

      final int size = need_create_dir.size();
      for (int i = size - 1; i >= 0; --i) {
        String p = need_create_dir.get(i);
        //
        // 更新父节点
        //
        if (direct_parent != null) {
          direct_parent.addChildStruct(p);
          rb.saveStruct(direct_parent);
        }

        FileStruct new_node = FileStruct.createDir(p);
        rb.saveStruct(new_node);
        direct_parent = new_node;
      }

      if (direct_parent == null && ROOT.equals(fs.path) == false) {
        direct_parent = rb.getStruct(ROOT);
      }

      if (direct_parent != null) {
        direct_parent.addChildStruct(fs.path);
        rb.saveStruct(direct_parent);
      }
      rb.saveStruct(fs);
    }
  }
}
