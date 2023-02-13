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
// 文件创建日期: 18-12-14 下午6:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/SafeDataFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import com.xboson.app.lib.IApiConstant;
import com.xboson.auth.impl.RoleBaseAccessControl;
import com.xboson.util.AES2;
import com.xboson.util.Hex;
import com.xboson.util.Tool;
import com.xboson.util.Version;
import com.xboson.util.c0nst.IConstant;


/**
 * 在不同的场景对数据/键进行加密
 */
public class SafeDataFactory implements IConstant, IApiConstant {

  private static SafeDataFactory instance;

  private static final IEncryptionStrategy ENC_DEFAULT;
  private static final IEncryptionStrategy ENC_JDBC;
  private static final IEncryptionStrategy ENC_RBAC;

  public static final String SCENES_RBAC = RoleBaseAccessControl.RBAC_HKEY;
  public static final String SCENES_JDBC =
          _R_KEY_PREFIX_ + _CACHE_REGION_JDBC_CONNECTION_;


  static {
    ENC_DEFAULT = new IEncryptionStrategy() {};
    ENC_JDBC = new OnlyValueEnc("jdbc");
    ENC_RBAC = new RBACKeyEnc("rbac");
  }


  private SafeDataFactory() {
  }


  /**
   * 返回场景对应的加密策略, 如果策略不存在会返回默认策略(默认策略不加密)
   */
  public static IEncryptionStrategy get(String scenes) {
    IEncryptionStrategy r = getMaybeNull(scenes);
    if (r == null) return ENC_DEFAULT;
    return r;
  }


  /**
   * 返回场景对应的加密策略, 如果策略不存在会 null
   */
  public static IEncryptionStrategy getMaybeNull(String scenes) {
    // 由于选项很少且不是动态增减, 用 map 消耗太大.
    switch (scenes) {
      case SCENES_RBAC:
        return ENC_RBAC;

      case SCENES_JDBC:
        return ENC_JDBC;

      default:
        return null;
    }
  }


  /**
   * 数据加密策略, 密钥和加密算法由策略选择.
   */
  public interface IEncryptionStrategy {

    /**
     * 加密 key 并返回
     */
    default String encodeKey(String s) { return s; }


    /**
     * 解密 key 并返回
     */
    default String decodeKey(String s) { return s; }


    /**
     * 加密数据并返回
     */
    default String encodeData(String s) { return s; }


    /**
     * 解密数据并返回, s 不能为 null 否则抛出异常
     */
    default String decodeData(String s) { return s; }


    /**
     * 如果允许使用 key 做模糊查询返回 true, 默认返回 true
     */
    default boolean keyAmbiguous() { return true; }

  }


  private static class OnlyKeyEnc implements IEncryptionStrategy {
    private AES2 aes;


    private OnlyKeyEnc(String pass) {
      aes = new AES2("k-"+ pass +"-enc:"+ Version.PKCRC);
    }


    @Override
    public String encodeKey(String s) {
      return Hex.encode64(aes.encryptBin(s.getBytes(CHARSET)));
    }


    @Override
    public String decodeKey(String s) {
      return new String(aes.decryptBin(Hex.decode64(s)));
    }
  }


  private static class RBACKeyEnc implements IEncryptionStrategy {
    private AES2 aes;


    private RBACKeyEnc(String pass) {
      aes = new AES2("rbac-"+ pass +"-enc:"+ Version.PKCRC);
    }


    @Override
    public String encodeKey(String s) {
      int be = s.indexOf(':');
      int ed = s.lastIndexOf(':');
      if (be == ed) {
        return s;
      }
      String a = s.substring(0, ed+1);
      String b = s.substring(ed+1);
      return a + Hex.encode64(aes.encryptBin(b.getBytes(CHARSET)));
    }


    @Override
    public String decodeKey(String s) {
      int be = s.indexOf(':');
      int ed = s.lastIndexOf(':');
      if (be == ed) {
        return s;
      }
      String a = s.substring(0, ed+1);
      String b = s.substring(ed+1);
      return a + new String(aes.decryptBin(Hex.decode64(b)));
    }
  }


  private static class OnlyValueEnc implements IEncryptionStrategy {
    private AES2 aes;


    private OnlyValueEnc(String pass) {
      aes = new AES2("v-"+ pass +"-enc:"+ Version.PKCRC);
    }


    @Override
    public String encodeData(String s) {
      return Hex.encode64(aes.encryptBin(s.getBytes(CHARSET)));
    }


    @Override
    public String decodeData(String s) {
      return new String(aes.decryptBin(Hex.decode64(s)));
    }
  }
}
