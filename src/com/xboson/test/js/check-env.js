var if_throw_ok = require("./util.js").if_throw_ok;
var assert = require("assert");

assert(__filename, '__filename cannot access');
assert(__dirname, '__dirname cannot access');
assert(__dirname.indexOf('/')>=0);
assert(__filename.indexOf('check-env.js')>=0);
assert(__filename.indexOf(__dirname) == 0, "bad __dirname or __filename");

assert.throws(function() {
  require("not_exist_module");
}, /cannot found.*not_exist_module/);


//
// Math
//
assert(Math, "Math cannot access");
assert.eq(Math.abs(-9), 9, "bad Math.abs()");
assert.eq(Math.floor(Math.PI), 3.0, "bad PI");
assert.eq(Math.sin(75 * Math.PI / 180),
    Math.cos(15 * Math.PI / 180), "sin(75) == cos(15)");


//
// Global var
//
var a = Math.random();
global.a = a;
assert.eq(require('./deep.js').getGlobalA(), a, "global not working");
global = {};
assert.eq(global.a, a, "global changed");


//
// console
//
assert.eq(typeof console, 'object', 'console cannot access');
assert.eq(typeof console.debug, 'function', 'console.debug()');
assert.eq(typeof console.log, 'function', 'console.log()');
assert.eq(typeof console.info, 'function', 'console.info()');
assert.eq(typeof console.warn, 'function', 'console.warn()');
assert.eq(typeof console.error, 'function', 'console.error()');
assert.eq(typeof console.fatal, 'function', 'console.fatal()');


//
// Date
//
var d = new Date();
assert(d, "new Data()");
assert.eq(typeof d.getDate, 'function', 'Date.getDate()');
assert.eq(typeof d.getMonth, 'function', 'Date.getMonth()');
assert.eq(typeof d.setMinutes, 'function', 'Date.setMinutes()');
assert.eq(typeof d.getMilliseconds, 'function', 'Date.getMilliseconds()');
assert.eq(typeof d.toLocaleDateString, 'function', 'Date.toLocaleDateString()');


//
// Uint16Array
//
assert(typeof Uint16Array, 'object', 'Uint16Array cannot access');
var ua = new Uint16Array(10);

assert(ua, "create Uint16Array fail");
//assert.eq(typeof ua.copyWithin, "function", "Uint16Array.copyWithin");
//assert.eq(typeof ua.entries, "function", "Uint16Array.entries");
assert.eq(typeof ua.byteOffset, "number", "Uint16Array.byteOffset");
assert.eq(typeof ua.toString, "function", "Uint16Array.toString");
assert.eq(typeof ua.set, "function", "Uint16Array.set");
assert.eq(typeof ua.buffer, "object", "Uint16Array.buffer");


//
// Buffer
//
assert(Buffer, "Buffer conant access");
var b = Buffer.from([1,2,3]);
b[1] = 99;
assert.eq(b[0], 1);
assert.eq(b[1], 99);
assert.eq(b[2], 3);