////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2021 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 21-5-17 上午8:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/UIExtRenderService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.been.Config;
import com.xboson.event.EventLoop;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.OnExitHandle;
import com.xboson.event.timer.TimeFactory;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Path;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.naming.event.NamingEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class UIExtRenderService extends OnExitHandle {

  public interface IRenderFile {
    //
    // 渲染回调, 调用结束后, http 应答返回, 不再接受任何调用
    //
    void render(byte[] content, String mime);
    //
    // 错误回调, 调用结束后, http 应答返回, 不再接受任何调用
    //
    void error(String message);
    //
    // 开启异步回调, 初始状态是同步回调(同一个线程)
    //
    void startAsync();
  }


  public interface IFileReader {
    byte[] readfile(String fullpath) throws IOException;
  }


  private final long CLEAR_CACHE_INTERVAL = 15 * 1000;
  private final long RETRY_INTERVAL = 5000;

  private final Log log;
  private AtomicLong idgen;
  private AtomicInteger server_idx;
  private Cli[] clients;
  private ConcurrentHashMap<Long, WaitAsk> waitAsk;
  private ConcurrentSkipListSet<String> extNames;
  private ClearCache cc;
  private IFileReader fs;
  private FileCachePool cache;


  public UIExtRenderService(IFileReader p) {
    this.log = LogFactory.create("ui-ext-render");
    this.idgen = new AtomicLong();
    this.server_idx = new AtomicInteger();
    this.extNames = new ConcurrentSkipListSet<>();
    this.waitAsk = new ConcurrentHashMap<>();
    this.cache = new FileCachePool();
    this.cc = new ClearCache();
    this.fs = p;
    initClient();
  }


  private void initClient() {
    Config cf = SysConfig.me().readConfig();
    if (cf.uiRenderServer != null) {
      clients = new Cli[cf.uiRenderServer.length];

      for (int i=0; i<cf.uiRenderServer.length; ++i) {
        try {
          clients[i] = new Cli(new URI(cf.uiRenderServer[i]));
          clients[i].connect();
        } catch (URISyntaxException e) {
          log.error("Render uri fail", e);
        }
      }
    } else {
      log.warn("None render server config");
      clients = new Cli[0];
    }
  }


  private Cli findClient() {
    Cli cli = null;
    server_idx.compareAndSet(clients.length, 0);
    int begin = server_idx.getAndIncrement();

    for (int i=0; i<clients.length; ++i) {
      cli = clients[(i + begin) % clients.length];
      if (cli.isOpen()) {
        return cli;
      }
    }
    return null;
  }


  public boolean canRender(String fullpath) {
    String ext = Path.me.extname(fullpath);
    return extNames.contains(ext);
  }


  /**
   * 必须首先调用 canRender 对文件进行测试
   */
  public void render(String fullpath, byte[] content, IRenderFile r, Object data) {
    render(fullpath, content, r, data, false);
  }


  /**
   * 渲染文件
   * @param fullpath 归一化的完整路径
   * @param content 文件内容
   * @param r 渲染回调
   * @param data 渲染文件时的上下文参数
   * @param noCache true 则强制不用缓存
   */
  public void render(String fullpath, byte[] content, IRenderFile r,
                     Object data, boolean noCache) {
    if (!noCache) {
      FileCache fc = cache.get(fullpath);
      if (fc != null) {
        fc.render(r);
        log.debug("Render from cache", fullpath);
        return;
      }
    }

    Cli cli = findClient();
    if (cli == null) {
      r.error("Render server offline");
      return;
    }

    WaitAsk wa = new WaitAsk();
    wa.setFullpath(fullpath);
    wa.render = r;

    try {
      ByteBuffer buf = cli.protocol.makeAskRender(wa.id, wa.filename, content, data);
      wa._wait();
      r.startAsync();
      cli.send(buf);
    } catch (Exception e) {
      r.error(e.getMessage());
    }
  }


  private class WaitAsk {
    IRenderFile render;
    String fullpath;
    String filename;
    String dir;
    long asktime;
    long id;

    WaitAsk() {
      this.asktime = System.currentTimeMillis();
      this.id = idgen.incrementAndGet();
    }

    void setFullpath(String f) {
      this.fullpath = f;
      this.filename = Path.me.basename(f);
      this.dir = Path.me.dirname(f);
    }

    void _wait() {
      waitAsk.put(id, this);
    }
  }


  private class FileCache {
    String fullpath;
    String mime;
    byte[] content;
    String[] deps;

    FileCache(String f, String m, byte[] c, String[] d) {
      this.fullpath = f;
      this.mime = m;
      this.content = c;
      this.deps = d;

      final String dir = Path.me.dirname(fullpath);
      for (int i=0; i<deps.length; ++i) {
        deps[i] = getAbsPath(deps[i], dir);
      }
    }

    void render(IRenderFile r) {
      try {
        r.render(content, mime);
      } catch (Exception e) {
        log.error("Render fail", e);
      }
    }
  }


  private class Ask implements UIExtProtocol.IAskListener {

    private Cli cli;


    Ask(Cli c) {
      cli = c;
    }


    @Override
    public void onRenderFile(long msg_id, byte[] content, String mime, String[] deps) {
      WaitAsk wa = waitAsk.remove(msg_id);
      if (wa == null) {
        log.warn("The rendered file returned, but it was not accepted", msg_id);
        return;
      }

      FileCache fc = new FileCache(wa.fullpath, mime, content, deps);
      cache.put(fc);
      fc.render(wa.render);
    }


    @Override
    public void onError(long msg_id, String msg) {
      WaitAsk wa = waitAsk.remove(msg_id);
      if (wa != null && wa.render != null) {
        try {
          wa.render.error(msg);
          return;
        } catch (Exception e) {
          log.error("Error", e.getMessage());
        }
      }
      log.error("Render Server response error", msg_id, msg);
    }


    @Override
    public void onAskFile(long msg_id, String filename) {
      WaitAsk wa = waitAsk.get(msg_id);
      if (wa == null) {
        log.error("Request depend file, but not found main file");
        return;
      }

      try {
        String fullpath = getAbsPath(filename, wa.dir);
        byte [] content = fs.readfile(fullpath);
        cli.send(cli.protocol.makeFileResp(msg_id, content));
      } catch(Exception e) {
        wa.render.error("File '"+ wa.fullpath
                +"' failed to load dependent, "+ e.getMessage());
        waitAsk.remove(msg_id);
      }
    }


    @Override
    public void onExtNames(long msg_id, String ext) {
      extNames.addAll(Arrays.asList(ext.split(" ")));
      log.debug("Render file type:", ext);
    }
  }


  private class Cli extends WebSocketClient {

    private UIExtProtocol protocol;
    private boolean do_reconnect = true;
    private boolean on_retry = false;


    Cli(URI serverUri) {
      super(serverUri);
      protocol = new UIExtProtocol(new Ask(this));
    }


    private void askExtNames() {
      try {
        WaitAsk wa = new WaitAsk();
        this.send(protocol.makeAskExt(wa.id));
      } catch(Exception e) {
        log.error("Ask ext name", e.getMessage());
      }
    }


    @Override
    public void onOpen(ServerHandshake h) {
      log.info("Connected", getRemoteSocketAddress(), h.getHttpStatusMessage());
      askExtNames();
      on_retry = false;
    }


    @Override
    public void onMessage(String s) {
      log.warn("server:", s);
    }


    @Override
    public void onMessage(ByteBuffer bytes) {
      try {
        protocol.parse(bytes);
      } catch(Exception e) {
        log.error("On message", e.getMessage());
        e.printStackTrace();
      }
    }


    @Override
    public void onClose(int i, String s, boolean b) {
      if (do_reconnect) {
        if (!on_retry) {
          log.warn("Server closed connect, reconnecting...");
        }
        Tool.sleep(RETRY_INTERVAL);
        // 必须在另外的线程中操作
        EventLoop.me().add(()->{
          on_retry = true;
          reconnect();
        });
      }
    }


    @Override
    public void onError(Exception e) {
      if (!on_retry) {
        log.error(e.getMessage());
      }
    }


    void destroy() {
      do_reconnect = false;
      try {
        closeBlocking();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  class ClearCache extends TimerTask {

    ClearCache() {
      TimeFactory.me().schedule(this,
              CLEAR_CACHE_INTERVAL, CLEAR_CACHE_INTERVAL);
    }

    @Override
    public void run() {
      clearAskQueue();
    }


    private void clearAskQueue() {
      long now = System.currentTimeMillis();
      List<Long> removeFlag = new ArrayList<>();

      for (WaitAsk a : waitAsk.values()) {
        if (now - a.asktime > CLEAR_CACHE_INTERVAL) {
          removeFlag.add(a.id);
        }
      }

      for (long id : removeFlag) {
        WaitAsk wa = waitAsk.remove(id);
        wa.render.error("Timeout");
      }
    }
  }


  /**
   * 复用了 TemplateEngine 的文件修改消息, 该消息由 UIIDE 脚本发送.
   * 这与 OnFileChangeHandle 中的定义不同
   */
  class FileCachePool extends GLHandle {

    private ConcurrentHashMap<String, FileCache> cache;
    private ConcurrentHashMap<String, Set<String>> reverse;


    FileCachePool() {
      this.cache = new ConcurrentHashMap<>();
      this.reverse = new ConcurrentHashMap<>();
      GlobalEventBus.me().on(TemplateEngine.RELOAD_TAGS, this);
    }

    @Override
    public void objectChanged(NamingEvent namingEvent) {
      Object path = namingEvent.getNewBinding().getObject();
      //Object type = namingEvent.getChangeInfo();
      String fullpath = Path.me.normalize((String) path);

      // 如果是主文件, 则删除该文件的缓存
      remove(fullpath);

      // 如果是被依赖文件, 则删除所有对应的主文件缓存
      Set<String> deps = reverse.get(fullpath);
      if (deps != null) {
        for (String d : deps) {
          remove(d);
        }
      }
      reverse.remove(fullpath);
    }

    void put(FileCache fc) {
      cache.put(fc.fullpath, fc);

      for (String dep : fc.deps) {
        Set<String> s = reverse.get(dep);
        if (s == null) {
          s = new ConcurrentSkipListSet<>();
          reverse.put(dep, s);
        }
        s.add(fc.fullpath);
      }
    }

    FileCache get(String full_path) {
      return cache.get(full_path);
    }

    void remove(String k) {
      FileCache fc = cache.remove(k);
      if (fc == null) return;

      // 删除与主文件关联的反向引用
      for (String file : fc.deps) {
        Set<String> s = reverse.get(file);
        if (s != null) {
          s.remove(k);
          if (s.isEmpty()) {
            reverse.remove(file);
          }
        }
      }
    }
  }


  private String getAbsPath(String filePath, String parentPath) {
    return Path.me.isAbsolute(filePath)
            ? filePath
            : Path.me.join(parentPath, filePath);
  }


  @Override
  protected void exit() {
    cc.cancel();
    for (Cli c : clients) {
      c.destroy();
    }
  }
}
