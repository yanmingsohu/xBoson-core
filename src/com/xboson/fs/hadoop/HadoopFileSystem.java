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
// 文件创建日期: 19-12-30 上午10:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/hadoop/HadoopFileSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.hadoop;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.xboson.been.XBosonException;
import com.xboson.fs.basic.IFileAttribute;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;


public class HadoopFileSystem implements IHadoopFileSystem {

  private FileSystem hfs;


  public HadoopFileSystem(String hdfsUri) throws IOException {
    Configuration conf = new Configuration(false);
    conf.set("fs.defaultFS", hdfsUri);
    hfs = FileSystem.get(conf);
  }


  @Override
  public InputStream openInputStream(String file) {
    try {
      return hfs.open(new Path(file));
    } catch (IOException e) {
      throw new XBosonException.IOError(e, file);
    }
  }


  @Override
  public OutputStream openOutputStream(String file) {
    try {
      return hfs.create(new Path(file), false);
    } catch(IOException e) {
      throw new XBosonException.IOError(e, file);
    }
  }


  @Override
  public HadoopFileAttr readAttribute(String path) {
    try {
      Path p = new Path(path);
      if (!hfs.exists(p)) {
        return null;
      }

      FileStatus st = hfs.getFileStatus(p);
      HadoopFileAttr attr = new HadoopFileAttr(toPathString(p),
              st.isFile() ? IFileAttribute.T_FILE : IFileAttribute.T_DIR,
              st.getModificationTime(), st.getLen());

      if (st.isDirectory()) {
        for (FileStatus s : hfs.listStatus(p)) {
          attr.dir_contain.add(toPathString(s.getPath()));
        }
      }
      return attr;
    } catch (IOException e) {
      throw new XBosonException.IOError(e, path);
    }
  }


  @Override
  public Set<HadoopFileAttr> readDir(String path) {
    Set<HadoopFileAttr> ret = new HashSet<>();
    try {
      for (FileStatus s : hfs.listStatus(new Path(path))) {
        HadoopFileAttr attr = new HadoopFileAttr(toPathString(s.getPath()),
                s.isFile() ? IFileAttribute.T_FILE : IFileAttribute.T_DIR,
                s.getModificationTime(), s.getLen());
        ret.add(attr);
      }
    } catch(IOException e) {
      throw new XBosonException.IOError(e, path);
    }
    return ret;
  }


  @Override
  public void move(String src, String to) {
    try {
      hfs.rename(new Path(src), new Path(to));
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }


  @Override
  public void delete(String file) {
    try {
      hfs.delete(new Path(file), false);
    } catch (IOException e) {
      throw new XBosonException.IOError(e, file);
    }
  }


  @Override
  public long modifyTime(String path) {
    try {
      FileStatus st = hfs.getFileStatus(new Path(path));
      return st.getModificationTime();
    } catch (IOException e) {
      throw new XBosonException.IOError(e, path);
    }
  }


  @Override
  public void makeDir(String path) {
    try {
      hfs.mkdirs(new Path(path));
    } catch (IOException e) {
      throw new XBosonException.IOError(e, path);
    }
  }


  /**
   * 该方法只返回路径的字符串表达, 忽略 uri 中的其他部分
   */
  static String toPathString(Path p) {
    return p.toUri().getPath();
  }

}
