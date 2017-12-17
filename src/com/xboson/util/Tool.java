////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 上午8:46:25
// 原始文件路径: xBoson/src/com/xboson/util/Tool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.thoughtworks.xstream.XStream;
import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Uuid;
import jdk.nashorn.internal.runtime.ECMAErrors;


public final class Tool {

  private static final ThreadLocal<SimpleDateFormat>
          dataformat = new ThreadLocal<>();

  private static final ThreadLocal<SecureRandom>
          secure_random_thread = new ThreadLocal<>();

  private static SnowflakeIdWorker id;
  private static Moshi moshi;
  private static XStream xml;
  private static com.xboson.script.lib.Path
          p = new com.xboson.script.lib.Path();

  private Tool() {}


  /**
   * 提供 uuid 转换
   */
  public static final Uuid uuid = new Uuid();


  /**
   * 保证性能和线程安全; moshi 内部已经对适配器做了缓存.
   * 返回的适配器已经注册了可用的对象转换器.
   *
   * @see ConverterInitialization
   */
  public static <E> JsonAdapter<E> getAdapter(Class<E> c) {
    if (moshi == null) {
      synchronized (Tool.class) {
        if (moshi == null) {
          Builder jsbuilded = new Moshi.Builder();
          ConverterInitialization.initJSON(jsbuilded);
          moshi = jsbuilded.build();
        }
      }
    }
    return moshi.adapter(c);
  }


  /**
   * 返回的 xml 解析器已经注册了所有可用的转换器,
   * XStream 对象是线程安全的, 可以在全局使用.
   *
   * @see ConverterInitialization
   * @see com.thoughtworks.xstream.annotations.XStreamAlias 转换 xml 时设置别名
   */
  public static XStream createXmlStream() {
    if (xml == null) {
      synchronized (Tool.class) {
        if (xml == null) {
          xml = new XStream();
          xml.autodetectAnnotations(true);
          ConverterInitialization.initXml(xml);
        }
      }
    }
    return xml;
  }


  public static void eq(byte[] a, byte[] b) {
    if (a.length != b.length)
      throw new RuntimeException("bad length " + a.length + " != " + b.length);

    for (int i=0; i<a.length; ++i) {
      if (a[i] != b[i]) {
        throw new RuntimeException("On " + i + " a=" + a[i] + " b=" + b[i] + " not eq");
      }
    }
  }


  /**
   * 代替 System.out.println(..) 支持动态参数
   */
  public static void pl(Object...o) {
    StringBuilder out = new StringBuilder(o.length * 10);
    for (int i=0; i<o.length; ++i) {
      out.append(o[i]);
      out.append(' ');
    }
    System.out.println(out.toString());
  }


  public static void println(byte[] a) {
    for (int i=0; i<a.length; ++i) {
      System.out.print(Integer.toString(a[i], 16) + " " );
      if (i % 16 == 0) System.out.println();
    }
    System.out.println();
  }


  /**
   * 返回完整堆栈字符串
   */
  public static String allStack(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter ps = new PrintWriter(sw);
    e.printStackTrace(ps);
    ps.flush();
    return sw.toString();
  }


  /**
   * 堆栈只保留 xboson 对象, 和脚本消息, Cause 中的消息也会被包含
   * @see XBosonException#filterStack(Throwable, StringBuilder)
   */
  public static void xbosonStack(Throwable e, StringBuilder out) {
    XBosonException.filterStack(e, out);
  }


  /**
   * @see XBosonException#filterStack(Throwable, StringBuilder)
   */
  public static String xbosonStack(Throwable e) {
    StringBuilder out = new StringBuilder();
    xbosonStack(e, out);
    return out.toString();
  }


  /**
   * 返回部分堆栈字符串, 包含至多 count 个堆栈
   */
  public static String miniStack(Throwable e, int count) {
    StringBuilder buff = new StringBuilder();
    StackTraceElement[] trace = e.getStackTrace();
    if (count <= 0) {
      count = trace.length;
    } else {
      count = Math.min(count, trace.length);
    }

    buff.append(e.getClass());
    buff.append(": ");
    buff.append(e.getMessage());

    for (int i=0; i<count; ++i) {
      buff.append("\r\n\t");
      buff.append(trace[i].getClassName());
      buff.append("->");
      buff.append(trace[i].getMethodName());
      buff.append("() [");
      buff.append(trace[i].getFileName());
      buff.append(" :");
      buff.append(trace[i].getLineNumber());
      buff.append("]");
    }

    if (count < trace.length) {
      buff.append("\r\n\t... ");
      buff.append(trace.length - count);
    }

    return buff.toString();
  }


