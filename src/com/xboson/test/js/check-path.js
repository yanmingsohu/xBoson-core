var if_throw_ok = require("./util.js").if_throw_ok;
var assert = require("assert");
var path = require("path");
assert.eq(path.normalize('/./a'), '/a', 'p1');
assert.eq(path.normalize('/./a/b.js'), '/a/b.js', 'p2');
assert.eq(path.normalize('/./a/../b.js'), '/b.js', 'p3');
assert.eq(path.normalize('/./a\\/../b.js'), '/b.js', 'p4');

if_throw_ok(function() {
  path.checkSafe('/../a');
}, "check path safe");