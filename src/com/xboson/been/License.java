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
// 文件创建日期: 18-2-5 下午6:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/Certificate.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.xboson.crypto.AbsLicense;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import com.xboson.util.config.YamlConfigImpl;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 软件使用证书
 */
public class License extends AbsLicense {

  private static final String FIX_CLASS = "com.xboson.been.License";

  public static final String LIC_FILE = "/license.txt";
  static final String PUB_FILE = "/public_key.pem";
  static final String REQ_FILE = "/license.req";
  static final String DIR_BASE = "/WEB-INF";

  public transient URL publicKeyFile;
  public transient String basePath;
  public String signature;


  public License() {
    basePath = SysConfig.me().readConfig().configPath;
    String pub = (Tool.isInJar ? "/WEB-INF" : "/WebRoot/WEB-INF") + PUB_FILE;
    publicKeyFile = Tool.getResource(License.class, pub);
  }


  public void setPublicKeyFile(ServletContext sc) throws MalformedURLException {
    publicKeyFile = getPublicKeyURL(sc);
  }


  public static URL getPublicKeyURL(ServletContext sc)
          throws MalformedURLException {
    final String _full = DIR_BASE + PUB_FILE;
    URL url = sc.getResource(_full);
    if (url == null) {
      url = Tool.getResource(License.class, _full);
    }
    if (url == null) throw
            new XBosonException.NotFound(_full);
    return url;
  }


  public String zz() {
    if (z == null) {
      super.z();
      return singleline(z);
    } else {
      String oz = z;
      super.z();
      return singleline(oz);
    }
  }


  @Override
  public String getPublicKeyFile() {
    return publicKeyFile.toString();
  }


  public File writeRequest() throws IOException {
    return writeFile(REQ_FILE);
  }


  public File writeLicense() throws IOException {
    return writeFile(LIC_FILE);
  }


  private File writeFile(String file) throws IOException {
    File outFile = new File(basePath + file);
    FileWriter fileOut = new FileWriter(outFile);
    writeTo(fileOut);
    return outFile;
  }


  public void writeTo(Writer out) throws YamlException {
    YamlConfig config = YamlConfigImpl.basicConfig();
    config.writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
    YamlWriter yaml = new YamlWriter(out, config);
    yaml.write(this);
    yaml.close();
  }


  public static License readLicense() throws IOException {
    String basePath = SysConfig.me().readConfig().configPath;
    StringBuilder buf = Tool.readFromFile(basePath + LIC_FILE);
    YamlConfig yc = new YamlConfig();
    yc.setClassTag(FIX_CLASS, License.class);
    YamlReader r = new YamlReader(buf.toString(), yc);
    return r.read(License.class);
  }


  @Override
  protected String signatureString() {
    return singleline(signature);
  }
}
