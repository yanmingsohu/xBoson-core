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
// 文件创建日期: 18-8-13 下午6:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/IBytesWriter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Signature;
import java.security.SignatureException;


/**
 * 一个简单的字节写出器, (写出后并不考虑如何还原)
 */
public interface IBytesWriter extends AutoCloseable {

  /**
   * 写出字节数组
   */
  void write(byte[] b);


  /**
   * 默认调用 write(byte[]) 写出字符串的字节形式
   * @see IConstant#CHARSET 字符串的字节编码方式
   */
  default void write(String x) {
    write(x.getBytes(IConstant.CHARSET));
  }


  /**
   * 写出长整型数据
   */
  default void write(long l) {
    write(new byte[] {
            (byte) (l & 0xFF),
            (byte) ((l>>8 ) & 0xFF),
            (byte) ((l>>16) & 0xFF),
            (byte) ((l>>24) & 0xFF),
            (byte) ((l>>32) & 0xFF),
            (byte) ((l>>40) & 0xFF),
            (byte) ((l>>48) & 0xFF),
            (byte) ((l>>56) & 0xFF),
    });
  }


  /**
   * 默认的关闭方法什么都不做
   */
  default void close() {}


  /**
   * 包装签名器为一个字节写出器, 签名器抛出的异常包装为 XBosonException
   * @see XBosonException
   */
  static IBytesWriter wrap(Signature si) {
    return (byte[] b) -> {
      try {
        si.update(b);
      } catch (SignatureException e) {
        throw new XBosonException(e);
      }
    };
  }


  /**
   * 包装输出流为一个字节写出器, 输出流抛出的异常包装为 XBosonException.IOError
   * @see XBosonException.IOError
   */
  static IBytesWriter wrap(OutputStream out) {
    return new IBytesWriter() {
      public void write(byte[] b) {
        try {
          out.write(b);
        } catch (IOException e) {
          throw new XBosonException.IOError(e);
        }
      }

      public void close() {
        try {
          out.close();
        } catch (IOException e) {
          throw new XBosonException.IOError(e);
        }
      }
    };
  }

}
