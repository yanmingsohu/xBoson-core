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
// 文件创建日期: 18-7-20 下午1:03
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/Digest.java
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
import com.xboson.script.lib.JsOutputStream;
import com.xboson.util.Hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 摘要算法
 * https://docs.oracle.com/en/java/javase/11/security/howtoimplaprovider.html
 */
public class Digest extends RuntimeUnitImpl {


  public Digest() {
    super(null);
  }


  public HashWarp sha1() {
    return new HashWarp("SHA-1");
  }


  public HashWarp sha224() {
    return new HashWarp("SHA-224");
  }


  public HashWarp sha256() {
    return new HashWarp("SHA-256");
  }


  public HashWarp sha384() {
    return new HashWarp("SHA-384");
  }


  public HashWarp sha512() {
    return new HashWarp("SHA-512");
  }


  public HashWarp md5() {
    return new HashWarp("md5");
  }


  public HashWarp md2() {
    return new HashWarp("md2");
  }


  public HashWarp sm3() {
    return new HashWarp("sm3", "BC");
  }


  public static class HashWarp implements IVisitByScript {
    private Hash h;

    public HashWarp(String algorithm) {
      h = new Hash(algorithm);
    }


    public HashWarp(String algorithm, String provider) {
      h = new Hash(algorithm, provider);
    }


    public void update(String s) {
      h.update(s);
    }


    public void update(int i) {
      h.update(i);
    }


    public void update(Bytes b) {
      h.update(b.bin());
    }


    public void update(Buffer.JsBuffer b) {
      h.update(b._buffer().array());
    }


    public Bytes digest() {
      return new Bytes(h.digest());
    }


    public long updatedBytes() {
      return h.updatedBytes();
    }


    public JsInputStream bind(InputStream i) {
      return new JsInputStream(new Input(i, h));
    }


    public JsOutputStream bind(OutputStream o) {
      return new JsOutputStream(new Output(o, h));
    }


    public JsOutputStream getOutput() {
      return new JsOutputStream(new _output());
    }


    private class _output extends OutputStream {
      public void write(int i) throws IOException {
        h.update(i);
      }

      public void write(byte[] buf, int begin, int len) throws IOException {
        h.update(buf, begin, len);
      }
    }
  }


  public static class Input extends InputStream implements IVisitByScript {
    private InputStream org;
    private Hash hash;

    private Input(InputStream o, Hash h) {
      org = o;
      hash = h;
    }

    public int read() throws IOException {
      int r = org.read();
      hash.update(r);
      return r;
    }

    public int read(byte[] buf, int begin, int len) throws IOException {
      int r = org.read(buf, begin, len);
      hash.update(buf, begin, r);
      return r;
    }

    public void close() throws IOException {
      org.close();
    }
  }


  public static class Output extends OutputStream implements IVisitByScript {
    private OutputStream org;
    private Hash hash;

    private Output(OutputStream o, Hash h) {
      org = o;
      hash = h;
    }

    public void write(int i) throws IOException {
      hash.update(i);
      org.write(i);
    }

    public void write(byte[] buf, int begin, int len) throws IOException {
      hash.update(buf, begin, len);
      org.write(buf, begin, len);
    }

    public void close() throws IOException {
      org.close();
    }
  }
}
