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
// 文件创建日期: 18-8-13 下午4:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/SignNode.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import java.io.Serializable;


/**
 * 签名链表, 使用 Map 或 List 在持久化时占用内存,
 * 随着 java 版本的升高 Map/List 可能二进制不兼容.
 */
public class SignNode implements Serializable {

  /** 内部签名使用块类型作为 id, 共识者签名使用共识者 id */
  public final String id;

  /** 签名, 签名本身不包含其他签名 */
  public final byte[] sign;

  /** 如果没有下一节点则为 null */
  public SignNode next;


  public SignNode(byte[] sign, String id) {
    this.sign = sign;
    this.id = id;
  }


  public SignNode(byte[] sign, int id) {
    this(sign, ""+id);
  }

}
