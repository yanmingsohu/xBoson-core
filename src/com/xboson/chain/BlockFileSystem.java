////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-13 下午12:47
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/BlockFileSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BlockFileSystem implements ITypes {

  private static final String PATH = "/chain";
  private static final long START_SIZE = 1 * 1024*1024;
  private static final long INCREMENT_SIZE = 1 * 1024*1024;
  private static BlockFileSystem instance;

  private Map<String, InnerChain> chain_cache;
  private final String rootDir;
  private final Log log;


  public static BlockFileSystem me() {
    if (instance == null) {
      synchronized (BlockFileSystem.class) {
        if (instance == null) {
          instance = new BlockFileSystem();
        }
      }
    }
    return instance;
  }


  private BlockFileSystem() {
    rootDir = SysConfig.me().readConfig().configPath + PATH;
    log     = LogFactory.create("chain-block-fs");
    chain_cache = new HashMap<>();
    try {
      Files.createDirectories(Paths.get(rootDir));
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }


  public synchronized InnerChain createChain(String name) {
    if (Tool.isNulStr(name))
      throw new NullPointerException("name");

    InnerChain chain = chain_cache.get(name);
    if (chain == null) {
      chain = new InnerChain(name);
      chain_cache.put(name, chain);
    }
    return chain;
  }


  public class InnerChain implements AutoCloseable {
    private final String name;
    private HTreeMap genesisMap;
    private DB db;

    private InnerChain(String name) {
      this.db = DBMaker.fileDB(rootDir +'/'+ name)
              .allocateStartSize(START_SIZE)
              .allocateIncrement(INCREMENT_SIZE)
              .transactionEnable()
              .make();

      this.name = name;
      this.genesisMap = metaTemplate("genesis");
    }


    /**
     * 递交所有操作到文件
     */
    public void commit() {
      db.commit();
    }


    /**
     * 回滚操作
     */
    public void rollback() {
      db.rollback();
    }


    /**
     * 关闭底层文件系统, 未递交的操作被丢弃
     */
    public void close() {
      chain_cache.remove(name);
      db.close();
      db = null;
      genesisMap = null;
    }


    /**
     * 如果通道已经存在抛出异常
     */
    public InnerChannel createChannel(String name) {
      HTreeMap<byte[], Block> map = channelTemplate(name).create();
      MetaBlock gb = new MetaBlock(name);
      genesisMap.put(name, gb);
      InnerChannel ch = new InnerChannel(map, gb, this);
      ch.push(gb.createGenesis());
      return ch;
    }


    /**
     * 如果通道不存在会抛出异常
     */
    public InnerChannel openChannel(String name) {
      HTreeMap<byte[], Block> map = channelTemplate(name).open();
      MetaBlock gb = (MetaBlock) genesisMap.get(name);
      return new InnerChannel(map, gb, this);
    }


    private DB.HashMapMaker channelTemplate(String name) {
      if (name.charAt(0) == '_')
        throw new XBosonException.BadParameter(
                "channel name", "cannot start with '_'");

      return db.hashMap(name)
              .keySerializer(Serializer.BYTE_ARRAY)
              .valueSerializer(SerializerBlock.me)
              .counterEnable();
    }


    private HTreeMap metaTemplate(String name) {
      return db.hashMap("_"+ name)
              .keySerializer(Serializer.STRING)
              .valueSerializer(Serializer.JAVA)
              .createOrOpen();
    }
  }


  public class InnerChannel {
    private Map<byte[], Block> map;
    private MetaBlock gb;
    private InnerChain chain;

    private InnerChannel(Map<byte[], Block> map, MetaBlock gb, InnerChain ic) {
      this.map    = map;
      this.gb     = gb;
      this.chain  = ic;
    }

    /**
     * 推入新块, 返回 key
     */
    public byte[] push(BlockBasic b) {
      return push(b.createBlock());
    }

    protected byte[] push(Block b) {
      do {
        b.key = Tool.uuid.getBytes(Tool.uuid.v4obj());
      } while(map.containsKey(b.key));

      b.create       = new Date();
      b.previousHash = gb.worldStateHash;
      b.previousKey  = gb.lastBlockKey;

      b.computeHash();
      map.put(b.key, b);

      gb.worldStateHash = b.hash;
      gb.lastBlockKey   = b.key;
      chain.genesisMap.put(gb.channelName, gb);
      return b.key;
    }


    public Block search(byte[] key) {
      return map.get(key);
    }


    public byte[] worldState() {
      return Arrays.copyOf(gb.worldStateHash, gb.worldStateHash.length);
    }


    public byte[] lastBlockKey() {
      return Arrays.copyOf(gb.lastBlockKey, gb.lastBlockKey.length);
    }


    public int size() {
      return map.size();
    }
  }
}
