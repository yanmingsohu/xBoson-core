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
// 文件创建日期: 17-12-10 上午11:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/AbsUnit.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;


public abstract class AbsUnit<E> implements IUnit<E> {
  protected IUnit parent;
  protected UnitOperating type;
  private boolean isLocked;


  @Override
  public void setParent(IUnit n) {
    checkLock();
    this.parent = n;
  }


  @Override
  public void setOperating(UnitOperating t) {
    checkLock();
    this.type = t;
  }


  @Override
  public void lock() {
    isLocked = true;
  }


  @Override
  public boolean isLocked() {
    return isLocked;
  }


  /**
   * 在锁定状态调用该方法会抛出异常
   */
  protected void checkLock() {
    if (isLocked)
      throw new UnsupportedOperationException("is locked");
  }


  @Override
  public UnitOperating getOperating() {
    return type;
  }


  @Override
  public IUnit getParent() {
    return parent;
  }


  @Override
  public String toString() {
    return String.valueOf(getData());
  }
}
