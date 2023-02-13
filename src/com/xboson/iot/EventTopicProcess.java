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
// 文件创建日期: 20-11-23 上午7:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/EventTopicProcess.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.squareup.moshi.JsonAdapter;
import com.xboson.util.Tool;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.rmi.RemoteException;


public class EventTopicProcess extends AbsWorker {

  private JsonAdapter<EventDoc> jadapter;


  @Override
  void beforeSubscribe(boolean reconnect) throws RemoteException {
    if (!reconnect) {
      jadapter = Tool.getAdapter(EventDoc.class);
    }
  }


  @Override
  public String name() {
    return TOPIC_EVENT;
  }


  @Override
  public void onMessage(String topic, MqttMessage msg) throws Exception {
    TopicInf inf = new TopicInf(topic);
    createNoExistsDevice(inf, null);

    EventDoc event = jadapter.fromJson(msg.toString());
    pushEvent(inf, event);
  }
}
