/* CatfoOD 2017年11月5日 上午11:47:37 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;


import com.xboson.util.IConversion;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.objects.NativeArrayBuffer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于辅助 js 对象与 java 对象桥接和转换.
 */
public abstract class JSObject implements IJSObject {


	@Override
	public boolean freeze() {
		return true;
	}


	@Override
	public void init() {
	}


	@Override
	public void destory() {
	}


	@Override
	public String env_name() {
		return null;
	}


///////////////////////////////////////////////////////////////////////////////
////-- 静态 函数/属性
///////////////////////////////////////////////////////////////////////////////

  public final static JSToJava default_convert = new JSToJava();
  static {
    default_convert.add(new PrimitiveConvert(
            Integer.class,    Integer.TYPE));
    default_convert.add(new PrimitiveConvert(
            Long.class,       Long.TYPE));
    default_convert.add(new PrimitiveConvert(
            Boolean.class,    Boolean.TYPE));
    default_convert.add(new PrimitiveConvert(
            Character.class,  Character.TYPE));
    default_convert.add(new PrimitiveConvert(
            Byte.class,       Byte.TYPE));
    default_convert.add(new PrimitiveConvert(
            Short.class,      Short.TYPE));
    default_convert.add(new PrimitiveConvert(
            Float.class,      Float.TYPE));
    default_convert.add(new PrimitiveConvert(
            Double.class,     Double.TYPE));

    default_convert.add(new _JSArray());
  }


  /**
   * 提取 js 传入的 ArrayBuffer 对象的底层存储缓冲区,
   * 反射并调用了 private 方法, 不同的 jdk 版本会不兼容.
   *
   * 测试的 jdk 版本:
   *    build 1.8.0_66-b17
   *
   * @param jsArrayBuffer ArrayBuffer 对象, 或 Uint16Array.buffer 属性
   * @return ByteBuffer 类型对象
   *
   * @throws NoSuchMethodException - 版本不兼容可能抛出
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  public static ByteBuffer getUnderlyingBuffer(Object jsArrayBuffer)
          throws NoSuchMethodException, InvocationTargetException,
          IllegalAccessException {
    ScriptObjectMirror sobj = (ScriptObjectMirror) jsArrayBuffer;

    if (!sobj.getClassName().equals("ArrayBuffer"))
        throw new ClassCastException("is not ArrayBuffer object");

    Method getScriptObject = sobj.getClass()
            .getDeclaredMethod("getScriptObject");
    getScriptObject.setAccessible(true);
    NativeArrayBuffer nad
            = (NativeArrayBuffer) getScriptObject.invoke(sobj);

    Method cloneBuffer = nad.getClass()
            .getDeclaredMethod("getNioBuffer");
    cloneBuffer.setAccessible(true);
    ByteBuffer buf = (ByteBuffer) cloneBuffer.invoke(nad);

    return buf;
  }


///////////////////////////////////////////////////////////////////////////////
////-- 辅助 接口/类
///////////////////////////////////////////////////////////////////////////////

  /**
   * Helper 配置器
   */
	public interface IConfig {
    /**
     * 对 Helper 对象进行配置
     * @param target
     */
	  void config(Helper target);
  }


  /**
   * 转换器描述
   */
  public interface IConversionDesc extends IConversion<Object,Object> {
    Class<?> valueClass();
  }


	static public class Helper extends AbstractJSObject {
    private Map<String, AbstractJSObject>
            attributes = new HashMap<>();

    protected void addAttr(String name, AbstractJSObject value) {
      attributes.put(name, value);
    }

    protected boolean hasAttr(String name) {
       return attributes.containsKey(name);
    }

    /**
     * 如果重写需要调用
     */
    public Object getMember(String name) {
      return attributes.get(name);
    }

    /**
     * 应用一个配置器
     * @param iconfig - 配置器的 class 必须有无参构造.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public boolean config(Class<? extends IConfig> iconfig) {
      try {
        return config(iconfig.newInstance());
      } catch(Exception e) {
        return false;
      }
    }

    /**
     * 应用一个配置器
     */
    public boolean config(IConfig c) {
      c.config(this);
      return true;
    }
  }


  /**
   * 配置 JSObjectHelper, 使导出的公共方法可以被 js 代码调用.
   */
	static public class ExportsFunction implements IConfig {

    @Override
    public void config(Helper target) {
      Class<?> myself = target.getClass();
	    Method[] methods = myself.getMethods();

      for (int i=0; i<methods.length; ++i) {
        Method m = methods[i];
        final String name = m.getName();

        if (target.hasAttr(name))
          continue;

        AbstractJSObject inv = createFunctionProxy(myself, name);
        target.addAttr(name, inv);
      }
    }

    /**
     * 创建本类函数的 js 函数代理
     * @param name 函数名
     * @return 函数代理
     */
    private AbstractJSObject createFunctionProxy(
            Class<?> myself, final String name) {
	    return new AbstractJSObject() {
        public Object call(Object thiz, Object... args) {
          try {
            Method m = myself.getMethod(name,
                    Tool.getClasses(args, default_convert));
            return m.invoke(thiz, args);

          } catch(InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
          } catch(Exception e) {
            throw new RuntimeException(e);
          }
        }

        public boolean isFunction() {
          return true;
        }
      };
    }
  }


  /**
   * 转换器的容器
   */
  static public class JSToJava implements IConversion<Object, Object> {
	  private Map<Class, IConversion<Object,Object>> map;

    JSToJava() {
      map = new HashMap<>();
    }

    public Object value(Object obj) {
      IConversion<Object,Object> c = map.get(obj.getClass());
      if (c != null) {
        return c.value(obj);
      }
      return obj;
    }

    public Class<?> type(Class<?> _class) {
      IConversion<Object,Object> c = map.get(_class);
      if (c != null) {
        return c.type(_class);
      }
      return _class;
    }

    public void add(IConversionDesc desc) {
      map.put(desc.valueClass(), desc);
    }
  }


  /**
   * 将原始类型的打包类型, 映射为原始类型
   */
  static public class PrimitiveConvert implements IConversionDesc {
    private Class<?> from;
    private Class<?> to;

    public PrimitiveConvert(Class<?> from, Class<?> to) {
      this.from = from;
      this.to   = to;
    }
    public Class<?> valueClass() {
      return from;
    }
    public Object value(Object obj) {
      return obj;
    }
    public Class<?> type(Class<?> c) {
      return to;
    }
  }


  /**
   * 将 js 数组转换为 java 对象数组
   */
  static public class _JSArray implements IConversionDesc {
    public Class<?> valueClass() {
      return NativeArray.class;
    }
    public Object value(Object obj) {
      NativeArray na = (NativeArray) obj;
      return na.asObjectArray();
    }
    public Class<?> type(Class<?> _class) {
      return Object[].class;
    }
  }


}
