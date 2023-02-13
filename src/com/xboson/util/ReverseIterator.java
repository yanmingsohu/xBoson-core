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
// 文件创建日期: 18-1-30 下午1:01
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ReverseIterator.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.util.Iterator;
import java.util.Set;


public class ReverseIterator<E> implements Iterator<E>, Iterable<E> {

  private int p;
  private Object[] arr;


  /**
   * 初始化一个反向迭代器
   * @param set 如果不是 LinkedHashSet 类型则没有意义
   */
  public <E> ReverseIterator(Set<E> set) {
    this.p   = set.size();
    this.arr = new Object[p];

    int i = -1;
    for (E e : set) {
      arr[++i] = e;
    }
  }


  @Override
  public boolean hasNext() {
    return p > 0;
  }


  @Override
  public E next() {
    return (E) arr[--p];
  }


  @Override
  public Iterator<E> iterator() {
    return this;
  }
}
