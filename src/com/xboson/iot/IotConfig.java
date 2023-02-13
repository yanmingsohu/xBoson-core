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
// 文件创建日期: 20-11-25 下午12:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/IotConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import org.bson.Document;

import java.io.Serializable;


public class IotConfig implements Serializable {

  public final int mqport;
  public final String mqhost;
  public final String mongodb;
  public final String dbname;


  public IotConfig(Document doc) {
    this.mongodb = doc.getString("mongodb");
    this.dbname  = doc.getString("dbname");
    this.mqhost  = doc.getString("mqhost");
    this.mqport  = ((Number)doc.get("mqport")).intValue();
  }


  public IotConfig() {
    this.mongodb = null; //"mongodb://localhost";
    this.dbname  = null; //"xboson-artemis-iot";
    this.mqhost  = null; //"localhost";
    this.mqport  = 1883;
  }


  public String getBrokerURL() {
    return "tcp://"+ mqhost +":"+ mqport;
  }
}
