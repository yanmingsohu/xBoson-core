////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:20
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SysImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.SqlCachedResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.j2ee.files.Directory;
import com.xboson.j2ee.files.FileInfo;
import com.xboson.j2ee.files.PrimitiveOperation;
import com.xboson.j2ee.resp.XmlResponse;
import com.xboson.script.lib.Buffer;
import com.xboson.util.*;
import com.xboson.util.converter.ScriptObjectMirrorJsonConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeJSON;
import jdk.nashorn.internal.runtime.ScriptObject;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * 每次请求一个实例
 */
public class SysImpl extends RuntimeUnitImpl {

  /**
   * 公共属性
   */
  public final Object request;
  public final Object requestParameterMap;

  private ConnectConfig orgdb;
  private Map<String, Object> retData;
  private ScriptObjectMirror printList;


  public SysImpl(CallData cd, ConnectConfig orgdb) {
    super(cd);
    this.orgdb = orgdb;
    this.request = new RequestImpl(cd);
    this.retData = new HashMap<>();
    this.requestParameterMap = new RequestParametersImpl(cd);
  }


  public void addRetData(Object o) {
    addRetData(o, "result");
  }


  public void addRetData(Object o, String key) {
    if (o instanceof ScriptObjectMirror) {
      retData.put(key, new ScriptObjectMirrorJsonConverter.Warp(o));
    } else {
      retData.put(key, o);
    }
  }


  public void setRetData(String code, String msg, String ...parm)
          throws IOException {
    int c = Integer.parseInt(code);
    setRetData(c, msg, parm);
  }


  public void setRetData(int code, String msg, String ...parm)
          throws IOException {
    cd.xres.setCode(code);
    cd.xres.setMessage(msg);
    for (int i=0; i<parm.length; ++i) {
      String name = parm[i];
      cd.xres.bindResponse(name, retData.get(name));
    }
    cd.xres.response();
  }


  public String getUserPID() {
    return cd.sess.login_user.pid;
  }


