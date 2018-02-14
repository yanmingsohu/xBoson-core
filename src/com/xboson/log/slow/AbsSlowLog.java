////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-23 上午10:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/slow/AbsSlowLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.slow;

import com.xboson.been.XBosonException;
import com.xboson.db.DbmsFactory;
import com.xboson.event.EventLoop;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Uuid;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;


/**
 * 慢日志父类, 可以创建多个实例, 所有实例都是用同一个数据库连接.
 * 插入的日志并不是立即进入 DB 而是在一个低优先级的单线程中执行 DB 写入.
 */
public abstract class AbsSlowLog extends OnExitHandle {

  /** 永久占用一个 DB 连接 */
  private static Connection __conn;

  private PreparedStatement ps;
  private Queue<Object[]> queue;
  private Runnable insert;
  private boolean hasWorker;
  private SimpleDateFormat format;

  /** 可以在子类中直接用, 记录异常 */
  protected final Log log;
  protected final static Uuid uuid = new Uuid();


  protected AbsSlowLog() {
    try {
      this.log        = LogFactory.create();
      this.queue      = new LinkedList<>();
      this.insert     = new Insert();
      this.hasWorker  = false;
      this.format     = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.999Z");
      buildSql();
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  private static Connection openConnection() throws SQLException {
    if (__conn == null) {
      synchronized (AbsSlowLog.class) {
        if (__conn == null) {
          __conn = DbmsFactory.me().open(SysConfig.me().readConfig().db);
        }
      }
    }
    return __conn;
  }


  private void checkConnect() {
    synchronized (AbsSlowLog.class) {
      try {
        if (__conn == null)
          return;
        if (__conn.isClosed()) {
          __conn = null;
        }
        if (!__conn.isValid(1000)) {
          Tool.close(__conn);
          __conn = null;
        }
        if (ps == null || ps.isClosed()) {
          __conn = null;
        }
      } catch (Exception e) {
        log.warn("Check connect fail", e);
        __conn = null;
      } finally {
        try {
          if (__conn == null) {
            buildSql();
          }
        } catch (SQLException e) {
          log.error("Reconnect fail", e);
        }
      }
    }
  }


  private void buildSql() throws SQLException {
    log.debug("Build Prepare Statement");
    Connection conn = openConnection();
    ps = conn.prepareStatement(getSql());
  }


  /**
   * 该方法返回一个 sql 文用于插入日志
   */
  protected abstract String getSql();


  /**
   * 调用该方法将日志参数压入队列中, 日志参数应与 getSql() 返回的 sql 对应.
   * 线程安全.
   */
  protected void insert(Object... param) {
    synchronized (queue) {
      queue.add(param);
      if (!hasWorker) {
        hasWorker = true;
        EventLoop.me().add(insert);
      }
    }
  }


  private class Insert implements Runnable {
    @Override
    public void run() {
      Object[] param;
      int count = 0;
      checkConnect();

      for (;;) {
        synchronized (queue) {
          param = queue.poll();
          hasWorker = param != null;
        }

        if (param == null)
          break;

        try {
          for (int i = 0; i < param.length; ++i) {
            ps.setObject(i + 1, param[i]);
          }
          ps.executeUpdate();
          ++count;
        } catch (Exception e) {
          log.error("Insert log fail", e, Arrays.toString(param));
          checkConnect();
        }
      }

      log.debug("Save", count, "logs");
    }
  }


  @Override
  protected void exit() {
    Tool.close(ps);
    Tool.close(__conn);
    ps = null;
    __conn = null;
  }


  /**
   * 返回当前时间的 RFC3339 格式字符串
   * "yyyy-MM-dd'T'HH:mm:ss.999Z"
   */
  protected String nowInternet() {
    return format.format(new Date());
  }
}
