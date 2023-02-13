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
// 文件创建日期: 17-12-10 下午5:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/QueryFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.AppContext;
import com.xboson.db.ConnectConfig;
import com.xboson.util.c0nst.IConstant;


/**
 * QueryImpl 工厂
 * @see QueryImpl
 */
public class QueryFactory implements IConstant {


  /**
   * 在必要时会创建 org 替换的查询对象
   *
   * @param sc 打开数据库连接
   * @param runtime 创建 js 对象
   * @return QueryImpl
   * @see QueryImpl
   */
  public static QueryImpl create(QueryImpl.SqlConnect sc,
                                 RuntimeUnitImpl runtime,
                                 ConnectConfig cc) {
    AppContext ac = AppContext.me();
    if (ac.isReplaceOrg() && (cc.flg != FLG_THIRD_PART)) {
      return new ReplaceOrgQueryImpl(sc, runtime, ac.originalOrg());
    } else {
      return new QueryImpl(sc, runtime);
    }
  }


  private QueryFactory() {}
}
