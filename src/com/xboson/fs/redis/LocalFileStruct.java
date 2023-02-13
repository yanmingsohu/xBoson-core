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
// 文件创建日期: 17-12-18 上午9:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/LocalFileStruct.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import java.io.Serializable;


public class LocalFileStruct extends RedisFileAttr implements Serializable {


  public LocalFileStruct(RedisFileAttr fs, boolean needSync) {
    super(fs);
    setSynchronization(needSync);
  }


  @Override
  public int mappingID() {
    return LocalFileMapping.ID;
  }


}
