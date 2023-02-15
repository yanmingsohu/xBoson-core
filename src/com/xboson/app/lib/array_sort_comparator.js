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
// 文件创建日期: 2017年11月11日 10:25
// 原始文件路径: xBoson/src/com/xboson/app/lib/array_sort_comparator.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

(function() {
//
// 配置 list 对象的 sort 函数, 该函数的底层使用 js 实现.
//
function array_sort_implement_js(p0) {
  var arr = this;
  var params = arguments;
  var names, ups;

  if (p0 == null || params.length == 0) {
    arr.sort();
  }
  else if (params.length % 2 != 0) {
    throw new Error("bad params length " + params.length);
  }
  else {
    names = [];
    ups = [];
    for (var i=0; i<params.length; i+=2) {
      names.push(params[i]);
      if (params[i+1] == '0') {
        ups.push({a:1, b:-1});
      } else {
        ups.push({a:-1, b:1});
      }
    }
    arr.sort(_sort_function);
  }


  function _sort_function(a, b) {
    for (var i=0; i<names.length; ++i) {
      var name = names[i];
      if (a[name] > b[name]) {
        return ups[i].a;
      } else if (a[name] < b[name]) {
        return ups[i].b;
      }
    }
    return 0;
  }
}

list.array_sort_implement_js = array_sort_implement_js;

})();