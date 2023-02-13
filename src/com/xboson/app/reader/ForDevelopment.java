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
// 文件创建日期: 17-12-16 上午10:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/reader/ForDevelopment.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.reader;

import com.xboson.app.ApiPath;
import com.xboson.app.XjOrg;
import com.xboson.been.XBosonException;
import com.xboson.db.SqlResult;
import com.xboson.fs.script.ScriptAttr;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ForDevelopment extends AbsReadScript {

  @Override
  public ScriptFile read(XjOrg org, String app, String mod, String api) {
    log.debug("Script From DB", mod, api);
    Object[] parm = new Object[] { app, mod, api };

    try (SqlResult res = org.query("open_api.sql", parm)) {
      ResultSet rs = res.getResult();
      ScriptAttr attr = new ScriptAttr();

      if (rs.next()) {
        if (! ZR001_ENABLE.equals(rs.getString("status")) ) {
          throw new XBosonException("API 已经禁用");
        }

        ScriptFile file = makeFile(attr,
                rs.getString("content"), rs.getInt("zip"));

        attr.fileSize   = file.content.length;
        attr.fileName   = api;
        attr.pathName   = '/' + mod;
        attr.fullPath   = ApiPath.toFile(mod, api);
        attr.createTime = rs.getDate("createdt").getTime();
        attr.modifyTime = rs.getDate("updatedt").getTime();

        log.debug("Load Script from DB:", mod, '/', api);
        return file;
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
    throw new XBosonException.NotFound("API:" + api);
  }


  @Override
  public String logName() {
    return "read-dev-api";
  }
}
