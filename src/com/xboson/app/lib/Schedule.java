////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-16 上午9:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/Schedule.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.IDict;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.event.timer.TimeFactory;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.DateParserFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import okhttp3.*;

import java.math.BigDecimal;
import java.util.*;


public class Schedule extends RuntimeUnitImpl {

  private static final String LOG_FILE = "insert-scheduler-log.sql";

  /**
   * 每个应用都有独立的上下文自然会将不同的 org 分开
   */
  private final Map<String, Task>
          management = Collections.synchronizedMap(new HashMap<>());

  private final Log log;
  private OkHttpClient hc;


  public Schedule() {
    super(null);
    this.log = LogFactory.create();
  }


  public void start(String id, Map<String, Object> config) {
    getStr(id, "id");
    Task task = management.get(id);
    if (task != null) {
      throw new XBosonException(
              "Schedule Task "+ task.schedulenm +"("+ id +") is running");
    }
    task = new Task(id, config);
    management.put(id, task);
  }


  public boolean stop(String id) {
    Task task = management.get(id);
    if (task == null) {
      return false;
    }
    task.stop();
    return true;
  }


  public Object info(String id) {
    return management.get(id);
  }


  private class Task implements IDict {
    private String  schedulenm;         // 名称
    private Date    start_time;         // 开始时间
    private Date    run_end_time;       // 结束时间
    private int     schedule_interval;  // 间隔时间
    private int     schedule_cycle;     // 间隔单位
    private int     run_times;          // 运行次数, -1 不限制
    private String  task_api;           // 任务 api
    private String  id;
    public  int     state;

    private ConnectConfig db;
    private TimerTask task;
    

    private Task(String id, Map<String, Object> config)
    {
      schedulenm        = getStr(config, "schedulenm");
      schedule_cycle    = getInt(config, "schedule_cycle");
      run_times         = getInt(config, "run_times");
      task_api          = getStr(config, "task_api");
      schedule_interval = getInt(config, "schedule_interval");
      run_end_time      = parseDate(config, "run_end_time");
      start_time        = parseDate(config, "start_time");

      this.db           = SysConfig.me().readConfig().db;
      this.state        = JOB_STATUS_INIT;
      this.id           = id;
      TimeFactory.me().schedule(new Inner(), start_time);
    }


    private class Inner extends TimerTask {
      private Inner() {
        task = this;
      }
      @Override
      public void run() {
        try {
          state = JOB_STATUS_RUNNING;
          callApi();
        } catch (Exception e) {
          log.error(e);
        }

        if (run_times > 0) {
          if (--run_times == 0) {
            state = JOB_STATUS_MAXCOUNT;
            stop();
            return;
          }
        }

        if (schedule_interval <= 0) {
          state = JOB_STATUS_DEL;
          stop();
          return;
        }

        Date next = nextDate();
        if (next == null
                || (run_end_time != null
                && next.compareTo(run_end_time) >= 0)) {
          state = JOB_STATUS_TIMEUP;
          stop();
          return;
        }

        start_time = next;
        TimeFactory.me().schedule(new Inner(), start_time);
      }
    }


    public void stop() {
      task.cancel();
      management.remove(id);
    }


    public String name() {
      return schedulenm;
    }


    private void callApi() throws Exception {
      log.debug("Call", task_api);
      HttpUrl.Builder url = HttpUrl.parse(task_api).newBuilder();
      Request.Builder req = new Request.Builder();
      req.url(url.build());

      Object[] parm = new Object[] { id, new Date(), null, task_api };

      try {
        Response resp = openClient().newCall(req.build()).execute();
        ResponseBody body = resp.body();
        parm[2] = body.byteStream();
        state = JOB_STATUS_STOP;
      } catch (Exception e) {
        state = JOB_STATUS_ERR;
        parm[2] = e.toString();
      }

      try (SqlResult sr = SqlReader.query(LOG_FILE, db, parm)) {
        sr.getUpdateCount();
      }
    }


    public Date nextDate() {
      Calendar c = Calendar.getInstance();
      c.setTime(start_time);

      switch (schedule_cycle) {
        case JOB_UNIT_YEAR:
          c.add(Calendar.YEAR, schedule_interval);
          break;
        case JOB_UNIT_MONTH:
          c.add(Calendar.MONTH, schedule_interval);
          break;
        case JOB_UNIT_WEEK:
          c.add(Calendar.WEEK_OF_YEAR, schedule_interval);
          break;
        case JOB_UNIT_DAY:
        case JOB_UNIT_DAY2:
          c.add(Calendar.DAY_OF_YEAR, schedule_interval);
          break;
        case JOB_UNIT_HOUR:
          c.add(Calendar.HOUR, schedule_interval);
          break;
        case JOB_UNIT_SECOND:
          c.add(Calendar.SECOND, schedule_interval);
          break;
        case JOB_UNIT_MINUTE:
          c.add(Calendar.MINUTE, schedule_interval);
          break;
        default:
          return null;
      }
      return c.getTime();
    }
  }


  private OkHttpClient openClient() {
    if (hc == null) {
      //
      // 这个对象可能很昂贵
      //
      hc = new OkHttpClient();
    }
    return hc;
  }


  private String getStr(String i, String name) {
    if (Tool.isNulStr(i))
      throw new XBosonException.BadParameter(
              "String " + name, "not null");
    return i;
  }


  private String getStr(Map<String, Object> map, String name) {
    return getStr(map.get(name).toString(), name);
  }


  private int getInt(Map<String, Object> map, String name) {
    try {
      Object o = map.get(name);
      if (o instanceof String) {
        return Integer.parseInt((String) o);
      }
      if (o instanceof BigDecimal) {
        return ((BigDecimal) o).intValue();
      }
      if (o instanceof Number) {
        return (int) o;
      }
      return Integer.parseInt(o.toString());
    } catch (Exception e) {
      log.debug("Parameter", name, e.toString());
      return 0;
    }
  }


  private Date parseDate(Map<String, Object> map, String name) {
    Object o = map.get(name);
    if (o instanceof Date) {
      return (Date) o;
    }

    try (DateParserFactory.DateParser
                 p = DateParserFactory.get(Tool.COMM_DATE_FORMAT)) {
      return p.parse(o.toString());
    } catch (Exception e) {
      log.debug("Parameter", name, e.toString());
      return null;
    }
  }
}
