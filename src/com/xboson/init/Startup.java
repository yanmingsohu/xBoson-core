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
// 文件创建日期: 17-11-16 下午7:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/Startup.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init;

import com.xboson.been.Config;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;

import javax.servlet.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 启动监听器, 并配置 servlet 和 filter 到容器中
 */
public class Startup implements ServletContextListener {

  public static final String INIT_FILE = "/system_initialize_finish";
  private static ServletContext sc;


  public void contextInitialized(ServletContextEvent sce) {
    Config config = SysConfig.me().readConfig();
    File init_file = new File(config.configPath + INIT_FILE);
    sc = sce.getServletContext();

    if (init_file.exists()) {
      system_startup(sc);
    } else {
      install(sc);
    }
    print_server_info(sc);
  }


  public void contextDestroyed(ServletContextEvent sce) {
    try {
      Touch.exit();
    } catch (Exception e) {/* Do nothing. */}
    sc = null;
  }


  /**
   * 系统安装
   */
  private void install(ServletContext sc) {
    ServletRegistration.Dynamic install =
            sc.addServlet("install",
                    com.xboson.init.Install.class);

    FilterRegistration.Dynamic page =
            sc.addFilter("page",
                    com.xboson.init.Install.InstallFilter.class);

    page.addMappingForUrlPatterns(null, false, "/*");
    install.addMapping("/install/api/*");
  }


  /**
   * 系统启动
   */
  private void system_startup(ServletContext sc) {
    Touch.me();

    FilterRegistration.Dynamic striker =
            sc.addFilter("Striker",
                    com.xboson.j2ee.container.Striker.class);

    FilterRegistration.Dynamic session =
            sc.addFilter("SessionCluster",
                    com.xboson.j2ee.container.SessionCluster.class);

    //
    // 运行时不再有授权限制
    //
    //FilterRegistration.Dynamic processes =
    //        sc.addFilter("Processes", Processes.me().new Filter());
    //processes.addMappingForUrlPatterns(null, false, "/*");

    ServletRegistration.Dynamic main =
            sc.addServlet("main",
                    com.xboson.j2ee.container.MainServlet.class);

    ServletRegistration.Dynamic ui =
            sc.addServlet("ui",
                    com.xboson.j2ee.ui.UIEngineServlet.class);

    ServletRegistration.Dynamic files =
            sc.addServlet("files",
                    com.xboson.j2ee.files.FileService.class);


    striker.addMappingForUrlPatterns(null, false, "/*");
    session.addMappingForUrlPatterns(null, false, "/*");
    striker.setAsyncSupported(true);
    session.setAsyncSupported(true);

    ui.addMapping("/face/*");
    ui.setLoadOnStartup(2);
    ui.setAsyncSupported(true);

    files.addMapping("/files/*");
    files.setLoadOnStartup(2);
    files.setMultipartConfig(new MultipartConfigElement(""));

    main.addMapping("/*");
    main.setLoadOnStartup(1);
  }


  private void print_server_info(ServletContext sc) {
    Log log = LogFactory.create("Startup");
    log.info("Server Info:", sc.getServerInfo());
    log.info("Servlet ver."+ sc.getMajorVersion() +"."+ sc.getMinorVersion());

    Enumeration<String> names = sc.getAttributeNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      log.info("Server Atribute", name +":", sc.getAttribute(name));
    }

    log.info("file.encoding =",
            System.getProperty("file.encoding"));
    log.info("Locale =",
            Locale.getDefault());
    log.info("java.library.path =",
            System.getProperty("java.library.path"));

    TimeZone zone = TimeZone.getDefault();
    log.info("Time Zone Name =", zone.getDisplayName());
    log.info("Time Zone ID =", zone.getID());
  }


  /**
   * 在系统初始化之后, 系统退出之前返回有效的对象, 否则返回 null
   */
  public static ServletContext getServletContext() {
    return sc;
  }

}
