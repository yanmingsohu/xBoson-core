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
// 文件创建日期: 17-11-22 下午2:02
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/XjModule.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.been.XBosonException;
import com.xboson.db.IDict;
import com.xboson.db.SqlResult;
import com.xboson.script.NotFoundModuleMaySkip;

import java.sql.ResultSet;
import java.sql.SQLException;


public class XjModule extends XjPool<XjApi> implements IDict {

  private XjOrg org;
  private XjApp app;
  private String name;
  private String id;


  XjModule(XjOrg org, XjApp app, String id) {
    this.id  = id;
    this.org = org;
    this.app = app;
    init_module();
    log.debug("Module success", id);
  }


  private void init_module() {
    try (SqlResult res = org.query("open_module.sql", app.getID(), id)) {
      ResultSet rs = res.getResult();
      if (rs.next()) {
        if (! ZR001_ENABLE.equals(rs.getString("status")) ) {
          throw new XBosonException("模块已经禁用");
        }
        name = rs.getString("modulenm");
      } else {
        throw new NotFoundModuleMaySkip(id);
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
  }


  public XjApi getApi(String api_id) {
    return getWithCreate(api_id);
  }


  @Override
  protected XjApi createItem(String apiid) {
    return new XjApi(org, app, this, apiid);
  }


  public String id() {
    return id;
  }


  @Override
  public String logName() {
    return "sc-core-module";
  }

}
