////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import java.util.Iterator;
import java.util.List;


/**
 * sql 解析后生成的语法树
 */
public class ParsedData {
  private List<IUnit> units;
  private IUnit lastKeyUnit;


  public ParsedData() {
    this.units = new ArrayList<>(100);
  }


  public void add(String unit) {
    String up_unit = unit.toUpperCase();

    if (SqlKeyWords.beginTable.contains(up_unit)) {
      TableNames nn = new TableNames(unit);
      nn.setParent(lastKeyUnit);
      units.add(nn);
      lastKeyUnit = nn;
    }
    else if (SqlKeyWords.key.contains(up_unit)) {
      IUnit nn = new KeyWord(unit);
      nn.setParent(lastKeyUnit);
      units.add(nn);

      if (! SqlKeyWords.skipParent.contains(up_unit)) {
        lastKeyUnit = nn;
      }
    }
    else {
      addExp(unit);
    }
  }


  public void addExp(String unit) {
    IUnit nn = new Expression(unit);
    nn.setParent(lastKeyUnit);
    units.add(nn);
  }


  public void add(IUnit n) {
    units.add(n);
    checkType(n);
  }


  private void checkType(IUnit n) {
    final UnitType t = n.getType();

    if (t == UnitType.ResetUseParent) {
      if (lastKeyUnit != null) {
        lastKeyUnit = lastKeyUnit.getParent();
      }
    }
    else if (t == UnitType.ClearParent) {
      lastKeyUnit = null;
    }
    else if (t == UnitType.ResetUseParentWhenAs) {
      if (lastKeyUnit != null && lastKeyUnit.getData().equals("AS")) {
        lastKeyUnit = lastKeyUnit.getParent();
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
      out.append("  (");
      if (pt != null) out.append(pt.getClass().getSimpleName());
      out.append(")\n");
    }
    return out.toString();
  }


  /**
   * 获取所有的组件
   */
  public List<IUnit> getUnits() {
    return units;
  }
}
