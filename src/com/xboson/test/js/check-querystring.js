var querystring = require("querystring");
var assert = require('assert');

var a = querystring.parse('foo=bar&abc=xyz&abc=123');
var b = {
          foo: 'bar',
          abc: ['xyz', '123']
        };

assert.deepEqual(a, b);


a = querystring.parse('w=%D6%D0%CE%C4&foo=bar', null, null,
                  { decodeURIComponent: decodeURIComponent });
assert(a);


a = querystring.stringify({ foo: 'bar', baz: ['qux', 'quux'], corge: '' });
b = 'foo=bar&baz=qux&baz=quux&corge=';
assert.eq(a, b);


a = querystring.stringify({ foo: 'bar', baz: 'qux' }, ';', ':');
b = 'foo:bar;baz:qux';
assert.eq(a, b);