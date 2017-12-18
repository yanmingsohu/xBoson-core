////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-18 下午2:59
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/EmitWithoutCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;

import javax.naming.event.NamingEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 仅在当前进程内发送消息, 退出时自动回收资源.
 * 全局单线程.
 */
public class EmitWithoutCluster {

  private static EmitWithoutCluster instance;
  private ExecutorService worker;
  private Log log;


  private EmitWithoutCluster() {
    worker = Executors.newSingleThreadExecutor();
    log = LogFactory.create();
  }


  public static EmitWithoutCluster me() {
    if (instance == null) {
      synchronized (EmitWithoutCluster.class) {
        if (instance == null) {
          instance = new EmitWithoutCluster();
        }
      }
    }
    return instance;
  }


  /**
   * 必须在 GlobalEventBus.destory() 中调用
   */
  void destory() {
    worker.shutdown();
    Tool.waitOver(worker);
    worker = null;
    log.info("destoryed");
  }


  public void emit(GlobalListener gl, NamingEvent data, boolean skip_error) {
    worker.execute(new PushEvent(gl, data, skip_error));
  }


  private class PushEvent implements Runnable {
    private NamingEvent data;
    private GlobalListener who;
    private boolean skip_error;

    private PushEvent(GlobalListener who, NamingEvent data, boolean skip_error) {
      this.who = who;
      this.data = data;
      this.skip_error = skip_error;
    }

    @Override
    public void run() {
      try {
        who.objectChanged(data);
      } catch(Exception err) {
        if (skip_error) {
          log.warn("Skip error by error");
        } else {
          QuickSender.emitError(err, this);
        }
      }
    }
  }
}
