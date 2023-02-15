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
// 文件创建日期: 2017年12月11日 13:47
// 原始文件路径: xBoson/src/com/xboson/app/lib/string_functions.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

(function() {
//
// 在脚本中, 经常会定义一个 sql 变量, 然后又试图使用 sql.query 执行 db 查询, 显然这是
// 因为 beetl 的设计缺陷允许定义的 sql 变量与系统 sql 模块区分, 在 js 标准中这是绝对不
// 允许的. 为了兼容这种情况, 在 String 对象上绑定与 sql 相关的方法, 这样这些 "错误" 的
// 脚本就可以正常运行了.
//
function __bind_sql_functions(func_name_list) {
  func_name_list.forEach(function(func_name) {

    String.prototype[func_name] = function() {
      var sql = moduleHandleContext.get("sql");
      //
      // java 导入的函数第一个参数是 this.
      //
      var arg = [sql];
      for (var i=0; i<arguments.length; ++i) {
        arg.push(arguments[i]);
      }
      return Function.call.apply(sql[ func_name ], arg);
    }

  });
}


__bind_sql_functions([
  "query", "queryPaging", "update", "updateBatch", "metaData",
  "commit", "rollback", "currentDBTimeString", "connection",
  "dbType", "msAccessConnection",
]);


//
// Function.call.apply 不能正确的调用 java 中重载的方法;
// 这些方法被单独重写.
//
String.prototype.query = function(_sql, _param, _name) {
  var sql = moduleHandleContext.get("sql");
  return sql.query(_sql, _param, _name || 'result');
};


String.prototype.queryPaging = function(_sql, _param,
      _pageNum, _pageSize, _save_to, _totalCount) {
  var sql = moduleHandleContext.get("sql");
  return sql.queryPaging(
      _sql,
      _param,
      _pageNum,
      _pageSize,
      _save_to || 'result',
      _totalCount || -1);
};


String.prototype.update = function(_sql, _param, _mcommit) {
  var sql = moduleHandleContext.get("sql");
  return sql.update(_sql, _param, _mcommit || false);
};


String.prototype.updateBatch = function(_sql, _param, _mcommit) {
  var sql = moduleHandleContext.get("sql");
  return sql.updateBatch(_sql, _param, _mcommit || false);
};


String.prototype.connection = function(key_url, user, ps) {
  var sql = moduleHandleContext.get("sql");

  switch (arguments.length) {
    case 0:
      return sql.connection();
    case 1:
      return sql.connection(key_url);
    case 3:
      return sql.connection(key_url, user, ps);
    default:
      throw new Error("bad arguments call sql.connection");
  }
};


})();