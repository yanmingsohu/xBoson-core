var assert = require("assert");

assert.eq(require("buffer").Buffer, Buffer, "Buffer module init");

var buf1, buf2, buf3, buf4, buf5, buf6;

function abc(_buf) {
  for (var i = 0; i < 26; i++) {
    // 97 是 'a' 的十进制 ASCII 值
    _buf[i] = i + 97;
  }
}


//
// 测试 from
//
buf1 = Buffer.from([1,2,3,"x"]);
buf2 = Buffer.from(buf1);
assert(buf1.equals(buf2), 'equals function ' + buf1 + ' = ' + buf2);

buf1[0] = 0x99;
//console.debug('N1:', buf1, 'N2:', buf2);
assert.notEqual(buf1[0], buf2[0], '对象 from 复制');

buf1 = Buffer.from('ABC');
buf2 = Buffer.from('BCD');
buf3 = Buffer.from('ABCD');


//
// 测试 compare
//
assert.eq(0, buf1.compare(buf1), "比较 buf1 myself");
assert.eq(-1, buf1.compare(buf2), "比较 buf1, buf2");
assert.eq(-1, buf1.compare(buf3), "比较 buf1, buf3");
assert.eq(1, buf2.compare(buf1), "比较 buf2, buf1");
assert.eq(1, buf2.compare(buf3), "比较 buf2, buf3");


//
// 测试 equals
//
buf1 = Buffer.alloc(10);
assert(buf1.equals([0,0,0,0,0, 0,0,0,0,0]),
  "创建一个长度为 10、且用 0 填充的 Buffer。");

buf2 = Buffer.alloc(10, 1);
assert(buf2.equals([1,1,1,1,1, 1,1,1,1,1]),
  '创建一个长度为 10、且用 0x1 填充的 Buffer。');

buf3 = Buffer.allocUnsafe(10);
assert(buf3.equals([0,0,0,0,0, 0,0,0,0,0]),
  "服务端程序强制清零, 不会创建未初始化的 Buffer");

buf4 = Buffer.from([1, 2, 3]);
assert(buf4.equals([0x1, 0x2, 0x3]),
  "创建一个包含 [0x1, 0x2, 0x3] 的 Buffer。");

buf5 = Buffer.from('tést');
assert(buf5.equals([0x74, 0xc3, 0xa9, 0x73, 0x74]),
  "创建一个包含 UTF-8 字节 [0x74, 0xc3, 0xa9, 0x73, 0x74] 的 Buffer。");

buf6 = Buffer.from('tést', 'latin1');
assert(buf6.equals([0x74, 0xe9, 0x73, 0x74]),
  "创建一个包含 Latin-1 字节 [0x74, 0xe9, 0x73, 0x74] 的 Buffer。");


//
// 创建两个 Buffer 实例 buf1 与 buf2 ，
// 并拷贝 buf1 中第 16 个至第 19 个字节到 buf2 第 8 个字节起。
//
buf1 = Buffer.allocUnsafe(26);
buf2 = Buffer.allocUnsafe(26).fill('!');
abc(buf1);
buf1.copy(buf2, 8, 16, 20);

assert.equal(buf2.toString('ascii', 0, 25),
    '!!!!!!!!qrst!!!!!!!!!!!!!',
    "buf1: " + buf1.toString("ascii"));


//
// 创建一个 Buffer ，并拷贝同一 Buffer 中一个区域的数据到另一个重叠的区域。
//
var buf = Buffer.allocUnsafe(26);
abc(buf);
buf.copy(buf, 0, 4, 10);

assert.equal(buf.toString('ascii'),
  'efghijghijklmnopqrstuvwxyz', "hex:" + buf);


//
// js es5 不支持迭代器协议, 所有使用 java 迭代器语法
//
var i = 0;
var iterator = buf.entries();
while (iterator.hasNext()) {
  assert.eq(buf[i++], iterator.next(), 'entries index:' + i);
}

i = 0;
iterator = buf.keys();
while (iterator.hasNext()) {
  assert.eq(i++, iterator.next(), 'key index:' + i);
}


//
// 与 `arr` 共享内存
//
var arr = new Uint16Array(2);
arr[0] = 5000;
arr[1] = 4000;
//console.log(typeof arr.buffer, arr.buffer);

var buf = Buffer.from(arr.buffer);
assert(buf.equals([0x88, 0x13, 0xa0, 0x0f]), '输出: <Buffer 88 13 a0 0f>');
// 改变原始的 Uint16Array 也将改变 Buffer
arr[1] = 6000;
assert(buf.equals([0x88, 0x13, 0x70, 0x17]), '输出: <Buffer 88 13 70 17>');


//
// 测试 indexOf
//
buf = Buffer.from('this is a buffer');

assert.eq(buf.indexOf('this'), 0);
assert.eq(buf.indexOf('is'), 2);
assert.eq(buf.indexOf(Buffer.from('a buffer')), 8);
// (97 是 'a' 的十进制 ASCII 值)
assert.eq(buf.indexOf(97), 8);
assert.eq(buf.indexOf(Buffer.from('a buffer example')), -1);
assert.eq(buf.indexOf(Buffer.from('a buffer example').slice(0, 8)), 8);


//
// 测试 slice
//
buf1 = Buffer.alloc(26);
abc(buf1);
buf2 = buf1.slice(0, 3);

assert.eq(buf2.toString(
    'ascii', 0, buf2.length), 'abc', 'slice() not working');
buf1[0] = 33;
assert.eq(buf2.toString(
    'ascii', 0, buf2.length), '!bc', "修改 buf1, buf2 也将改变");