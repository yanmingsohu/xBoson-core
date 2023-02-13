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
// 文件创建日期: 17-11-26 下午1:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/FileInfo.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.files;

import com.xboson.db.SqlResult;
import com.xboson.util.Tool;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;


/**
 * 继承 Serializable 防止属性被混淆
 */
public class FileInfo implements AutoCloseable, Serializable {

  public String file_name;
  public String dir_name;

  public transient String type;
  public transient long last_modified;
  private transient SqlResult db_conn;
  public transient InputStream input;
  public transient OutputStream output;


  /**
   * 用这个构造函数创建的对象需要关闭
   */
  public FileInfo(String dir, String file, SqlResult db_conn) {
    this.file_name = file;
    this.dir_name = dir;
    this.db_conn = db_conn;
  }


  public FileInfo(String dir, String file) {
    this.file_name = file;
    this.dir_name = dir;
  }


  @Override
  public void close() throws Exception {
    Tool.close(input);
    Tool.close(db_conn);
    Tool.close(output);
    input = null;
    db_conn = null;
    output = null;
  }


  @Override
  protected void finalize() throws Throwable {
    close();
  }
}