  public String getUserPID(String userid) throws Exception {
    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      String sql = SqlReader.read("user_id_to_pid.sql");
      List<Map<String, Object>> rows = scr.query(sql, userid);
      if (rows.size() > 0) {
        Map<String, Object> o = rows.get(0);
        return (String) o.get("pid");
      }
    }
    return null;
  }


  public Object getUserPID(String ...users) throws Exception {
    Map<String, Object> ret = new HashMap<>(users.length);

    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      String sql = SqlReader.read("user_id_to_pid.sql");

      for (int i=0; i<users.length; ++i) {
        List<Map<String, Object>> rows = scr.query(sql, users[i]);
        if (rows.size() > 0) {
          Map<String, Object> o = rows.get(0);
          ret.put(users[i], o.get("pid"));
        }
      }
    }
    return ret;
  }


  public Object getUserAdminFlag() throws Exception {
    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      String sql = SqlReader.read("user_admin_flag.sql");

      List<Map<String, Object>> rows = scr.query(
              sql, cd.sess.login_user.pid, orgdb.getDatabase());

      if (rows.size() > 0) {
        Map<String, Object> o = rows.get(0);
        return o.get("admin_flag");
      }
    }
    return 0;
  }


  public String getUserIdByOpenId() {
    return cd.sess.login_user.userid;
  }


  public Object getUserOrgList() throws Exception {
    List<Map<String, Object>> ret;
    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      ret = scr.query(SqlReader.read("user0003"), cd.sess.login_user.pid);
    }
    return ret;
  }


  public Object getUserLoginExpiration() {
    return SysConfig.me().readConfig().sessionTimeout * 60;
  }


  public String uuid() {
    return Tool.uuid.ds();
  }


  public synchronized long nextId() {
    return Tool.nextId();
  }


  public String randomNumber() {
    return randomNumber(6);
  }


  public String randomNumber(Integer i) {
    if (i == null) i = 6;
    return (long)(Math.random() * Math.pow(10, i)) + "";
  }


  public String randomDouble(int p, int s) {
    BigDecimal a = BigDecimal.valueOf(Math.random() * Math.pow(10, p));
    return a.setScale(s, BigDecimal.ROUND_DOWN).toString();
  }


  public int randomIntWithMaxValue(int max) {
    return (int)(Math.random() * max);
  }


  public String randomString(int len) {
    return Tool.randomString(len);
  }


  public String pinyinFirstLetter(String zh) {
    return ChineseInital.getAllFirstLetter(zh);
  }


  public String formattedNumber(double v, String f) {
    java.text.DecimalFormat df = new java.text.DecimalFormat(f);
    return df.format(v);
  }


  public Object instanceFromJson(String str) {
    return jsonParse(str);
  }


  public Object jsonFromInstance(Object obj) {
    return jsonStringify(obj);
  }


  public Object instanceFromXml(String xmlstr) {
    return Tool.createXmlStream().fromXML(xmlstr);
  }


  public Object xmlFromInstance(Object obj) throws IOException {
    ScriptObjectMirrorJsonConverter.Warp warp
            = new ScriptObjectMirrorJsonConverter.Warp(obj);

    Writer out = new StringWriter();
    out.append(XmlResponse.XML_HEAD);
    Tool.createXmlStream().toXML(warp, out);
    return out.toString();
  }


  public Object emptyToNull(String str) {
    if (str == null) return null;
    str = str.trim();
    if (str.length() <= 0) return null;
    if (str.equalsIgnoreCase("NULL")) return null;
    return str;
  }


  public Object isNumber(Object v) {
    try {
      Double.parseDouble(v.toString());
      return true;
    } catch(Exception e) {
      return false;
    }
  }


  public Object parseInt(Object v) {
    try {
      return Integer.parseInt(v.toString());
    } catch(Exception e) {
      return 0;
    }
  }


  public Object executeJavaScript(Object a, Object b) {
    throw new UnsupportedOperationException("executeJavaScript");
  }


  public void printValue(Object v) {
    if (printList == null) {
      printList = createJSList();
      cd.xres.bindResponse("print",
              new ScriptObjectMirrorJsonConverter.Warp(printList));
    }
    printList.setSlot(printList.size(), v);
  }


  public Object bytes(String s) {
    return wrapBytes( s.getBytes(IConstant.CHARSET) );
  }


  public Object encodeBase64(String v) {
    return encodeBase64( v.getBytes(IConstant.CHARSET) );
  }


  public Object encodeBase64(byte[] b) {
    return wrapBytes( Base64.getEncoder().encode(b) );
  }


  public Object encodeBase64(Buffer.JsBuffer buf) {
    return encodeBase64( buf._buffer().array() );
  }


  public String encodeBase64String(String v) {
    return encodeBase64String( v.getBytes(IConstant.CHARSET) );
  }


  public String encodeBase64String(byte[] b) {
    return Base64.getEncoder().encodeToString(b);
  }


  public String encodeBase64String(Buffer.JsBuffer buf) {
    return encodeBase64String( buf._buffer().array() );
  }


  public Object decodeBase64(String v) {
    return wrapBytes( Base64.getDecoder().decode(v) );
  }


  public Object decodeBase64(byte[] b) {
    return wrapBytes( Base64.getDecoder().decode(b) );
  }


  public Object decodeBase64(Buffer.JsBuffer buf) {
    return decodeBase64( buf._buffer().array() );
  }


  public String decodeBase64String(String v) {
    return new String(Base64.getDecoder().decode(v), IConstant.CHARSET);
  }


  public String decodeBase64String(byte[] v) {
    return new String(Base64.getDecoder().decode(v), IConstant.CHARSET);
  }


  public String decodeBase64String(Buffer.JsBuffer buf) {
    return decodeBase64String( buf._buffer().array() );
  }


  public String md5(String v) {
    return Hex.upperHex(Password.md5(v));
  }


  public String encrypt(String content, String ps) throws Exception {
    AES2 ekey = new AES2(ps);
    return ekey.encrypt(content);
  }


  public String decrypt(String cipher_text, String ps) {
    AES2 ekey = new AES2(ps);
    return new String(ekey.decrypt(cipher_text), IConstant.CHARSET);
  }


  public boolean regexFind(String regex, String str) {
    return Pattern.matches(regex, str);
  }


  public Object regexSplit(String regex, String str) {
    return str.split(regex);
  }


  public String regexReplaceFirst(String regex, String str, String repl) {
    return str.replaceFirst(regex, repl);
  }


  public String regexReplaceAll(String regex, String str, String repl) {
    return str.replaceAll(regex, repl);
  }


  public Object lotteryRate(double[] list) {
    return lotteryRate(list, null);
  }


  /**
   * 俄罗斯轮盘赌 !!
   */
  public int lotteryRate(double[] list, int[] ign) {
    final double d = Math.random() * 101;
    double a = 0;
    int i;

    // ign 如果为 null, 性能最好
    if (ign != null && ign.length > 0) {
      // 防止对 list 的修改
      list = Arrays.copyOf(list, list.length);

      double share = 0;
      int listcount = list.length;

      // 计算要分摊的忽略列表中指定的项
      for (i=0; i<ign.length; ++i) {
        int index = ign[i];
        if (list[index] < 0) continue;
        share += list[index];
        list[index] = -1;
        --listcount;
      }

      // 将分摊值加入正常项
      share = share / listcount;
      for (i=0; i<list.length; ++i) {
        if (list[i] >= 0) {
          list[i] += share;
        }
      }
    }

    for (i=0; i<list.length; ++i) {
      if (list[i] >= 0) {
        a += list[i];
        if (d < a) {
          break;
        }
      }
    }
    return i;
  }


  /**
   * 从文件中解析 csv
   * @param fileInfo [dirname(忽略), filename, charset]
   * @see #parseCsv(Reader, String, String, String, String[], int)
   */
  public Object csvToList(String[] fileInfo, String delimiter, String quote,
                          String escape, String[] header, int preview) {
    String dir = Directory.get(cd);
    try (FileInfo info = PrimitiveOperation.me().openReadFile(dir, fileInfo[1])) {
      InputStreamReader reader = new InputStreamReader(info.input, fileInfo[2]);
      return parseCsv(reader, delimiter, quote, escape, header, preview);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 解析 csv 字符串
   * @see #parseCsv(Reader, String, String, String, String[], int)
   */
  public Object csvToList(String content, String delimiter, String quote,
                          String escape, String[] header, int preview) {
    StringReader reader = new StringReader(content);
    return parseCsv(reader, delimiter, quote, escape, header, preview);
  }


  /**
   * 解析 csv 文本, 返回 list 对象
   *
   * @param read 读取 read 中的文本
   * @param delimiter csv 列分隔符
   * @param quote csv 使用引号来包装一个列值, quote 来定义引号,
   * @param escape (忽略) 使用引号包装后, 输出引号前的转义符号
   * @param header 自定义表头, null 或空数组则从文件中解析
   * @param preview >0 则只输出指定的行, 否则输出全部
   * @return
   */
  private Object parseCsv(Reader read, String delimiter, String quote,
                          String escape, String[] header, int preview) {
    CsvPreference.Builder cb = new CsvPreference.Builder(
            quote.charAt(0), delimiter.charAt(0), "\r\n");

    try (CsvMapReader csv = new CsvMapReader(read, cb.build())) {
      if (header == null || header.length < 1) {
        header = csv.getHeader(true);
      }
      ScriptObjectMirror list = createJSList();

      Map<String, String> row;
      for (int i=0; ;++i) {
        row = csv.read(header);
        if (row == null) break;
        if (preview > 0 && i >= preview-1) break;
        list.setSlot(i, createJSObject(row));
      }

      read.close();
      return list;
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 转换 list 数据为 csv, 并保存在文件中.
   *
   * @param dir 目录, (忽略 仅为兼容设计)
   * @param filename 保存文件名
   * @param charset 文件编码
   * @param list 数据
   */
  public void listToCsv(String dir, String filename,
                        String charset, Object[] list) throws IOException {
    dir = Directory.get(cd);
    StringBufferOutputStream output = new StringBufferOutputStream();

    try (CsvMapWriter csv = new CsvMapWriter(
            output.openWrite(charset), CsvPreference.STANDARD_PREFERENCE) ) {

      ScriptObjectMirror firstRow = wrap(list[0]);
      String[] header = firstRow.getOwnKeys(false);
      csv.writeHeader(header);

      for (int i = 0; i < list.length; ++i) {
        if (list[i] instanceof Map) {
          csv.write((Map) list[i], header);
        }
        else if (list[i] instanceof ScriptObject) {
          ScriptObjectMirror js = wrap(list[i]);
          csv.write(js, header);
        }
        else {
          throw new XBosonException("bad type:" + list[i]);
        }
      }

      csv.flush();
      PrimitiveOperation.me().updateFile(
              dir, filename, output.openInputStream());
    }
  }


  /**
   * 将符合条件的 clist 中的元素附加到 plist 元素的 keyname 属性上.
   * associate 可以设置多个属性, 他们是 '并且' 的关系.
   * 生成的 keyname 属性是一个数组, 数组中保存着复制来的 clist 中的元素.
   *
   * @param plist 元素是 object 对象
   * @param clist 元素是 object 对象
   * @param associate [[k1, k2],[...], ...] k1 是 plist 属性名, k2 是 clist 属性名.
   * @param keyname 在 plist 中创建的数组属性名称
   */
  public void setRetList(Object[] plist, Object[] clist,
                         Object[] associate, String keyname) {
    Map<String, ScriptObjectMirror> cache = new HashMap<>(clist.length);
    final int PARENT_KEY = 0;
    final int CHILD_KEY = 1;

    for (int i=0; i<clist.length; ++i) {
      ScriptObjectMirror cobj = wrap(clist[i]);
      StringBuilder allkey = new StringBuilder();

      for (int a=0; a<associate.length; ++a) {
        ScriptObjectMirror ass = wrap(associate[a]);
        String key = (String) ass.getSlot(CHILD_KEY);
        Object val = cobj.get(key);
        String pkey = (String) ass.getSlot(PARENT_KEY);
        allkey.append(pkey).append('=').append(val).append(';');
      }

      String complex_key = allkey.toString();
      ScriptObjectMirror arr = cache.get(complex_key);
      if (arr == null) {
        arr = createJSList();
        cache.put(complex_key, arr);
      }
      arr.setSlot(arr.size(), clist[i]);
    }

    for (int i=0; i<plist.length; ++i) {
      ScriptObjectMirror pobj = wrap(plist[i]);
      StringBuilder allkey = new StringBuilder();

      for (int a=0; a<associate.length; ++a) {
        ScriptObjectMirror ass = wrap(associate[a]);
        String key = (String) ass.getSlot(PARENT_KEY);
        Object val = pobj.get(key);
        allkey.append(key).append('=').append(val).append(';');
      }

      Object cobj = cache.get(allkey.toString());
      if (cobj != null) {
        pobj.put(keyname, cobj);
      } else {
        pobj.put(keyname, createJSList());
      }
    }
  }


  /**
   * 将平行的 list 数据转换为深层 tree 格式; 数据对象根据属性 child_key 来寻找含有
   * 属性 parent_key 的数据对象, 并将自身附加到属性名 keyname 的数组上;
   * 如果数据对象 parent_key 为 null, 则认为是根节点;
   * 支持无限深层的 tree 数据格式.
   *
   * @param dataList 原始数据
   * @param parent_key 父节点属性名称
   * @param child_key 子节点属性名称
   * @param keyname 生成的子节点数组
   * @return 返回 tree 格式的数据
   */
  public Object transformTreeData(Object[] dataList, String parent_key,
                                  String child_key, String keyname) {
    Map<String, ScriptObjectMirror> mapping = new HashMap<>(dataList.length);
    ScriptObjectMirror root = createJSList();

    for (int i=0; i<dataList.length; ++i) {
      ScriptObjectMirror cobj = wrap(dataList[i]);
      String pkey = cobj.getMember(parent_key).toString();
      mapping.put(pkey, cobj);

      Object ckey = cobj.getMember(child_key);
      if (ckey == null || Tool.isNulStr(ckey.toString())) {
        root.setSlot(root.size(), cobj);
      }
    }

    for (int i=0; i<dataList.length; ++i) {
      ScriptObjectMirror cobj = wrap(dataList[i]);
      Object chkey = cobj.getMember(child_key);

      if (chkey != null) {
        ScriptObjectMirror parent = mapping.get(chkey.toString());

        if (parent != null) {
          ScriptObjectMirror child_list;
          if (parent.hasMember(keyname)) {
            child_list = (ScriptObjectMirror) parent.getMember(keyname);
          } else {
            child_list = createJSList();
            parent.setMember(keyname, child_list);
          }

          child_list.setSlot(child_list.size(), dataList[i]);
        }
      }
    }
    return root;
  }


  /**
   * !!! 这个实现可能不正确 !!!
   */
  public Object getRelatedTreeData(Object[] all, Object[] filter,
                                   String parent_attr, String child_attr) {
    ScriptObjectMirror ret  = createJSList();
    Set<String> allset      = array2Set(all, parent_attr);
    Set<String> filterset   = array2Set(filter, parent_attr);
    int jsi = ret.size() - 1;

    for (int i=0; i<filter.length; ++i) {
      ret.setSlot(++jsi, filter[i]);
    }

    for (int i=0; i<all.length; ++i) {
      ScriptObjectMirror cobj = wrap(all[i]);
      if (cobj.hasMember(child_attr)) {
        Object pk = String.valueOf( cobj.getMember(child_attr) );

        if (allset.contains(pk) && filterset.contains(pk) == false) {
          ret.setSlot(++jsi, all[i]);
        }
      }
    }
    return ret;
  }


  public boolean isEmpty(String o) {
    return o == null || o.length() < 1;
  }


  public boolean isEmpty(Object[] arr) {
    return arr == null || arr.length == 0;
  }


  public boolean isEmpty(ScriptObject js) {
    return js.isEmpty();
  }


  public boolean isEmpty(Object n) {
    return n == null;
  }


  public String toString(Object o) {
    return o.toString();
  }


  public boolean toBool(boolean b) {
    return b;
  }


  public boolean toBool(String s) {
    return s != null && (s.equals("1") || s.equalsIgnoreCase("true"));
  }


  public boolean toBool(Number n) {
    return n.intValue() > 0;
  }


  public boolean toBool(Object o) {
    return false;
  }


  public char charAt(String str, int index) {
    return str.charAt(index);
  }


  public int indexOf(String str, String find) {
    return str.indexOf(find);
  }


  public int size(ScriptObject js) {
    return js.size();
  }


  public boolean startWith(String str, String begin) {
    return str.startsWith(begin);
  }


  public boolean endWith(String str, String end) {
    return str.endsWith(end);
  }


  public int length(String str) {
    return str.length();
  }


  public String subString(String str, int begin) {
    return str.substring(begin);
  }


  public String subStringTo(String str, int begin, int end) {
    return str.substring(begin, end);
  }


  public Object split(String str, String regex) {
    String[] s = str.split(regex);
    ScriptObjectMirror js = createJSList(s.length);
    for (int i=0; i<s.length; ++i) {
      js.setSlot(i, s[i]);
    }
    return js;
  }


  public boolean contain(String str, String sub) {
    return str.contains(sub);
  }


  public String toLowerCase(String str) {
    return str.toLowerCase();
  }


  public String toUpperCase(String str) {
    return str.toUpperCase();
  }


  public String replace(String str, String what, String replacement) {
    return str.replace(what, replacement);
  }


  public String format(String format, Object[] parm) {
    return MessageFormat.format(format, parm);
  }


  public String trim(String s) {
    return s.trim();
  }


  public double trunc(double d, int scale) {
    return BigDecimal.valueOf(d)
            .setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
  }


  /**
   * 压缩 list 到 path 目录中, 动态生成文件.
   * 尝试提前打开 blob 输出流, 而不是将数据堆积在内存.
   *
   * @param list 要压缩的数据列表, 数据结构: [{name: 压缩项目名, content: 内容字符串,
   *             path: 读取文件路径}, {..}, ...], content/path 只能选一个使用.
   * @param path 保存目录, 基于当前用户目录的子目录
   * @return 返回生成的文件名
   */
  public String listToZip(Object[] list, final String path) throws Exception {
    String base = Directory.get(cd);
    String dir  = Tool.normalize( base + '/' + path);
    String file = Tool.uuid.v4() + ".zip";

    StringBufferOutputStream buf = new StringBufferOutputStream();
    ZipOutputStream zip = new ZipOutputStream(buf);

    for (int i=0; i<list.length; ++i) {
      ScriptObjectMirror obj = wrap(list[i]);
      ZipEntry zipEntry = new ZipEntry(obj.getMember("name").toString());
      zip.putNextEntry(zipEntry);

      if (obj.hasMember("content")) {
        String content = (String) obj.getMember("content");
        zip.write(content.getBytes(IConstant.CHARSET));
        continue;
      }

      if (obj.hasMember("path")) {
        String ipath = (String) obj.getMember("path");
        try (FileInfo in = PrimitiveOperation.me().openReadFile(base, ipath)) {
          Tool.copy(in.input, zip, false);
        }
        continue;
      }

      throw new XBosonException("Nothing to write zip file. " +
              "list[{ name, content, path }], must set content or path.");
    }

    zip.closeEntry();
    zip.finish();
    zip.flush();

    PrimitiveOperation.me().updateFile(dir, file, buf.openInputStream());
    return file;
  }


  /**
   * 解压缩文件, 返回解压的数据
   *
   * @param path 压缩文件路径, 基于当前用户目录的子目录
   * @param filename 压缩文件名
   * @return 解压的 list 数据对象
   */
  public Object zipToList(String path, String filename) throws Exception {
    String base = Directory.get(cd);
    String dir  = Tool.normalize( base + '/' + path);
    ScriptObjectMirror arr = createJSList();
    int reti = arr.size()-1;

    try (FileInfo file = PrimitiveOperation.me().openReadFile(dir, filename)) {
      ZipInputStream zip = new ZipInputStream(file.input);

      for (;;) {
        ZipEntry entry = zip.getNextEntry();
        if (entry == null)
          break;

        ScriptObjectMirror jentry = createJSObject();
        arr.setSlot(++reti, jentry);
        jentry.setMember("name", entry.getName());

        StringBufferOutputStream buf = new StringBufferOutputStream();
        Tool.copy(zip, buf, false);
        jentry.setMember("content", buf.toString());
      }

      zip.close();
    }
    return arr;
  }


/////////////////////////////////////////////////////////////// ---- //
// Not implements Functions
/////////////////////////////////////////////////////////////// ---- //


  public String setReportData(String fileName, Object[] data,
                              String tmpPath, String downPath) {
    throw new UnsupportedOperationException("setReportData");
  }


  public String convertCsvToXls(Object... p) {
    throw new UnsupportedOperationException("convertCsvToXls");
  }


  /**
   * 没有 api 用到这个函数
   */
  public void bizLog(String logid, Object... parms) {
    throw new UnsupportedOperationException("bizLog");
  }
}
