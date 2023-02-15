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
// 文件创建日期: 2018年1月19日 20:02
// 原始文件路径: xBoson/src/com/xboson/script/lib/process.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

var sys_module_provider;
var sys_process;
var Event = require("events");
var process = module.exports = new Event();


process.versions = {
};


process.env = {
};


process.cwd = function() {
  return '/';
};


//
// 初始化方法只能调用一次
//
process.init = function(_sys_module_provider) {
  delete process.init;
  sys_module_provider = _sys_module_provider;
  sys_process = process.binding('process');
  process.versions.engineVersion = sys_process.engineVersion();
  process.versions.languageVersion = sys_process.languageVersion();
};


process.lock = function(locker, cb) {
  if (!locker) {
    locker = sys_process;
  }
  return sys_process.lock(locker, cb);
};


process.hrtime = function(stime) {
  var ret = sys_process.hrtime();
  if (stime) {
    ret[0] -= stime[0];
    ret[1] -= stime[1];
  }
  return ret;
};


process.binding = function(n) {
  return sys_module_provider.getModule('sys/' + n).exports;
};
