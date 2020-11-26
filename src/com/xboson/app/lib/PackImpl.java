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
import com.xboson.script.lib.JsInputStream;
import com.xboson.util.c0nst.IConstant;
import org.apache.commons.fileupload.util.Streams;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class PackImpl implements IVisitByScript {

  public PackImpl() {}


  public Object createZipWriter(OutputStream out) {
    Zip z = new Zip(out);
    ModuleHandleContext.autoClose(z);
    return z;
  }


  public Object createUnZipReader(InputStream i) {
    UnZip uz = new UnZip(i);
    ModuleHandleContext.autoClose(uz);
    return uz;
  }


  /**
   * 打包数据接口, js 不支持 default 方法
   */
  interface IPack extends AutoCloseable, IVisitByScript {
    /**
     * 添加一个文件, i 是文件的输入流
     */
    void add(String filepath, InputStream i) throws IOException;

    void add(String filepath, String data) throws IOException;

    void add(String filepath, Bytes data) throws IOException;

    void add(String filepath, Buffer.JsBuffer buf) throws IOException;
  }


  interface IUnPack extends AutoCloseable, IVisitByScript {
    boolean hasNext() throws IOException;
    String path() throws IOException;
    boolean isDirectory() throws IOException;
    long size() throws IOException;
    long getTime() throws IOException;
    JsInputStream openInput() throws IOException;
  }


  public static abstract class AbsPack implements IPack {
    public void add(String filepath, String data) throws IOException {
      add(filepath, new ByteArrayInputStream(data.getBytes(IConstant.CHARSET)));
    }

    public void add(String filepath, Bytes data) throws IOException {
      add(filepath, new ByteArrayInputStream(data.bin()));
    }

    public void add(String filepath, Buffer.JsBuffer buf) throws IOException {
      add(filepath, new ByteArrayInputStream(buf._buffer().array()));
    }
  }


  public static class Zip extends AbsPack implements IPack {

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


    public void close() throws Exception {
      zout.close();
    }
  }


  public static class UnZip implements IUnPack {

    private ZipInputStream i;
    private ZipEntry ze;


    private UnZip(InputStream _i) {
      this.i = new ZipInputStream(_i);
    }


    public void close() throws Exception {
      ze = null;
      i.close();
    }


    public boolean hasNext() throws IOException {
      this.ze = i.getNextEntry();
      return ze != null;
    }


    public String path() throws IOException {
      if (ze == null) throw new IOException("No entry set");
      return ze.getName();
    }


    public boolean isDirectory() throws IOException {
      if (ze == null) throw new IOException("No entry set");
      return ze.isDirectory();
    }


    public long size() throws IOException {
      if (ze == null) throw new IOException("No entry set");
      return ze.getSize();
    }


    public long getTime() throws IOException {
      if (ze == null) throw new IOException("No entry set");
      return ze.getTime();
    }


    @Override
    public JsInputStream openInput() throws IOException {
      if (ze == null) throw new IOException("No entry set");
      return new JsInputStream(i);
    }
  }
}
