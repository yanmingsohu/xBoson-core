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
// 文件创建日期: 21-5-17 上午8:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/UIExtRenderService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.squareup.moshi.JsonAdapter;
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
    void render(byte[] content, String mime, String path);
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


  public interface ISequenceParameters {
    // 必须返回一个非空的 json 字符串
    String get();
  }


  private final long CLEAR_CACHE_INTERVAL = 15 * 1000;
  private final long RETRY_INTERVAL = 5000;

  private final Log log;
  private AtomicLong idgen;
  private AtomicInteger server_idx;
  private Cli[] clients;
  private Map<Long, WaitAsk> waitAsk;
  // 优化, 统计所有远程渲染服务器扩展名
  private Set<String> extNames;
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


  private Cli findClient(String fullpath) {
    String ext = Path.me.extname(fullpath);
    server_idx.compareAndSet(clients.length, 0);
    int begin = server_idx.getAndIncrement();

    for (int i=0; i<clients.length; ++i) {
      Cli cli = clients[(i + begin) % clients.length];
      if (cli.isOpen() && cli.canRender(ext)) {
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
  public void render(String fullpath, byte[] content,
                     IRenderFile r, ISequenceParameters data) {
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
                     ISequenceParameters data, boolean noCache) {
    String parameters = data.get();
    if (!noCache) {
      FileCache fc = cache.get(fullpath, parameters);
      if (fc != null) {
        fc.render(r);
        log.debug("Render from cache", fullpath);
        return;
      }
    }

    Cli cli = findClient(fullpath);
    if (cli == null) {
      r.error("All Render server offline");
      return;
    }

    WaitAsk wa = new WaitAsk(parameters);
    wa.setFullpath(fullpath);
    wa.render = r;

    try {
      ByteBuffer buf = cli.protocol.makeAskRender(
              wa.id, wa.filename, content, wa.parameters);
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
    String parameters;
    long asktime;
    long id;

    WaitAsk(String parameters) {
      this.asktime = System.currentTimeMillis();
      this.id = idgen.incrementAndGet();
      this.parameters = parameters;
    }

    WaitAsk() {
      this("{}");
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
        r.render(content, mime, fullpath);
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
      cache.put(fc, wa.parameters);
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
      String[] names = ext.split(" ");
      cli.setExtNames(names);
      log.debug("Render file type:", ext);
    }
  }


  private class Cli extends WebSocketClient {
    // 每个服务器支持的可渲染文件类型可以不同
    private Set<String> _extNames;
    private UIExtProtocol protocol;
    private boolean do_reconnect = true;
    private boolean on_retry = false;


    Cli(URI serverUri) {
      super(serverUri);
      this.protocol = new UIExtProtocol(new Ask(this));
      this._extNames = new ConcurrentSkipListSet<>();
    }

    private void askExtNames() {
      try {
        WaitAsk wa = new WaitAsk();
        this.send(protocol.makeAskExt(wa.id));
      } catch(Exception e) {
        log.error("Ask ext name", e.getMessage());
      }
    }

    boolean canRender(String ext) {
      return _extNames.contains(ext);
    }

    void setExtNames(String[] names) {
      for (String n : names) {
        _extNames.add(n);
        extNames.add(n);
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
      log.warn(getRemoteSocketAddress(), "server:", s);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
      try {
        protocol.parse(bytes);
      } catch(Exception e) {
        log.error(getRemoteSocketAddress(), "On message", e.getMessage());
        e.printStackTrace();
      }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
      if (do_reconnect) {
        if (!on_retry) {
          log.warn(getRemoteSocketAddress(), "Server closed connect, reconnecting...");
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
        log.error(getRemoteSocketAddress(), e.getMessage());
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


  private class ClearCache extends TimerTask {

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
  private class FileCachePool extends GLHandle {

    // {带参数的主文件: 缓存项}
    private Map<String, FileCache> cache;
    // {依赖文件: [无参数主文件]}
    private Map<String, Set<String>> reverse;
    // {无参数主文件: [带参数主文件]}
    private Map<String, Set<String>> fileParam;


    FileCachePool() {
      this.cache = new ConcurrentHashMap<>();
      this.reverse = new HashMap<>();
      this.fileParam = new HashMap<>();
      GlobalEventBus.me().on(TemplateEngine.RELOAD_TAGS, this);
    }

    @Override
    public void objectChanged(NamingEvent namingEvent) {
      Object path = namingEvent.getNewBinding().getObject();
      //Object type = namingEvent.getChangeInfo();
      String withoutParam = Path.me.normalize((String) path);
      removeMainWithoutParam(withoutParam);
    }

    private synchronized void removeMainWithoutParam(String withoutParam) {
      Set<String> files = fileParam.remove(withoutParam);

      if (files != null) {
        for (String withParamFile : files) {
          // 如果是主文件, 则删除该文件的缓存
          FileCache fc = cache.remove(withParamFile);
          if (fc != null) {
            removeDeps(fc.deps, withoutParam);
          }
        }
      }

      // 如果是被依赖文件, 则删除所有对应的主文件缓存
      Set<String> deps = reverse.remove(withoutParam);
      if (deps != null) {
        for (String d : deps) {
          removeMainWithoutParam(d);
        }
      }
    }

    private void removeDeps(String[] deps, String withoutParam) {
      for (String file : deps) {
        Set<String> s = reverse.get(file);
        if (s != null) {
          s.remove(withoutParam);
          if (s.isEmpty()) {
            reverse.remove(file);
          }
        }
      }
    }

    synchronized void put(FileCache fc, String parameter) {
      String fp = fc.fullpath + parameter;
      cache.put(fp, fc);

      Set<String> wp = fileParam.get(fc.fullpath);
      if (wp == null) {
        wp = new ConcurrentSkipListSet<>();
        fileParam.put(fc.fullpath, wp);
      }
      wp.add(fp);

      for (String dep : fc.deps) {
        Set<String> s = reverse.get(dep);
        if (s == null) {
          s = new ConcurrentSkipListSet<>();
          reverse.put(dep, s);
        }
        s.add(fc.fullpath);
      }
    }

    /**
     * 获取一个缓存项, 不同的请求参数对应不同的缓存项
     * @param full_path - 文件路径
     * @param parameter - 请求文件的参数
     * @return
     */
    FileCache get(String full_path, String parameter) {
      return cache.get(full_path + parameter);
    }
  }


  private String getAbsPath(String filePath, String parentPath) {
    return Path.me.isAbsolute(filePath)
            ? filePath
            : Path.me.join(parentPath, filePath);
  }


  private String sequenceParameters(Object data) {
    String parm;
    if (data != null) {
      JsonAdapter json = Tool.getAdapter(data.getClass());
      parm = json.toJson(data);
    } else {
      parm = "{}";
    }
    return parm;
  }


  @Override
  protected void exit() {
    cc.cancel();
    for (Cli c : clients) {
      c.destroy();
    }
  }
}