  /**
   * 优化性能, 总是返回完整的日期/时间字符串
   */
  public static String formatDate(Date d) {
    SimpleDateFormat f = dataformat.get();
    if (f == null) {
      f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      dataformat.set(f);
    }
    return f.format(d);
  }


  /**
   * 复制并关闭文件
   * @throws IOException
   */
  public static void copy(InputStream src, OutputStream dst, boolean close) throws IOException {
    try {
      byte[] buff = new byte[1024];
      int len = 0;
      while ((len = src.read(buff)) > 0) {
        dst.write(buff, 0, len);
      }
    } finally {
      if (close) {
        close(src);
        close(dst);
      }
    }
  }


  public static StringBuilder readFromFile(String filename) throws IOException {
    FileReader r = null;
    try {
      StringBuilder out = new StringBuilder();
      r = new FileReader(filename);
      char[] buff = new char[1024];
      int len = 0;
      while ((len = r.read(buff)) > 0) {
        out.append(buff, 0, len);
      }
      return out;
    } finally {
      close(r);
    }
  }


  public static void close(Closeable c) {
    try {
      if (c != null) c.close();
    } catch(Exception e) {
      LogFactory.create().debug(e);
    }
  }


  public static void close(AutoCloseable c) {
    try {
      if (c != null) c.close();
    } catch(Exception e) {
      LogFactory.create().debug(e);
    }
  }


  /**
   * 等待线程结束后返回
   */
  public static void waitOver(Thread t) {
    try {
      if (t != null) t.join();
    } catch(Exception e) {
      LogFactory.create().debug(e);
    }
  }


  public static short reverseBytesShort(short s) {
    int i = s & 0xffff;
    int reversed = (i & 0xff00) >>> 8
        |          (i & 0x00ff)  << 8;
    return (short) reversed;
  }


  public static int reverseBytesInt(int i) {
    return (i & 0xff000000) >>> 24
        |  (i & 0x00ff0000) >>>  8
        |  (i & 0x0000ff00)  <<  8
        |  (i & 0x000000ff)  << 24;
  }

  
  public static long reverseBytesLong(long v) {
    return (v & 0xff00000000000000L) >>> 56
        |  (v & 0x00ff000000000000L) >>> 40
        |  (v & 0x0000ff0000000000L) >>> 24
        |  (v & 0x000000ff00000000L) >>>  8
        |  (v & 0x00000000ff000000L)  <<  8
        |  (v & 0x0000000000ff0000L)  << 24
        |  (v & 0x000000000000ff00L)  << 40
        |  (v & 0x00000000000000ffL)  << 56;
  }


  /**
   * 获取对象数组中每个对象的 class
   * @param args 当函数返回, 元素可能被 convert 改变
   * @param convert 可以 null, 对每个数组元素做数据转换和类型转换
   * @return 返回经过转换的 class 数组
   */
  public static Class<?>[] getClasses(
          Object[] args, IConversion<Object, Object> convert) {
    Class<?>[] classs = new Class<?>[args.length];

    for (int i=0; i<classs.length; ++i) {
      Object arg = args[i];

      if (convert != null) {
        Object old = arg;
        arg        = convert.value(arg);
        classs[i]  = convert.type(old.getClass());
        args[i]    = arg;
      } else {
        classs[i] = arg.getClass();
      }
    }
    return classs;
  }

  /**
   * 返回 i 做为无符号字节的 16 进制字符串
   */
  public static String ubytehex(byte i) {
    int ii = Byte.toUnsignedInt(i);
    return "" + hex(ii >> 4) + hex(ii);
  }


  /**
   * 将 (i & 0x0F) 的值转换为 16 进制字符
   */
  public static char hex(Number i) {
    int b = 0x0F & i.intValue();
    if (b >= 0) {
      if (b < 10) {
        return (char) ((int) '0' + b);
      }
      if (b < 16) {
        return (char) ((int) 'A' + (b-10));
      }
    }
    throw new IllegalArgumentException("number on 0-15");
  }


  /**
   * 返回字符串首字母大写的形式
   */
  public static String upperFirst(String n) {
    return Character.toUpperCase(n.charAt(0)) + n.substring(1);
  }


