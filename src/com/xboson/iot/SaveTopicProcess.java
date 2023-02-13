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
