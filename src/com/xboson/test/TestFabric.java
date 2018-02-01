////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-2-1 下午12:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestFabric.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;


public class TestFabric extends Test {

  private HFClient client;

  /**
   * https://stackoverflow.com/questions/44585909/hyperledger-java-sdk-working-example
   * https://github.com/hyperledger/fabric-sdk-java
   * https://github.com/hyperledger/fabric-samples
   */
  public void test() throws Throwable {
    try {
      connect();
    } catch(Exception e) {
      warn("Cannot found Fabric server", e);
    }
  }


  private void connect() throws Exception {
    sub("Connect to server");
    CryptoSuite cs = CryptoSuite.Factory.getCryptoSuite();
    client = HFClient.createNewInstance();
    client.setCryptoSuite(cs);
    client.setUserContext(new FabricUser());

    Peer p = client.newPeer(
            "peer", "grpc://10.0.0.7:7051");
    Channel channel = client.newChannel("mychannel");
    Orderer ord = client.newOrderer(
            "OA", "grpc://10.0.0.7:7050");
    channel.addOrderer(ord);
    channel.addPeer(p);
    channel.initialize();
    msg("Fabric ok", p, channel);

    queryFabcar(channel, "CAR1");
  }


  private void queryFabcar(Channel channel, String key) throws Exception {
    sub("Querying for", key);

    QueryByChaincodeRequest req = client.newQueryProposalRequest();
    ChaincodeID cid = ChaincodeID.newBuilder().setName("fabcar").build();
    req.setChaincodeID(cid);
    req.setFcn("queryCar");
    req.setArgs(new String[] { key });
    Collection<ProposalResponse> resps = channel.queryByChaincode(req);

    for (ProposalResponse resp : resps) {
      String payload = new String(resp.getChaincodeActionResponsePayload());
      msg("response: " + payload);
    }
  }



  public static void main(String[] a) throws Throwable {
    new TestFabric();
  }


  public static class FabricUser implements User, Enrollment {

    String BASE = "D:\\javaee-project\\[opensource-lib-other]\\fabric-sdk\\";
    private PrivateKey pkey;
    private String cert;

    public FabricUser() throws IOException, GeneralSecurityException {
      pkey = loadPrivateKey(Paths.get(BASE + "key.pem"));
      cert = new String(Files.readAllBytes(Paths.get(BASE + "cert.pem")));
    }

    @Override
    public String getName() {
      return "user1";
    }


    @Override
    public Set<String> getRoles() {
      return Collections.emptySet();
    }


    @Override
    public String getAccount() {
      return null;
    }


    @Override
    public String getAffiliation() {
      return null;
    }


    @Override
    public Enrollment getEnrollment() {
      return this;
    }


    @Override
    public String getMspId() {
      return "Org1MSP";
    }


    @Override
    public PrivateKey getKey() {
      return pkey;
    }


    @Override
    public String getCert() {
      return cert;
    }
  }


  /***
   * loading private key from .pem-formatted file, ECDSA algorithm
   * (from some example on StackOverflow, slightly changed)
   * @param fileName - file with the key
   * @return Private Key usable
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static PrivateKey loadPrivateKey(Path fileName)
          throws IOException, GeneralSecurityException {
    PrivateKey key = null;
    InputStream is = null;
    try {
      is = new FileInputStream(fileName.toString());
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      StringBuilder builder = new StringBuilder();
      boolean inKey = false;
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (!inKey) {
          if (line.startsWith("-----BEGIN ") && line.endsWith(" PRIVATE KEY-----")) {
            inKey = true;
          }
          continue;
        } else {
          if (line.startsWith("-----END ") && line.endsWith(" PRIVATE KEY-----")) {
            inKey = false;
            break;
          }
          builder.append(line);
        }
      }
      //
      byte[] encoded = DatatypeConverter.parseBase64Binary(builder.toString());
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
      KeyFactory kf = KeyFactory.getInstance("ECDSA");
      key = kf.generatePrivate(keySpec);
    } finally {
      is.close();
    }
    return key;
  }

}
