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
// 文件创建日期: 17-12-13 下午6:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/ISState.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


/**
 * 前向状态机接口
 */
public interface ISState extends IStateOperator {

  /**
   * 读取一个字符, 返回操作码
   * @see IStateOperator 操作码枚举接口
   */
  int read(byte ch) throws IOException;


  /**
   * 状态机处理好的中间数据, 压入 strarr 中
   * @param strarr
   */
  void setData(String[] strarr);


  /**
   * 状态机将最终代码输出到 out.
   * @param out
   */
  void setOutput(OutputStream out);

}
