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
// 文件创建日期: 17-11-23 上午8:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/IConstant.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.c0nst;

import com.xboson.chain.ChainSignerProvider;
import com.xboson.chain.ISignerProvider;
import com.xboson.db.IDict;

import javax.crypto.Cipher;
import java.nio.charset.Charset;


/**
 * 编程用常量, 不要放敏感数据
 */
public interface IConstant extends IDict {

  /**
   * 尽可能不要直接使用, 而是用 CHARSET
   */
  String CHARSET_NAME = "UTF-8";

  /**
   * 全局编码
   */
  Charset CHARSET = Charset.forName(CHARSET_NAME);

  /**
   * 平台机构的机构 ID
   */
  String SYS_ORG = "a297dfacd7a84eab9656675f61750078";

  /**
   * 平台应用前缀, 这些应用总是在平台机构上运行
   */
  String SYS_APP_PREFIX = "ZYAPP_";

  /**
   * 平台模块前缀, 这些应用总是在平台机构上运行
   */
  String SYS_MOD_PREFIX = "ZYMODULE_";

  /**
   * 匿名用户名
   */
  String Anonymous = "anonymous";

  /**
   * 平台表前缀, 不会被 sql 替换
   */
  String SYS_TABLE_NOT_REPLACE = "sys_pl_";

  /**
   * HTTP 应答可接收的等待时间, 超过后应抛出异常而是不等待, 毫秒
   */
  int RESPONSE_ACCEPTABLE_TIMEOUT = 5000;

  /**
   * 默认运算节点 ID
   */
  short DEFAULT_NODE_ID_SHORT = 0;
  String DEFAULT_NODE_ID = DEFAULT_NODE_ID_SHORT +"";
  short MASTER_NODE = 0;
  String MASTER_NODE_STR = MASTER_NODE +"";

  /**
   * 默认超时 (秒), session 和 redis 使用.
   */
  int DEFAULT_TIMEOUT = 3 * 60 * 60;

  /**
   * 常用加密/摘要算法名字
   */
  String MD5_NAME       = "MD5";
  String SHA256_NAME    = "SHA-256";
  String AES_NAME       = "AES";
  String SHA1_PRNG_NAME = "SHA1PRNG";
  String PBK1_NAME      = "PBKDF2WithHmacSHA1";
  String AES_C_P_NAME   = "AES/CBC/PKCS5Padding";

  int DECRYPT_MODE = Cipher.DECRYPT_MODE;
  int ENCRYPT_MODE = Cipher.ENCRYPT_MODE;

  char SP_CH = ' ';
  /** 2个空格 */
  String SP     = "  ";
  /** 4个空格 */
  String SPSP   = SP+SP;
  /** 6个空格 */
  String SPSPSP = SP+SPSP;
  /** 换行 */
  String ENTER  = "\n";
  /** 空字符串 */
  String NULL_STR = "";
  String ZERO_STR = "0";
  /** 允许用户多点登录标志 */
  String MULTI_LOGIN = "1";

  /** "初始化" */
  String INITIALIZATION = "Initialization";
  /** "销毁" */
  String DESTORYED = "Destoryed";
  /** 生成的请求 ID */
  String REQUEST_ID = "requestid";
  /** 多点用户登录在 redis 使用的 key */
  String REDIS_KEY_MULTI_LOGIN = "XB.UserPID.SessionID";

  /** 区块链签名提供商, 必须有默认构造函数 */
  Class<? extends ISignerProvider>
          CHAIN_SIGNER_PROVIDER = ChainSignerProvider.class;


  /** 数据源配置表 sys_pl_drm_ds001, flg 枚举字段, 旧系统的系统数据源 */
  int FLG_OLD_SYS = 0;
  /** 数据源配置表 sys_pl_drm_ds001, flg 枚举字段, 2.0 系统的系统数据源 */
  int FLG_SYS = 9;
  /** 数据源配置表 sys_pl_drm_ds001, flg 枚举字段, 第三方数据源 */
  int FLG_THIRD_PART = 1;
}
