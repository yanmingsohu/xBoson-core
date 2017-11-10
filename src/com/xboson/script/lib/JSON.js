
module.exports = {
  warp : warp,
};

//
// 包装原始 JSON 对象, 实现 java 对象的 json 化.
// stringify : 如果本机方法无法序列化, 则会在对象本身寻找 toJSON() 方法并调用
//
function warp(_json) {
  var ret = {
    parse     : _json.parse,
    stringify : stringify,
  };

  Object.freeze(ret);
  return ret;


  //
  // https://developer.mozilla.org/zh-CN/docs/Web/JavaScript
  //        /Reference/Global_Objects/JSON/stringify#toJSON_方法
  //
  function stringify(obj) {
    var ret = _json.stringify(obj);
    if (ret) return ret;

    if (ret == undefined && typeof obj.toJSON == 'function') {
      return obj.toJSON();
    }
  }
}
