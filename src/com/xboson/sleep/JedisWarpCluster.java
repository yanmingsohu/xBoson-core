////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-5-22 下午7:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/JedisWarpCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import com.xboson.util.AutoCloseableProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisRedirectionException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class JedisWarpCluster implements InvocationHandler {

  private final JedisCluster cluster;
  private final Jedis proxy;


  public JedisWarpCluster(JedisCluster clu) {
    cluster = clu;
    Class c = Jedis.class;
    proxy = (Jedis) Proxy.newProxyInstance(c.getClassLoader(),
            c.getInterfaces(), this);
  }


  public Jedis getProxy() {
    return proxy;
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
    final String name = method.getName();

    switch (name) {
      case "close":
      case "shutdown":
        return null;

      default:
        Class[] pt =  method.getParameterTypes();
        Method clu_method = cluster.getClass().getMethod(name, pt);
        return clu_method.invoke(cluster, args);
    }


  }
}
