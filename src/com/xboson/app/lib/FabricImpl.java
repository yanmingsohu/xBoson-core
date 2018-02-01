////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-2-1 下午5:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/FabricImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.script.lib.Buffer;
import com.xboson.util.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class FabricImpl extends RuntimeUnitImpl {

  private static final Buffer BUF = new Buffer();


  public FabricImpl() {
    super(null);
  }


  /**
   * {
   *   name : "channel name",
   *   peer : [ 'grpcURL', ... ],
   *   orderer: [ 'grpcURL', ... ],
   *   account : {
   *     name : 'user name',
   *     mspid : '',
   *     roles : ['role', ... ],
   *     affiliation : '',
   *     privateKey : '',
   *     certificate : '',
   *   }
   * }
   */
  public Channel0 newChannel(ScriptObjectMirror conf) throws Exception {
    return new Channel0(new Mirror(conf));
  }


  public class Channel0 implements Closeable {

    private HFClient client;
    private Channel channel;


    private Channel0(Mirror conf) throws Exception {
      client = HFClient.createNewInstance();
      client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
      client.setUserContext(new User0(conf));
      channel = client.newChannel(conf.string("name"));
      addPeers(conf);
      addOrderers(conf);
      channel.initialize();
      ModuleHandleContext.autoClose(this);
    }

    private void addPeers(Mirror conf) throws Exception {
      Mirror list = conf.list("peer");
      int i = 0;
      for (String url : list.each(String.class)) {
        Peer peer = client.newPeer("peer"+ (++i), url);
        channel.addPeer(peer);
      }
    }

    private void addOrderers(Mirror conf) throws Exception {
      Mirror list = conf.list("orderer");
      int i = 0;
      for (String url : list.each(String.class)) {
        Orderer peer = client.newOrderer("orderer"+ (++i), url);
        channel.addOrderer(peer);
      }
    }

    @Override
    public void close() throws IOException {
      channel.shutdown(false);
    }

    /**
     * {
     *   chaincodeId : '',
     *   fcn : '',
     *   args : [''],
     * }
     */
    public Object queryByChaincode(ScriptObjectMirror $conf) throws Exception {
      Mirror conf   = new Mirror($conf);
      String name   = conf.string("chaincodeId");
      String fcn    = conf.string("fcn");
      Mirror jsarg  = conf.list("args");
      String[] args = new String[jsarg.size()];
      int i = -1;

      for (String arg : jsarg.each(String.class)) {
        args[++i] = arg;
      }

      QueryByChaincodeRequest req = client.newQueryProposalRequest();
      ChaincodeID cid = ChaincodeID.newBuilder().setName(name).build();
      req.setChaincodeID(cid);
      req.setFcn(fcn);
      req.setArgs(args);

      Collection<ProposalResponse> resps = channel.queryByChaincode(req);
      ScriptObjectMirror ret = createJSList(resps.size());
      i = -1;

      for (ProposalResponse resp : resps) {
        Buffer.JsBuffer payload =
                BUF.from(resp.getChaincodeActionResponsePayload());
        ScriptObjectMirror resData = createJSObject();
        ret.setSlot(++i, resData);

        resData.setMember("status", resp.getChaincodeActionResponseStatus());
        resData.setMember("message", resp.getMessage());
        resData.setMember("payload", payload);
      }
      return ret;
    }
  }


  public class User0 implements User {
    private Mirror user;

    private User0(Mirror conf) {
      user = conf.jsobj("account");
    }

    @Override
    public String getName() {
      return user.string("name");
    }

    @Override
    public Set<String> getRoles() {
      Mirror list = user.list("roles");
      Set<String> ret = new HashSet<>(list.size());
      for (String role : list.each(String.class)) {
        ret.add(role);
      }
      return ret;
    }

    @Override
    public String getAccount() {
      return IConstant.NULL_STR;
    }

    @Override
    public String getAffiliation() {
      return user.string("affiliation");
    }

    @Override
    public Enrollment getEnrollment() {
      return new Enrollment0(user);
    }

    @Override
    public String getMspId() {
      return user.string("mspid");
    }
  }


  public class Enrollment0 implements Enrollment {
    private Mirror user;

    private Enrollment0(Mirror user) {
      this.user = user;
    }

    @Override
    public PrivateKey getKey() {
      try {
        String str = formatPrivateKey(user.string("privateKey"));
        byte[] encoded = DatatypeConverter.parseBase64Binary(str);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("ECDSA");
        return kf.generatePrivate(keySpec);
      } catch (Exception e) {
        throw new XBosonException(e);
      }
    }

    @Override
    public String getCert() {
      return user.string("certificate");
    }
  }


  private String formatPrivateKey(String in) throws IOException {
    BufferedReader br = new BufferedReader(new StringReader(in));
    StringBuilder builder = new StringBuilder();
    boolean inKey = false;

    for (String line = br.readLine(); line != null; line = br.readLine()) {
      if (!inKey) {
        if (line.startsWith("-----BEGIN ")
                && line.endsWith(" PRIVATE KEY-----")) {
          inKey = true;
        }
        continue;
      } else {
        if (line.startsWith("-----END ")
                && line.endsWith(" PRIVATE KEY-----")) {
          // inKey = false;
          break;
        }
        builder.append(line);
      }
    }
    return builder.toString();
  }
}
