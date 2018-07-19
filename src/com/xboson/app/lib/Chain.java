////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-19 上午11:08
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/Chain.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.IJson;
import com.xboson.been.JsonHelper;
import com.xboson.been.XBosonException;
import com.xboson.chain.Block;
import com.xboson.chain.Btc;
import com.xboson.chain.IPeer;
import com.xboson.chain.PeerFactory;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.Hex;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Iterator;


/**
 * 区块链 api
 */
public class Chain implements IAResource {

  private static final String SQL_FILE = "open_chain";


  public Object open(String chain_id) {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    String[] names = getChainConfig(chain_id);
    if (names == null) {
      throw new XBosonException.NotFound("chain: "+ chain_id);
    }
    return new ChainImpl(names[0], names[1], names[2]);
  }


  @Override
  public String description() {
    return "app.module.chain.peer.platform()";
  }


  private String[] getChainConfig(String chain_id) {
    String sql = SqlReader.read(SQL_FILE);
    SqlImpl sqlimpl = (SqlImpl) ModuleHandleContext._get("sql");
    Object[] parm = { chain_id };

    try (QueryImpl.ResultReader rr = sqlimpl.queryStream(sql, parm)) {
      Iterator<ScriptObjectMirror> it = rr.iterator();
      if (it.hasNext()) {
        ScriptObjectMirror row = it.next();
        return new String[] {
                row.get("physical_chain").toString(),
                row.get("physical_channel").toString(),
                row.get("apiPath").toString(),
        };
      }
      return null;
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  public class ChainImpl {
    private String chain;
    private String channel;
    private String apiPath;
    private IPeer peer;


    private ChainImpl(String chain, String channel, String apiPath) {
      this.peer = PeerFactory.me().peer();
      this.chain = chain;
      this.channel = channel;
      this.apiPath = apiPath;
    }


    /**
     * 生成公钥/私钥对 对象
     */
    public KeyPairJs generateKeyPair() {
      return new KeyPairJs();
    }


    public BlockKey genesisKey() throws Exception {
      return new BlockKey(peer.genesisKey(chain, channel));
    }


    public BlockKey lastBlockKey() throws Exception {
      return new BlockKey(peer.lastBlockKey(chain, channel));
    }


    public BlockKey worldState() throws Exception {
      return new BlockKey(peer.worldState(chain, channel));
    }


    public Block search(BlockKey k) throws Exception {
      return peer.search(chain, channel, k.bin());
    }


    public Block search(String key) throws Exception {
      return search(new BlockKey(key));
    }
  }


  public class BlockKey implements IJson {
    private byte[] key;
    private String skey;


    public BlockKey(String s) {
      this.skey = s;
    }


    public BlockKey(byte[] k) {
      this.key = k;
    }


    @Override
    public String toString() {
      if (skey == null) {
        skey = Hex.encode64(key);
      }
      return skey;
    }


    public byte[] bin() {
      if (key == null) {
        key = Hex.decode64(skey);
      }
      return key;
    }


    @Override
    public String toJSON() {
      return JsonHelper.toJSON(toString());
    }
  }


  public class KeyPairJs {
    public final String publicKey;
    public final String privateKey;

    private KeyPairJs() {
      Btc b       = new Btc();
      publicKey   = b.publicKeyStr();
      privateKey  = b.privateKeyStr();
    }
  }
}
