////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午1:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/sql/SqlResult.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

import com.xboson.been.XBosonException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 该结果集封装了 Connect, ResultSet 等对象,
 * 在完成查询后应该调用 close 关闭, 更好的方式是使用 try(SqlResult r = ...) {}
 */
public class SqlResult implements AutoCloseable {

  private Connection conn;
  private PreparedStatement ps;
  private boolean is_update;


  public SqlResult(Connection conn, PreparedStatement ps) throws SQLException {
    this.conn = conn;
    this.ps = ps;
    this.is_update = ! ps.execute();
  }


  /**
   * 返回查询结果集, 如果查询不是 select, 则返回 null.
   */
  public ResultSet getResult() {
    if (is_update) return null;
    try {
      return ps.getResultSet();
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
  }


  /**
   * 如果语句只是更新, 没有返回结果集.
   */
  public boolean isUpdate() {
    return is_update;
  }


  /**
   * 关闭所有资源
   */
  @Override
  public void close() {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
  }


  /**
   * 将结果数据库集转换为列表对象并返回, 每行数据是一个 map.
   * 适合小数据集的转换, 并将结果附加在 json 上.
   */
  public List resultToList() throws SQLException {
    List<Map<String, Object>> ret = new ArrayList<>();
    try (ResultSet rs = getResult()) {
      ResultSetMetaData meta = rs.getMetaData();
      int cc = meta.getColumnCount();

      while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        ret.add(row);
        for (int i=1; i<=cc; ++i) {
          row.put(meta.getColumnLabel(i), rs.getObject(i));
        }
      }
    }
    return ret;
  }
}
