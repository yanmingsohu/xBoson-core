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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/StateTopicProcess.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.rmi.RemoteException;


public class StateTopicProcess extends AbsWorker {


  @Override
  void beforeSubscribe(boolean reconnect) throws RemoteException, MqttException {
  }


  @Override
  public String name() {
    return TOPIC_STATE;
  }


  @Override
  void onMessage(String topic, MqttMessage msg) throws Exception {
    TopicInf inf = new TopicInf(topic);
    String state = msg.toString();
    if (! createNoExistsDevice(inf, state)) {
      changeState(inf.genDeviceID(), state);
    }
    addCounter();
  }
}
