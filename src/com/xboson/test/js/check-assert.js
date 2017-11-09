var if_throw_ok = require("./util.js").if_throw_ok;
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
