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
// 文件创建日期: 2017年11月3日 下午4:19:45
// 原始文件路径: xBoson/src/com/xboson/log/FileAndConsoleOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;

import java.io.IOException;
import java.util.Date;

public class FileAndConsoleOut implements ILogWriter {
	
	private FileOut file;
	private ConsoleOut cons;
	
	
	public FileAndConsoleOut() {
		try {
			file = new FileOut();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cons = new ConsoleOut();
	}

	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		cons.output(d, l, name, msg);
		if (file != null) {
			file.output(d, l, name, msg);
		}
	}

	@Override
	public void destroy(ILogWriter replace) {
		cons.destroy(replace);
		file.destroy(replace);
	}

}
