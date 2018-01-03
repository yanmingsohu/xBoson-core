////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-3 下午1:46
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/mongo/MongoFileSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.xboson.fs.basic.IStreamOperator;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;


public class MongoFileSystem implements IStreamOperator<MongoFileAttr> {

  private MongoDatabase db;
  private GridFSBucket fs;
  private String diskName;


  MongoFileSystem(MongoDatabase db, String diskName) {
    this.fs = GridFSBuckets.create(db, diskName);
    this.diskName = diskName;
    this.db = db;
  }


  @Override
  public MongoFileAttr readAttribute(String path) {
    for (GridFSFile file : fs.find(Filters.eq("filename", path))) {
      return MongoFileAttr.create(file);
    }
    return null;
  }


  @Override
  public Set<MongoFileAttr> readDir(String path) {
    Set<MongoFileAttr> list = new HashSet<>();
    for (GridFSFile file : fs.find(Filters.eq("filename", path))) {
      list.add(MongoFileAttr.create(file));
    }
    return list;
  }


  @Override
  public void move(String src, String to) {
//    fs.rename(null, to);
  }


  @Override
  public void delete(String path) {
    for (GridFSFile file : fs.find(Filters.eq("filename", path))) {
      fs.delete(file.getObjectId());
    }
  }


  @Override
  public long modifyTime(String path) {
    return 0;
  }


  @Override
  public void makeDir(String path) {
  }


  @Override
  public InputStream openInputStream(String file) {
    return fs.openDownloadStream(file);
  }


  @Override
  public OutputStream openOutputStream(String file) {
    delete(file);
    return fs.openUploadStream(file);
  }
}
