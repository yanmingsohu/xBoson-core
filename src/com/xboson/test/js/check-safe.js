var if_throw_ok = require("./util.js").if_throw_ok;
var assert = require("assert");


assert.eq(typeof safe_context, 'undefined', "safe_context not safe");
assert.eq(typeof fncontext, 'undefined', 'fncontext not safe');


var safe__warp_main = false;
__warp_main = null;
__warp_main(function() {
  safe__warp_main = true;
})({}, {});
assert(safe__warp_main, "__warp_main not safe");


assert.throws(function() {
  crossval = 109;
  if (require('./deep.js').t2() == crossval) {
    throw new Error("!! change sand box context value");
  }
}, /ReferenceError.*crossval/);


assert.throws(function() {
  var a = require("./deep.js");
  a.terr(10, 'deep exception');
}, /Error.*deep exception/);


assert.throws(function() {
  load("foo.js");
}, /ReferenceError.*load/);


assert.throws(function() {
  var System  = Java.type("java.lang.System");
  console.error("fail:", System);
  fail = true;
}, /ReferenceError.*Java/);


assert.throws(function() {
  var imports = new JavaImporter(java.util, java.io);
}, /ReferenceError.*JavaImporter/);
  
  
assert.throws(function() {
  var r = new java.lang.Runnable() {
      run: function() { print("run"); }
  };
}, /ReferenceError.*lang/);


assert.throws(function() {
  var o = console.getClass().getResource("/");
}, /reflection not supported/);


assert.eq(typeof $ENV, "undefined", "$ENV");
assert.eq(typeof $EXEC, "undefined", "$EXEC");
assert.eq(typeof $ARG, "undefined", "$ARG");


//if_throw_ok(function() {
//  var files = `ls -l`;
//  console.log(files);
//}, "block ls -l on system process");


assert.throws(function() {
  eval("1+1");
}, /ReferenceError.*eval/);


assert.throws(function() {
  exit(99);
}, /ReferenceError.*exit/);


assert.throws(function() {
  quit(98);
}, /ReferenceError.*quit/);


assert.throws(function() {
  var List = java.util.List;
}, /ReferenceError.*util/);


assert.throws(function() {
  var JFrame = javax.swing.JFrame;
}, /ReferenceError.*swing/);


assert.throws(function() {
  var Vector = Packages.java.util.Vector;
}, /ReferenceError.*Packages/);
