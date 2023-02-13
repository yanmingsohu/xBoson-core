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
// 文件创建日期: 17-11-23 下午12:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/RequestImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.AppContext;
import com.xboson.been.CallData;
import com.xboson.been.IJson;
import com.xboson.been.XBosonException;
import com.xboson.script.JSObject;
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Checker;
import com.xboson.script.lib.JsInputStream;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * sys.request 的实现,
 * 对属性的读取映射到 http 参数上.
 *
 * 自定义参数优先级高于 http 参数.
 */
public class RequestImpl extends JSObject.Helper implements IJson {

  private static final String Cookie = "cookie";

  private CallData cd;
  private Map<String, Object> extendParameter;
  private Checker ck;
  private SysImpl sys;


  public RequestImpl(CallData cd, SysImpl sys) {
    this.cd = cd;
    this.extendParameter = AppContext.me().getExtendParameter();
    this.ck = new Checker();
    this.sys = sys;

    //
    // 类中的方法会导出, 供 js 调用
    //
    super.config(new JSObject.ExportsFunction());
  }


  public String getHeader(String name) {
    if (Cookie.equalsIgnoreCase(name)) {
      return null;
    }
    return cd.req.getHeader(name);
  }


  @Override
  public boolean hasMember(String name) {
    return extendParameter.containsKey(name)
            || cd.req.getParameter(name) != null
            || super.hasMember(name);
  }


  @Override
  public Object getMember(String name) {
    Object ret = extendParameter.get(name);
    if (ret == null) {
      ret = cd.req.getParameter(name);
    }
    if (ret == null) {
      ret = super.getMember(name);
    }
    if (ret instanceof String && Tool.isNulStr((String) ret)) {
      return null;
    }
    return ret;
  }


  public int forEach(ScriptObjectMirror callback) {
    int count = 0;
    Enumeration<String> i = cd.req.getParameterNames();
    while (i.hasMoreElements()) {
      String k = i.nextElement();
      callback.call(null, k, cd.req.getParameter(k));
      ++count;
    }
    return count;
  }


  /**
   * 返回 string 类型的 http 请求参数, 当值不符合检查条件会抛出异常
   *
   * @param name 参数名
   * @param min 最小长度, 当 min <= 0 则允许返回 null, 否则值长度必须 >= min
   * @param max 最大长度, 值长度必须 <= max
   * @return 返回参数值但参数为空字符串会返回 null
   */
  public String getString(String name, int min, int max) {
    String val = (String) getMember(name);
    if (Tool.isNulStr(val)) {
      if (min <= 0) return null;
      throw new XBosonException.BadParameter(name,
              "字符串类型参数不能为空");
    }

    int len = val.length();
    if (len < min) {
      throw new XBosonException.BadParameter(name,
              "字符串类型参数值长度不能小于"+ min);
    }

    if (len > max) {
      throw new XBosonException.BadParameter(name,
              "字符串类型参数值长度不能超过"+ max);
    }
    return val;
  }


  /**
   * 最大长度 4096
   * @see #getString(String, int, int)
   */
  public String getString(String name, int min) {
    return getString(name, min, 4096);
  }


  /**
   * 不允许空字符串
   * @see #getString(String, int, int)
   */
  public String getString(String name) {
    return getString(name, 1);
  }


  /**
   * 返回整数类型的 http 参数
   *
   * @param name 参数名
   * @param allowNull true 当参数为空时返回 0, 否则会抛出异常
   * @param min 值必须 >= min
   * @param max 值必须 <= max
   * @return 参数值
   */
  public long getInteger(String name, boolean allowNull, double min, double max) {
    String val = (String) getMember(name);
    if (Tool.isNulStr(val)) {
      if (allowNull) return 0;
      throw new XBosonException.BadParameter(name,
              "整数类型参数不能为空");
    }
    long i = Long.parseLong(val);
    if (i < min) {
      throw new XBosonException.BadParameter(name,
              "整数类型参数不能小于" + min);
    }
    if (i > max) {
      throw new XBosonException.BadParameter(name,
              "整数类型参数不能大于" + max);
    }
    return i;
  }


  /**
   * 不限制最大值
   * @see #getInteger(String, boolean, double, double)
   */
  public long getInteger(String name, boolean allowNull, double min) {
    return getInteger(name, allowNull, min, Long.MAX_VALUE);
  }


  /**
   * 不限制最大值和最小值
   * @see #getInteger(String, boolean, double, double)
   */
  public long getInteger(String name, boolean allowNull) {
    return getInteger(name, allowNull, Long.MIN_VALUE, Long.MAX_VALUE);
  }


  /**
   * 不允许空值
   * @see #getInteger(String, boolean, double, double)
   */
  public long getInteger(String name) {
    return getInteger(name, false);
  }


  public long getInteger(String name, boolean allowNull, int min, int max) {
    return getInteger(name, allowNull, (double)min, (double)max);
  }


