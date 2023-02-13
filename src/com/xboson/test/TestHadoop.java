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
// 文件创建日期: 19-12-30 下午3:09
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestHadoop.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.net.URI;


public class TestHadoop {

  public static void main(String[] a) throws Exception {
    Configuration conf = new Configuration();
    conf.set("fs.defaultFS", "hdfs://10.0.0.9:9000");

    FileSystem fs = FileSystem.get(conf);
    fs.mkdirs(new Path("test/a"));
    OutputStream out = fs.create(new Path("test/a/t1.txt"));
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    writer.write("Hello Hadoop!");
    writer.close();

    System.out.println("Write OK");

    InputStream in = fs.open(new Path("test/a/t1.txt"));
    InputStreamReader r = new InputStreamReader(in, "UTF-8");
    BufferedReader br = new BufferedReader(r);
    String line = br.readLine();
    while (line != null) {
      System.out.println(line);
      line = br.readLine();
    }
    br.close();

    System.out.println("Read OK");

    FileSystem fs2 = FileSystem.get(conf);
    System.out.println(fs == fs2);

    FileStatus[] fss = fs.listStatus(new Path("test"));
    for (FileStatus s : fss) {
      URI u = s.getPath().toUri();
      System.out.println(u.getPath() +" "+ s.getModificationTime() +" "+ s.isFile());
    }
  }

}
