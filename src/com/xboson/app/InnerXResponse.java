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
// 文件创建日期: 17-12-11 下午3:54
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/InnerXResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.j2ee.container.XResponse;

import java.io.IOException;
import java.util.Map;


/**
 * 内部应答对象, 对应答的调用不做任何动作, 不会真的给客户端应答,
 * 仅保存应答数据, 并通过方法返回它们.
 */
public class InnerXResponse extends XResponse {

  private boolean isResponsed;
  private Map<String, Object> root;


  public InnerXResponse(Map<String, Object> root) {
    super(root);
    this.root = root;
  }


  @Override
  public void response() throws IOException {
    isResponsed = true;
  }


  @Override
  public boolean isResponsed() {
    return isResponsed;
  }


  public Map<String, Object> getResponseRoot() {
    return root;
  }


  @Override
  public void setCode(int code) {
    super.setCode(code);
    root.put("ret", Integer.toString(code));
  }
}
