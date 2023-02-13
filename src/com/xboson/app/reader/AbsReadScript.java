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
// 文件创建日期: 17-12-16 上午10:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/reader/AbsReadScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.reader;

import com.xboson.app.ApiEncryption;
import com.xboson.app.XjOrg;
import com.xboson.app.fix.SourceFix;
import com.xboson.app.lib.IApiConstant;
import com.xboson.fs.script.ScriptAttr;
import com.xboson.log.ILogName;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.c0nst.IConstant;


/**
 * 读取脚本文件, 该对象是多线程可重入的.
 */
public abstract class AbsReadScript
        implements IConstant, IApiConstant, ILogName {


  protected final Log log;


  public AbsReadScript() {
    log = LogFactory.create(this);
  }


  /**
   * 必须实现该方法, 返回已经打过补丁的脚本源代码.
   * 读取不到脚本必须抛出异常.
   */
  public abstract ScriptFile read(XjOrg org, String app, String mod, String api);


  /**
   * 为代码打补丁, 返回脚本文件
   */
  protected ScriptFile makeFile(ScriptAttr attr, String cipherText, int z) {
    byte[] original_byte = ApiEncryption.me.decryptApi2(cipherText, z);
    byte[] content = SourceFix.autoPatch(original_byte);
    if (z != 0) {
      cipherText = ApiEncryption.encryptApi(new String(original_byte, CHARSET));
    }
    return new ScriptFile(content, cipherText, attr);
  }
}
