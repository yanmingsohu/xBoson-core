////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-10 上午10:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/SqlParser.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;

import com.xboson.db.analyze.unit.*;

import java.util.Iterator;


/**
 * sql 词法分析器, 不做任何语法检查
 */
public class SqlParser {

  /**
   * 不是完整的词法解析器实现, 仅用于替换表格名称.
   */
  public static ParsedData parse(String sql) {
    ParsedData pd = new ParsedData();

    int size = sql.length();
    int begin = 0;
    boolean inString = false;

    for (int i=0; i<size; ++i) {
      char ch = sql.charAt(i);
      if (ch == '\r') continue;

      if (ch == '\'' || ch == '\"') {
        if (sql.charAt(i-1) != '\\') {
          inString = !inString;
        }
      }

      if (SqlKeyWords.notation.contains(ch)) {
        if (i > begin) {
          String unit = sql.substring(begin, i).trim();
          if (unit.length() > 0) {
            if (inString) {
              pd.addExp(unit);
            } else {
              pd.add(unit);
            }
          }
        }
        begin = i+1;

        switch (ch) {
          case ' ':
          case '\t':
            pd.add(new Blank(ch));
            break;
          case '(':
            pd.add(new BeginBrackets());
            break;
          case ')':
            pd.add(new EndBrackets());
            break;
          case '\n':
            pd.add(new NewLine());
            break;
          case ',':
            pd.add(new Comma());
            break;
          case '{':
            pd.add(new BeginBigParantheses());
            break;
          case '}':
            pd.add(new EndBigParantheses());
            break;
          case ';':
            pd.add(new Semicolon());
            break;
        }
      }
    }

    if (begin < size) {
      String unit = sql.substring(begin, size).trim();
      if (unit.length() > 0) {
        pd.add(unit);
      }
    }

    return pd;
  }


  /**
   * 迭代解析后的 sql 指令, 将所有 '表格名称' 相关的字段发送给 ul
   */
  public static void tableNames(ParsedData pd, IUnitListener ul) {
    Iterator<IUnit> it = pd.getUnits().iterator();
    int name_flag = 0;

    while (it.hasNext()) {
      IUnit un = it.next();

      if (un instanceof Comma || un instanceof KeyWord) {
        name_flag = 0;
        continue;
      }

      if (name_flag == 0
              && un instanceof Expression
              && un.getParent() instanceof TableNames) {
        ul.on(un);
        ++name_flag;
      }
    }
  }


  public static void tableNames(SqlParserCached.ParsedDataHandle handle,
                                IUnitListener ul) {
    tableNames(handle.pd, ul);
  }


  /**
   * 将语法树还原为 sql 文
   */
  public static String stringify(ParsedData pd) {
    StringBuilder out = new StringBuilder();
    Iterator<IUnit> it = pd.getUnits().iterator();
    while (it.hasNext()) {
      out.append(it.next().stringify());
    }
    return out.toString();
  }


  /**
   * @see #stringify(ParsedData)
   */
  public static String stringify(SqlParserCached.ParsedDataHandle handle) {
    return stringify(handle.pd);
  }
}
