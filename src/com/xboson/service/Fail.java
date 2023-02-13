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
// 文件创建日期: 2017年11月2日 下午4:16:17
// 原始文件路径: xBoson/src/com/xboson/service/Fail.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import java.io.IOException;

import javax.servlet.ServletException;

import com.xboson.been.CallData;
import com.xboson.j2ee.container.XService;

/**
 * 服务注册失败占位符, 不要注册到服务列表
 */
public class Fail extends XService {
	
	private ServletException err;
	
	
	public Fail(ServletException e) {
		err = e;
	}
	
	
	public Fail(Exception e) {
		err = new ServletException(e);
	}


	@Override
	public void service(CallData data) throws ServletException, IOException {
		throw err;
	}
}
