var if_throw_ok = require("./util.js").if_throw_ok;
var assert = require("assert");

console.log("is working [", __dirname, __filename);


if (__filename.indexOf(__dirname) != 0)
  throw new Error("bad __dirname or __filename");


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


if_throw_ok(function() {
  require("not_exist_module");
}, "require not exist throw");