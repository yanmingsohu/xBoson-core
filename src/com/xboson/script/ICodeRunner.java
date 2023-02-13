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
// 文件创建日期: 2017年11月5日 下午4:27:04
// 原始文件路径: xBoson/src/com/xboson/script/ICodeRunner.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.Module;


public interface ICodeRunner {

  /**
   * 通过该方法运行文件中的代码, 脚本不存在返回 null
   */
  Module run(String path);


	/**
	 * 运行一个打包好的脚本
	 */
  Module run(AbsWrapScript ws);


  /**
   * 删除缓存中的模块
   */
  void changed(String module_path);


  /**
   * 添加脚本事件监听器.
   */
  void addScriptEventListener(IScriptEventListener l);


  /**
   * 删除脚本监听器.
   */
  void removeScriptEventListener(IScriptEventListener l);
}
