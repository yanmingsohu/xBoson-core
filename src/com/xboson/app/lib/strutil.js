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
// 原始文件路径: xBoson/src/com/xboson/app/lib/strutil.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////


//
// 为兼容原脚本而设计
//
var strutil = {
};


(function(strutil) {

strutil.startWith   = _startWith;
strutil.endWith     = _endWith;
strutil.length      = _length;
strutil.subString   = _subString;
strutil.subStringTo = _subStringTo;
strutil.split       = _split;
strutil.contain     = _contain;
strutil.toUpperCase = _toUpperCase;
strutil.toLowerCase = _toLowerCase;
strutil.replace     = _replace;
strutil.format      = _format;
strutil.trim        = _trim;
strutil.formatDate  = _formatDate;
strutil.index       = _index;
strutil.lastIndex   = _lastIndex;
Object.freeze(strutil);

function _startWith(a, b) {
  return a.startWith(b);
}

function _endWith(a, b) {
  return a.endWith(b);
}

function _length(a) {
  return a.length;
}

function _subString(a, b, c) {
  return a.substring(b);
}

function _subStringTo(a, b, c) {
  return a.substring(b, c);
}

function _split(a, b) {
  return a.split(b);
}

function _contain(a, b) {
  return a.indexOf(b) >= 0;
}

function _toUpperCase(a) {
  return a.toUpperCase();
}

function _toLowerCase(a) {
  return a.toLowerCase();
}

function _replace(a, b, c) {
  return a.replace(b, c);
}

function _format(arguments) {
  throw new Error("unsupport strutil.format()");
}

function _trim(a) {
  return a.trim();
}

function _formatDate(a, b) {
  throw new Error("unsupport strutil.formatDate()");
}

function _index(a, b) {
  return a.indexOf(b);
}

function _lastIndex(a, b) {
  return a.lastIndexOf(b);
}

})(strutil);