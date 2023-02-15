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
// 文件创建日期: 2017年12月08日 16:56
// 原始文件路径: xBoson/src/com/xboson/app/lib/transform_tree_data.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////


function transformTreeData(list, primary_key, parent_ref_key, child_list_key) {
  var size = list.length;
  var root = [];
  var mapping = {};

  //
  // 第一次循环记录所有主键
  //
  for (var i=0; i<size; ++i) {
    var item = list[i];
    mapping[ item[primary_key] ] = item;
  }

  //
  // 第二次循环检查依赖关系
  //
  for (var i=0; i<size; ++i) {
    var item = list[i];
    var ref = item[parent_ref_key];

    if (ref) {
      var parent = mapping[ref];

      if (parent && (parent !== item)) {
        var child_list = parent[child_list_key];
        if (!child_list) {
          child_list = parent[child_list_key] = [];
        }
        child_list.push(item);
      } else {
        //
        // 指向的父节点并不存在, 或指向自身, 则认为是根对象
        //
        root.push(item);
      }
    } else {
      //
      // 没有父指针属性, 则认为是根对象
      //
      root.push(item);
    }
  }
  return root;
}


function getRelatedTreeData(all, filter, primary_key, parent_ref_key) {
  var filter_set    = {};
  var all_map       = {};
  var result        = [];
  var pending_list  = [];
  var processed_map = {};
  var processed_map_size = 0;

  for (var i=0; i<filter.length; ++i) {
    var data = filter[i];
    filter_set[ data[primary_key] ] = 1;
    result.push(data);
    pending_list.push(data);
  }

  for (var i=0; i<all.length; ++i) {
    var data = all[i];
    all_map[ data[primary_key] ] = data;
  }

  check_parent_ref();

  while (processed_map_size > 0) {
    for (var n in processed_map) {
      pending_list.push(processed_map[n]);
    }
    processed_map = {};
    processed_map_size = 0;
    check_parent_ref();
  }

return result;

  function check_parent_ref() {
    for (var i=0; i<pending_list.length; ++i) {
      var parent_key_id = pending_list[i][parent_ref_key];
      if (!parent_key_id) continue;
      
      var parentData = all_map[parent_key_id];
      if (parentData && filter_set[parent_key_id] != 1) {
        processed_map[parent_key_id] = parentData;
        filter_set[parent_key_id] = 1;
        result.push(parentData);
        ++processed_map_size;
      }
    }
    pending_list = [];
  }
}


function setRetList(plist, clist, associate, keyname) {
  var PARENT_KEY = 0;
  var CHILD_KEY  = 1;
  var cache = {};

  for (var i=0; i<clist.length; ++i) {
    var cobj = clist[i];
    var allkey = [];

    for (var a=0; a<associate.length; ++a) {
      var ass = associate[a];
      var key = ass[CHILD_KEY];
      var pkey = ass[PARENT_KEY];
      var val = cobj[key];
      allkey.push(pkey, '=', val, ';');
    }

    var complex_key = allkey.join('');
    var arr = cache[complex_key];
    if (!arr) {
      cache[complex_key] = arr = [];
    }
    arr.push(clist[i]);
  }

  for (var i=0; i<plist.length; ++i) {
    var pobj = plist[i];
    var allkey = [];

    for (var a=0; a<associate.length; ++a) {
      var ass = associate[a];
      var key = ass[PARENT_KEY];
      var val = pobj[key];
      allkey.push(key, '=', val, ';');
    }

    var cobj = cache[ allkey.join('') ];
    if (cobj) {
      pobj[keyname] = cobj;
    } else {
      pobj[keyname] = [];
    }
  }
}