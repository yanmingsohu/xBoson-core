var punycode = require('punycode');
var assert = require('assert');

assert.eq(punycode.decode('maana-pta'), 'mañana');
assert.eq(punycode.decode('--dqo34k'), '☃-⌘');

assert.eq(punycode.encode('mañana'), 'maana-pta');
assert.eq(punycode.encode('☃-⌘'), '--dqo34k');

assert.eq(punycode.toASCII('mañana.com'), 'xn--maana-pta.com');
assert.eq(punycode.toASCII('☃-⌘.com'), 'xn----dqo34k.com')
assert.eq(punycode.toASCII('example.com'), 'example.com');

assert.eq(punycode.toUnicode('xn--maana-pta.com'),'mañana.com');
assert.eq(punycode.toUnicode('xn----dqo34k.com'), '☃-⌘.com');
assert.eq(punycode.toUnicode('example.com'), 'example.com');

assert.deepEqual(punycode.ucs2.decode('abc'), [0x61, 0x62, 0x63], 'ucs2 decode 1');
// surrogate pair for U+1D306 tetragram for centre:
assert.deepEqual(punycode.ucs2.decode('\uD834\uDF06'), [0x1D306], 'ucs2 decode 2');

assert.eq(punycode.ucs2.encode([0x61, 0x62, 0x63]), 'abc', 'ucs2 encode 1');
assert.eq(punycode.ucs2.encode([0x1D306]), '\uD834\uDF06', 'ucs2 encode 2');

console.log('punycode.version =', punycode.version);