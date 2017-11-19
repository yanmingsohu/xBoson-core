////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午8:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/ui/IUIFileProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import java.io.IOException;


/**
 * 对 ui 文件的操作, 接口尽可能简单, 每个属性都是分离的
 */
public interface IUIFileProvider {

  /**
   * 读取文件内容
   *
   * @param path 路径可以包含 '/../'.
   * @return 文件的字节内容, 文件不存在返回 null
   */
  byte[] readFile(String path) throws IOException;


  /**
   * 文件的最后修改时间
   *
   * @param path 路径可以包含 '/../'.
   * @return 文件修改时间, 毫秒; 如果文件不存在返回 -1.
   */
  long modifyTime(String path);


  /**
   * 创建目录, 如果上级目录是不存在的, 在必要时会自动生成这些目录
   *
   * @param path 路径可以包含 '/../'.
   */
  void makeDir(String path) throws IOException;


  /**
   * 修改文件/创建文件, 同时会改变文件的修改时间;
   * 如果文件的路径中包含不存在的目录, 必要时会自动生成这些目录
   *
   * @param path 文件路径, 路径可以包含 '/../'.
   * @param bytes 文件内容.
   */
  void writeFile(String path, byte[] bytes) throws IOException;
}
