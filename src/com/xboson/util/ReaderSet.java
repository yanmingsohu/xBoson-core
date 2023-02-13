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
// 文件创建日期: 17-12-12 下午6:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ReaderSet.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 这个 Reader 本身保存了一系列的 Reader, 读取时, 依次从各个 Reader 中读取
 * 直到所有 Reader 都读取完毕.
 *
 * mark() 方法不被支持, 调用 reset() 会将流指针重制到 0 位置
 * 来重新读取所有的数据.
 *
 * 插入的 Reader 对象必须支持 mark().
 */
public class ReaderSet extends Reader {

  private List<ReaderWrap> readers;
  private int rindex;
  private int mark;


  public ReaderSet() {
    readers = new ArrayList<>();
    rindex = 0;
    mark = 0;
  }



  public ReaderSet(Reader...readersArr) {
    this();
    for (int i=0; i<readersArr.length; ++i) {
      add(readersArr[i]);
    }
  }


  /**
   * 这将插入一个 StringReader
   */
  public void add(String str) {
    add(new StringReader(str));
  }


  public void add(ByteBuffer buf) {
    add(buf.array());
  }


  public void add(byte[] buf) {
    readers.add(new ByteReaderWrap(buf));
  }


  public void add(char[] buf) {
    add(new CharArrayReader(buf));
  }


  /**
   * 将 Reader 插入集合中, 之前如果 read 方法已经到达流末尾, 此时有可能继续读取数据.
   * Reader 必须支持 mark()
   */
  public void add(Reader d) {
    if (d != null) {
      try {
        d.mark(1);
        readers.add(new ReaderWrap(d));
      } catch (IOException e) {
        throw new XBosonException(e);
      }
    }
  }


  @Override
  public int read(char[] buf, int off, int len) throws IOException {
    int readlen = 0;

    while (rindex < readers.size()) {
      ReaderWrap r = readers.get(rindex);
      int rlen = r.r.read(buf, off, len);

      if (rlen <= 0) {
        ++rindex;
        continue;
      } else {
        len -= rlen;
        off += rlen;
        readlen += rlen;

        if (len == 0) break;
      }
    }

    return readlen <= 0 ? -1 : readlen;
  }


  @Override
  public long skip(long l) throws IOException {
    throw new UnsupportedOperationException("skip()");
  }


  @Override
  public boolean markSupported() {
    return true;
  }


  @Override
  public void reset() throws IOException {
    for (int i=0; i<readers.size(); ++i) {
      readers.get(i).reset();
    }
    rindex = mark;
  }


  /**
   * 什么都不做, 通常在别的系统中会试图调用该方法来释放资源, 但是
   * ReaderSet 被设计成支持复位后重新读取数据, 所以这里什么都不做.
   *
   * @see #closeAll() 真正释放资源
   */
  @Override
  public void close() {
  }


  /**
   * 关闭所有的 Reader
   * @throws IOException
   */
  public void closeAll() throws IOException {
    if (readers == null) return;
    Iterator<ReaderWrap> it = readers.iterator();

    while (it.hasNext()) {
      Tool.close(it.next().r);
    }
    readers = null;
  }


  private class ReaderWrap {
    Reader r = null;
    ReaderWrap(Reader r) { this.r = r; }
    ReaderWrap() {}
    void reset() throws IOException { r.reset(); }
  }


  private class ByteReaderWrap extends ReaderWrap {
    byte[] buf;
    ByteReaderWrap(byte[] buf) {
      this.buf = buf;
      reset();
    }
    void reset() {
      r = new InputStreamReader(new ByteArrayInputStream(buf));
    }
  }
}
