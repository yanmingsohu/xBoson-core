////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.util.SysConfig;

import javax.servlet.*;
import java.io.File;


/**
 * 启动监听器
 */
public class Startup implements ServletContextListener {

  public static final String INIT_FILE = "/system_initialize_finish";


  public void contextInitialized(ServletContextEvent sce) {
    Config config = SysConfig.me().readConfig();
    File init_file = new File(config.configPath + INIT_FILE);

    if (init_file.exists()) {
      system_startup(sce.getServletContext());
    } else {
      install(sce.getServletContext());
    }
  }


  public void contextDestroyed(ServletContextEvent sce) {
  }


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


  void system_startup(ServletContext sc) {
    Touch.me();

    FilterRegistration.Dynamic striker =
            sc.addFilter("Striker",
                    com.xboson.j2ee.container.Striker.class);

    FilterRegistration.Dynamic session =
            sc.addFilter("SessionCluster",
                    com.xboson.j2ee.container.SessionCluster.class);

    ServletRegistration.Dynamic main =
            sc.addServlet("main",
                    com.xboson.j2ee.container.MainServlet.class);


    striker.addMappingForUrlPatterns(null, false, "/*");
    session.addMappingForUrlPatterns(null, false, "/*");

    main.addMapping("/*");
    main.setLoadOnStartup(1);
  }

}
