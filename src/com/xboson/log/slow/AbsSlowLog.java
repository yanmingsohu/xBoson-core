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
// 文件创建日期: 18-1-23 上午10:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/slow/AbsSlowLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.slow;

import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.event.EventLoop;
import com.xboson.event.OnExitHandle;
import com.xboson.log.ILogName;
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
public abstract class AbsSlowLog extends OnExitHandle implements ILogName {

  /** 日志线程最长执行时间, 单位: 分钟 */
  private final static int MAX_RUN_TIME_MINUTE = 15;

  private Queue<Object[]> queue;
  private Runnable insert;
  private boolean hasWorker;
  private SimpleDateFormat format;
  private boolean isDestroy;
  private ConnectConfig db;

  /**
   * 可以在子类中直接用, 记录异常;
   */
  protected final Log log;
  protected final static Uuid uuid = new Uuid();


  protected AbsSlowLog() {
    try {
      this.log        = createLog();
      this.queue      = new LinkedList<>();
      this.insert     = new Insert();
      this.hasWorker  = false;
      this.format     = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.999Z");
      this.db         = SysConfig.me().readConfig().db;
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 该方法在初始化时调用, 创建一个日志对象用于记录日志的日志;
   * 默认这个日志如果写入数据库会导致自调用, 必要时需要重写该方法.
   */
  protected Log createLog() {
    return LogFactory.create(this);
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
    if (isDestroy) return;
    synchronized (queue) {
      queue.add(param);
      if (!hasWorker) {
        hasWorker = true;
        EventLoop.me().add(insert, MAX_RUN_TIME_MINUTE*60*1000);
      }
    }
  }


  private class Insert implements Runnable {
    @Override
    public void run() {
      Object[] param;
      int count = 0;

      try (Connection conn = DbmsFactory.me().open(db)) {
        PreparedStatement ps = conn.prepareStatement(getSql());

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
            log.debug(Arrays.toString(param));
          } catch (Exception e) {
            log.error("Insert log fail", e, Arrays.toString(param));
          }
        }
      } catch (SQLException e) {
        log.error("Insert log fail", Tool.allStack(e));
      } finally {
        hasWorker = false;
      }

      log.debug("Save", count, "logs");
    }
  }


  public void destroy() {
    isDestroy = true;
    removeExitListener();
    exit();
  }


  @Override
  protected void exit() {
  }


  /**
   * 返回当前时间的 RFC3339 格式字符串
   * "yyyy-MM-dd'T'HH:mm:ss.999Z"
   */
  protected String nowInternet() {
    return format.format(new Date());
  }
}
