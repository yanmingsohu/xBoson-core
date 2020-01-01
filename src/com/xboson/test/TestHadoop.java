////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
