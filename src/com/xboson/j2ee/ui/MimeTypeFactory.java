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
// 文件创建日期: 17-11-26 下午3:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/MimeType.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.util.Tool;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import java.net.URL;


public class MimeTypeFactory {

  public static final String MIME_FILE = "./mime-types.properties";
  private static FileTypeMap mime;


  /**
   * 在需要时创建全局唯一实例
   * @return
   */
  public static FileTypeMap getFileTypeMap() {
    if (mime == null) {
      synchronized (MimeTypeFactory.class) {
        if (mime == null) {
          mime = new MimetypesFileTypeMap(
                  Tool.readFileFromResource(MimeTypeFactory.class, MIME_FILE)
                          .openInputStream());
        }
      }
    }
    return mime;
  }

}
