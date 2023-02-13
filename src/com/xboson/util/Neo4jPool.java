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
