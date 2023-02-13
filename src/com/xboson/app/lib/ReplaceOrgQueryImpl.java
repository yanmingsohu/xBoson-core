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
// 文件创建日期: 17-12-10 下午5:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/ReplaceOrgQueryImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.Page;
import com.xboson.db.ConnectConfig;
import com.xboson.db.analyze.*;
import com.xboson.util.JavaConverter;
import com.xboson.util.SysConfig;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Set;


/**
 * 在查询前会替换 schema
 */
public class ReplaceOrgQueryImpl extends QueryImpl
        implements IUnitListener, IConstant {

  private final String replaceSchemaPrefix;
  private final Set<String> sysTables;


  public ReplaceOrgQueryImpl(SqlConnect sc,
                             RuntimeUnitImpl runtime,
                             String replaceOrg) {
    super(sc, runtime);
    this.replaceSchemaPrefix = replaceOrg + ".";
    this.sysTables = JavaConverter.arr2set(
            SysConfig.me().readConfig().sysTableList);
  }


  @Override
  public int query(ScriptObjectMirror list, String sql, Object[] param)
          throws Exception {
    return super.query(list, replaceSql(sql), param);
  }


  @Override
  public int queryPaging(ScriptObjectMirror list, String sql,
                         Object[] param, Page p, ConnectConfig cc) throws Exception {
    return super.queryPaging(list, replaceSql(sql), param, p, cc);
  }


  public String replaceSql(String sql) {
    SqlContext ctx = new SqlContext();
    SqlParserCached.ParsedDataHandle handle = SqlParserCached.parse(sql);
    SqlParser.tableNames(ctx, handle, this);
    return SqlParser.stringify(ctx, handle);
  }


  @Override
  public void on(SqlContext ctx, IUnit u) {
    String tableName = (String) u.getData();

    //
    // 已经有前缀的不再处理
    //
    if (tableName.indexOf(".") >= 0)
      return;

    if (tableName.startsWith(SYS_TABLE_NOT_REPLACE))
      return;

    if (sysTables.contains(tableName))
      return;

    ctx.set(u, replaceSchemaPrefix + tableName);
  }
}
