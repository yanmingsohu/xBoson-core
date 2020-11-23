////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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

  // Function on_data(payload, dev)
  String FUNCTION_DATA = "on_data";
  // Function on_cmd(cmd, dev)
  String FUNCTION_CMD  = "on_cmd";

  int EVENT_CODE_MESSAGE_FAIL = 2001;
  int EVENT_LEVEL_MESSAGE_FAIL = 3;
  int MAX_THREAD = 30;
  int CONN_TIMEOUT_SEC = 10;

  int TYPE_DATA  = 1;
  int TYPE_EVENT = 2;
  int TYPE_STATE = 3;
  int TYPE_CMD   = 4;
  int TYPE_SAVE  = 5;

  int QOS_0 = 0;
  int QOS_1 = 1;
  int QOS_2 = 2;
}
