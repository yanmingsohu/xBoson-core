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
// 文件创建日期: 18-8-14 上午8:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/DBFunctions.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.Config;
import com.xboson.been.Witness;
import com.xboson.been.XBosonException;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.SysConfig;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 从 db 中获取数据的操作集合
 */
public class DBFunctions {

  private static final String SQL_KEYS    = "open_chain_key";
  private static final String SQL_WITNESS = "open_witness";

  private static DBFunctions instance;
  private final Config cf;


  private DBFunctions() {
    cf =  SysConfig.me().readConfig();
  }


  public static DBFunctions me() {
    if (instance == null) {
      synchronized (DBFunctions.class) {
        if (instance == null) {
          instance = new DBFunctions();
        }
      }
    }
    return instance;
  }


  /**
   * 在脚本环境中调用该方法
   */
  public KeyPair[] openChainKeys(String chain, String channel) {
    KeyPair[] keys = new KeyPair[ ITypes.LENGTH +1 ];
    Object[] parm = { chain, channel };

    try (SqlResult sr = SqlReader.query(SQL_KEYS, cf.db, parm)) {
      ResultSet rs = sr.getResult();
      while (rs.next()) {
        PublicKey pub = Btc.publicKey(rs.getString("publickey"));
        PrivateKey pri = Btc.privateKey(rs.getString("privatekey"));
        int index      = rs.getInt("type");
        keys[index]    = new KeyPair(pub, pri);
      }
      return keys;
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  public Witness getWitness(String witnessId) {
    Object[] parm = { witnessId };

    try (SqlResult sr = SqlReader.query(SQL_WITNESS, cf.db, parm)) {
      ResultSet rs = sr.getResult();
      if (rs.next()) {
        return new Witness(witnessId,
                rs.getString("publickey"),
                rs.getString("urlperfix"),
                rs.getString("host"),
                rs.getString("algorithm"),
                rs.getInt("port"));
      }
      return null;
    } catch (SQLException e) {
      throw new XBosonException(e);
    }
  }
}
