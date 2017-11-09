// 通用库/初始化脚本
"use strict"
this.global = {};
this.process = { versions: {} };
this.Buffer;


(function(context) { // 引导代码

var sys_module_provider;
var pathlib;
var safe_context = {};


// 删除所有全局危险对象, 并绑定到内部对象上.
[
  'exit',     'quit',
  'Java',     'JavaImporter',
  'Packages', 'eval',   'print',
  'loadWithNewGlobal',  'load',
  '$ENV',     '$EXEC',  '$ARG',
].forEach(function(name) {
  safe_context[name] = context[name];
  delete context[name];
});


readOnlyAttr(context, '__warp_main', __warp_main);
rwAttrOnClosed(context, 'javax.script.filename');

context.__set_sys_module_provider = __set_sys_module_provider;
context.__boot_over = __boot_over;


function __boot_over() {
  delete context.__set_sys_module_provider;
  delete context.__boot_over;
  context.Buffer = sys_module_provider.getInstance('buffer').Buffer;
  Object.freeze(context);
}


function __warp_main(fn) { // 主函数包装器
	var app;
	var currmodule;
	var dirname;
	var fncontext = {};
	
	
	return function(module, _app) {
		app = _app;
		module.exports = {};
		module.children = [];
		currmodule = module;
		
		var dirname = get_dirname(module.filename);
		return fn.call(fncontext,
		    require, module, dirname, module.filename, module.exports);
	}


	function get_dirname(filename) {
	  if (dirname)
	    return dirname;

	  if (!filename)
	    return null;

    dirname = filename.split("/");
    if (dirname.length > 2) {
      dirname.pop();
      dirname = dirname.join('/');
    } else {
      dirname = '/';
    }
    return dirname;
	}
	

	//
	// 所有的缓存都在 java 上处理.
	//
	function require(path) {
	  if (path[0] == '.') {
	    if (path[1] == '/') {
        path = pathlib.normalize(get_dirname()+ '/'+ path);
      }
	  }
	  else if (path[0] == '/') {
	    path = pathlib.normalize(path);
	  }
	  else {
      if (sys_module_provider) {
        var sysmod = sys_module_provider.getInstance(path);
        if (sysmod) {
          return sysmod;
        } else {
          throw new Error("cannot found sys module '" + path + "'");
        }
      } else {
        throw new Error("sys module provider not set");
      }
	  }

		if (!app) throw new Error("vfs not provide");

		var mod = app.run(path);
		mod.parent = currmodule;
		currmodule.children.push(mod);
		return mod.exports;
	}
}


function __set_sys_module_provider(_provider) {
  sys_module_provider = _provider;
  pathlib = _provider.getInstance('path');

  process.binding = function(n) {
    return _provider.getInstance('sys/' + n);
  };
}


//
// 让对象的属性冻结为固定值
//
function readOnlyAttr(obj, name, value, _get) {
  Object.defineProperty(obj, name, {
    enumerable  : false,
    writable    : false,
    configurable: false,
    value       : value,
  });
}


//
// 即使冻结对象, 这个属性也可以读写
//
function rwAttrOnClosed(obj, name) {
  var value;
  Object.defineProperty(obj, name, {
    enumerable  : false,
    configurable: false,
    get         : function() { return value },
    set         : function(v) { value = v },
  });
}


})(this);