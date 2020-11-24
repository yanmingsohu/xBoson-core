////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.script.lib.Bytes;
import com.xboson.util.Hex;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeInt8Array;
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
  private MongoCollection<Document> cmdTable;
  private ScriptObjectMirror ondata;
  private ScriptObjectMirror oncmd;
  private Module mod;
  private JsonAdapter<Map> json;


  /**
   * 导出到 js 环境中
   */
  public class JsDevice {

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
    public void sendCmd(Map<String, Object> cmd) throws MqttException {
      checkCmd(cmd);
      Object o = oncmd.call(null, cmd, this);
      byte[] bytes = getBytes(o);
      mq.publish(inf.genTopic(TOPIC_CMD), bytes, qos, true);
      saveCmd(cmd, inf, bytes);
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
  }


  public DataTopicProcess() {
    tcmd = new HashMap<>();
    json = Tool.getAdapter(Map.class);
  }


  /**
   * 检查命令数据是否符合定义, 并做数据转换
   */
  private void checkCmd(Map<String, Object> cmd) {
    for (String k : cmd.keySet()) {
      DevDataType.ITransform t = tcmd.get(k);
      if (t == null) {
        throw new XBosonException(k +" is not defined in the product command list");
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
