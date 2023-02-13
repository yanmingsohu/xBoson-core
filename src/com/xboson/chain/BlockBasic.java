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
// 文件创建日期: 18-7-14 上午7:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/BlockBasic.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.util.Tool;

import java.io.Serializable;
import java.util.Arrays;


/**
 * 由用户输入的字段
 */
public class BlockBasic implements ITypes, Serializable {

  /** 当前块数据 */
  protected byte[] data;
  /** 生成块的用户 id */
  protected String userid;
  /** 指向链码区块 key; 当 type = CHAINCODE_CONTENT 无效. */
  protected byte[] chaincodeKey;
  /** 链码 完整路径, org/app/mod/api; 当 type = CHAINCODE_CONTENT 有效. */
  protected String apiPath;
  /** 链码 hash; 当 type = CHAINCODE_CONTENT 有效. */
  protected String apiHash;
  /** 块类型 */
  public int type;


  /**
   * 用于链码块的构建
   */
  public BlockBasic(byte[] data, String userid, String apiPath, String apiHash) {
    setData(data);
    setUserid(userid);
    setApiHash(apiHash);
    setApiPath(apiPath);
    type = CHAINCODE_CONTENT;
  }


  /**
   * 用于普通数据块的构建
   */
  public BlockBasic(byte[] data, String userid, byte[] chaincodeKey) {
    setData(data);
    setUserid(userid);
    setChaincodeKey(chaincodeKey);
    type = NORM_DATA;
  }


  public BlockBasic() {
  }


  public Block createBlock() {
    Block b = new Block();
    b.setData(data);
    b.setUserid(userid);
    b.setApiHash(apiHash);
    b.setApiPath(apiPath);
    b.setChaincodeKey(chaincodeKey);
    if (type > 0) {
      b.type = type;
    } else {
      b.type = NORM_DATA;
    }
    return b;
  }


  public void setData(byte[] d) {
    if (d == null || d.length < 1)
      throw new NullPointerException();
    this.data = d;
  }


  public void setUserid(String uid) {
    if (Tool.isNulStr(uid))
      throw new NullPointerException("String uid "+uid);
    this.userid = uid;
  }


  public void setApiPath(String path) {
    this.apiPath = path;
  }


  public void setApiHash(String hash) {
    this.apiHash = hash;
  }


  public void setChaincodeKey(byte[] c) {
    this.chaincodeKey = c;
  }


  public byte[] getData() {
    return Arrays.copyOf(data, data.length);
  }


  public String getApiHash() {
    return apiHash;
  }


  public String getApiPath() {
    return apiPath;
  }


  public String getUserId() {
    return userid;
  }


  public byte[] getChaincodeKey() { return chaincodeKey; }
}
