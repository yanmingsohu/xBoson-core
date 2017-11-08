console.log("is working [", __dirname, __filename);


if (__filename.indexOf(__dirname) != 0)
  throw new Error("bad __dirname or __filename");


function if_throw_ok(fn, emsg, showstack) {
  var _throw = 0;
  try {
    fn();
  } catch(e) {
    _throw = e;
    emsg += ' - [ ' + e + ' ]';
    // console.log(e.stack)
  }

  if (!_throw) {
    throw new Error(emsg);
  } else {
    var arr = ["OK", emsg || fn.anem];
    if (showstack) arr.push(_throw.stack);
    console.info(arr);
  }
}


/////////////////////////////////////////////////////////////////////
////- 检查沙箱安全
/////////////////////////////////////////////////////////////////////

if (typeof safe_context != 'undefined') {
  throw new Error("get safe_context object");
}


__warp_main = null;
__warp_main(function() {
  console.log("OK __warp_main() not modify");
})({}, {});


if_throw_ok(function() {
  crossval = 109;
  if (require('./a.js').t2() == crossval) {
    throw new Error("!! change sand box context value");
  }
}, 'not change context val');


var a = Math.random();
global.a = a;
require('./a.js').t3(a);
console.log("OK global is working");


if_throw_ok(function() {
  var a = require("./a.js");
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


/////////////////////////////////////////////////////////////////////
////- 模块功能正常
/////////////////////////////////////////////////////////////////////

if_throw_ok(function() {
  require("not_exist_module");
}, "require not exist throw");

// --------------------------------------- console

var c = require("console");
c.debug("module console.debug ok");
c.log("module console.log ok");
c.info("module console.info ok");
c.warn("module console.log ok");
c.error("module console.error ok");
c.fatal("module console.fatal ok");

// --------------------------------------- assert

var assert = require("assert");

assert.ok(true, 'nothing');
if_throw_ok(function() {
  assert(0, 'must throw');
}, 'assert working');

assert.deepEqual({a:1, b:2}, {a:1, b:2}, "deepEqual");
if_throw_ok(function() {
  assert.deepEqual({a:1, b:2}, {a:1, b:[1,2]});
}, 'deepEqual2');

if_throw_ok(function() {
  assert.doesNotThrow(function() {
    throw new TypeError('错误信息');
  }, SyntaxError);
}, 'doesNotThrow() 1');

if_throw_ok(function() {
  assert.doesNotThrow(function() {
      throw new TypeError('错误信息');
  }, TypeError);
}, 'doesNotThrow() 2');

if_throw_ok(function _t() {
  assert.fail('a', 'e', 'm', 'op', _t);
}, 'assert.fail');

assert.throws(function() {
  throw new Error();
}, Error, 'good');

if_throw_ok(function () {
  assert.throws(function() {
    throw new Error();
  }, TypeError, 'throws');
}, 'assert.throw');

assert.notStrictEqual(1, 2);
assert.ok(1);
assert.notEqual(1, 2);
assert.notDeepStrictEqual({ a: 1 }, { a: '1' });
assert.notDeepEqual({ a: 1 }, { a: 2 });
assert.ifError(0);
assert.equal(1, '1');


// --------------------------------------- path

var path = require("path");
assert.eq(path.normalize('/./a'), '/a', 'p1');
assert.eq(path.normalize('/./a/b.js'), '/a/b.js', 'p2');
assert.eq(path.normalize('/./a/../b.js'), '/b.js', 'p3');
assert.eq(path.normalize('/./a\\/../b.js'), '/b.js', 'p4');

if_throw_ok(function() {
  path.checkSafe('/../a');
}, "check path safe");


// --------------------------------------- events

var Event = require("events");
var event = new Event();

var tt = 0;
event.on("a", function() {
  ++tt;
  console.debug("event working");
});
event.emit('a');
event.emit('a');
assert.eq(tt, 2, 'not recv event');

if_throw_ok(function() {
  event.emit("error", 'throws');
}, 'error event');


// --------------------------------------- buffer
//var buf1, buf2, buf3;
//var Buffer = require("buffer").Buffer;
//
//buf1 = Buffer.from([1,2,3,"x"]);
//buf2 = Buffer.from(buf1);
//buf1[0] = 99;
//console.debug('N1:', buf1, 'N2:', buf2);
//assert.notEqual(buf1[0], buf2[0]);
//
//buf1 = Buffer.from('ABC');
//buf2 = Buffer.from('BCD');
//buf3 = Buffer.from('ABCD');
//
//assert.eq(0, buf1.compare(buf1), "buf1 myself");
//assert.eq(-1, buf1.compare(buf2), "buf1, buf2");
//assert.eq(-1, buf1.compare(buf3), "buf1, buf3");
//assert.eq(1, buf2.compare(buf1), "buf2, buf1");
//assert.eq(1, buf2.compare(buf3), "buf2, buf3");
//
//buf1 = Buffer.allocUnsafe(26);
//buf2 = Buffer.allocUnsafe(26).fill('!');
//
//for (var i = 0; i < 26; i++) {
//  // 97 是 'a' 的十进制 ASCII 值
//  buf1[i] = i + 97;
//}
//
//buf1.copy(buf2, 8, 16, 20);
//
//// 输出: !!!!!!!!qrst!!!!!!!!!!!!!
//console.log(buf2.toString('ascii', 0, 25));


/////////////////////////////////////////////////////////////////////
////- 全局功能正常
/////////////////////////////////////////////////////////////////////

console.debug("console.debug ok");
console.log("console.log ok");
console.info("console.info ok");
console.warn("console.log ok");
console.error("console.error ok");
console.fatal("console.fatal ok");

assert.eq(Math.abs(-9), 9, "bad Math.abs()");
assert.ok(Math.floor(Math.PI) == 3.0, "bad PI");

var d = new Date();
console.debug(d);
