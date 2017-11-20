////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午8:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/ui/RedisFileMapping.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import java.io.IOException;


/**
 * redis 文件模式.
 *
 * 读取: 从 redis 缓存中读取文件, 如果文件不存在则返回 404, 即使本地文件存在.
 * 写入: 判断 redis 中保存的文件修改时间, 条件允许则保存文件; 写入结束后,
 *      将修改记录加入消息队列
 */
public class RedisFileMapping implements IUIFileProvider {

  private RedisBase rb;


  public RedisFileMapping() {
    rb = new RedisBase();
  }


  @Override
  public byte[] readFile(String path) throws IOException {
    return rb.readFile(path);
  }


  @Override
  public long modifyTime(String path) {
    return rb.getModifyTime(path);
  }


  @Override
  public void makeDir(String path) throws IOException {
    rb.sendCreateDirNotice(path);
  }


  @Override
  public void writeFile(String path, byte[] bytes) throws IOException {
    rb.writeFile(path, bytes);
    rb.writeModifyTime(path, System.currentTimeMillis());
    rb.sendModifyNotice(path);
  }


  @Override
  public void deleteFile(String file) {
    rb.deleteFile(file);
    rb.sendDeleteNotice(file);
  }
}
