////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-8 上午8:11
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/QueryImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.Page;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public class QueryImpl {

  public interface SqlConnect {
    Connection open() throws Exception;
  }


  private RuntimeUnitImpl runtime;
  private SqlConnect sc;


  public QueryImpl(SqlConnect sc, RuntimeUnitImpl runtime) {
    this.sc = sc;
    this.runtime = runtime;
  }


  /**
   * 针对 js 环境, 执行 sql 查询
   *
   * @param runtime 用于创建对象
   * @param list 结果集将绑定在 list 上
   * @param sql 查询
   * @param param 参数绑定
   * @return 查询结果总数
   * @throws Exception
   */
  public int query(ScriptObjectMirror list, String sql, Object[] param)
          throws Exception {

    PreparedStatement ps = sc.open().prepareStatement(sql);

    for (int i=1; i<=param.length; ++i) {
      Object p = param[i-1];
      ps.setObject(i, p);
    }

    ResultSet rs = ps.executeQuery();
    ResultSetMetaData meta = rs.getMetaData();
    int column = meta.getColumnCount();
    int row_count = 0;
    int arri = list.size()-1;

    while (rs.next()) {
      ScriptObjectMirror row = runtime.createJSObject();
      list.setSlot(++arri, row);
      ++row_count;

      for (int c = 1; c<=column; ++c) {
        row.setMember(meta.getColumnLabel(c), rs.getObject(c));
      }
    }

    rs.close();
    ps.close();
    return row_count;
  }


  public int queryPaging(ScriptObjectMirror list, String sql, Object[] param,
                         Page p) throws Exception {
    throw new UnsupportedOperationException("queryPaging");
  }

}
