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
// 文件创建日期: 17-11-11 下午2:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/Touch.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init;

import com.xboson.app.SystemStartupScript;
import com.xboson.auth.AuthFactory;
import com.xboson.chain.PeerFactory;
import com.xboson.db.DbmsFactory;
import com.xboson.db.sql.SqlReader;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.fs.script.FileSystemFactory;
import com.xboson.j2ee.container.Processes;
import com.xboson.j2ee.container.UrlMapping;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.RpcFactory;
import com.xboson.script.SandboxFactory;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.BouncyCastleProviderReg;
import com.xboson.util.ChineseDictionary;
import com.xboson.util.SysConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;


/**
 * 只要触摸过, 系统就能初始化 !
 */
public final class Touch {


/****************************************************************************
 * 初始化对象列表
 * @see com.xboson.rpc.RpcGlobalInitList RPC 对象注册初始化列表
 ***************************************************************************/
  private static void __init__process() {
    GlobalEventBus.me();
    SysConfig.me();
    LogFactory.me();
    DbmsFactory.me().registeringDefaultDriver();
    Processes.me().init(Startup.getServletContext());
    UrlMapping.me();
    RedisMesmerizer.me();
    SandboxFactory.version();
    FileSystemFactory.me();
    AuthFactory.me();
    SqlReader.me();
    SystemStartupScript.me();
    ClusterManager.me();
    RpcFactory.me();
    PeerFactory.me();
    ChineseDictionary.init();
    BouncyCastleProviderReg.me();
  }



  private static final int S_ZERO   = 0;
  private static final int S_INITED = 1;
  private static final int S_EXIT   = 2;
  private static int state = S_ZERO;


  public synchronized static void me() {
    if (state != S_ZERO)
      throw new RuntimeException("cannot start system");

    //
    // 日志子系统尚未初始化
    //
    System.out.println("[" + new Date()
            + "] [Touch.me] ---------- xBoson system boot -----------\n");

    GlobalEventBus.me().emit(Names.initialization, Touch.class);
    __init__process();
    GlobalEventBus.me().emit(Names.already_started, Touch.class);
    state = S_INITED;
  }


  public synchronized static void exit() {
    if (state == S_EXIT) return;
    if (state != S_INITED)
      throw new RuntimeException("cannot exit system");

    LogFactory.create().info(
            "---------- xBoson system leaving -----------");

    GlobalEventBus.me().emit(Names.exit, Touch.class);
    state = S_EXIT;
  }


  static public class Init implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
      me();
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
      exit();
    }
  }
}
