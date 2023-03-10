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
// 文件创建日期: 20-11-23 上午7:11
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/DataTopicProcess.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.mongodb.client.MongoCollection;
import com.squareup.moshi.JsonAdapter;
import com.xboson.been.Module;
import com.xboson.been.XBosonException;
import com.xboson.script.IVisitByScript;
import com.xboson.script.lib.Bytes;
import com.xboson.util.Hex;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataTopicProcess extends AbsWorker implements IDeviceCommandProcessor {

  private Map<String, DevDataType.ITransform> tcmd;
  private Map<String, Boolean> tmeta;
  private MongoCollection<Document> cmdTable;
  private ScriptObjectMirror ondata;
  private ScriptObjectMirror oncmd;
  private Module mod;
  private JsonAdapter<Map> json;


  /**
   * 导出到 js 环境中
   */
  public class JsDevice implements IVisitByScript {

    private TopicInf inf;


    private JsDevice(TopicInf inf) {
      this.inf = inf;
    }


    /**
     * 保存设备数据
     * @param data K 是变量名, V 是变量值
     */
    public void saveData(Map<String, Object> data) throws MqttException {
      Map<String, Object> send = new HashMap<>();
      send.put("time", util.now.now);
      send.put("data", data);
      byte[] payload = json.toJson(send).getBytes(IConstant.CHARSET);
      mq.publish(inf.genTopic(TOPIC_SAVE), payload, qos, true);
    }


    /**
     * 发送结构化命令, 该命令经过设备脚本 'on_cmd' 转换后发送到设备
     * @param cmd K 是命令名, V 是命令值
     */
    public void sendCmd(Map<String, Object> cmd) throws RemoteException {
      processCommand(inf, cmd);
    }


    /**
     * 修改设备的状态
     */
    public void changeState(String s) {
      DataTopicProcess.this.changeState(inf.genDeviceID(), s);
    }


    /**
     * 推送事件
     */
    public void sendEvent(int code, int level, String msg, Map data) throws RemoteException {
      EventDoc doc = new EventDoc(code, msg, level, util.now.now);
      doc.data = data;
      pushEvent(inf, doc);
    }


    /**
     * 更新设备 meta
     */
    public void setMeta(Map<String, Object> meta) throws RemoteException {
      for (String key : meta.keySet()) {
        if (! tmeta.containsKey(key)) {
          throw new RemoteException("'"+ key +"' is not defined in the product meta list");
        }
        if ((meta.get(key) == null) && tmeta.get(key)) {
          throw new RemoteException("'"+ key +"' meta value be not null");
        }
      }
      deviceTable.updateOne(
              new Document("_id", inf.genDeviceID()),
              new Document("meta", meta));
    }


    public String deviceId() {
      return inf.device;
    }

    public String productId() {
      return inf.product;
    }

    public String scenesId() {
      return inf.scenes;
    }
  }


  public DataTopicProcess() {
    tcmd  = new HashMap<>();
    tmeta = new HashMap<>();
    json  = Tool.getAdapter(Map.class);
  }


  @Override
  public void sendCommand(TopicInf inf, Map<String, Object> cmd) throws RemoteException {
    if (!mod.loaded) reloadScript();
    processCommand(inf, cmd);
  }


  /**
   * 发送命令基础函数
   * @param inf 地址
   * @param cmd 命令数据
   * @throws RemoteException
   */
  private void processCommand(TopicInf inf, Map<String, Object> cmd) throws RemoteException {
    try {
      checkCmd(cmd);
      Object o = oncmd.call(null, cmd, this);
      byte[] bytes = getBytes(o);
      mq.publish(inf.genTopic(TOPIC_CMD), bytes, qos, true);
      saveCmd(cmd, inf, bytes);

    } catch (MqttException e) {
      throw new RemoteException(e.getMessage());
    }
  }


  /**
   * 检查命令数据是否符合定义, 并做数据转换
   */
  private void checkCmd(Map<String, Object> cmd) {
    for (String k : cmd.keySet()) {
      DevDataType.ITransform t = tcmd.get(k);
      if (t == null) {
        throw new XBosonException("'"+ k +"' is not defined in the product command list");
      }
      cmd.put(k, t.t(cmd.get(k)));
    }
  }


  private void saveCmd(Map<String, Object> data, TopicInf inf, byte[] payload) {
    Document doc = new Document();
    doc.put("devid",    inf.genDeviceID());
    doc.put("scenes",   inf.scenes);
    doc.put("product",  inf.product);
    doc.put("cd",       new Date());
    doc.put("data",     data);
    doc.put("payload",  Hex.upperHex(payload));

    cmdTable.insertOne(doc);
  }


  private byte[] getBytes(Object o) {
    if (o instanceof String) {
      return ((String)o).getBytes(IConstant.CHARSET);
    }
    if (!(o instanceof ScriptObjectMirror)) {
      throw new XBosonException("Function `on_cmd` must return Int8Array/Uint8Array/String");
    }
    ScriptObjectMirror m = (ScriptObjectMirror)o;
    byte[] bytes = new byte[m.size()];
    for (int i=0; i<bytes.length; ++i) {
      bytes[i] = (byte)(int) m.getSlot(i);
    }
    return bytes;
  }


  private ScriptObjectMirror getFunc(ScriptObjectMirror exports, String name)
          throws RemoteException
  {
    ScriptObjectMirror f = (ScriptObjectMirror) exports.get(name);
    if (f == null || (!f.isFunction())) {
      throw new RemoteException(
              "The `"+ name +"` function is not exported in the script");
    }
    return f;
  }


  @Override
  void beforeSubscribe(boolean reconnect) throws RemoteException {
    if (!reconnect) {
      cmdTable = util.openDb(TABLE_CMD);
    }
    reloadScript();
    updateProductInfo();
  }


  private synchronized void reloadScript() throws RemoteException {
    mod = util.run(script);
    ScriptObjectMirror exports = (ScriptObjectMirror) mod.exports;
    ondata = getFunc(exports, FUNCTION_DATA);
    oncmd  = getFunc(exports, FUNCTION_CMD);
  }


  private void updateProductInfo() throws RemoteException {
    Document p = util.openProduct(pid);
    tcmd.clear();

    for (Object cmd : p.get("cmd", List.class)) {
      Document c = (Document) cmd;
      String name = c.getString("name");
      String type = c.getString("type");
      DevDataType.ITransform t = DevDataType.getTransform(type);
      tcmd.put(name, t);
    }

    for (Object meta : p.get("meta", List.class)) {
      Document m = (Document) meta;
      String name = m.getString("name");
      Number notnull = (Number) m.get("notnull");
      tmeta.put(name, notnull.intValue() != 0);
    }
  }


  @Override
  public String name() {
    return TOPIC_DATA;
  }


  @Override
  public void onMessage(String topic, MqttMessage msg) throws Exception {
    if (!mod.loaded) reloadScript();

    TopicInf inf = new TopicInf(topic);
    createNoExistsDevice(inf, null);
    Bytes payload = new Bytes(msg.getPayload());
    ondata.call(null, payload, new JsDevice(inf));
  }
}