  public long getInteger(String name, boolean allowNull, int min) {
    return getInteger(name, allowNull, (double)min, Double.MAX_VALUE);
  }


  /**
   * 返回浮点(小数)类型的 http 参数
   *
   * @param name 参数名
   * @param allowNull 允许空参数
   * @param min 值必须 >= min
   * @param max 值必须 <= max
   * @return 浮点数
   */
  public double getFloat(String name, boolean allowNull, double min, double max) {
    String val = (String) getMember(name);
    if (Tool.isNulStr(val)) {
      if (allowNull) return 0;
      throw new XBosonException.BadParameter(name,
              "浮点类型参数不能为空");
    }
    double i = Double.parseDouble(val);
    if (i < min) {
      throw new XBosonException.BadParameter(name,
              "浮点类型参数不能小于" + min);
    }
    if (i > max) {
      throw new XBosonException.BadParameter(name,
              "浮点类型参数不能大于" + max);
    }
    return i;
  }


  public double getFloat(String name, boolean allowNull, int min, int max) {
    return getFloat(name, allowNull, (double) min, (double) max);
  }


  /**
   * 不限制最大值
   * @see #getFloat(String, boolean, double, double)
   */
  public double getFloat(String name, boolean allowNull, double min) {
    return getFloat(name, allowNull, min, Double.MAX_VALUE);
  }


  public double getFloat(String name, boolean allowNull, int min) {
    return getFloat(name, allowNull, min, Double.MAX_VALUE);
  }


  /**
   * 不限制最大值和最小值
   * @see #getFloat(String, boolean, double, double)
   */
  public double getFloat(String name, boolean allowNull) {
    return getFloat(name, allowNull, Double.MIN_VALUE, Double.MAX_VALUE);
  }


  /**
   * 不允许空值
   * @see #getFloat(String, boolean, double, double)
   */
  public double getFloat(String name) {
    return getFloat(name, false, Double.MIN_VALUE, Double.MAX_VALUE);
  }


  /**
   * 判断 http 参数是否是真值, 真值的条件为: 一定非空, 与字符串 'true'(不敏感大小写)
   * 比较为 true, 或者转换为数字不为 0, 其他情况皆为 false.
   *
   * @param name 参数名
   * @return 比较结果
   */
  public boolean isTrue(String name) {
    try {
      String s = (String) getMember(name);
      if (s == null)
        return false;
      if (s.equalsIgnoreCase("true"))
        return true;
      if (Integer.parseInt(s) != 0)
        return true;
    } catch (Exception e) {
    }
    return false;
  }


  /**
   * 路径字符串安全检查, 不安全的路径会抛出异常
   */
  public String getSafePath(String name, boolean allowNull) {
    String val = getString(name, allowNull ? -1 : 1);
    if (val != null) {
      ck.safepath(val, "路径参数:" + name + ", 不安全");
    }
    return val;
  }


  /**
   * 不允许空
   * @see #getSafePath(String, boolean)
   */
  public String getSafePath(String name) {
    return getSafePath(name, false);
  }


  public Buffer.JsBuffer body() throws IOException {
    return body(sys.maxPostBody);
  }


  public JsInputStream openStream() throws IOException {
    return new JsInputStream(cd.req.getInputStream());
  }


  /**
   * 以 Buffer 对象的形式返回 http body 的二进制数据.
   * 如果内容长度超过限制抛出异常;
   * 如果没有 body 数据返回 null;
   */
  public Buffer.JsBuffer body(int limit) throws IOException {
    final int clen = sys.checkBodySize(limit);
    if (clen <= 0) return null;
    Buffer.JsBuffer buf = new Buffer().alloc(clen);

    InputStream i = cd.req.getInputStream();
    int d = 0, pos = 0;
    for (;;) {
      d = i.read();
      if (d < 0) {
        break;
      }
      buf.writeUInt8(d, pos++);
    }
    i.close();
    return buf;
  }


  public int contentLength() {
    return cd.req.getContentLength();
  }


  public String contentType() {
    return cd.req.getContentType();
  }


  public Object multipart(ScriptObjectMirror callback) throws IOException {
    sys.checkBodySize(sys.maxPostBody);
    Multipart mp = new Multipart(cd.req, sys.maxPostBody);
    return mp.parse(callback);
  }


  public String toJSON() {
    StringBuilder buf = new StringBuilder();
    buf.append("{\"parm\": ");
    Map<String, String[]> map = cd.req.getParameterMap();
    buf.append(Tool.getAdapter(Map.class).toJson(map));
    buf.append(", \"ex\": ");
    buf.append(Tool.getAdapter(Map.class).toJson(extendParameter));
    buf.append("}");
    return buf.toString();
  }


  public String toString() {
    return toJSON();
  }


  public Object toObject() {
    Map<String, String> ret = new HashMap<>();
    Enumeration<String> i = cd.req.getParameterNames();
    while (i.hasMoreElements()) {
      String k = i.nextElement();
      ret.put(k, cd.req.getParameter(k));
    }
    return ret;
  }
}
