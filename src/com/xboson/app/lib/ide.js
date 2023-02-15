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
// 文件创建日期: 2017年12月14日 13:34
// 原始文件路径: xBoson/src/com/xboson/app/lib/ide.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

//
// 原脚本 ide 模块
//
var ide = {};


(function(ide) {

ide.searchApiContent = _searchApiContent;
ide.encodeApiScript = _encodeApiScript;
ide.decodeApiScript = _decodeApiScript;

Object.freeze(ide);


//
// 在 api 列表中搜索关键字, 并返回新的数组
//
function _searchApiContent(keyword, list, caseSensitive) {
  var ret = [];
  var se = moduleHandleContext.get('se');
  // 静默失败
  if (!se) return null;

  if (!caseSensitive) {
    keyword = keyword.toLowerCase();
  }

  for (var i=0; i<list.length; ++i) {
    var _api = list[i];

    if (_api && _api.content && _api.content.length > 0) {
      var content = se.decryptApi2(_api.content, _api.zip || 0);

      if (!caseSensitive) {
        content = content.toLowerCase();
      }
      if (content.indexOf(keyword) >= 0) {
        ret.push(_api);
      }
    }
  }

  if (ret.length > 0) {
    return ret;
  }
  return null;
}


function _encodeApiScript(code, zip) {
  var se = moduleHandleContext.get('se');
  // 静默失败
  if (!se) return null;
  return se.encodeApi2(code, zip || 0);
}


function _decodeApiScript(code, zip) {
  var se = moduleHandleContext.get('se');
  // 静默失败
  if (!se) return null;
  return se.decryptApi2(code, zip || 0);
}

})(ide);