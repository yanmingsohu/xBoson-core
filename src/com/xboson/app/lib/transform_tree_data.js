////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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

  for (var i=0; i<size; ++i) {
    var item = list[i];
    mapping[ item[primary_key] ] = item;

    if (! item[parent_ref_key]) {
      root.push(item);
    }
  }

  for (var i=0; i<size; ++i) {
    var item = list[i];
    var ref = item[parent_ref_key];

    if (ref) {
      var parent = mapping[ref];
      if (parent) {
        var child_list = parent[child_list_key];
        if (!child_list) {
          child_list = parent[child_list_key] = [];
        }
        child_list.push(item);
      }
    }
  }
  return root;
}


function getRelatedTreeData(all, filter, primary_key, parent_ref_key) {
  var ret = [];
  var mapping = {};

  for (var i=0; i<filter.length; ++i) {
    mapping[ filter[i][primary_key] ] = filter[i];
  }

  for (var i=0; i<all.length; ++i) {
    var ref = all[i];
    var main = mapping[ ref[primary_key] ];
    if (main) {
      ret.push(main);
    } else {
      ret.push(ref);
    }
  }
  return ret;
}