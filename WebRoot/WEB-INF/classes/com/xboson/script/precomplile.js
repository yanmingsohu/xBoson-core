// 通用库/初始化脚本

this.global = {};
this.eval = null;


function __warp_main(fn) {
	var app;
	var currmodule;
	
	
	return function(module, _app) {
		app = _app;
		module.exports = {};
		module.children = [];
		currmodule = module;
		
		var dirname = module.filename;
		if (dirname) {
			dirname = dirname.split("/");
			if (dirname.length > 2) {
				dirname.pop();
				dirname = dirname.join('/');
			} else {
				dirname = '/';
			}
			console.log("DD", dirname)
		}
		
		return fn(require, module, dirname, module.filename, module.exports);
	}
	
	
	function require(path) {
		if (!app) throw new Error("vfs not provide");
		var mod = app.run(path);
		mod.parent = currmodule;
		currmodule.children.push(mod);
		return mod.exports;
	}
}