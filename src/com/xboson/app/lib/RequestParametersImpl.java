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
// 文件创建日期: 17-11-24 下午12:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/RequestParametersImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Context;


/**
 * 总是返回 http 参数的数组形式
 */
public class RequestParametersImpl extends AbstractJSObject {

  private CallData cd;


  public RequestParametersImpl(CallData cd) {
    this.cd = cd;
  }


  @Override
  public boolean hasMember(String name) {
    return cd.req.getParameter(name) != null;
  }


  @Override
  public Object getMember(String name) {
    String[] arr = cd.req.getParameterValues(name);
    if (arr == null) {
      return null;
    }
    ScriptObjectMirror tar = RuntimeUnitImpl.createJSList(arr.length);
    for (int i=0; i<arr.length; ++i) {
      tar.setSlot(i, arr[i]);
    }
    return tar;
  }
}