  /**
   * @see #findPackage(Package)
   * @deprecated 不推荐在正式代码中使用, 可用于测试
   * @param packageName 包名
   * @return 即使找不到类, 也会返回空数组
   */
  public static Set<Class> findPackage(String packageName)
          throws IOException, ClassNotFoundException {
    Package pk = Package.getPackage(packageName);
    if (pk == null)
      throw new XBosonException.NotExist(
              "cannot find package: " + packageName);

    return findPackage(pk);
  }


  /**
   * 获取包下的所有类类型, 排除子包, 排除内部类,
   * java bug: 如果没有访问过这个包, 无法在 findPackage 中查找
   *
   * @deprecated 不推荐在正式代码中使用, 可用于测试
   * @param pk 包
   * @return 即使找不到类, 也会返回空数组
   */
  public static Set<Class> findPackage(Package pk)
          throws IOException, ClassNotFoundException {
    if (pk == null)
      throw new XBosonException.NullParamException("Package pk");

    String packageName = pk.getName();
    String packagePath = packageName.replaceAll("\\.", "/");
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(packagePath);

    String fullPath = url.getPath();
    File[] files = new File(fullPath).listFiles();

    String tmp = fullPath.replaceAll("/|\\\\", ".");
    int prefixLength = tmp.indexOf(packageName) -1;

    Set<Class> ret = new HashSet<>();

    for (int i=0; i<files.length; ++i) {
      File file = files[i];
      String name = file.toString();
      name = name.substring(prefixLength);

      if (name.lastIndexOf(".class") == name.length() - 6) {
        if (name.indexOf('$') >= 0) continue;
        name = name.substring(0, name.length() - 6);
        name = name.replaceAll("/|\\\\", ".");
        ret.add(Class.forName(name));
      }
    }

    return ret;
  }


  /**
   * @see java.lang.Thread#sleep(long)
   */
  public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch(Exception e) {
      LogFactory.create().debug(e);
    }
  }


  public static boolean isNulStr(String s) {
    return s == null || s.trim().length() == 0;
  }


  /**
   * 使用 unix 风格规范化路径
   * @see com.xboson.script.lib.Path#normalize(String)
   */
  public static String normalize(String path) {
    return p.normalize(path);
  }


  /**
   * 生成随机字符串
   * @param charLength 字符数量.
   */
  public static String randomString(int charLength) {
    char[] ch = new char[charLength];
    for (int i=0; i<charLength; ++i) {
      ch[i] = (char) (Math.random() * 94 + 33);
    }
    return new String(ch);
  }


  /**
   * 生成随机长度字节数组
   * @param byteLength 字节数量
   */
  public static byte[] randomBytes(int byteLength) {
    byte[] buf = new byte[byteLength];
    SecureRandom r = secure_random_thread.get();
    if (r == null) {
      r = new SecureRandom();
      secure_random_thread.set(r);
    }
    r.nextBytes(buf);
    return buf;
  }


  /**
   * Twitter 的分布式自增ID算法 snowflake
   */
  public synchronized static long nextId() {
    if (id == null) {
      Config c = SysConfig.me().readConfig();
      long a = c.clusterNodeID & 0x1F;
      long b = (c.clusterNodeID >> 5) & 0x1F;
      id = new SnowflakeIdWorker(a, b);
    }
    return id.nextId();
  }


  /**
   * 利用类加载器加载 class 中的文件, 并返回缓冲区
   */
  public static StringBufferOutputStream readFileFromResource(
          Class<?> base, String filepath) {

    try (InputStream r = base.getResourceAsStream(filepath)) {
      if (r == null) {
        throw new XBosonException.NotExist(
                "File not found:" + filepath);
      }
      StringBufferOutputStream buf = new StringBufferOutputStream();
      buf.write(r, false);
      return buf;
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 将 T 类型数组转换为 Set, 没有附加的处理
   */
  public static<T> Set<T> arr2set(T[] arr) {
    Set<T> set = new HashSet<>();
    for (int i=0; i<arr.length; ++i) {
      set.add(arr[i]);
    }
    return set;
  }


  /**
   * 读取 reader 中所有字符到字符串缓冲区;
   * 该方法仅用于调试, 不应该在生产环境中使用 !
   */
  public static StringBuilder reader2String(Reader r, String ...msg) {
    try {
      System.out.println();
      StringBuilder str = new StringBuilder("\n---[BEGIN]---\n");
      str.append(Arrays.toString(msg));
      char[] ch = new char[256];
      int len = r.read(ch);

      while (len >0) {
        str.append(ch, 0, len);
        len = r.read(ch);
      }
      str.append("\n---[END]---\n");
      return str;
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }
}
