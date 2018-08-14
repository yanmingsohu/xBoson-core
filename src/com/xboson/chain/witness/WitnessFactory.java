////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-8-13 下午3:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/WitnessFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.been.Witness;
import com.xboson.been.XBosonException;
import com.xboson.chain.DBFunctions;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.util.Tool;
import com.xboson.util.WeakMemCache;

import javax.naming.event.NamingEvent;


public class WitnessFactory {

  private static WitnessFactory instance;

  private WeakMemCache<String, Witness> witnessPool;


  private WitnessFactory() {
    this.witnessPool = new WeakMemCache<>(new CreateWitness());
    GlobalEventBus.me().on(Names.witness_update, new OnUpdate());
  }


  public static WitnessFactory me() {
    if (instance == null) {
      synchronized (WitnessFactory.class) {
        if (instance == null) {
          instance = new WitnessFactory();
        }
      }
    }
    return instance;
  }


  public Witness get(String witness_id) {
    if (Tool.isNulStr(witness_id))
      throw new XBosonException.NullParamException("String witnessId");

    return witnessPool.getOrCreate(witness_id);
  }


  /**
   * 更新见证者数据, 当主机地址变更时调用
   */
  public void update(String witnessId) {
    GlobalEventBus.me().emit(Names.witness_update, witnessId);
  }



  public WitnessConnect openConnection(String witnessId) {
    Witness wit = get(witnessId);
    return new WitnessConnect(wit);
  }


  private class CreateWitness implements WeakMemCache.ICreator<String, Witness> {
    @Override
    public Witness create(String wid) {
      Witness ret = DBFunctions.me().getWitness(wid);
      if (ret == null)
        throw new XBosonException.NotExist("Witness from id:"+ wid);

      return ret;
    }
  }


  private class OnUpdate extends GLHandle {
    @Override
    public void objectChanged(NamingEvent e) {
      String id = (String) e.getNewBinding().getObject();
      witnessPool.remove(id);
    }
  }
}
