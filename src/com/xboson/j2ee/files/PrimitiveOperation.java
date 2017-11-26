////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.j2ee.ui.MimeTypeFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.activation.FileTypeMap;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;


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


  /**
   * 打开文件
   *
   * @param dir 目录
   * @param file 文件名
   * @return 文件消息包装文件输入流和数据库连接资源, 需要关闭
   */
  public FileInfo openFile(String dir, String file) {
    try {
      // 不能在这里关闭 db connect 否则读取流也会关闭
      SqlResult sr = SqlReader.query(OPEN, db, dir, file);
      ResultSet rs = sr.getResult();

      if (rs.next()) {
        FileInfo fi = new FileInfo(dir, file, sr);
        fi.input = rs.getBinaryStream("content");
        fi.last_modified = rs.getTimestamp("update-time").getTime();
        fi.type = rs.getString("content-type");

        return fi;
      } else {
        throw new XBosonException("Not found file: " + dir +' '+ file, 404);
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
  }


  /**
   * 创建文件, 或修改已有的文件
   *
   * @param dir 目录
   * @param file 文件名
   * @param type 文件 mime 类型
   * @param read 文件内容输入流
   * @return 创建新文件返回 1, 更新文件返回 >1, 失败抛出异常.
   */
  public int updateFile(String dir, String file, String type, InputStream read) {
    String id = Tool.uuid.zip();
    try (SqlResult sr = SqlReader.query(
            CREATE, db, id, file, dir, type, read)) {
      return sr.getUpdateCount();
    }
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
   * 删除今天之前的所有临时文件
   * @return 删除的文件数量
   */
  public int cleanUpYesterdayTrash() {
    try (SqlResult sr = SqlReader.query(CLEAN, db)) {
      return sr.getUpdateCount();
    }
  }
}
