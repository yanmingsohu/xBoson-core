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
// 文件创建日期: 17-12-10 下午4:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/SqlParserCached.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;

import com.xboson.been.XBosonException;
import com.xboson.util.SysConfig;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;


/**
 * 用于生成缓存的 sql 语法树
 */
public class SqlParserCached extends BaseKeyedPooledObjectFactory<String, ParsedData> {

  /**
   * 小于这个长度的 sql 直接解析不放入缓存
   */
  public static final int CACHE_SQL_LENGTH = 100;


  private static GenericKeyedObjectPool<String, ParsedData> cached;
  static {
    cached = new GenericKeyedObjectPool<>(new SqlParserCached(),
            SysConfig.defaultKeyPoolConfig());
  }


  private SqlParserCached() {}


  @Override
  public ParsedData create(String s) throws Exception {
    return SqlParser.parse(s);
  }


  @Override
  public PooledObject<ParsedData> wrap(ParsedData parsedData) {
    return new DefaultPooledObject<>(parsedData);
  }


  /**
   * 生成的语法树可能来自缓存
   * @param sql
   * @return
   */
  public static ParsedDataHandle parse(String sql) {
    if (sql.length() < CACHE_SQL_LENGTH) {
      return new ParsedDataHandle(SqlParser.parse(sql));
    } else {
      try {
        ParsedData pd = cached.borrowObject(sql);
        return new ParsedDataHandle(sql, pd);
      } catch (Exception e) {
        throw new XBosonException(e);
      }
    }
  }


  /**
   * 解析后 sql 语法树的句柄, 该对象可回收, 对象不可变
   */
  static public class ParsedDataHandle implements AutoCloseable {
    ParsedData pd;
    private String sql;

    public ParsedDataHandle(String sql, ParsedData pd) {
      this.pd = pd;
      this.sql = sql;
    }

    /** 不缓存 */
    public ParsedDataHandle(ParsedData pd) {
      this.pd = pd;
    }

    public void close() {
      if (sql != null) {
        cached.returnObject(sql, pd);
        pd = null;
        sql = null;
      }
    }

    public String toString() {
      return pd.toString();
    }

    protected void finalize() throws Throwable {
      close();
    }
  }
}
