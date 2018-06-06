////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-6-5 下午6:13
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/XmlImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.j2ee.resp.XmlResponse;
import com.xboson.script.lib.JsOutputStream;
import com.xboson.util.c0nst.IConstant;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class XmlImpl {

  public static final String BEG_TN      = "<";
  public static final String END_TN      = ">";
  public static final String SELF_END_TN = "/>";
  public static final String END_END_TN  = "</";
  public static final String ATTR_EQ     = "=";
  public static final String ATTR_QM     = "\"";
  public static final String SP          = " ";
  public static final String ENTER       = "\n";

  public static final int ST_BEGIN       = 0;
  public static final int ST_BEG_ATTR    = 2;
  public static final int ST_BET_BODY    = 5;
  public static final int ST_SUB_TAG     = 10;
  public static final int ST_END_TAG     = 99;


  public XmlRoot build(JsOutputStream out, boolean pretty) throws IOException {
    return new XmlRoot(out, pretty);
  }


  public XmlRoot build(JsOutputStream out) throws IOException {
    return build(out, false);
  }


  public class XmlRoot {

    private JsOutputStream out;
    private XmlTag last;
    private boolean pretty;


    private XmlRoot(JsOutputStream out, boolean pretty) throws IOException {
      this.out    = out;
      this.pretty = pretty;
    }


    public void writeHead() throws IOException {
      if (last != null)
        throw new IllegalStateException();

      out.write(XmlResponse.XML_HEAD);
    }


    public XmlTag tag(String name) throws IOException {
      if (last != null) last.end();
      XmlTag x = new XmlTag(name, out, pretty);
      x.begin();
      last = x;
      return x;
    }


    public void end() throws IOException {
      if (last != null) {
        last.end();
        last = null;
      }
      out.flush();
      out = null;
    }
  }


  public class XmlTag {

    private JsOutputStream out;

    /** 记录最后一个子节点 */
    private XmlTag lastSub;
    private String name;
    private int state;
    private boolean pretty;
    private int deep;


    private XmlTag(String name, JsOutputStream out, boolean pretty)
            throws IOException {
      this.name   = name;
      this.state  = ST_BEGIN;
      this.out    = out;
      this.pretty = pretty;
    }


    private void begin() throws IOException {
      if (pretty) indentation();
      out.write(BEG_TN);
      out.write(name);
    }


    public XmlTag attr(String name, Object val) throws IOException {
      return attr(name, String.valueOf(val));
    }


    public XmlTag attr(String name, String value) throws IOException {
      if (state >= ST_BEG_ATTR)
        throw new IllegalStateException();

      this.state = ST_BEG_ATTR;
      out.write(SP);
      out.write(name);
      out.write(ATTR_EQ);
      out.write(ATTR_QM);
      out.write(value);
      out.write(ATTR_QM);
      return this;
    }


    private void indentation() throws IOException {
      out.write(ENTER);
      for (int i=0; i<deep; ++i) {
        out.write(SP);
      }
    }


    public XmlTag tag(String name) throws IOException {
      if (state > ST_SUB_TAG) {
        throw new IllegalStateException();
      }
      if (state < ST_BET_BODY) {
        out.write(END_TN);
      }
      state = ST_SUB_TAG;

      if (lastSub != null) lastSub.end();
      XmlTag x = new XmlTag(name, out, pretty);
      x.deep = deep + 2;
      x.begin();
      lastSub = x;
      return x;
    }


    public XmlTag text(Object body) throws IOException {
      return text(String.valueOf(body));
    }


    public XmlTag text(String body) throws IOException {
      if (state > ST_BET_BODY) {
        throw new IllegalStateException();
      } else if (state < ST_BET_BODY) {
        out.write(END_TN);
      }
      this.state = ST_BET_BODY;

      OutputStreamWriter w = new OutputStreamWriter(out, IConstant.CHARSET);
      int len = body.length();

      for (int i=0; i<len; ++i) {
        char c = body.charAt(i);
        switch (c) {
          case '<':
            w.write("&lt;");
            break;
          case '>':
            w.write("&gt;");
            break;
          case '&':
            w.write("&amp;");
            break;
          case '\'':
            w.write("&apos;");
            break;
          case '"':
            w.write("&quot;");
            break;
          default:
            w.append(c);
            break;
        }
      }
      // dont close
      w.flush();
      return this;
    }


    public XmlTag xml(Object body) throws IOException {
      return xml(String.valueOf(body));
    }


    public XmlTag xml(String body) throws IOException {
      if (state > ST_BET_BODY) {
        throw new IllegalStateException();
      } else if (state < ST_BET_BODY) {
        out.write(END_TN);
      }

      this.state = ST_BET_BODY;
      out.write(body);
      return this;
    }


    public void end() throws IOException {
      if (state == ST_END_TAG) {
        return;
      }
      else if (state <= ST_BEG_ATTR) {
        out.write(SELF_END_TN);
        return;
      }

      if (lastSub != null) {
        lastSub.end();
        lastSub = null;
      }

      if (pretty && state >= ST_SUB_TAG) indentation();
      this.state = ST_END_TAG;

      out.write(END_END_TN);
      out.write(name);
      out.write(END_TN);
    }
  }
}
