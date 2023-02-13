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
// 文件创建日期: 18-8-12 上午10:12
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/IConsensusUnit.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.chain.Block;
import com.xboson.db.analyze.ParseException;

import java.io.Serializable;


/**
 * 共识表达式解析后的单元, 该对象需要能序列化, 不要存储复杂的对象.
 */
public interface IConsensusUnit extends Serializable {


  /**
   * 添加子表达式
   */
  void addAction(IConsensusUnit subAct);


  /**
   * 执行当前单元, 如果返回 false 或抛出异常说明执行失败
   */
  boolean doAction(IConsensusContext d, Block b) throws ParseException;


  /**
   * 检查当前单元是否有效, 无效的单元配置将抛出异常, 默认什么都不做
   */
  default void check() throws ParseException {}

}
