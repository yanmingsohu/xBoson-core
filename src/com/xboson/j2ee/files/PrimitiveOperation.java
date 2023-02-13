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
// 文件创建日期: 17-11-26 下午3:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/files/PrimitiveOperation.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.files;

import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.j2ee.ui.MimeTypeFactory;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.activation.FileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;


/**
 * 数据交换文件, 底层操作
 */
public class PrimitiveOperation {

  private static final String OPEN   = "file_open.sql";
  private static final String CREATE = "file_create_or_replace.sql";
  private static final String CLEAN  = "file_clean_trash.sql";
  private static PrimitiveOperation instance;


  private ConnectConfig db;
  private FileTypeMap types;


  private PrimitiveOperation() {
    db = SysConfig.me().readConfig().db;
    types = MimeTypeFactory.getFileTypeMap();
  }


  public static PrimitiveOperation me() {
    if (instance == null) {
      synchronized (PrimitiveOperation.class) {
        if (instance == null) {
          instance = new PrimitiveOperation();
        }
      }
    }
    return instance;
  }


  public Blob createBlob() throws SQLException {
    return DbmsFactory.me().open(db).createBlob();
  }


  /**
   * 打开文件
   *
   * @param dir 目录
   * @param file 文件名
   * @return 文件消息包装文件输入流和数据库连接资源, 需要关闭
   */
  public FileInfo openReadFile(String dir, String file) {
    SqlResult sr = null;
    boolean needClose = true;
    try {
      // 不能在这里关闭 db connect 否则读取流也会关闭
      sr = SqlReader.query(OPEN, db, dir, file);
      ResultSet rs = sr.getResult();

      if (rs.next()) {
        FileInfo fi = new FileInfo(dir, file, sr);
        fi.input = rs.getBinaryStream("content");
        fi.last_modified = rs.getTimestamp("update-time").getTime();
        fi.type = rs.getString("content-type");

        needClose = false;
        return fi;
      } else {
        throw new XBosonException("Not found file: " + dir +' '+ file, 404);
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    } finally {
      if (needClose)
        Tool.close(sr);
    }
  }


  /**
   * 创建文件, 或修改已有的文件
   *
   * @param dir 目录
   * @param file 文件名
   * @param type 文件 mime 类型
   * @param read 驱动从 read 中读取数据存入列中
   * @return 创建新文件返回 1, 更新文件返回 >1, 失败抛出异常.
   */
  public int updateFile(String dir, String file, String type, InputStream read) {
    String id = Tool.uuid.zip();
    try (SqlResult sr = SqlReader.query(
            CREATE, db, id, file, dir, type, read)) {
      return sr.getUpdateCount();
    }
  }


  public SqlResult updateFile(String dir, String file, Blob content) {
    String id = Tool.uuid.zip();
    String type = types.getContentType(file);

    SqlResult sr = SqlReader.query(CREATE, db, id, file, dir, type, content);
    return sr;
  }


  /**
   * 通过文件名推断文件类型
   *
   * @see #updateFile(String, String, String, InputStream)
   */
  public int updateFile(String dir, String file, InputStream read) {
    return updateFile(dir, file, types.getContentType(file), read);
  }


  /**
   * 修改已有文件, 比 updateFile 效率更高, 无需再内存中堆积数据.
   * [需要 blob 字段有足够的空间才能写入]
   *
   * @deprecated 该方法不能工作, 需要进一步测试
   * @param dir
   * @param file
   * @return 向返回的流写入数据
   */
  public FileInfo openWriteFile(String dir, String file) {
    try {
      SqlResult sr = SqlReader.query(OPEN, db, dir, file);
      ResultSet rs = sr.getResult();

      if (rs.next()) {
        FileInfo fi = new FileInfo(dir, file, sr);
        fi.last_modified = rs.getTimestamp("update-time").getTime();
        fi.type = rs.getString("content-type");

        Blob content = rs.getBlob("content");
        content.truncate(0);
        fi.output = content.setBinaryStream(1);
        fi.output.write("abc".getBytes());
        rs.updateBlob("content", content);

        if ((ResultSet.CONCUR_UPDATABLE & rs.getConcurrency()) == 0) {
          throw new XBosonException("Cannot modify file");
        }

        return fi;
      } else {
        throw new XBosonException("Not found file: " + dir +' '+ file, 404);
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 创建一个任务对象, 执行后删除2天之前的所有临时文件
   */
  public CleanTask createCleanTask() {
    return new CleanTask();
  }


  public class CleanTask extends TimerTask {
    private Log log = LogFactory.create();

    public void run() {
      try (SqlResult sr = SqlReader.query(CLEAN, db)) {
        int c = sr.getUpdateCount();
        log.debug("Clean Up Yesterday Trash Upload", c, "files.");
      }
    }
  }
}
