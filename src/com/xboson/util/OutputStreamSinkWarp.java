/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/*  yanming-sohu@sohu.com Q.412475540 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: CatfoOD 2017年11月4日 上午8:10:35
// 原始文件路径: xBoson/src/com/xboson/util/OutputStreamSinkWarp.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.xboson.util.c0nst.IConstant;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Source;
import okio.Timeout;


/**
 * 包装 OutputStream, 在生成 json 时直接写入对客户端的应答流, 而不是堆积在内存中.
 * 
 * https://docs.oracle.com/javase/8/docs/api/
 * https://github.com/square/okio/blob/master/okio/src/main/java/okio/Buffer.java
 * http://square.github.io/okio/1.x/okio/okio/package-tree.html
 */
public class OutputStreamSinkWarp implements BufferedSink {
	
	private OutputStream writer;
	private Charset utf8 = IConstant.CHARSET;
	private boolean open;


	public OutputStreamSinkWarp(OutputStream writer) {
		this.writer = writer;
		this.open = true;
	}


	@Override
	public boolean isOpen() {
		return open;
	}


	@Override
	public void close() throws IOException {
		writer.close();
		open = false;
	}


	@Override
	public Timeout timeout() {
		return Timeout.NONE;
	}


	@Override
	public void write(Buffer buf, long len) throws IOException {
		buf.copyTo(writer, 0, len);
	}


	@Override
	public Buffer buffer() {
		throw new RuntimeException("not support");
	}


	@Override
	public BufferedSink emit() throws IOException {
		writer.flush();
		return this;
	}


	@Override
	public BufferedSink emitCompleteSegments() throws IOException {
		writer.flush();
		return this;
	}


	@Override
	public void flush() throws IOException {
		writer.flush();
	}


	@Override
	public OutputStream outputStream() {
		throw new RuntimeException("not support");
	}


	@Override
	public BufferedSink write(ByteString b) throws IOException {
		b.write(writer);
		return this;
	}



	@Override
	public int write(ByteBuffer b) throws IOException {
		int i = b.position();
		int end = b.remaining();
		int c = 0;
		byte d;

		while (i < end) {
			d = b.get(i);
			writer.write((int)(d & 255));
			++i;
			++c;
		}
		return c;
	}


	@Override
	public BufferedSink write(byte[] sink) throws IOException {
		writer.write(sink);
		return this;
	}


	@Override
	public BufferedSink write(Source src, long len) throws IOException {
		throw new RuntimeException("not support");
	}


	@Override
	public BufferedSink write(byte[] sink, int pos, int rlen) throws IOException {
		writer.write(sink, pos, rlen);
		return this;
	}


	@Override
	public long writeAll(Source source) throws IOException {
		throw new RuntimeException("not support");
	}


	@Override
	public BufferedSink writeByte(int abyte) throws IOException {
		writer.write(abyte);
		return this;
	}


	@Override
	public BufferedSink writeDecimalLong(long i) throws IOException {
		writeUtf8(new BigDecimal(i).toString());
		return this;
	}


	@Override
	public BufferedSink writeHexadecimalUnsignedLong(long i)
			throws IOException {
		System.out.println("OutputStreamSinkWarp.writeHexadecimalUnsignedLong() WARN !!!");
		writeUtf8(new BigDecimal(i).toString());
		return this;
	}


	@Override
	public BufferedSink writeInt(int i) throws IOException {
		writeUtf8(Integer.toString(i));
		return this;
	}


	@Override
	public BufferedSink writeIntLe(int i) throws IOException {
		writeInt(Tool.reverseBytesInt(i));
		return this;
	}


	@Override
	public BufferedSink writeLong(long i) throws IOException {
		writeUtf8(Long.toString(i));
		return this;
	}


	@Override
	public BufferedSink writeLongLe(long i) throws IOException {
		writeLong(Tool.reverseBytesLong(i));
		return this;
	}


	@Override
	public BufferedSink writeShort(int i) throws IOException {
		writeUtf8(Short.toString((short) i));
		return this;
	}


	@Override
	public BufferedSink writeShortLe(int i) throws IOException {
		writeShort(Tool.reverseBytesShort((short) i));
		return this;
	}


	@Override
	public BufferedSink writeString(String s, Charset c)
			throws IOException {
		writer.write(s.getBytes(c));
		return this;
	}


	@Override
	public BufferedSink writeString(String str, int beginIndex, int endIndex, Charset charset)
			throws IOException {
		writer.write(str.substring(beginIndex, endIndex).getBytes(charset));
		return this;
	}


	@Override
	public BufferedSink writeUtf8(String str) throws IOException {
		writer.write(str.getBytes(utf8));
		return  this;
	}


	@Override
	public BufferedSink writeUtf8(String str, int beginIndex, int endIndex)
			throws IOException {
		writer.write(str.substring(beginIndex, endIndex).getBytes(utf8));
		return this;
	}


	@Override
	public BufferedSink writeUtf8CodePoint(int codePoint) throws IOException {
		if (codePoint < 0x80) {
      // Emit a 7-bit code point with 1 byte.
      writeByte(codePoint);

    } else if (codePoint < 0x800) {
      // Emit a 11-bit code point with 2 bytes.
      writeByte(codePoint >>  6        | 0xc0); // 110xxxxx
      writeByte(codePoint       & 0x3f | 0x80); // 10xxxxxx

    } else if (codePoint < 0x10000) {
      if (codePoint >= 0xd800 && codePoint <= 0xdfff) {
        // Emit a replacement character for a partial surrogate.
        writeByte('?');
      } else {
        // Emit a 16-bit code point with 3 bytes.
        writeByte(codePoint >> 12        | 0xe0); // 1110xxxx
        writeByte(codePoint >>  6 & 0x3f | 0x80); // 10xxxxxx
        writeByte(codePoint       & 0x3f | 0x80); // 10xxxxxx
      }

    } else if (codePoint <= 0x10ffff) {
      // Emit a 21-bit code point with 4 bytes.
      writeByte(codePoint >> 18        | 0xf0); // 11110xxx
      writeByte(codePoint >> 12 & 0x3f | 0x80); // 10xxxxxx
      writeByte(codePoint >>  6 & 0x3f | 0x80); // 10xxxxxx
      writeByte(codePoint       & 0x3f | 0x80); // 10xxxxxx

    } else {
      throw new IllegalArgumentException(
          "Unexpected code point: " + Integer.toHexString(codePoint));
    }
		
		return this;
	}
}
