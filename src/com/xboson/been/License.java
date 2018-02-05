////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-2-5 下午6:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/Certificate.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.xboson.crypto.IVerification;
import com.xboson.util.SysConfig;
import com.xboson.util.config.YamlConfigImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;


/**
 * 软件使用证书
 */
public class License implements IVerification {

  public static final String PUB_FILE = "/public_key.pem";
  public static final String LIC_FILE = "/license.txt";

  public String appName;
  public String company;
  public String dns;
  public String email;
  public long beginTime;
  public long endTime;
  public String signature;

  private transient String base;


  public License() {
    base = SysConfig.me().readConfig().configPath;
  }


  @Override
  public String getPublicKeyFile() {
    return base + PUB_FILE;
  }


  @Override
  public byte[] message() {
    return (appName + company + dns + email + beginTime + endTime).getBytes();
  }


  @Override
  public byte[] signature() {
    return Base64.getDecoder().decode(signature);
  }


  public void writeLicense() throws IOException {
    FileWriter fileOut = new FileWriter(base + LIC_FILE);
    YamlWriter yaml = new YamlWriter(fileOut, YamlConfigImpl.basicConfig());
    yaml.write(this);
    yaml.close();
  }
}
