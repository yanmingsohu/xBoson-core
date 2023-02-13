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
// 文件创建日期: 18-1-3 下午2:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/mongo/MongoFileAttr.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.mongo;

import com.mongodb.client.model.Filters;
import com.xboson.fs.basic.AbsFileAttr;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Collection;


public class MongoFileAttr extends AbsFileAttr {

  /** 文件内容节点 id */
  public final ObjectId content_id;

  private Bson filter_id;


  public MongoFileAttr(MongoFileAttr fs) {
    super(fs);
    content_id = fs.content_id;
  }


  public MongoFileAttr(org.bson.Document doc) {
    super(doc.getString("path"),
            doc.getInteger("type"),
            doc.getLong("lastModify"));

    this.content_id = doc.getObjectId("content_id");

    if (type == T_DIR) {
      super.dir_contain.addAll((Collection) doc.get("contains"));
    }
  }


  public MongoFileAttr(String path, int type, long lastModify, ObjectId id) {
    super(path, type, lastModify);
    this.content_id = id;
  }


  public final MongoFileAttr cloneNewContnet(ObjectId id) {
    return new MongoFileAttr(path, type, System.currentTimeMillis(), id);
  }


  public static MongoFileAttr createFile(String file, ObjectId id) {
    return new MongoFileAttr(file, T_FILE, System.currentTimeMillis(), id);
  }


  public static MongoFileAttr createDir(String path) {
    return new MongoFileAttr(path, T_DIR, System.currentTimeMillis(), null);
  }


  public org.bson.Document toDocument() {
    org.bson.Document doc = new org.bson.Document();
    doc.append("path", path);
    doc.append("_id", path);
    doc.append("type", type);
    doc.append("lastModify", lastModify);
    doc.append("contains", dir_contain);
    doc.append("content_id", content_id);
    return doc;
  }


  public Bson toFilterID() {
    if (filter_id == null) {
      filter_id = Filters.eq("_id", path);
    }
    return filter_id;
  }
}
