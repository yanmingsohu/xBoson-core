////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-17 上午8:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/ISigner.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import java.io.Serializable;


/**
 * 签名者对象必须可以序列化, 不能是非静态内部类, 签名对象将被保存在链上.
 * 签名/验证方法应该对称, 不要在验证时超出签名的范围.
 *
 * <a href='https://www.ibm.com/developerworks/cn/java/j-lo-serial/index.html'>
 *   Java 序列化的高级认识</a>
 */
public interface ISigner extends Serializable {

  /**
   * 签名区块, 不同的区块类型签名方法不同;
   * 签名时, hash 为空
   */
  void sign(Block block);


  /**
   * 验证区块, 不同的区块类型, 验证方法不同; block 永远不为 null.
   * @return 验证成功返回 true, 否则返回 false.
   */
  boolean verify(Block block);

}
