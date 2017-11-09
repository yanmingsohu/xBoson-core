package com.xboson.script.lib;

import com.xboson.script.JSObject;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeArrayBuffer;
import jdk.nashorn.internal.runtime.arrays.ArrayData;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 全局静态方法
 */
public class Buffer {

  public static String DEFAULT_ENCODING = "utf8";


  public JsBuffer alloc(int size, int fill, String encoding) {
    JsBuffer buf = new JsBuffer(encoding);
    buf.buf = ByteBuffer.allocate(size);
    buf.fill((byte) fill);
    return buf;
  }


  public JsBuffer alloc(int size, int fill) {
    return alloc(size, fill, DEFAULT_ENCODING);
  }


  public JsBuffer alloc(int size) {
    return alloc(size, 0, DEFAULT_ENCODING);
  }


  /**
   * 创建的对象不支持 slice();
   */
  public JsBuffer allocUnsafe(int size) {
    JsBuffer buf = new JsBuffer(DEFAULT_ENCODING);
    buf.buf = ByteBuffer.allocateDirect(size);
    return buf;
  }


  /**
   * 创建的对象不支持 slice();
   */
  public JsBuffer allocUnsafeSlow(int size) {
    JsBuffer buf = allocUnsafe(size);
    buf.fill((byte)0);
    return buf;
  }


  public int byteLength(String string, String encoding)
          throws UnsupportedEncodingException {
    return string.getBytes(encoding).length;
  }


  public int byteLength(String str) throws UnsupportedEncodingException {
    return byteLength(str, DEFAULT_ENCODING);
  }


  public int compare(JsBuffer a, JsBuffer b) {
    return a.compare(b);
  }


  public JsBuffer concat(JsBuffer[] list, int totalLength) {
    if (totalLength <= 0) {
      totalLength = 0;
      for (int i=0; i<list.length; ++i) {
        totalLength += list[i].length();
      }
    }

    JsBuffer ret = new JsBuffer(DEFAULT_ENCODING);
    ret.buf = ByteBuffer.allocateDirect(totalLength);
    int pos = 0;

    for (int i=0; i<list.length; ++i) {
      list[i].buf.position(0);
      int len = list[i].length();
      while (len >= 0) {
        ret.buf.put(list[i].buf.get());
        --len;
        if (++pos >= totalLength) {
          return ret;
        }
      }
    }
    return ret;
  }


  public JsBuffer concat(JsBuffer[] list) {
    return concat(list, -1);
  }


  public JsBuffer from(byte[] array, int byteOffset, int length) {
    JsBuffer buf = new JsBuffer(DEFAULT_ENCODING);
    buf.buf = ByteBuffer.wrap(array, byteOffset, length);
    return buf;
  }


  public JsBuffer from(byte[] array, int byteOffset) {
    return from(array, byteOffset, array.length - byteOffset);
  }


  public JsBuffer from(byte[] array) {
    return from(array, 0, array.length);
  }


  public JsBuffer from(String string, String encoding) {
    JsBuffer buf = new JsBuffer(encoding);
    buf.buf = ByteBuffer.wrap(string.getBytes(buf.encoding));
    return buf;
  }


  public JsBuffer from(String string) {
    return from(string, DEFAULT_ENCODING);
  }


  public JsBuffer from(JsBuffer other) {
    return other.clone();
  }


  public JsBuffer from(Object _ArrayBuffer) {
    try {
      ByteBuffer buf = JSObject.getUnderlyingBuffer(_ArrayBuffer);
      JsBuffer ret = new JsBuffer(DEFAULT_ENCODING);
      ret.buf = buf;

      return ret;
    } catch (Exception e) {
      e.printStackTrace();
    }

    throw new RuntimeException(
            "Buffer.from(ArrayBuffer) not implement.");
  }


  public boolean isBuffer(Object o) {
    return o instanceof JsBuffer;
  }


  public boolean isEncoding(String encoding) {
    return Charset.availableCharsets().containsKey(encoding);
  }


  /**
   * 实例对象
   */
  public class JsBuffer extends JSObject.Helper {
    private ByteBuffer buf;
    private Charset encoding;


    public JsBuffer(String encoding) {
      this.encoding = Charset.forName(encoding);
      config(JSObject.ExportsFunction.class);
    }

    @Override
    public Object getSlot(int index) {
      return buf.get(index);
    }


    @Override
    public boolean hasSlot(int slot) {
      return slot >=0 && slot < buf.limit();
    }


    @Override
    public void setSlot(int index, Object value) {
      if (value instanceof Number) {
        byte v = ((Number) value).byteValue();
        buf.put(index, v);
      }
    }

    @Override
    public Object getMember(String name) {
      // 属性优先于方法的获取
      if (name.equals("length")) {
        return length();
      }
      return super.getMember(name);
    }


    public JsBuffer fill(byte value, int offset, int length) {
      for (int i=offset; i<length; ++i) {
        buf.put(i, value);
      }
      return this;
    }


    public JsBuffer fill(byte value, int offset) {
      return fill(value, offset, length() - offset);
    }


    public JsBuffer fill(byte value) {
      return fill(value, 0, buf.limit());
    }


    public JsBuffer fill(String str, int offset, int len, String encoding)
            throws UnsupportedEncodingException {
      return fill(str.getBytes(encoding)[0], offset, len);
    }


