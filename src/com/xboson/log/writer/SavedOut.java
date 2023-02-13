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
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 2017年11月3日 下午2:12:33
// 原始文件路径: xBoson/src/com/xboson/log/SavedOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 在内存中记录日志, 直到被取代时, 将所有日志发送给取代者
 */
public class SavedOut implements ILogWriter {
	
	private Deque<Data> saved = new LinkedList<Data>();

	
	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		saved.addLast(new Data(d, l, name, msg));
	}

	
	@Override
	public void destroy(ILogWriter replace) {
		while (!saved.isEmpty()) {
			Data d = saved.pollFirst();
			replace.output(d.d, d.l, d.n, d.m);
		}
		saved = null;
	}

	
	static class Data {
		Date d;
		Level l;
		String n;
		Object[] m;
		
		Data(Date d, Level l, String n, Object[] m) {
			this.d = d;
			this.l = l;
			this.n = n;
			this.m = m;
		}
	}
}
