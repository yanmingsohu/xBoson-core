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
// 文件创建日期: 17-11-11 下午12:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/IMesmerizer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

/**
 * 持久化接口; 如果底层连接发生错误, 会抛出运行时异常,
 * 不能保证休眠的数据一定可以唤醒, 可能发生缓存重制或超时.
 */
public interface IMesmerizer {

  /**
   * 持久化数据, 实现需要检查 data 类型来正确处理数据,
   * 当 data 继承了 ITimeout, 超时的对象不会保存, 且被删除
   * @param data
   */
  void sleep(ISleepwalker data);

  /**
   * 唤醒持久化的数据,
   * 当  继承了 ITimeout, 唤醒后发现对象超时则返回null, 且被删除.
   * @param c
   * @param id 如果为空会抛出异常
   * @return 如果找不到 id 对应的缓存对象, 则返回 null
   */
  ISleepwalker wake(Class<? extends ISleepwalker> c, String id);

  /**
   * 删除一条数据
   * @param data
   */
  void remove(ISleepwalker data);

  /**
   * 删除该类型的所有数据
   * @param data
   */
  void removeAll(ISleepwalker data);
}
