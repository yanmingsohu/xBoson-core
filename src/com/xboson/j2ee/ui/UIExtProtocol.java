////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2021 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 21-5-17 上午8:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/UIExtProtocol.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.squareup.moshi.JsonAdapter;
import com.xboson.been.XBosonException;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;

import java.nio.ByteBuffer;


/**
 * 协议设计在 xboson-ui-ext 项目中说明
 */
public class UIExtProtocol {

  public interface IAskListener {

    /**
     * 接受到渲染服务器的应答时调用该方法
     * @param msg_id
     * @param content
     * @param mime
     * @param deps
     */
    void onRenderFile(long msg_id, byte[] content, String mime, String[] deps);


    /**
     * 接受服务器的错误应答
     * @param msg_id
     * @param msg
     */
    void onError(long msg_id, String msg);


    /**
     * 接受服务器的单文件请求
     * @param msg_id
     * @param filename
     */
    void onAskFile(long msg_id, String filename);


    /**
     * 接受服务器的扩展名应答
     * @param msg_id
     * @param exts
     */
    void onExtNames(long msg_id, String ext);
  }


  private IAskListener ls;


  UIExtProtocol(IAskListener l) {
    this.ls = l;
  }


  private String str(byte[] s) {
    return new String(s, IConstant.CHARSET);
  }


  public void parse(ByteBuffer buf) {
    int fn_num = buf.get(0);
    long msg_id = buf.getLong(1);
    int pkg_cnt = buf.get(9);
    byte[][] pkg_data = new byte[pkg_cnt][];
    int data_offset = 10 + 4*pkg_cnt;

    for (int i=0; i<pkg_cnt; ++i) {
      int len_offset = 10 + 4*i;
      int pkg_len = buf.getInt(len_offset);
      pkg_data[i] = new byte[pkg_len];
      copy(buf, pkg_data[i], data_offset);
      data_offset += pkg_len;
    }

    ask(fn_num, msg_id, pkg_data);
  }


  private ByteBuffer make(byte fn_num, long msg_id, byte[][] pkg_data) {
    if (pkg_data.length > 127)
      throw new XBosonException.BadParameter(
              "pkg_data","Data packet cannot be larger than 127");

    int pkg_len = 0;
    for (byte[] b: pkg_data) {
      pkg_len += b.length;
    }

    int data_offset = 10 + 4* pkg_data.length;
    ByteBuffer buf = ByteBuffer.allocateDirect(data_offset + pkg_len);

    buf.put(0, fn_num);
    buf.putLong(1, msg_id);
    buf.put(9, (byte) pkg_data.length);

    for (int i=0; i<pkg_data.length; ++i) {
      int len_offset = 10 + 4*i;
      buf.putInt(len_offset, pkg_data[i].length);
      copy(pkg_data[i], buf, data_offset);
      data_offset += pkg_data[i].length;
    }

    return buf;
  }


  private void ask(int fn_num, long msg_id, byte[][] pkg_data) {
    switch (fn_num) {
      case 3:
        String mime = str(pkg_data[1]);
        String deps[] = new String[pkg_data.length - 2];
        for (int i=2; i<pkg_data.length; ++i) {
          deps[i-2] = str(pkg_data[i]);
        }
        ls.onRenderFile(msg_id, pkg_data[0], mime, deps);
        break;

      case 4:
        String msg = str(pkg_data[0]);
        ls.onError(msg_id, msg);
        break;

      case 6:
        String filename = str(pkg_data[0]);
        ls.onAskFile(msg_id, filename);
        break;

      case 5:
        String ext = str(pkg_data[0]);
        ls.onExtNames(msg_id, ext);
        break;
    }
  }


  /**
   * 构建扩展名询问消息
   */
  public ByteBuffer makeAskExt(long msg_id) {
    return make((byte) 1, msg_id, new byte[0][]);
  }


  /**
   * 构建渲染请求消息
   */
  public ByteBuffer makeAskRender(long msg_id, String filename,
                                  byte[] content, Object data) {
    String parm;
    if (data != null) {
      JsonAdapter json = Tool.getAdapter(data.getClass());
      parm = json.toJson(data);
    } else {
      parm = "{}";
    }

    byte[][] pkgdata = new byte[3][];
    pkgdata[0] = filename.getBytes(IConstant.CHARSET);
    pkgdata[1] = content;
    pkgdata[2] = parm.getBytes(IConstant.CHARSET);

    return make((byte) 2, msg_id, pkgdata);
  }


  /**
   * 构建单文件应答消息
   */
  public ByteBuffer makeFileResp(long msg_id, byte[] content) {
    byte[][] pkg = new byte[1][];
    pkg[0] = content;
    return make((byte) 7, msg_id, pkg);
  }


  private void copy(ByteBuffer src, byte[] dst, int src_off) {
    for (int i=0; i<dst.length; ++i) {
      dst[i] = src.get(i + src_off);
    }
  }


  private void copy(byte[] src, ByteBuffer dst, int dst_off) {
    for (int i=0; i<src.length; ++i) {
      dst.put(i + dst_off, src[i]);
    }
  }
}
