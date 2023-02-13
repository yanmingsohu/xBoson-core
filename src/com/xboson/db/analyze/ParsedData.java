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
// 文件创建日期: 17-12-10 上午10:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/ParsedData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;

import com.xboson.db.analyze.unit.Expression;
import com.xboson.db.analyze.unit.KeyWord;
import com.xboson.db.analyze.unit.TableNames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * sql 解析后生成的语法树
 */
public class ParsedData {
  private List<IUnit> units;
  private IUnit lastKeyWordUnit;
  private boolean isLocked;


  public ParsedData() {
    this.units = new ArrayList<>(100);
  }


  public void add(String unit) {
    if (isLocked)
      throw new UnsupportedOperationException("is locked");

    String up_unit = unit.toUpperCase();

    if (SqlKeyWords.beginTable.contains(up_unit)) {
      TableNames nn = new TableNames(unit, up_unit);
      nn.setParent(lastKeyWordUnit);
      units.add(nn);
      lastKeyWordUnit = nn;
    }
    else if (SqlKeyWords.key.contains(up_unit)) {
      IUnit nn = new KeyWord(unit, up_unit);
      nn.setParent(lastKeyWordUnit);
      units.add(nn);

      if (! SqlKeyWords.skipParent.contains(up_unit)) {
        lastKeyWordUnit = nn;
      }
    }
    else {
      addExp(unit);
    }
  }


  public void addExp(String unit) {
    if (isLocked)
      throw new UnsupportedOperationException("is locked");

    IUnit nn = new Expression(unit);
    nn.setParent(lastKeyWordUnit);
    units.add(nn);
  }


  public void add(IUnit n) {
    if (isLocked)
      throw new UnsupportedOperationException("is locked");

    units.add(n);
    checkOperating(n);
  }


  private void checkOperating(IUnit n) {
    final UnitOperating t = n.getOperating();

    if (t == UnitOperating.ResetUseParent) {
      if (lastKeyWordUnit != null) {
        lastKeyWordUnit = lastKeyWordUnit.getParent();
      }
    }
    else if (t == UnitOperating.ClearParent) {
      lastKeyWordUnit = null;
    }
    else if (t == UnitOperating.ResetUseParentWhenAs) {
      if (lastKeyWordUnit != null && lastKeyWordUnit.getData().equals("AS")) {
        lastKeyWordUnit = lastKeyWordUnit.getParent();
      }
    }
  }


  public String toString() {
    StringBuilder out = new StringBuilder();
    Iterator<IUnit> it = units.iterator();
    while (it.hasNext()) {
      IUnit un = it.next();
      IUnit pt = un.getParent();
      out.append(un.getClass());
      out.append(" = ");
      out.append(un.getData());
      if (pt != null) {
        out.append("  (");
        out.append(pt.getClass().getSimpleName());
        out.append(")");
      }
      out.append("\n");
    }
    return out.toString();
  }


  /**
   * 获取所有的组件
   */
  public List<IUnit> getUnits() {
    return Collections.unmodifiableList(units);
  }


  public boolean isLocked() {
    return isLocked;
  }


  /**
   * 锁定语法树, 使之无法改变.
   */
  public void lock() {
    if (isLocked) return;
    isLocked = true;

    for (IUnit u : units) {
      u.lock();
    }

    units = new ArrayList<>(units);
  }
}
