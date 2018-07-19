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

import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import org.apache.commons.codec.binary.Hex;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * <a href='https://jankotek.gitbooks.io/mapdb/content/htreemap/'>MapDB</a>
 */
public class BlockFileSystem implements ITypes {

  private static final char META_PREFIX     = '_';
  private static final char CHANNEL_PREFIX  = '~';
  private static final String PATH          = "/chain";
  private static final String CHAIN_EXT     = ".chain";
  private static final String SYS_FILE      = "/system.db";
  private static final int INIT_SIZE        = 16 * 1024;
  private static final int INCREMENT_SIZE   = 1  * 1024*1024;
  private static BlockFileSystem instance;

  private Map<String, InnerChain> chain_cache;
  private final String rootDir;
  private final int increment;
  private final DB sysdb;
  private final HTreeMap.KeySet chainNames;


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
    Config cf   = SysConfig.me().readConfig();
    rootDir     = Tool.isNulStr(cf.chainPath)
                ? cf.configPath+PATH : cf.chainPath;
    increment   = cf.chainIncrement > 0
                ? cf.chainIncrement : INCREMENT_SIZE;
    chain_cache = new HashMap<>();

    sysdb       = makeDB(SYS_FILE);
    chainNames  = sysdb.hashSet("chains").createOrOpen();

    try {
      Files.createDirectories(Paths.get(rootDir));
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }


  /**
   * 创建/获取链, 该方法已经同步
   */
  public synchronized InnerChain getChain(String name) {
    if (Tool.isNulStr(name))
      throw new NullPointerException("name");

    InnerChain chain = chain_cache.get(name);
    if (chain == null) {
      chain = new InnerChain(name);
      chain_cache.put(name, chain);
      chainNames.add(name);
      sysdb.commit();
    }
    return chain;
  }


  public boolean chainExists(String name) {
    return chainNames.contains(name);
  }


  public Set<String> allChainNames() {
    return Collections.unmodifiableSet(chainNames);
  }


  private synchronized void closeCache(InnerChain chain) {
    chain_cache.remove(chain.name);
  }


  private DB makeDB(String fileName) {
    return DBMaker.fileDB(rootDir +'/'+ fileName)
            .allocateStartSize(INIT_SIZE)
            .allocateIncrement(increment)
            .transactionEnable()
            .make();
  }


  public class InnerChain implements AutoCloseable {
    private final String name;
    private HTreeMap genesisMap;
    private HTreeMap signerMap;
    private DB db;


    private InnerChain(String name) {
      this.db         = makeDB(name + CHAIN_EXT);
      this.name       = name;
      this.genesisMap = metaTemplate("genesis");
      this.signerMap  = metaTemplate("signer");
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
      closeCache(this);
      db.close();
      db = null;
      genesisMap = null;
      signerMap = null;
    }


    /**
     * 如果通道已经存在抛出异常, 签名器将被绑定到区块, 任何对签名器类的修改都会引起异常.
     */
    public InnerChannel createChannel(String name, ISigner si) {
      return createChannel(name, si, MetaBlock.createGenesis());
    }


    /**
     * 使用完整的创世区块创建通道.
     * [为多节点同步而设计]
     */
    InnerChannel createChannel(String name, ISigner si, Block genesis) {
      HTreeMap<byte[], Block> map = channelTemplate(name).create();
      MetaBlock gb = new MetaBlock(name);
      genesisMap.put(name, gb);
      signerMap.put(name, si);

      InnerChannel ch = new InnerChannel(map, gb, this, si);
      gb.genesisKey = ch.pushOriginal(genesis);
      genesisMap.put(name, gb);
      return ch;
    }


    /**
     * 如果通道不存在会抛出异常
     */
    public InnerChannel openChannel(String name) {
      HTreeMap<byte[], Block> map = channelTemplate(name).open();
      MetaBlock gb = (MetaBlock) genesisMap.get(name);
      ISigner signer = (ISigner) signerMap.get(name);
      return new InnerChannel(map, gb, this, signer);
    }


    public Set<String> allChannelNames() {
      return Collections.unmodifiableSet(genesisMap.keySet());
    }


    public boolean channelExists(String name) {
      return db.exists(CHANNEL_PREFIX + name);
    }


    private DB.HashMapMaker channelTemplate(String name) {
      return db.hashMap(CHANNEL_PREFIX + name)
              .keySerializer(Serializer.BYTE_ARRAY)
              .valueSerializer(SerializerBlock.me)
              .layout(16, 128, 4)
              .counterEnable();
    }


    private HTreeMap metaTemplate(String name) {
      return db.hashMap(META_PREFIX + name)
              .keySerializer(Serializer.STRING)
              .valueSerializer(Serializer.JAVA)
              .createOrOpen();
    }
  }


  public class InnerChannel {
    private Map<byte[], Block> map;
    private MetaBlock gb;
    private InnerChain chain;
    private ISigner signer;


    private InnerChannel(Map<byte[], Block> map, MetaBlock gb,
                         InnerChain ic, ISigner si) {
      this.map    = map;
      this.gb     = gb;
      this.chain  = ic;
      this.signer = si;
    }


    /**
     * 推入新块, 返回 key
     */
    public byte[] push(BlockBasic b) {
      return push(b.createBlock());
    }


    /**
     * 块必须经由该方法上链
     */
    protected byte[] push(Block b) {
      do {
        b.key = Tool.uuid.getBytes(Tool.uuid.v4obj());
      } while(map.containsKey(b.key));

      b.create       = new Date();
      b.previousHash = gb.worldStateHash;
      b.previousKey  = gb.lastBlockKey;

      signer.sign(b);
      b.computeHash();
      return pushOriginal(b);
    }


    /**
     * 不执行验证/生成步骤, 直接将区块上链.
     * [为多节点同步而设计]
     */
    byte[] pushOriginal(Block b) {
      if (!Arrays.equals(b.previousHash, gb.worldStateHash))
        throw new VerifyException("bad previous hash", b.previousHash);

      if (!Arrays.equals(b.previousKey, gb.lastBlockKey))
        throw new VerifyException("bad previous key", b.previousKey);

      if (map.containsKey(b.key))
        throw new VerifyException("key conflict", b.key);

      map.put(b.key, b);

      gb.worldStateHash = b.hash;
      gb.lastBlockKey   = b.key;
      chain.genesisMap.put(gb.channelName, gb);
      return b.key;
    }


    public Block search(byte[] key) {
      Block b = map.get(key);
      if (b != null && (! signer.verify(b)) ) {
        throw new VerifyException("Key-Hex: "+ Hex.encodeHexString(key));
      }
      return b;
    }


    public byte[] worldState() {
      return Arrays.copyOf(gb.worldStateHash, gb.worldStateHash.length);
    }


    public byte[] lastBlockKey() {
      return Arrays.copyOf(gb.lastBlockKey, gb.lastBlockKey.length);
    }


    public byte[] genesisKey() {
      return Arrays.copyOf(gb.genesisKey, gb.genesisKey.length);
    }


    public int size() {
      return map.size();
    }
  }
}
