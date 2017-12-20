////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午8:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/fs/ui/IUIFileProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.been.XBosonException;

import java.io.IOException;
import java.util.Set;


/**
 * 对 ui 文件的操作, 接口尽可能简单, 每个属性都是分离的.
 * 所有的路径参数已经规范化, 不包含任何 "/./" 和 "/../", 并且使用 unix 分隔符,
 * 路径为 ui 虚拟目录, 跟目录应该包含 'ui' 't' 'web' 'lib' 等目录
 */
public interface IUIFileProvider {

  /** 根路径, 也是默认路径 */
  String ROOT = "/";

  /** 结果集最大数量, 超过后的数据被忽略 */
  int MAX_RESULT_COUNT = 30;


  /**
   * 快速读取文件内容, 不推荐使用; 尝试读取目录会抛出异常.
   * 应该使用 readAttribute()/readFileContent() 的组合来读取文件.
   *
   * @param path 路径
   * @return 文件的字节内容, 文件不存在返回 null
   * @throws XBosonException.IOError
   */
  byte[] readFile(String path);


  /**
   * 读取文件内容, 目录会抛出异常
   * @param fs
   * @throws XBosonException.IOError
   */
  void readFileContent(FileStruct fs);


  /**
   * 文件的最后修改时间, 使用 readAttribute() 可以返回
   * 可用性更强的属性, 尽可能不使用该方法.
   *
   * @param path 路径
   * @return 文件修改时间, 毫秒; 如果文件不存在返回 -1.
   * @throws XBosonException.IOError
   */
  long modifyTime(String path);


  /**
   * 读取路径上文件(目录)的属性, 不存在的路径返回 null.
   * @throws XBosonException.IOError
   */
  FileStruct readAttribute(String path);


  /**
   * 创建目录, 如果上级目录是不存在的, 在必要时会自动生成这些目录.
   * 如果目录已经存在, 则什么都不做.
   *
   * @param path 路径
   */
  void makeDir(String path);


  /**
   * 修改文件/创建文件, 同时会改变文件的修改时间;
   * 如果文件的路径中包含不存在的目录, 必要时会自动生成这些目录.
   *
   * @param path 文件
   * @param bytes 文件内容.
   * @throws IOException
   * @throws XBosonException.IOError
   */
  void writeFile(String path, byte[] bytes);


  /**
   * 删除文件/目录, 非空目录抛异常, 如果目录不存在则抛出异常.
   *
   * @param file
   * @throws XBosonException.IOError
   */
  void delete(String file);


  /**
   * 移动文件/目录到新的目录, 如果目的目录已经存在或源目录不存在会抛出异常
   *
   * @param src 源目录/文件
   * @param to 目的目录
   * @throws XBosonException.IOError
   */
  void move(String src, String to);


  /**
   * 读取目录
   *
   * @param path 目录路径, 如果是文件会抛出异常
   * @return 目录中的文件列表
   * @throws XBosonException.IOError
   */
  Set<FileStruct> readDir(String path);


  /**
   * 模糊查询符合路径的完整路径集合, 总是大小写敏感的, 自行添加匹配模式.
   */
  FinderResult findPath(String pathName);


  /**
   * 查询文件内容, 返回文件列表
   *
   * @param basePath 开始目录
   * @param content 要搜索的文本
   * @param cs true 则启用大小写敏感
   */
  FinderResult findContent(String basePath, String content, boolean cs);

}
