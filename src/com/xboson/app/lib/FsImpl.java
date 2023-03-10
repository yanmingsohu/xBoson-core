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
// 文件创建日期: 17-12-19 下午2:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/FsImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.fs.basic.IFileAttribute;
import com.xboson.fs.basic.IFileOperatorBase;
import com.xboson.fs.basic.IStreamOperator;
import com.xboson.fs.hadoop.HadoopFileSystem;
import com.xboson.fs.mongo.MongoFileAttr;
import com.xboson.fs.mongo.SysMongoFactory;
import com.xboson.fs.node.NodeFileFactory;
import com.xboson.fs.redis.RedisFileAttr;
import com.xboson.fs.redis.FinderResult;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.ui.UIFileFactory;
import com.xboson.script.IVisitByScript;
import com.xboson.script.lib.Checker;
import com.xboson.script.lib.JsInputStream;
import com.xboson.script.lib.JsOutputStream;
import com.xboson.util.CreatorFromUrl;
import com.xboson.util.Tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class FsImpl implements IVisitByScript {

  public interface URLTypes {
    /** hdfs://10.0.0.9:9000 */
    String HDFS = "hdfs";

    /** share://diskName */
    String SHARE = "share";
  }


  private CreatorFromUrl<IFileOperatorBase> fsCreater;


  public FsImpl() {
    fsCreater = new CreatorFromUrl<>();

    fsCreater.reg(URLTypes.HDFS, (v, p, url, data)->{
      return new WrapStream<>(new HadoopFileSystem(url));
    });

    fsCreater.reg(URLTypes.SHARE, (v, p, url, data)->{
      return openShare(v);
    });
  }


  public Object open() {
    return open("ui");
  }


  public Object open(String fsTypeName) {
    boolean runOnSysOrg = (boolean) ModuleHandleContext._get("runOnSysOrg");

    if (!runOnSysOrg)
      throw new XBosonException.NotImplements("只能在平台机构中引用");

    if (fsTypeName == null)
      throw new XBosonException.NullParamException("String fsTypeName");


    switch (fsTypeName) {
      case "ui":
        return new Wrap(UIFileFactory.open());

      case "node":
        return new Wrap(NodeFileFactory.open());

      case "share":
        return new WrapStream<MongoFileAttr>(SysMongoFactory.me().openFS());

      default:
        throw new XBosonException.NotFound(
                "File System Type:" + fsTypeName);
    }
  }


  public IFileOperatorBase openURI(String uri) {
    return fsCreater.create(uri);
  }


  public IFileOperatorBase openShare(String diskName) {
    if (Tool.isNulStr(diskName))
      throw new XBosonException.NullParamException("String diskName");

    Checker.me.symbol(diskName, "无效的磁盘名称格式");

    return new WrapStream(SysMongoFactory.me().openFS(diskName));
  }


  public void pipe(InputStream i, OutputStream o) throws IOException {
    Tool.copy(i, o, true);
  }


  /**
   * 在接口退出时关闭打开的流, 必须返回包装器
   */
  private class WrapStream<Attr extends IFileAttribute>
          implements IStreamOperator<Attr>, IVisitByScript {

    private IStreamOperator<Attr> real;


    private WrapStream(IStreamOperator<Attr> real) {
      this.real = real;
    }


    @Override
    public InputStream openInputStream(String file) {
      InputStream i = real.openInputStream(file);
      ModuleHandleContext.autoClose(i);
      return new JsInputStream(i);
    }


    @Override
    public OutputStream openOutputStream(String file) {
      OutputStream o = real.openOutputStream(file);
      ModuleHandleContext.autoClose(o);
      return new JsOutputStream(o);
    }


    @Override
    public Attr readAttribute(String path) {
      return real.readAttribute(path);
    }


    @Override
    public Set<Attr> readDir(String path) {
      return real.readDir(path);
    }


    @Override
    public void move(String src, String to) {
      real.move(src, to);
    }


    @Override
    public void delete(String file) {
      real.delete(file);
    }


    @Override
    public long modifyTime(String path) {
      return real.modifyTime(path);
    }


    @Override
    public void makeDir(String path) {
      real.makeDir(path);
    }
  }


  /**
   * 包装器防止调用不在接口中的方法
   */
  private class Wrap implements IRedisFileSystemProvider, IVisitByScript {
    private final IRedisFileSystemProvider o;

    private Wrap(IRedisFileSystemProvider o ) {
      this.o = o;
    }

    @Override
    public byte[] readFile(String path) {
      return o.readFile(path);
    }


    @Override
    public void readFileContent(RedisFileAttr fs) {
      o.readFileContent(fs);
    }


    @Override
    public long modifyTime(String path) {
      return o.modifyTime(path);
    }


    @Override
    public RedisFileAttr readAttribute(String path) {
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
    public Set<RedisFileAttr> readDir(String path) {
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
