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
// 文件创建日期: 18-5-9 下午7:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestFabircShim.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;
//
//import io.grpc.ManagedChannel;
//import org.apache.log4j.BasicConfigurator;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.hyperledger.fabric.shim.ChaincodeBase;
//import org.hyperledger.fabric.shim.ChaincodeStub;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.security.KeyFactory;
//
//
//public class TestFabircShim {
//
//  public static void main(String[] args) throws Throwable {
//    BasicConfigurator.configure();
//    // 出错说明缺少 ECDSA jar 包, 或 ECDSA 没有注册到服务
//    KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
//    Test.msg(kf.getAlgorithm());
//
//    Code c = new Code();
//    c.start("cc0", "10.0.0.104:7052");
//  }
//
//
//  public static abstract class ChaincodeNoThread extends ChaincodeBase {
//
//    /**
//     * 该方法不会再线程中启动 chaincode
//     *
//     * @param id chaincode id
//     * @param addr "10.0.0.104:7052" 格式的地址
//     */
//    public void start(String id, String addr) throws
//            NoSuchMethodException,
//            InvocationTargetException,
//            IllegalAccessException {
//
//      Code c = new Code();
//      Method processCommandLineOptions =
//              ChaincodeBase.class.getDeclaredMethod(
//                      "processCommandLineOptions", String[].class);
//
//      processCommandLineOptions.setAccessible(true);
//      Object arg = new String[] { "-i", id, "-a", addr };
//      processCommandLineOptions.invoke(c, arg);
//
//      ManagedChannel connection = c.newPeerClientConnection();
//      c.chatWithPeer(connection);
//    }
//  }
//
//
//  public static class Code extends ChaincodeNoThread {
//
//    @Override
//    public Response init(ChaincodeStub stub) {
//      Test.msg("CC init");
//      return newSuccessResponse();
//    }
//
//
//    @Override
//    public Response invoke(ChaincodeStub stub) {
//      Test.msg("CC invoke");
//      return newSuccessResponse();
//    }
//  }
//}
