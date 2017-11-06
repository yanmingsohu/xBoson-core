

console.log("is working [", __dirname, __filename);
var fail = false;


if (module.parent) {
	module.exports = function() {
		throw new Error();
	}
	return;
} else {
	try {
		require("/a.js")();
		fail = true;
		console.error("fail: cannot throw error");
	} catch(e) {
		console.info("1.1 Right", e.message);
	}
}


try {
  var System  = Java.type("java.lang.System");
  console.error("fail:", System);
  fail = true;
} catch(e) {
  console.info("1 Right", e.message);
}
  
  
try {
  var r = new java.lang.Runnable() {
      run: function() { print("run"); }
  }
  console.error("fail: can new java.lang.Runnable()", r, java);
  fail = true;
} catch(e) {
  console.info("2 Right", e.message);
}

  
try {
  var o = console.getClass().getResource("/");
  console.error("security fail, Free Use: " + o);
  fail = true;
} catch(e) {
  console.info("3 Right", e.message);
}
  
try {  
  
  console.log(typeof $ENV, typeof $EXEC, typeof $ARG);
  console.log($ENV, $EXEC, $ARG);
  fail = true;
} catch(e) {
  console.info("4 Right", e.message);
}
  

if (eval) {
  console.error("eval() can use");
  fail = true;
} 
  
  
if (fail) throw new Error('something fail.');

console.log("All, Script env is safe");