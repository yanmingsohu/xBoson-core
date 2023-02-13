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
// 文件创建日期: 17-11-13 上午11:50
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/AppPool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.reader.AbsReadScript;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.util.SysConfig;


/**
 * 应用池维护机构下的所有 app, 这些对象已经缓存, 并且线程安全.
 */
public class AppPool extends XjPool<XjOrg> {

  private ConnectConfig dbcc;
  private AbsReadScript script_reader;


  public AppPool(AbsReadScript reader) {
    if (reader == null) {
      throw new XBosonException.NullParamException("AbsReadScript reader");
    }
    this.dbcc = SysConfig.me().readConfig().db;
    this.script_reader = reader;
  }


  @Override
  protected XjOrg createItem(String id) {
    return new XjOrg(dbcc, id, script_reader);
  }


  /**
   * 创建或获取缓存的 org
   * @param id
   * @return
   */
  public XjOrg getOrg(String id) {
    return super.getWithCreate(id);
  }


  @Override
  public String logName() {
    return "sc-core-pool";
  }
}
