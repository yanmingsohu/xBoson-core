var url = require('url');
var assert = require('assert');


function URL(a) {
  return url.parse(a);
}

function check(a, b) {
  var m = url.parse(a);
  if (!b) b = a;
  assert.eq(m.format(), b, 'bad');
  return m;
}

function check2(a, b, attr) {
  var m = url.parse(a);
  if (!b) b = a;
  assert.eq(m[attr], b, 'bad', attr);
  return m;
}


check('https://example.org/');
check('https://你好你好', 'https://xn--6qqa088eba/');

var u = check2('https://example.org/foo#bar', '#bar', 'hash');
u.hash = 'baz';
assert.eq(u.format(), 'https://example.org/foo#baz');

u = check2('https://example.org:81/foo', 'example.org:81', 'host');
u.host = 'example.com:82';
assert.eq(u.format(), 'https://example.com:82/foo');

// !!! 测试不通过 !!!
u = check2('https://example.org:81/foo', 'example.org', 'hostname');
//u.hostname = 'example.com:82';
//assert.eq(u.href, 'https://example.com:81/foo');

check2('https://example.org/foo', 'https://example.org/foo', 'href');

// !!! 测试不通过
// check2('https://example.org/foo/bar?baz', 'https://example.org', 'origin');
// check2('https://abc:xyz@example.com', 'xyz', 'password')

check2('https://example.org/abc/xyz?123', '/abc/xyz', 'pathname');
check2('https://example.org:8888', '8888', 'port');
check2('https://example.org', 'https:', 'protocol');
