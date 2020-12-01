////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-30 下午12:08
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/Neo4jPool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.event.OnExitHandle;
import com.xboson.event.timer.ResourceCleanup;
import com.xboson.util.c0nst.IConstant;
import org.neo4j.driver.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Neo4j driver 内部自行管理连接池, 该类管理 driver
 * https://neo4j.com/docs/api/java-driver/4.2/
 */
public class Neo4jPool extends OnExitHandle {

  private static Neo4jPool me;
  private final Map<IKey, Driver> pool;
  private final Config config;
  private final ResourceCleanup<IKey> rc;


  public static Neo4jPool me() {
    if (me == null) {
      synchronized (Neo4jPool.class) {
        if (me == null) {
          me = new Neo4jPool();
        }
      }
    }
    return me;
  }


  private Neo4jPool() {
    this.config = defaultConfig();
    this.pool = new HashMap<>();
    this.rc = new ResourceCleanup<IKey>("neo4j-pool", pool);
  }


  @Override
  protected void exit() {
    for (Driver d : pool.values()) {
      d.close();
    }
    pool.clear();
  }


  private Config defaultConfig() {
    return Config.builder()
            .withFetchSize(100)
            .withMaxConnectionPoolSize(20)
            .withConnectionTimeout(IConstant.RESPONSE_ACCEPTABLE_TIMEOUT, TimeUnit.MILLISECONDS)
            .withConnectionAcquisitionTimeout(IConstant.RESPONSE_ACCEPTABLE_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();
  }


  /**
   * 返回的对象不保证随时可用, 当资源超时会被销毁
   */
  public Driver open(IKey key) {
    synchronized (rc.lock) {
      Driver d = pool.get(key);
      if (d == null) {
        d = GraphDatabase.driver(key.uri(), key.auth(), config);
        pool.put(key, d);
      }
      return d;
    }
  }


  public Driver open(String uri) {
    return open(new KeyNoAuth(uri));
  }


  public Driver open(String uri, String username, String password) {
    return open(new KeyWithAuth(uri, username, password));
  }


  public interface IKey extends ResourceCleanup.IKeyTime {
    AuthToken auth();
    String uri();
  }


  private abstract class AbsKey implements IKey {
    private long last;

    AbsKey() {
      update();
    }

    void update() {
      this.last = System.currentTimeMillis();
    }

    @Override
    public long freezingTime() {
      return System.currentTimeMillis() - last;
    }


    @Override
    public void release(Object v) {
      ((Driver)v).close();
    }
  }


  private class KeyNoAuth extends AbsKey implements IKey {
    private String uri;


    private KeyNoAuth(String u) {
      this.uri = u;
    }


    @Override
    public boolean equals(Object o) {
      super.update();
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      return uri.equals(((KeyNoAuth) o).uri);
    }


    @Override
    public int hashCode() {
      return uri.hashCode();
    }


    @Override
    public AuthToken auth() {
      return AuthTokens.none();
    }


    @Override
    public String uri() {
      return uri;
    }
  }


  private class KeyWithAuth extends AbsKey implements IKey {
    private String u, p, uri;


    private KeyWithAuth(String uri, String user, String pass) {
      this.u = user;
      this.p = pass;
      this.uri = uri;
    }


    public AuthToken auth() {
      return AuthTokens.basic(u, p);
    }


    @Override
    public String uri() {
      return uri;
    }


    public String toString() {
      return uri +"/"+ u +"/"+ p;
    }


    @Override
    public boolean equals(Object o) {
      super.update();
      if (this == o) return true;
      if (o == null || KeyWithAuth.class != o.getClass()) return false;
      KeyWithAuth auth = (KeyWithAuth) o;
      return Objects.equals(uri, auth.uri) &&
              Objects.equals(u, auth.u) &&
              Objects.equals(p, auth.p);
    }


    @Override
    public int hashCode() {
      return Objects.hash(uri, u, p);
    }
  }
}
