/* CatfoOD 2017年11月5日 上午11:47:37 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;


import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.AbstractJSObject;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认总是冻结的且不可变变量值.
 * 类中的静态方法用于辅助 js 对象与 java 桥接.
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


	public interface IConfig {
	  void config(JSObjectHelper target);
  }


	static public class JSObjectHelper extends AbstractJSObject {
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
  }


  /**
   * 配置 JSObjectHelper, 使导出的公共方法可以被 js 代码调用.
   */
	static public class ExportsFunction implements IConfig {

    @Override
    public void config(JSObjectHelper target) {
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
            Method m = myself.getMethod(name, Tool.getClasses(args));
            return m.invoke(thiz, args);
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

}
