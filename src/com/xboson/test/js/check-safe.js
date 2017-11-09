var if_throw_ok = require("./util.js").if_throw_ok;



if (typeof safe_context != 'undefined') {
  throw new Error("get safe_context object");
}


__warp_main = null;
__warp_main(function() {
  console.log("OK __warp_main() not modify");
})({}, {});


if_throw_ok(function() {
  crossval = 109;
  if (require('./deep.js').t2() == crossval) {
    throw new Error("!! change sand box context value");
  }
}, 'not change context val');


var a = Math.random();
global.a = a;
require('./deep.js').t3(a);
console.log("OK global is working");


if_throw_ok(function() {
  var a = require("./deep.js");
  a.terr(10, 'deep exception');
}, "deep exception");


if_throw_ok(function() {
  load("foo.js");
}, "block load()");


if_throw_ok(function() {
  var System  = Java.type("java.lang.System");
  console.error("fail:", System);
  fail = true;
}, "block Java.type function");


if_throw_ok(function() {
  var imports = new JavaImporter(java.util, java.io);
}, "block JavaImporter()");
  
  
if_throw_ok(function() {
  var r = new java.lang.Runnable() {
      run: function() { print("run"); }
  };
}, "block new java.lang.Runnable()");

  
if_throw_ok(function() {
  var o = console.getClass().getResource("/");
}, "block .getClass()");


if_throw_ok(function() {
  //console.log(typeof $ENV, typeof $EXEC, typeof $ARG);
  console.log($ENV, $EXEC, $ARG);
  fail = true;
}, "block $ENV, $EXEC, $ARG");


//if_throw_ok(function() {
//  var files = `ls -l`;
//  console.log(files);
//}, "block ls -l on system process");
  

if_throw_ok(function() {
  eval("1+1");
}, "block eval()");


if_throw_ok(function() {
  exit(99);
  quit(98);
}, "block exit() and quit()");


if_throw_ok(function() {
  var Vector = Packages.java.util.Vector;
  var JFrame = javax.swing.JFrame;  // javax == Packages.javax
  var List = java.util.List;        // java == Packages.java
  console.log(JFrame, List)
}, "block Packages");

