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
// 文件创建日期: 2017年11月7日 12:38
// 原始文件路径: xBoson/src/com/xboson/script/safe/SafeBinding.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.safe;


import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;


/**
 * !! 废弃
 * 用于阻止不安全的全局变量访问.
 */
public class SafeBinding implements InvocationHandler {

  private Bindings hide_bind;
  private Object hide_global;


  public SafeBinding(Bindings o) {
    hide_bind = o;
  }


  public static void warpSafeBinding(ScriptEngine se) {
    Bindings bind = se.getBindings(ScriptContext.ENGINE_SCOPE);
    InvocationHandler handler = new SafeBinding(bind);

    Bindings bind_proxy = (Bindings) Proxy.newProxyInstance(
            bind.getClass().getClassLoader(),
            new Class<?>[] { Bindings.class, JSObject.class },
            handler);

    se.setBindings(bind_proxy, ScriptContext.ENGINE_SCOPE);

    Object o = se.getContext().getAttribute(NashornScriptEngine.NASHORN_GLOBAL);
    System.out.println(o);
  }


  public void warpSafeGlobal(Object g) {
    InvocationHandler handler = new SafeGlobal();

    hide_global = Proxy.newProxyInstance(
            g.getClass().getClassLoader(),
            new Class<?>[] { JSObject.class },
            handler);
  }


  @Override
  public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
    System.out.println(" bind call@ "
            + method.getName() + " " + Arrays.deepToString(objects));

    if (objects[0].equals(NashornScriptEngine.NASHORN_GLOBAL)) {
      if (hide_global == null && method.getName().equals("put")) {
        Object global = objects[1];
        System.out.println("!!!! " + global.getClass() +" "+ global);
        System.out.println("#### " + (hide_bind == global));
        warpSafeGlobal(global);
      } else if (hide_global != null && method.getName().equals("get")) {
        return hide_global;
      }
    }

    return method.invoke(hide_bind, objects);
  }


  public class SafeGlobal implements InvocationHandler {
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      System.out.println(" global call@ "
              + method.getName() + " " + Arrays.deepToString(objects));
      return method.invoke(hide_global, objects);
    }
  }
}