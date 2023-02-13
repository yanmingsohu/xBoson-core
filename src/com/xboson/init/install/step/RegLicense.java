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
// 文件创建日期: 18-2-7 下午3:27
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/RegLicense.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.xboson.been.License;
import com.xboson.crypto.Crypto;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.util.Tool;
import com.xboson.util.Version;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


public class RegLicense implements IStep {

  @Override
  public int order() {
    return 0;
  }


  @Override
  public boolean gotoNext(HttpData data) throws Exception {
    int op = data.getInt("op");
    if (op == 1) {
      if (data.getStr("skip") != null) {
        return true;
      }
      op1genreq(data);
    }
    else if (op == 2) {
      return op2uplicense(data);
    }

    return false;
  }


  private boolean op2uplicense(HttpData data) throws Exception {
    data.ajax = true;

    String yaml = data.getStr("yaml");
    YamlReader r = new YamlReader(yaml);
    License li = r.read(License.class);
    li.setPublicKeyFile(data.sc);

    if (Tool.isNulStr(li.signature)) {
      throw new Exception("授权没有签名");
    }

    if (! Crypto.me().verification(li)) {
      throw new Exception("授权许可无效");
    }

    FileWriter out = new FileWriter(data.cf.configPath + li.LIC_FILE);
    li.writeTo(out);
    data.msg = "next";
    return true;
  }


  private void op1genreq(HttpData data) throws Exception {
    License req = new License();
    req.appName    = Version.Name;
    req.company    = data.getStr("company");
    req.dns        = data.getStr("dns");
    req.email      = data.getStr("email");

    int use = data.getInt("useTime");
    if (use <= 0) {
      data.msg = "\"使用时长无效\"";
      return;
    }
    if (Tool.isNulStr(req.company)) {
      data.msg = "\"公司名称无效\"";
      return;
    }
    if (Tool.isNulStr(req.dns)) {
      data.msg = "\"域名无效\"";
      return;
    }
    if (Tool.isNulStr(req.email)) {
      data.msg = "\"邮箱无效\"";
      return;
    }

    Date now = new Date();
    Calendar then = Calendar.getInstance();
    then.setTime(now);
    then.add(Calendar.YEAR, use);

    req.beginTime = now.getTime();
    req.endTime = then.getTimeInMillis();
    req.api = new HashSet<>();
    req.zz();

    StringWriter out = new StringWriter();
    req.writeTo(out);
    req.writeLicense();
    req.writeRequest();

    AjaxData d = new AjaxData();
    d.msg = "ok";
    d.code = out.toString();
    data.msg = Tool.getAdapter(AjaxData.class).toJson(d);
  }


  public static class AjaxData {
    public String msg;
    public String code;
  }


  @Override
  public String getPage(HttpData data) {
    return "reg-license.jsp";
  }
}
