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
// 文件创建日期: 17-12-8 上午9:19
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/service/Captcha.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.been.CallData;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


@XPath("/captcha")
public class Captcha extends XService {

  public final static String IMG_TYPE = "png";
  public final static String IMG_MIME = "image/png";

  private Font drawFont;

  public Captcha() {
    File cd = new File(SysConfig.me().readConfig().configPath);
    ImageIO.setCacheDirectory(cd);
    drawFont = new Font("Arial", Font.ITALIC, 20);
  }


  @Override
  public void service(CallData data) throws Exception {
    String code = data.sess.captchaCode;
    if (null == code) {
      code = Tool.randomString(6);
      data.sess.captchaCode = code.toLowerCase();
    }

    BufferedImage img = new BufferedImage(100, 40, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, 100, 40);
    g.setColor(Color.red);
    g.setFont(drawFont);
    g.drawString(code, 10, 30);

    data.resp.setContentType(IMG_MIME);
    ImageIO.write(img, IMG_TYPE, data.resp.getOutputStream());
  }


  @Override
  public boolean needLogin() {
    return false;
  }
}
