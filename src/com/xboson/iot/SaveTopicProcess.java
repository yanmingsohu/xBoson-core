////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:11
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/SaveTopicProcess.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.squareup.moshi.JsonAdapter;
import com.xboson.been.XBosonException;
import com.xboson.util.Tool;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.rmi.RemoteException;
import java.util.*;


public class SaveTopicProcess extends AbsWorker {

  private Map<String, DevDataType.ITransform> tdata;
  private MongoCollection<Document> dataTable;
  private JsonAdapter<Map> json;
  private UpdateOptions upsert;


  public SaveTopicProcess() {
    tdata = new HashMap<>();
    json = Tool.getAdapter(Map.class);
    upsert = new UpdateOptions().upsert(true);
  }


  private void updateProductInfo() throws RemoteException {
    Document p = util.openProduct(pid);
    tdata.clear();

    for (Object data : p.get("data", List.class)) {
      Document d = (Document) data;
      String name = d.getString("name");
      String type = d.getString("type");
      DevDataType.ITransform t = DevDataType.getTransform(type);
      tdata.put(name, t);
    }
  }


  @Override
  void beforeSubscribe(boolean reconnect) throws RemoteException {
    updateProductInfo();
    dataTable = util.openDb(TABLE_DATA);
  }


  @Override
  public String name() {
    return TOPIC_SAVE;
  }


  @Override
  public void onMessage(String topic, MqttMessage msg) throws Exception {
    TopicInf inf = new TopicInf(topic);
    createNoExistsDevice(inf, null);
    String str = msg.toString();
    Map<String, Object> data = json.fromJson(str);
    checkdata(data, inf);
  }


  private void checkdata(Map<String, Object> recv, TopicInf ti) {
    Object o = recv.get("data");
    if (!(o instanceof Map)) {
      throw new XBosonException("`data` Field is not a Map");
    }
    Date d = new Date( ((Number) recv.get("time")).longValue() );
    Saver saver = new Saver(ti, d);

    Map<String, Object> data = (Map<String, Object>)o;
    for (String k : data.keySet()) {
      DevDataType.ITransform t = tdata.get(k);
      if (t == null) {
        throw new XBosonException("'"+ k +"' is not defined in the product data list");
      }
      saver.pushData(k, t.t(data.get(k)));
    }
    saver.updateDevice();
  }


  private class Saver {

    private TopicInf inf;
    private Date dd;
    private Calendar c;
    private String devFullId;
    private Document filter;
    private Document up;
    private Document set;
    private int data_count;


    Saver(TopicInf inf, Date d) {
      this.inf       = inf;
      this.devFullId = inf.genDeviceID();
      this.filter    = new Document();
      this.up        = new Document();
      this.set       = new Document("$set", up);
      this.dd        = d;
      this.c         = Calendar.getInstance();
      c.setTime(d);
    }


    void pushData(String dataName, Object v) {
      up.put("l", v);
      up.put("dev", devFullId);

      _save(dataName, c, v, DT_YEAR);
      _save(dataName, c, v, DT_MONTH);
      _save(dataName, c, v, DT_DAY);
      _save(dataName, c, v, DT_HOUR);
      _save(dataName, c, v, DT_MINUTE);
      _save(dataName, c, v, DT_SECOND);
      ++data_count;
    }


    private void _save(String dataName, Calendar c, Object v, int dt) {
      String id = Util.dataId(devFullId, dataName, dt, c);
      String key = _update_val_key(dt, c);
      filter.put("_id", id);
      up.put("_id", id);
      up.put(key, v);

      dataTable.updateOne(filter, set, upsert);
      up.remove(key);
    }


    private void updateDevice() {
      Document filter = new Document("_id", devFullId);
      Document up = new Document();
      up.put("$set", new Document("dd", dd));
      up.put("$inc", new Document("dc", data_count));
      deviceTable.updateOne(filter, up);
    }


    private String _update_val_key(int dt, Calendar c) {
      switch (dt) {
        case DT_YEAR:
          return "v."+ c.get(Calendar.YEAR);

        case DT_MONTH:
          return "v."+ (1 + c.get(Calendar.MONTH));

        case DT_DAY:
          return "v."+ c.get(Calendar.DAY_OF_MONTH);

        case DT_HOUR:
          return "v."+ c.get(Calendar.HOUR_OF_DAY);

        case DT_MINUTE:
          return "v."+ c.get(Calendar.MINUTE);

        case DT_SECOND:
          return "v."+ c.get(Calendar.SECOND);
      }
      throw new XBosonException("Invalid data type "+ dt);
    }
  }
}
