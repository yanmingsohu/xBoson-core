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
// 文件创建日期: 17-11-26 下午2:36
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/files/DirectoryGenerate.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.files;

import com.xboson.been.ApiCall;
import com.xboson.been.CallData;
import com.xboson.been.SessionData;
import com.xboson.been.UrlSplit;
import com.xboson.util.Tool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


/**
 * 路径生成规则:
 *    有 org 参数, 则一级目录为 org,
 *    否则, 用户登录, 则一级目录为 用户 id.
 *    否则, 目录为 temporary/ + servlet 服务路径之后的路径字符串;
 */
public class DirectoryGenerate {

  public static String get(HttpServletRequest req) throws ServletException {
    SessionData sess = SessionData.get(req);
    UrlSplit sp = new UrlSplit(req);
    return get(sess, sp, null);
  }


  public static String get(CallData cd) {
    return get(cd.sess, cd.url, null);
  }


  public static String get(ApiCall ac) {
    return get(ac.call.sess, ac.call.url, ac.org);
  }


  /**
   * 生成当前用户的文件系统根目录
   *
   * @param sess 从中取得当前登录用户
   * @param sp 临时目录从请求路径中获取
   * @param org 机构 id
   * @return 根目录字符串
   */
  public static String get(SessionData sess, UrlSplit sp, String org) {
    String dirname;

    if (org != null) {
      dirname = "/" + org;
    }
    else if (sess != null && sess.login_user != null) {
      dirname = "/" + sess.login_user.userid;
    }
    else {
      dirname = "/temporary";
      if (sp.getLast() != null) {
        dirname = Tool.normalize(dirname + sp.getLast());
      }
    }

    return dirname;
  }
}
