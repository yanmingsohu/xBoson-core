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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
