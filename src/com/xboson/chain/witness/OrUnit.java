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
// 文件创建日期: 18-8-12 上午10:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/OrUnit.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;


import com.xboson.chain.Block;
import com.xboson.db.analyze.ParseException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;


public class OrUnit implements IConsensusUnit {
  private TwiceUnit root;
  private transient TwiceUnit next;
  private transient Log _log;


  public OrUnit() {
    next = root = new TwiceUnit();
  }


  @Override
  public void addAction(IConsensusUnit subAct) {
    next.current = subAct;
    next.next = new TwiceUnit();
    next = next.next;
  }


  @Override
  public boolean doAction(IConsensusContext d, Block b) {
    TwiceUnit next = root;

    while (next.current != null) {
      try {
        if (next.current.doAction(d, b)) {
          return true;
        } else {
          next = next.next;
        }
      } catch (Exception e) {
        openLog().warn(e);
        next = next.next;
      }
    }
    return false;
  }


  private Log openLog() {
    if (_log == null) {
      _log = LogFactory.create("consesus-or");
    }
    return _log;
  }


  /**
   * 至少有一个参数, 否则抛出异常
   */
  @Override
  public void check() throws ParseException {
    if (root.current == null) {
      throw new ParseException("No parameter");
    }
  }

}
