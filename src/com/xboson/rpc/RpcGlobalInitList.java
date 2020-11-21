////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-1-20 下午8:22
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/RpcGlobalInitList.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.app.lib.IOTImpl;
import com.xboson.app.lib.PmImpl;
import com.xboson.distributed.XLock;


/**
 * 所有 RPC 对象需要在启动时注册到注册表中, 把注册代码写到这里.
 */
public class RpcGlobalInitList {


  /**
   * RPC 注册列表, 包外禁止访问
   */
  static final void init(RpcFactory rpc) {
    PmImpl.regTo(rpc);
    IOTImpl.regTo(rpc);
    XLock.me();
  }

}
