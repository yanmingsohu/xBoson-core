////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-1-6 上午10:49
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/PackImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.script.IVisitByScript;
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Bytes;
import org.apache.commons.fileupload.util.Streams;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class PackImpl implements IVisitByScript {

  public PackImpl() {}


  public Object createZipWriter(OutputStream out) {
    Zip z = new Zip(out);
    ModuleHandleContext.autoClose(z);
    return z;
  }


  /**
   * 打包数据接口
   */
  interface IPackData extends AutoCloseable {
    /**
     * 添加一个文件, i 是文件的输入流
     */
    void add(String filepath, InputStream i) throws IOException;
    void add(String filepath, String data) throws IOException;
    void add(String filepath, Bytes data) throws IOException;
    void add(String filepath, Buffer.JsBuffer buf) throws IOException;

    /**
     * 关闭流
     * @throws IOException
     */
    void close() throws IOException;
  }


  static public abstract class PackBase implements IPackData {

    public void add(String filepath, String data) throws IOException {
      add(filepath, new ByteArrayInputStream(data.getBytes("utf-8")));
    }

    public void add(String filepath, Bytes data) throws IOException {
      add(filepath, new ByteArrayInputStream(data.bin()));
    }

    public void add(String filepath, Buffer.JsBuffer buf) throws IOException {
      add(filepath, new ByteArrayInputStream(buf._buffer().array()));
    }
  }


  static public class Zip extends PackBase implements IPackData {
    private ZipOutputStream zout;

    private Zip(OutputStream out) {
      zout = new ZipOutputStream(out);
    }

    public void add(String filepath, InputStream i) throws IOException {
      ZipEntry zipEntry = new ZipEntry(filepath);
      zout.putNextEntry(zipEntry);
      Streams.copy(i, zout, false);
      zout.closeEntry();
    }

    public void close() throws IOException {
      zout.close();
    }
  }
}
