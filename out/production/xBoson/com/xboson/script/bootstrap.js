// 通用库/初始化脚本

this.global = {};
this.eval = null;

delete this.Java;


(function(context) {

var sys_module_provider;
var pathlib;


context.__warp_main = __warp_main;
context.__boot_over = __boot_over;
context.__set_sys_module_provider = __set_sys_module_provider;


function __warp_main(fn) {
	var app;
	var currmodule;
	var dirname;
	
	
	return function(module, _app) {
		app = _app;
		module.exports = {};
		module.children = [];
		currmodule = module;
		
		var dirname = get_dirname(module.filename);
		return fn(require, module, dirname, module.filename, module.exports);
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
	  if (sys_module_provider) {
	    var sysmod = sys_module_provider.getInstance(path);
	    if (sysmod) return sysmod;
	  }

		if (!app) throw new Error("vfs not provide");
		if (path[0] == '.') {
		  if (path[1] == '/') {
		    path = pathlib.normalize(get_dirname()+ '/'+ path);
		  }
		}
		var mod = app.run(path);
		mod.parent = currmodule;
		currmodule.children.push(mod);
		return mod.exports;
	}
}


function __set_sys_module_provider(_provider) {
  sys_module_provider = _provider;
  pathlib = _provider.getInstance('path');
}


function __boot_over() {
  delete context.__set_sys_module_provider;
  delete context.__boot_over;
}


})(this);