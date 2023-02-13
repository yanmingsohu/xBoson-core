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
// 文件创建日期: 19-1-21 下午4:49
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestUPNP.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.log.LogFactory;
import com.xboson.log.writer.ConsoleOut;
import com.xboson.util.Tool;
import com.xboson.util.UPnP;


public class TestUPNP {

  public static void main(String[] av) {
    LogFactory.me().setWriter(new ConsoleOut());
    UPnP p = UPnP.me();
    p.rmTcpPortMapping(9091);
    p.setTcpPortMapping(8082, 9090, UPnP.LOCAL_HOST);
    Tool.pl("ok");
  }

}
