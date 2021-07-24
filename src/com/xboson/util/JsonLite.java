////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2021 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
