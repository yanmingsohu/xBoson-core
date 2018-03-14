////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-4 上午8:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/AppContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.reader.ForDevelopment;
import com.xboson.app.reader.ForProduction;
import com.xboson.auth.IAWho;
import com.xboson.been.*;
import com.xboson.event.timer.TimeFactory;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.log.slow.RequestApiLog;
import com.xboson.rpc.ClusterManager;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.JavaConverter;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 作为全局脚本的入口, 维护所有运行着的沙箱/应用池
 */
public class AppContext implements IConstant {

  /** 在线程超过这个运行时间后, 降低运行优先级, 毫秒 */
  public static final long LOW_CPU_TIME = 2 * 60 * 1000;

  private static AppContext instance;
  private AppPool production;
  private AppPool development;
  private Set<String> shareApp;
  private RequestApiLog apilog;
  private ProcessManager pm;
  private String nodeID;
  private Log log;


  private AppContext() {
    log         = LogFactory.create();
    production  = new AppPool(new ForProduction());
    development = new AppPool(new ForDevelopment());
    shareApp    = JavaConverter.arr2setLower(
                  SysConfig.me().readConfig().shareAppList);
    apilog      = new RequestApiLog();
    pm          = new ProcessManager();
    nodeID      = ClusterManager.me().localNodeID();
  }


  /**
   * 该方法支持嵌套请求, 前一个请求的参数会被保留在 ThreadLocalData.nestedCall 中.
   * App Mod Api 参数都被转换为小写.
   */
  public void call(ApiCall ac) {
    String apiPath = ApiPath.getPath(ac);
    log.debug("Call::", apiPath);

    try {
      ThreadLocalData tld = createLocalData(ac);
      make_extend_parameter(tld);
      apilog.log(ac);

      //
      // 跨机构调用共享 APP 中的 api, 此时 org 可以是另一个机构, 这种跨机构
      // 调用 api 的行为只支持平台机构; 此时 api 以 root 的数据库权限启动, 而参数中
      // 的 org 仍然是原先机构的 id.
      //
      // 即使 org 是其他机构, 运行的仍然是平台机构中的 api, 所以不会有代码越权访问资源,
      // 必须保证平台机构 api 逻辑安全.
      //
      if (shareApp.contains(ac.app)) {
        ac.org = SYS_ORG;
        tld.replaceOrg = true;
      }

      XjOrg org = chooseAppPool(tld).getOrg(ac.org);
      XjApp app = org.getApp(ac.app);
      app.run(ac.call, ac.mod, ac.api);

    } catch (XBosonException x) {
      throw x;

    } catch (Exception e) {
      throw new XBosonException(e);

    } catch (ThreadDeath dead) {
      log.warn("Killed", apiPath, Tool.miniStack(dead, 6));
      makeLastMessage("Api Process Killed");

    } finally {
      ThreadLocalData current = pm.get();
      if (current.nestedCall != null) {
        pm.start(current.nestedCall);
      } else {
        pm.exit();
      }
    }
  }


  /**
   * 线程被 kill, 不能正常应答(抛异常或发送错误消息都不可用), 这里发送最后一条消息,
   * 防止浏览器不停的请求这个没有应答的 api.
   */
  private void makeLastMessage(String msg) {
    try {
      PrintWriter out = pm.get().ac.call.resp.getWriter();
      out.write('"');
      out.write(msg);
      out.write('"');
      out.flush();
    } catch (IOException e) {
      log.error("makeLastMessage", e);
    }
  }


  /**
   * 's' 调试状态标记
   *    d：执行最新代码并返回调试信息，
   *    r：执行已发布代码并返回调试信息
   */
  private AppPool chooseAppPool(ThreadLocalData tld) {
    ApiTypes type = ApiTypes.of( tld.ac.call.req.getParameter("s") );
    tld.__dev_mode = type;

    switch (type) {
      case Development:
        return development;

      case Production:
        return production;
    }
    throw new XBosonException.NotExist("Unknow type " + type);
  }


