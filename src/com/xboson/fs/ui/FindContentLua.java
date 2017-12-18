////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-18 下午5:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/FindContentLua.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.sleep.LuaScript;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;

import java.util.List;


/**
 * 线程安全的, 创建一个即可
 */
public class FindContentLua {

  private LuaScript find;
  private String basePath;


  /**
   * 从根目录开始搜索
   */
  public FindContentLua() {
    this("/");
  }


  /**
   * 设置搜索目录
   */
  public FindContentLua(String basePath) {
    StringBufferOutputStream buf =
            Tool.readFileFromResource(RedisBase.class, "find.lua");

    this.find = LuaScript.compile(buf);
    this.basePath = basePath;
  }


  /**
   * 大小写敏感的搜索
   */
  public List<String> find(String content) {
    return find(content, true);
  }


  public List<String> find(String content, boolean caseSensitive) {
    return (List<String>) find.eval(1,
            RedisBase.CONTENT_NAME, content, basePath,
            Boolean.toString(caseSensitive));
  }


  @Override
  public String toString() {
    return find.toString();
  }
}