    public JsBuffer fill(String str, int offset, int len)
            throws UnsupportedEncodingException {
      return fill(str, offset, len, DEFAULT_ENCODING);
    }


    public JsBuffer fill(String str, int offset) throws UnsupportedEncodingException {
      return fill(str, offset, length()-offset, DEFAULT_ENCODING);
    }


    public JsBuffer fill(String str) throws UnsupportedEncodingException {
      return fill(str.getBytes(DEFAULT_ENCODING)[0], 0, length());
    }


    public int compare(JsBuffer target, int targetStart, int targetEnd,
                       int sourceStart, int sourceEnd) {
      if (target == this) return 0;

      int tlen = targetEnd - targetStart;
      int slen = sourceEnd - sourceStart;
      int len = Math.max(tlen, slen);

      for (int i=0; i<len; ++i) {
        int si = i + sourceStart;
        int ti = i + targetStart;
        if (si >= sourceEnd) return -1;
        if (ti >= targetEnd) return 1;

        byte s = this.buf.get(si);
        byte t = target.buf.get(ti);
        if (s > t) return 1;
        else if (s < t) return -1;
      }
      return 0;
    }


    public int compare(JsBuffer target) {
      return compare(target, 0, target.length(), 0, length());
    }


    public int copy(JsBuffer target, int targetStart,
                    int sourceStart, int sourceEnd) {
      int len = sourceEnd - sourceStart;
      for (int i = 0; i<len; ++i) {
        target.buf.put(i + targetStart, buf.get(i + sourceStart) );
      }
      return 0;
    }


    public int copy(JsBuffer t) {
      return copy(t, 0, 0, length());
    }


    public boolean equals(JsBuffer other) {
      return buf.equals(other.buf);
    }


    public boolean equals(Object[] other) {
      if (other.length != length())
        return false;

      try {
        for (int i = 0; i < other.length; ++i) {
          int a = (int) other[i];
          int b = Byte.toUnsignedInt(buf.get(i));
          if (a != b) {
            return false;
          }
        }
        return true;
      } catch(Exception e) {
        return false;
      }
    }


    public int length() {
      return buf.limit();
    }


    public JsBuffer clone() {
      JsBuffer newbuf = new JsBuffer(encoding.name());
      newbuf.buf = ByteBuffer.allocateDirect(length());
      for (int i=length()-1; i>=0; --i) {
        newbuf.buf.put(i, buf.get(i));
      }
      return newbuf;
    }


    public String toString() {
      StringBuilder out = new StringBuilder();
      out.append("<Buffer");
      int len = Math.min(20, length());
      buf.position(0);
      while (--len >= 0) {
        out.append(" ");
        int d = Byte.toUnsignedInt(buf.get());
        out.append(Tool.hex(d >> 4));
        out.append(Tool.hex(d));
      }
      if (length() > 20) {
        out.append(" ..");
      }
      out.append('>');
      return out.toString();
    }


    public String toString(String encoding, int begin, int length)
            throws UnsupportedEncodingException {
      byte[] tmp = new byte[length];
      buf.position(begin);
      buf.get(tmp, 0, length);
      return new String(tmp, encoding);
    }


    public String toString(String encoding, int begin)
            throws UnsupportedEncodingException {
      return toString(encoding, begin, length() - begin);
    }


    public String toString(String encoding)
            throws UnsupportedEncodingException {
      return toString(encoding, 0, length());
    }


    public Iterator<?> entries() {
      return new Iterator<Object>() {
        int i = 0;
        public boolean hasNext() {
          return i < length();
        }
        public Object next() {
          return buf.get(i++);
        }
      };
    }


    public Iterator<?> keys() {
      return new Iterator<Object>() {
        int i = 0;
        public boolean hasNext() {
          return i < length();
        }
        public Object next() {
          return i++;
        }
      };
    }


    public int indexOf(JsBuffer value, int offset) {
      int len = length();
      int vl = value.length();
      int vp = 0;

      for (int i=offset; i<len; ++i) {
        if (value.buf.get(vp) == buf.get(i)) {
          if (++vp >= vl) {
            return i + 1 - vl;
          }
        } else {
          vp = 0 ;
        }
      }
      return -1;
    }


    public int indexOf(JsBuffer value) {
      return indexOf(value, 0);
    }


    public int indexOf(String str, int offset, String encoding) {
      return indexOf(from(str, encoding), offset);
    }


    public int indexOf(String str, int offset) {
      return indexOf(str, offset, DEFAULT_ENCODING);
    }


    public int indexOf(String str) {
      return indexOf(str, 0, DEFAULT_ENCODING);
    }


    public int indexOf(int _i) {
      final byte b = (byte) _i;
      int len = length();
      for (int i=0; i<len; ++i) {
        if (b == buf.get(i)) {
          return i;
        }
      }
      return -1;
    }


    /**
     * allocUnsafe 创建的对象不支持 array() 方法
     */
    public JsBuffer slice(int start, int end) {
      try {
        byte[] inner = buf.array();
        JsBuffer ret = new JsBuffer(encoding.name());
        ret.buf = ByteBuffer.wrap(inner, start, end);

        return ret;
      } catch(UnsupportedOperationException e) {
        throw new UnsupportedOperationException(
                "cannot slice() use 'allocUnsafe' create Buffer", e);
      }
    }
  }
}
