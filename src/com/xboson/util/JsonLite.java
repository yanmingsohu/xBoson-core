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
// 文件创建日期: 7/24/21 10:02 AM
// 原始文件路径: /Users/mac/projects/xBoson/src/com/xboson/util/JsonLite.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;


import com.xboson.been.XBosonException;


/**
 * 一个快速 json 生成器，不能保证结果正确性，必须按顺序调用其中的方法,
 */
public class JsonLite {

  private StringBuilder buf;
  private Ref<Boolean> end;
  private boolean isRoot = false;
  private int count = 0;


  public static JsonLite objectRoot() {
    return new JsonLite(new StringBuilder(), true);
  }


  public static Arr arrayRoot() {
    return new Arr(new StringBuilder(), true);
  }


  private JsonLite(StringBuilder buf, boolean root) {
    this.buf = buf;
    end = new Ref<>(false);
    this.isRoot = root;
    buf.append('{');
  }


  public JsonLite putObject(String name) {
    checkend();
    pushName(name);
    return new JsonLite(this.buf, false);
  }


  public Arr putArray(String name) {
    checkend();
    pushName(name);
    return new Arr(this.buf, false);
  }


  public JsonLite put(String n, String v) {
    checkend();
    pushName(n);
    putStringValue(buf, v);
    return this;
  }


  private static void putStringValue(StringBuilder buf, String v) {
    buf.append('"');
    for (int i=0; i<v.length(); ++i) {
      char c = v.charAt(i);
      switch (c) {
        case '"':
          buf.append("\\\"");
          break;
        case '\\':
          buf.append("\\\\");
          break;
        default:
          buf.append(c);
          break;
      }
    }
    buf.append('"');
  }


  public JsonLite put(String n, int v) {
    checkend();
    pushName(n);
    buf.append(v);
    return this;
  }


  public JsonLite put(String n, float v) {
    checkend();
    pushName(n);
    buf.append(v);
    return this;
  }


  public JsonLite put(String n, boolean v) {
    checkend();
    pushName(n);
    buf.append(v);
    return this;
  }


  public JsonLite putNull(String n) {
    checkend();
    pushName(n);
    buf.append("null");
    return this;
  }


  public void end() {
    checkend();
    buf.append('}');
    end.x = true;
  }


  private void pushName(String n) {
    if (count > 0) {
      buf.append(',');
    }
    buf.append('"').append(n).append('"').append(':');
    count++;
  }


  private void checkend() {
    if (end.x) throw new XBosonException.Closed("json lite");
  }


  public String toString() {
    if (!isRoot) throw new IllegalStateException("not root");
    if (!end.x) throw new IllegalStateException("not end");
    return buf.toString();
  }


  public static class Arr {

    private StringBuilder buf;
    private boolean end = false;
    private int count = 0;
    private boolean isRoot = false;

    private Arr(StringBuilder _buf, boolean root) {
      this.buf = _buf;
      this.isRoot = root;
      buf.append('[');
    }

    public void end() {
      if (end) throw new XBosonException.Closed("json array");
      buf.append(']');
      end = true;
    }

    public Arr put(String v) {
      putCheck();
      putStringValue(buf, v);
      return this;
    }

    public Arr put(int v) {
      putCheck();
      buf.append(v);
      return this;
    }

    public Arr put(float v) {
      putCheck();
      buf.append(v);
      return this;
    }

    public Arr put(boolean v) {
      putCheck();
      buf.append(v);
      return this;
    }

    public JsonLite putObject() {
      putCheck();
      return new JsonLite(buf, false);
    }

    public Arr putArray() {
      putCheck();
      return new Arr(buf, false);
    }

    private void putCheck() {
      if (end) throw new XBosonException.Closed("json array");
      if (count++ > 0) buf.append(',');
    }

    public String toString() {
      if (!isRoot) throw new IllegalStateException("not root");
      if (!end) throw new IllegalStateException("not end");
      return buf.toString();
    }
  }
}
