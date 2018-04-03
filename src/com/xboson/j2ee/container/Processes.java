////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-2-7 下午12:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/Processes.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import com.xboson.been.License;
import com.xboson.been.XBosonException;
import com.xboson.crypto.Crypto;
import com.xboson.event.timer.TimeFactory;
import com.xboson.fs.watcher.INotify;
import com.xboson.fs.watcher.SingleFile;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;
import com.xboson.util.Version;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Base64;
import java.util.TimerTask;
import java.util.zip.CRC32;

import static com.xboson.been.License.PUB_FILE;


public class Processes extends HttpFilter {

  /**
   * 在 TestSign 中生成
   * @see com.xboson.test.TestSign
   */
  public static final String s[] = new String[] {
      /* 0 */ "QmFkIExpY2Vuc2UsIENvcHkgcHJvdGVjdGlvbg==",
      /* 1 */ "QmFkIExpY2Vuc2UsIFdyb25nIGFwcGxpY2F0aW9u",
      /* 2 */ "QmFkIExpY2Vuc2UsIFNpZ25hdHVyZSBmYWls",
      /* 3 */ "QmFkIExpY2Vuc2UsIEhhcyBub3QgeWV0IHN0YXJ0ZWQ=",
      /* 4 */ "QmFkIExpY2Vuc2UsIE92ZXIgdGhlIHVzZSBvZiB0aW1l",
      /* 5 */ "TGljZW5zZQ==",
      /* 6 */ "VXBkYXRl",
      /* 7 */ "YmFkIHB1YmxpYyBrZXk=",
      /* 8 */ "Tm8gbGljZW5zZSB0byBydW4gdGhlIGZ1bmN0aW9uLCA=",
  };

  static {
    Base64.Decoder d = Base64.getDecoder();
    for (int i=0; i<s.length; ++i) {
      s[i] = new String(d.decode(s[i]));
    }
  }


  private String msg;
  private License license;
  private Log log;


  @Override
  protected void doFilter(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain chain)
          throws IOException, ServletException {

    if (msg != null) {
      response.getWriter().write(msg);
      return;
    }
    chain.doFilter(request, response);
  }


  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    super.init(filterConfig);
    init(filterConfig.getServletContext());
  }


  public void init(ServletContext sc) {
    try {
      final long PERIOD = 12 * 60 * 60 * 1000;
      log = LogFactory.create(s[5]);
      license = License.readLicense();
      license.setPublicKeyFile(sc);

      Point point = new Point();
      point.run();
      TimeFactory.me().schedule(point, PERIOD, PERIOD);

    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  private class Point extends TimerTask implements INotify {
    private SingleFile sf;


    private Point() throws IOException {
      File f = new File(license.LIC_FILE);
      sf = new SingleFile(license.basePath, f.getName(), this);
    }


    @Override
    public void run() {
      try {
        msg = execution(license);
      } catch (Exception e) {
        msg = e.getMessage();
      }
      if (msg != null) log.warn(msg);
    }


    @Override
    public void nofify(String basename,
                       String filename,
                       WatchEvent event,
                       WatchEvent.Kind kind) throws IOException
    {
      log.info(s[6], basename, filename);
      String publicKeyFile = license.publicKeyFile;
      license = License.readLicense();
      license.publicKeyFile = publicKeyFile;
      run();
    }


    @Override
    public void remove(String basename) {
    }
  }


  private String execution(License license) throws Exception {
    CRC32 crc = new CRC32();
    crc.update(Tool.readAllBytes(license.getPublicKeyFile()));

    if (crc.getValue() != Version.PKCRC) {
      return s[7];
    }

    if (! license.zz().equals(License.singleline(license.z))) {
      return s[0];
    }

    if (! Version.Name.equals(license.appName)) {
      return s[1];
    }

    if (! Crypto.me().verification(license)) {
      return s[2];
    }

    if (license.beginTime > System.currentTimeMillis()) {
      return s[3];
    }

    if (license.endTime < System.currentTimeMillis()) {
      return s[4];
    }

    return null;
  }


  /**
   * 返回错误消息, 当返回 null 说明授权基础验证通过.
   */
  public String message() {
    return msg;
  }


  /**
   * 返回初始化完成的证书对象
   */
  public License message2() {
    return license;
  }
}
