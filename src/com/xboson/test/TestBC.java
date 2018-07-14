////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-13 下午7:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestBC.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.JsonHelper;
import com.xboson.chain.Block;
import com.xboson.chain.BlockBasic;
import com.xboson.chain.BlockFileSystem;
import com.xboson.chain.Btc;
import com.xboson.util.Tool;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;


public class TestBC extends Test {

  public static void main(String[] av) {
    new TestBC();
  }


  public void test() throws Exception {
    bfs();
    btcWall();
  }


  public void btcWall() throws Exception {
    sub("BTC wallet");

    Btc btc = new Btc();

    byte[] publicKey  = btc.publicKey();
    byte[] privateKey = btc.privateKey();

    msg("public key", Hex.encodeHexString(publicKey), publicKey.length);
    msg("private key", Hex.encodeHexString(privateKey), privateKey.length);
    msg("bitcoinAddress", btc.wallet());
  }


  public void bfs() {
    sub("block file system");

    BlockFileSystem bc = BlockFileSystem.me();
    BlockFileSystem.InnerChain chain = bc.createChain("test");

    try {
      chain.createChannel("ch0");
    } catch (Exception e) {
      msg(e.getMessage());
    }
    BlockFileSystem.InnerChannel ch = chain.openChannel("ch0");
    byte[] oldworld = ch.worldState();
    Block pre = ch.search(ch.lastBlockKey());
    BlockBasic bb = new BlockBasic(Tool.randomBytes(10),
            "user", "/api", "0");
    byte[] key = ch.push(bb);
    msg("push", Arrays.toString(key), ch.size());
    chain.commit();

    Block b = ch.search(key);
    ok(Arrays.equals(bb.getData(), b.getData()), "data field");
    byte[] world = ch.lastBlockKey();
    ok(Arrays.equals(b.key, world), "world 1");
    ok(Arrays.equals(b.previousHash, oldworld), "world 2");
    ok(Arrays.equals(b.previousKey, pre.key), "previous key");
    ok(Arrays.equals(b.previousHash, pre.hash), "previous hash");

    msg("search", JsonHelper.toJSON(b));
    msg("world", JsonHelper.toJSON(ch.worldState()));
    msg("last-block", JsonHelper.toJSON(world));

    chain.close();
  }
}
