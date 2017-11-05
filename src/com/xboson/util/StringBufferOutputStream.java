/* CatfoOD 2017年11月4日 上午9:29:03 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 字符串缓冲区输入流, 用流的方式写入字符串, toString() 获取结果
 */
public class StringBufferOutputStream extends OutputStream {
	
	private byte[] buf = new byte[1024];
	private int pos = 0;
	
	
	public void write(InputStream src) throws IOException {
		Tool.copy(src, this, true);
	}

	
	@Override
	public void write(int b) throws IOException {
		buf[pos] = (byte) b;
		if (++pos >= buf.length) {
			buf = Arrays.copyOf(buf, buf.length * 2);
		}
	}
	
	
	public String toString() {
		return new String(buf, 0, pos);
	}
}