  private ThreadLocalData createLocalData(ApiCall ac) {
    ThreadLocalData tld = new ThreadLocalData();
    tld.who = ac.call.sess.login_user;
    tld.orgid = ac.org;
    tld.ac = ac;
    tld.beginAt = System.currentTimeMillis();

    ThreadLocalData previous = pm.get();
    if (previous != null) {
      tld.nestedCall = previous;
    }
    pm.start(tld);
    return tld;
  }


  private void make_extend_parameter(ThreadLocalData tld) {
    Map<String, Object> ex = tld.ac.exparam;
    if (ex == null) {
      tld.ac.exparam = ex = new HashMap<>();
    }
    tld.exparam = ex;
    ex.put("org", tld.ac.org);
    ex.put("app", tld.ac.app);
    ex.put("mod", tld.ac.mod);
    ex.put(REQUEST_ID, Tool.uuid.ds());
  }


  /**
   * 返回扩展请求参数
   */
  public Map<String, Object> getExtendParameter() {
    return pm.get().exparam;
  }


  /**
   * HTTP 原始请求时的机构参数, 在运行后 HTTP 中的参数可以被替换为机构 orgid
   */
  public String originalOrg() {
    return pm.get().orgid;
  }


  /**
   * 返回当前 api 的抽象文件路径
   */
  public String getCurrentApiPath() {
    return pm.get().getApiPath();
  }


  /**
   * 返回 api 类型
   */
  public ApiTypes getApiModeType() {
    return pm.get().__dev_mode;
  }


  /**
   * 如果在 app 上下文中返回 true;
   * 在上下文中意味着可以安全的调用上下文相关函数而不会抛出异常.
   */
  public boolean isInContext() {
    return pm.get() != null;
  }


  /**
   * 当读取了脚本代码后调用该方法, 返回计数器的值, 并将计数器 +add,
   * 不在脚本上下文中总是返回 -1.
   */
  public int readScriptCount(int add) {
    ThreadLocalData tld = pm.get();
    if (tld == null)
      return -1;
    int c = tld.scriptReadCount;
    ++tld.scriptReadCount;
    return c;
  }


  /**
   * 在任何位置都可以安全调用该方法, 返回当前登录的用户,
   * 如果没有用户登录会抛出异常.
   */
  public IAWho who() {
    IAWho r = pm.get().who;
    if (r == null) {
      throw new XBosonException("not login");
    }
    return r;
  }


  /**
   * 当 org 被替换后, 返回 ture.
   * 替换的 org 通过 originalOrg() 可以得到, 只在 sys 机构时发生, 用于 sys 机构中的
   * api 访问其他机构中的表.
   */
  public boolean isReplaceOrg() {
    return pm.get().replaceOrg;
  }


  /**
   * 调用该方法允许该线程被 kill;
   * 在完全初始化之后才调用, 否则会造成多线程共享对象不一致.
   */
  public void readyForKill() {
    pm.get().readyForKill();
  }


  /**
   * 当前 js 环境是嵌套调用的返回 true.
   */
  public boolean isNestedCall() {
    return pm.get().nestedCall != null;
  }


  /**
   * 返回进程管理器
   */
  public ProcessManager getProcessManager() {
    return pm;
  }


  /**
   * 返回当前应用上下文
   */
  public static AppContext me() {
    if (instance == null) {
      synchronized (AppContext.class) {
        if (instance == null) {
          instance = new AppContext();
        }
      }
    }
    return instance;
  }


  /**
   * 线程变量保存包装器, 构造函数在 createLocalData() 中.
   * @see #createLocalData(ApiCall)
   */
  public class ThreadLocalData {
    /** 嵌套调用时将前一个调用的数据保存 */
    private ThreadLocalData nestedCall;

    /** @see #getExtendParameter() */
    private Map<String, Object> exparam;

    /** 当前调用用户 */
    private IAWho who;

    /** @see #originalOrg() */
    private String orgid;

    private ApiCall ac;

    /** true: HTTP 参数机构id 已经被替换 */
    private boolean replaceOrg;

    /** @see #readScriptCount(int)  */
    private int scriptReadCount;

    /** 请求开始的时间, ms */
    private long beginAt;

    private String __cache_path;
    private ApiTypes __dev_mode;
    private boolean __is_low_priority;
    private boolean __is_ready_for_kill;

