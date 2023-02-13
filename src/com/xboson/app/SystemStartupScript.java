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
// 文件创建日期: 17-12-11 上午10:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/SystemStartupScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.been.XBosonException;
import com.xboson.event.EventLoop;
import com.xboson.j2ee.emu.EmuJeeContext;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;


/**
 * 在 EventLoop 线程中进行缓存初始化, 这些过程写在脚本中.
 * @see EventLoop
 */
public class SystemStartupScript implements IConstant, Runnable {

  private static boolean is_init = false;
  private Log log;


  public static void me() {
    if (is_init) return;
    is_init = true;
    SystemStartupScript sss = new SystemStartupScript();
    EventLoop.me().add(sss);
  }


  private SystemStartupScript() {
    log = LogFactory.create("sc-startup");
  }


  @Override
  public void run() {
    try {
      log.info(INITIALIZATION, "Script Startup..");

      //
      // 初始化 cache 脚本所在的机构/app/模块
      //
      EmuJeeContext ac = new EmuJeeContext();
      ac.callApi(SYS_ORG, "26c0f25501d24c0993515d445e1215a5",
              "cacheinit", "total");

      log.info(INITIALIZATION, "Success");

    } catch (XBosonException.Closed c) {
      log.warn(INITIALIZATION, c);
    } catch (Exception e) {
      log.error(INITIALIZATION, "Fail", Tool.allStack(e));
    }
  }
}
