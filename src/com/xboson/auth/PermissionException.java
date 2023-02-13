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
// 文件创建日期: 17-11-15 下午3:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/PermissionException.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

import com.xboson.been.XBosonException;


/**
 * 权限异常
 */
public class PermissionException extends XBosonException {

  public PermissionException() {
    super();
  }


  public PermissionException(String s) {
    super(s, 1101);
  }


  public PermissionException(String s, Throwable throwable) {
    super(s, throwable);
  }


  public PermissionException(Throwable throwable) {
    super(throwable);
  }


  public PermissionException(IAWho who, IAWhere where, IAResource res) {
    this(who, where, res, 1101);
  }


  public PermissionException(IAWho who, IAWhere where, IAResource res, int code)
  {
    super("权限错误"
            + ";\n 目的: "   + where
            + ";\n 操作者: " + who.identification()
            + ";\n 资源: "   + res.description()
            , code);
  }


  protected PermissionException(String s, Throwable throwable, boolean b, boolean b1) {
    super(s, throwable, b, b1);
  }
}
