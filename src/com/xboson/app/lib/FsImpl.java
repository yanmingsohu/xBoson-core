////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-19 下午2:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/FsImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.fs.redis.FileStruct;
import com.xboson.fs.redis.FinderResult;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.ui.UIFileFactory;

import java.util.Set;


public class FsImpl {


  public Object open() {
    return open("ui");
  }


  public Object open(String fsTypeName) {
    boolean runOnSysOrg = (boolean) ModuleHandleContext._get("runOnSysOrg");
    if (!runOnSysOrg) {
      throw new XBosonException.NotImplements("只能在平台机构中引用");
    }
    if (fsTypeName == null) {
      throw new XBosonException.NullParamException("String fsTypeName");
    }

    switch (fsTypeName) {
      case "ui":
        return new Wrap(UIFileFactory.openWithConfig());

      default:
        throw new XBosonException.NotFound(
                "File System Type:" + fsTypeName);
    }
  }


  /**
   * 包装器防止调用不在接口中的方法
   */
  private class Wrap implements IRedisFileSystemProvider {
    private final IRedisFileSystemProvider o;

    private Wrap(IRedisFileSystemProvider o ) {
      this.o = o;
    }

    @Override
    public byte[] readFile(String path) {
      return o.readFile(path);
    }


    @Override
    public void readFileContent(FileStruct fs) {
      o.readFileContent(fs);
    }


    @Override
    public long modifyTime(String path) {
      return o.modifyTime(path);
    }


    @Override
    public FileStruct readAttribute(String path) {
      return o.readAttribute(path);
    }


    @Override
    public void makeDir(String path) {
      o.makeDir(path);
    }


    @Override
    public void writeFile(String path, byte[] bytes) {
      o.writeFile(path, bytes);
    }


    @Override
    public void delete(String file) {
      o.delete(file);
    }


    @Override
    public void move(String src, String to) {
      o.move(src, to);
    }


    @Override
    public Set<FileStruct> readDir(String path) {
      return o.readDir(path);
    }


    @Override
    public FinderResult findPath(String pathName) {
      return o.findPath(pathName);
    }


    @Override
    public FinderResult findContent(String basePath, String content, boolean cs) {
      return o.findContent(basePath, content, cs);
    }
  }
}