    /**
     * 返回未被替换的原始参数.
     */
    String getApiPath() {
      if (__cache_path == null) {
        __cache_path = ApiPath.getPath(exparam, ac.api);
      }
      return __cache_path;
    }

    private ThreadLocalData() {}


    /**
     * @see AppContext#readyForKill()
     */
    private void readyForKill() {
      __is_ready_for_kill = true;
    }
  }


  /**
   * '进程' 管理器, 该对象会被导出到脚本环境, 必须仔细设计.
   */
  public class ProcessManager implements IProcessState {
    private final static long INTERVAL = 30 * 1000;
    private Map<Thread, ThreadLocalData> running;
    private Map<Long, Thread> id;


    private ProcessManager() {
      running = new ConcurrentHashMap<>();
      id = new ConcurrentHashMap<>();
      startCpuChecker();
    }


    private void startCpuChecker() {
      TimeFactory.me().schedule(new TimerTask() {
        public void run() {
          cpuSafe();
        }
      }, INTERVAL, INTERVAL);
    }


    /**
     * 请求线程开始进入管理区
     */
    private void start(ThreadLocalData data) {
      Thread t = Thread.currentThread();
      running.put(t, data);
      id.put(t.getId(), t);
    }


    /**
     * 请求线程退出
     */
    private void exit() {
      Thread t = Thread.currentThread();
      running.remove(t);
      id.remove(t.getId());
    }


    /**
     * 返回当前线程的绑定数据
     */
    private ThreadLocalData get() {
      Thread t = Thread.currentThread();
      return running.get(t);
    }


    /**
     * 检查所有线程, 一旦发现线程执行时间过长, 则降低运行优先级
     */
    private void cpuSafe() {
      for (Map.Entry<Thread, ThreadLocalData> entry : running.entrySet()) {
        ThreadLocalData tld = entry.getValue();

        if (tld.__is_low_priority == false
                && System.currentTimeMillis() - tld.beginAt > LOW_CPU_TIME) {

          Thread t = entry.getKey();
          t.setPriority(Thread.MIN_PRIORITY);
          tld.__is_low_priority = true;

          log.debug("Change Process Min Priority, Thread:", t.getId(),
                  '"'+ t.getName() +'"', ", API:", tld.getApiPath());
        }
      }
    }


    /**
     * 列出所有运行中的线程
     */
    public PublicProcessData[] list() {
      PublicProcessData[] ppd = new PublicProcessData[running.size()];
      int i = -1;

      for (Map.Entry<Thread, ThreadLocalData> entry : running.entrySet()) {
        ppd[++i] = createPD(entry.getKey(), entry.getValue());
      }
      return ppd;
    }


    /**
     * 终止 api 进程.
     * @param processId 进程 id
     * @return 停止了正在运行的进程返回 true, 如果进程不存或已经停止在返回 false
     */
    public int kill(long processId) {
      Thread t = id.get(processId);
      if (t == null)
        return KILL_NO_EXIST;

      if (! t.isAlive())
        return KILL_IS_KILLED;

      ThreadLocalData tld = running.get(t);
      if (tld == null)
        return KILL_NO_EXIST;

      if (! tld.__is_ready_for_kill)
        return KILL_NO_READY;

      //
      // 必须这样做, 脚本上下文的设计可以保证安全的 stop 线程.
      // [ 除非有 bug :( ]
      //
      t.stop();
      return KILL_OK;
    }


    public int stop(long processId) {
      return kill(processId);
    }


    private PublicProcessData createPD(Thread t, ThreadLocalData tld) {
      PublicProcessData pd = new PublicProcessData();
      pd.processId = t.getId();
      pd.org = tld.ac.org;
      pd.app = tld.ac.app;
      pd.mod = tld.ac.mod;
      pd.api = tld.ac.api;
      pd.beginAt = tld.beginAt;
      pd.runningTime = System.currentTimeMillis() - tld.beginAt;
      pd.nodeID = nodeID;

      if (tld.who instanceof LoginUser) {
        pd.callUser = ((LoginUser) tld.who).userid;
      } else {
        pd.callUser = tld.who.identification();
      }
      return pd;
    }
  }

}
