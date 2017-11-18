////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午1:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/sql/SqlReader.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.sql;

import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.SqlResult;
import com.xboson.util.StringBufferOutputStream;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;


/**
 * 读取本目录中的 sql 语句
 */
public class SqlReader {

  private final static Map<String, String> sqlCache = new HashMap<>();


  /**
   * 返回该类所在包下的 sql 文内容.
   */
  public static String read(String file) {
    String ret = sqlCache.get(file);
    if (ret != null)
      return ret;

    synchronized (sqlCache) {
      ret = sqlCache.get(file);
      if (ret != null)
        return ret;

      URL sqlfile = SqlReader.class.getResource("./" + file);
      if (sqlfile == null) {
        sqlfile = SqlReader.class.getResource("./" + file + ".sql");
      }
      if (sqlfile == null) {
        throw new XBosonException("cannot found .sql file: " + file);
      }

      StringBufferOutputStream str = new StringBufferOutputStream();
      try {
        str.write(sqlfile.openStream());
        ret = str.toString();
        sqlCache.put(file, ret);
      } catch (IOException e) {
        throw new XBosonException("read file: " + file, e);
      }
    }

    return ret;
  }


  /**
   * 执行 sql 文, 返回一个连接和数据的封装包, 可以对数据做进一步处理.
   *
   * @param filename 保存 sql 文的文件名
   * @param config 数据库连接配置
   * @param parm sql 文绑定数据
   * @return 对 sql 相关对象的封装
   * @throws XBosonException.XSqlException
   */
  public static SqlResult query(String filename, ConnectConfig config, Object...parm) {
    String sql = read(filename);
    try {
      Connection conn = DbmsFactory.me().open(config);
      PreparedStatement ps = conn.prepareStatement(sql);

      if (parm != null) {
        for (int i=0; i<parm.length; ++i) {
          ps.setObject(i+1, parm[i]);
        }
      }
      return new SqlResult(conn, ps);

    } catch(Exception e) {
      throw new XBosonException.XSqlException(sql, e);
    }
  }
}
