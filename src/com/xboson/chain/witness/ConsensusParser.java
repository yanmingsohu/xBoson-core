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
// 文件创建日期: 18-8-12 上午9:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/ConsensusParser.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.db.analyze.IUnit;
import com.xboson.db.analyze.ParseException;
import com.xboson.db.analyze.ParsedData;
import com.xboson.db.analyze.SqlParser;
import com.xboson.db.analyze.unit.*;
import com.xboson.util.Tool;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 共识表达式解析器, 解析后的表达式对象可以序列化.
 * 表达式语法:
 *    表达式 = 动作(参数...)
 *    动作 = and | or
 *    参数 = 表达式 | 字符串
 */
public class ConsensusParser {

  private IConsensusPubKeyProvider keys;
  private Map<String, PublicKey> usedKeys;


  public ConsensusParser(IConsensusPubKeyProvider keys) {
    this.keys = keys;
    this.usedKeys = new HashMap<>();
  }


  /**
   * 解析共识表达式,
   * 如果是一个空字符, 会返回一个什么都不做的执行单元
   * @throws ParseException
   */
  public IConsensusUnit parse(String s) throws ParseException {
    if (Tool.isNulStr(s))
      return new DoNothingUnit();

    ParsedData pd = SqlParser.parse(s);
    Iterator<IUnit> it = pd.getUnits().iterator();
    // System.out.println(pd);

    try {
      while (it.hasNext()) {
        IUnit unit = it.next();
        Class c = unit.getClass();

        if (c == KeyWord.class) {
          return parseAction(it, unit);
        }
        else if (c == Blank.class || c == NewLine.class) {
          continue;
        }
        else {
          throw new ParseException("Begin at Action Word");
        }
      }

      return new DoNothingUnit();
    } catch (Exception e) {
      throw new ParseException("Consensus Grammatical errors, "
              + e.getMessage() +": "+ s);
    }
  }


  private IConsensusUnit parseAction(Iterator<IUnit> it, IUnit<String> u) {
    IConsensusUnit cunit = toConsensusUnit(u);
    boolean isbegin = false;
    boolean hasend = false;

    while (it.hasNext()) {
      IUnit sunit = it.next();
      Class c = sunit.getClass();

      if (c == Blank.class || c == NewLine.class) {
        continue;
      }

      if (c == BeginBrackets.class) {
        if (isbegin) throw new ParseException("Begin brackets twice");
        isbegin = true;
        continue;
      } else {
        if (!isbegin) throw new ParseException("Need begin brackets");
      }

      if (c == KeyWord.class) {
        IConsensusUnit sub = parseAction(it, sunit);
        cunit.addAction(sub);
      }
      else if (c == Expression.class) {
        cunit.addAction(parseParm(sunit));
      }
      else if (c == EndBrackets.class) {
        hasend = true;
        break;
      }
    }

    if (!hasend) {
      throw new ParseException("Not end brackets");
    }

    cunit.check();
    return cunit;
  }


  private IConsensusUnit toConsensusUnit(IUnit<String> u) {
    String name = u.getData();

    switch (name) {
      case "and":
        return new AndUnit();

      case "or":
        return new OrUnit();

      default:
        throw new ParseException("Unknow action '"+ name + "'");
    }
  }


  private ParmUnit parseParm(IUnit<String> u) {
    String id = u.getData();
    char ch0 = id.charAt(0);
    char chL = id.charAt(id.length()-1);

    if ((ch0 == '"' || ch0 == '\'') && ch0 == chL) {
      id = id.substring(1, id.length()-1);
    }

    PublicKey pubkey = usedKeys.get(id);
    if (pubkey == null) {
      pubkey = keys.getKey(id);
      usedKeys.put(id, pubkey);
    }
    return new ParmUnit(id, pubkey);
  }


  /**
   * 返回 见证者id 对应的公钥 map
   */
  public Map<String, PublicKey> getUsedPublicKeys() {
    return usedKeys;
  }
}
