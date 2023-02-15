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
// 文件创建日期: 2017年12月13日 20:43
// 原始文件路径: xBoson/src/com/xboson/app/lib/compatible-syntax.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////


//
// 代码修正器将对 java 的函数调用转换为对该函数的调用.
//
function __inner_call(_func_name, _obj) {
  var func_name = _func_name;
  if (!_obj) throw new Error("object is null: @object." + func_name);

  try {
    if (_obj.constructor == Array) {
      arguments[0] = list;
      return Function.call.apply(list[func_name], arguments);
    } else {
      arguments[0] = map;
      return Function.call.apply(map[func_name], arguments);
    }
  } catch(e) {
    throw new Error(
        "Can not invoke @object."+ func_name +"(...), " + e.message);
  }
}


//
// 虚拟属性转换规则, 属性对应函数调用
//
var ___virtual_transform_roles = {
  // 对象转换器
  obj : {
  },
  // 数组转换器
  arr : {
    size : function(arr) { return arr.length; },
  },
};
Object.freeze(___virtual_transform_roles);


//
// 代码修正器对虚拟属性转换为该函数的调用
//
function __virtual_attr(_obj, _attr_name) {
  if (!_obj) throw new Error("Object is null: object.~" + _attr_name);

  if (_obj.constructor == Array) {
    var func = ___virtual_transform_roles.arr[_attr_name];
    if (func) return func(_obj);
    throw new Error("Unsupport array.~" + _attr_name);
  } else {
    var func = ___virtual_transform_roles.obj[_attr_name];
    if (func) return func(_obj);
    throw new Error("Unsupport object.~" + _attr_name);
  }
}


//
// 创建含有 {key, value} 属性的字符串对象
//
function __createKVString(key, value) {
  var ret;
  if (typeof value == 'string') {
    ret = new String(value);
  } else if (value !== null && value !== undefined) {
    ret = value;
  } else {
    return value;
  }

  try {
    if (! ret.key) {
      Object.defineProperty(ret, 'key', {
        enumerable  : false,
        writable    : false,
        configurable: true,
        value       : key,
      });
    }

    if (! ret.value) {
      Object.defineProperty(ret, 'value', {
        enumerable  : false,
        writable    : false,
        configurable: true,
        value       : value,
      });
    }
  } catch(e) {
    var sys = moduleHandleContext.get("sys");
    sys.printValue("WARN: {for} Key Value not bind; "+ e.message);
    return value;
  }
  return ret;
}
