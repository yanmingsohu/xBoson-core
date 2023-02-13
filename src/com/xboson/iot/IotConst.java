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
// 文件创建日期: 20-11-23 上午7:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/IotConst.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;


public interface IotConst {

  String[] z = {
    "cnxHP35Px1clhUlW40WcI72AoUWPhb+eo6oHHvE+wRw=",
    "GubGgmbOD+rdyFYGv0FjEeCs7Ata7g7oGlbQ+2coHEI=",
    "w4NxSRyn1JiYANA+Pty0dsmlnzKsTmY7mps9tFhZ0RI=",
    "JMFs8cSdAcz22fctK765uRYrfyfDyAUr5x4DzZ7diuQ=",
    "D9YxXKvMxULvisxsHGDPT5j2qeS6ONJ+jEB3/gHqfw4=",
    "SkHInFweX68Cv3AFi8PmdmYrxVKKkCtFuRM4aayOGng=",
    "prjnarB1No700sxVG1ONDntHQP7YMHOaVPOxD+mydfg=",
    "XTGlYcj3VmDJNE9TGmNzrs0bXA/e+bOmuK1+I6TAXgg=",
    "IgGSfXxw7HMg4rJ2i1yzCcVsZjMoPksWxo99hmrIFwg=",
    "LBf7FkcoQhPEyhG9Kpp/Dgkzwg7mwbVED8XIdieXvao=",
    "Qo+220dOU8ZQ8S+QpXdUhSeUCyA+7NSnNUD9P/7bRuE=",
    "HnLL4gV7ABPkvzIQrb/r73LQTrX1uSdSicQU/IGWFoA=",
    "4YvX2uYP+p3/clqCvWF1WmV85d+xjYNBv/oa4BU4iHg=",
    "pSd+g/pM/fVHon433jEz8kLyUbOtJbGKFnNdJvNrhFU=",
    "CA0e+0R/fWgSptimQTeWS8f763UgJqKyZanLvMEZFfQ=",
    "fApO1am4r54E6K7jSf9sxNkihhniX/Q4kWnN6nLhFpU=",
    "pf3dV2I7q6ntOoEsyrkm6zB2/wgFgznx2LZowQQJU60=",
    "JCxEkXR0o9/6lTrpucC8HTKQ0c+M8tOGZR4Jzp8zhrI=",
    "zOyRF7lel3h7DKfYimt9gYMaprLI1qvpC+UX38nira0=",
    "OZPsOSxKnKPzV+1Hk+758ZwpGEN8qKaE5Dge86lcBNE=",
    "27NdGLa5OYGAcqMH3O8q740ntgEMSr2t6pqiY0WuT2s=",
    "THgyNqDyFI6cWzZiwSaqdf3mE1x/tgXFU6JxHlvfOE8=",
    "ZbKiyQBRkfLOCL1VtRWSqGYiXUCoarNXta5uR4P7rks=",
    "34DKoxshUUIcqW/4bIvfKPmqyCBUTgbqSF/2bEzg6Z8=",
    "8oJSM6KV83YXdS76bxFklUQER384243r+lO+H0Yrv5M=",
    "EDeriya0+fCTXP7kp5qKRBqtPGzTCvYsvVqsn01wPBI=",
    "JtP+sAykmYf6KqjtA6zqwy4LeJ5oXPhNVVzQCcLcS10=",
    "Lmt9wwsi3IigpYMz6gBqnzRwcM5ualbVM+fpKRorxaE=",
    "GPyxAWu2nULNREMU67joUjwZ5UqtIr7feoY4UrmVQlo=",
    "Ol6fStSfKZZ2oktRJOc7dVNyj3f0lFSmpV7LxNu7xX8=",
    "X5P6mWLhE+2NJqKplj0GBbTy+xF+dwMrFseuOuFLYYE=",
    "RbJEEjpqkEagquGuwVDO87TRwuwNCtjTB9QgUn9KFbk="
  };


  String XBOSON_WORKER = "(xboson-worker)";
  String RPC_NAME      = "XB.rpc.IOT.Runtime";
  String CONF_NAME     = "iot-manager";
  String SCRIPT_PATH_P = "./";
  String MQ_WORK_PATH  = "/paho-mq";
  String ID_TEST_P     = "^[a-zA-Z0-9]+$";

  String TABLE_DEVICE  = "device";
  String TABLE_PRODUCT = "product";
  String TABLE_SCENES  = "scenes";
  String TABLE_ADDRESS = "address";
  String TABLE_EVENT   = "event_his";
  String TABLE_SCRIPT  = "script";
  String TABLE_CMD     = "cmd_his";
  String TABLE_DATA    = "dev-data";

  String TOPIC_SAVE    = "save";
  String TOPIC_DATA    = "data";
  String TOPIC_CMD     = "cmd";
  String TOPIC_STATE   = "state";
  String TOPIC_EVENT   = "event";

  int DT_YEAR   = 1;
  int DT_MONTH  = 2;
  int DT_DAY    = 3;
  int DT_HOUR   = 4;
  int DT_MINUTE = 5;
  int DT_SECOND = 6;

  // Function on_data(payload, dev)
  String FUNCTION_DATA = "on_data";
  // Function on_cmd(cmd, dev)
  String FUNCTION_CMD  = "on_cmd";

  int EVENT_CODE_MESSAGE_FAIL = 2001;
  int EVENT_LEVEL_MESSAGE_FAIL = 3;
  int MAX_THREAD = 30;
  int CONN_TIMEOUT_SEC = 10;
  int AUTO_START_DELAY = 3000;

  int TYPE_DATA  = 1;
  int TYPE_EVENT = 2;
  int TYPE_STATE = 3;
  int TYPE_CMD   = 4;
  int TYPE_SAVE  = 5;

  int QOS_0 = 0;
  int QOS_1 = 1;
  int QOS_2 = 2;
}
