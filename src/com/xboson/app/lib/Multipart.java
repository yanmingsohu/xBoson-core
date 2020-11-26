////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-5-21 下午12:49
// 原始文件路径: E:/xboson/xBoson/src/com/xboson/app/lib/Multipart.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.script.IVisitByScript;
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Bytes;
import com.xboson.script.lib.JsInputStream;
import com.xboson.util.LimitInputStream;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.ParameterParser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

public class Multipart extends RuntimeUnitImpl {

  public static final int BUFFER_SIZE = 16 * 1024;

  private HttpServletRequest req;
  private byte[] boundary;
  private int bufferSize;
  private int maxBody;


  public Multipart(HttpServletRequest req, int maxBody) {
    super(null);
    this.req        = req;
    this.bufferSize = BUFFER_SIZE;
    this.maxBody    = maxBody;
  }


  public int parse(ScriptObjectMirror callback) throws IOException {
    if (req.getContentLength() == 0) return 0;
    if (!callback.isFunction())
      throw new XBosonException("callback must Function");

    parseBoundary();
    return parseBody(callback);
  }


  private int parseBody(ScriptObjectMirror callback) throws IOException {
    DiyMultipartStream ms = new DiyMultipartStream(
            LimitInputStream.wrap(req.getInputStream(), maxBody),
            boundary, bufferSize, null);

    boolean nextPart = ms.skipPreamble();
    int count = 0;

    while(nextPart) {
      String header = ms.readHeaders();
      MultipartItem item = new MultipartItem(header, ms);
      callback.call(null, item);
      if (! item.isRead) ms.discardBodyData();
      nextPart = ms.readBoundary();
      ++count;
    }
    return count;
  }


  private void parseHeader(ScriptObjectMirror header, String headersStr) {
    ParameterParser pp = new ParameterParser();
    int end = 0, st = 0;

    for (;;st = end+2) {
      end = headersStr.indexOf('\r', st);
      if (end < 0) break;

      String line = headersStr.substring(st, end);
      int i = line.indexOf(':');
      if (i >= 0) {
        String hname = line.substring(0, i);
        String hvalue = line.substring(i+1);

        switch (hname.toLowerCase()) {
          case "content-disposition":
            Map<String, String> p = pp.parse(hvalue, ';');
            header.putAll(p);
            break;

          default:
            header.put(hname, hvalue);
        }
      }
    }
  }


  private void parseBoundary() {
    do {
      String type = req.getContentType();
      if (type == null) break;

      String find = "multipart/form-data";
      int i = type.indexOf(find);
      if (i < 0) break;

      i += find.length();
      find = "boundary=";
      i = type.indexOf(find, i);
      if (i < 0) break;

      String str = type.substring(i + find.length());
      boundary = str.getBytes(IConstant.CHARSET);
    } while (false);

    if (boundary == null) {
      throw new XBosonException.BadParameter(
              "Content-type", "Not multipart/form-data");
    }
  }


  private class DiyMultipartStream extends MultipartStream {

    private Method newInputStream;


    DiyMultipartStream(InputStream wrap, byte[] boundary, int bufferSize,
                              MultipartStream.ProgressNotifier o) {
      super(wrap, boundary, bufferSize, o);
    }


    /**
     * hack 方法, 如果 MultipartStream 内部实现改变, 该方法会持续失败
     */
    InputStream openInputStream() throws Exception {
      if (newInputStream == null) {
        newInputStream = MultipartStream.class.getDeclaredMethod("newInputStream");
        newInputStream.setAccessible(true);
      }
      return (InputStream) newInputStream.invoke(this);
    }
  }


  public class MultipartItem implements IVisitByScript {

    public final ScriptObjectMirror header;
    private final DiyMultipartStream ms;
    private boolean isRead;


    private MultipartItem(String headerStr, DiyMultipartStream ms) {
      this.ms     = ms;
      this.isRead = false;
      this.header = createJSObject();
      parseHeader(this.header, headerStr);
    }


    public Object readBuffer() throws IOException {
      StringBufferOutputStream output = new StringBufferOutputStream();
      readTo(output);
      return new Buffer().from(output.toBytes());
    }


    public Object readBytes() throws IOException {
      StringBufferOutputStream output = new StringBufferOutputStream();
      readTo(output);
      return new Bytes(output.toBytes());
    }


    public String readString(String charset) throws IOException {
      StringBufferOutputStream output = new StringBufferOutputStream();
      readTo(output);
      return new String(output.toBytes(), charset);
    }


    public String readString() throws IOException {
      return readString(IConstant.CHARSET_NAME);
    }


    public int readTo(OutputStream out) throws IOException {
      try {
        if (isRead) throw new XBosonException("Cannot read repeatedly");
        return ms.readBodyData(out);
      } finally {
        isRead = true;
      }
    }


    public JsInputStream openInputStream() throws Exception {
      return new JsInputStream(ms.openInputStream());
    }
  }

}
