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
// 文件创建日期: 2017年11月2日 下午6:00:58
// 原始文件路径: xBoson/src/com/xboson/log/ILogWriter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;

import java.util.Date;

/**
 * 日志输出
 */
public interface ILogWriter {

	/**
	 * 压入新的日志条目
	 */
	void output(Date d, Level l, String name, Object[] msg);
	
	/**
	 * 销毁当前日志
	 * @param replace - 替换当前输出器的新输出器, 可能为 null
	 */
	void destroy(ILogWriter replace);
	
}
