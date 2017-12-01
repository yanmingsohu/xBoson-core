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