////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-10-20 下午1:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/CountImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.PermissionException;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.AES2;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 统计工具
 * TODO: 未完成
 */
public class CountImpl {

  public final static int DB_NUM = 1;

  private Map<String, String> passCache;
  private CTYPE[] allType = new CTYPE[10];

  /** 总计 */
  public final CTYPE TOTAL = new CTYPE(0, "-");
  /** 全年统计 */
  public final CTYPE YEAR  = new CTYPE(1, "YYYY");
  /** 月统计 */
  public final CTYPE MONTH = new CTYPE(2, "YYYY-MM");
  /** 每日统计 */
  public final CTYPE DAY   = new CTYPE(3, "YYYY-MM-dd");
  /** 每天小时统计 */
  public final CTYPE HOUR  = new CTYPE(4, "YYYY-MM-dd HH");

  /** 固定月总计 */
  public final CTYPE FIX_MONTH    = new CTYPE(5, "MM");
  /** 固定日总计(月) */
  public final CTYPE FIX_DAY_MON  = new CTYPE(6, "dd");
  /** 固定日总计(年) */
  public final CTYPE FIX_DAY_YEAR = new CTYPE(7, "DDD");
  /** 固定小时总计 */
  public final CTYPE FIX_HOUR     = new CTYPE(8, "HH");
  /** 固定周总计(年) */
  public final CTYPE FIX_WEEK     = new CTYPE(9, "ww");


  public CountImpl() {
    passCache = Collections.synchronizedMap(new WeakHashMap<>());
  }


  /**
   * 创建实例, 返回访问数据的密钥
   */
  public String create(String key) throws Exception {
    String pass = Tool.randomString(15);
    String val  = Tool.randomString(100);
    String enc  = new AES2(pass).encrypt(val);
    transaction((j) -> {
      j.set("C."+ key +".ENC", enc);
      j.set("C."+ key +".VAL", val);
    });
    return pass;
  }


  /**
   * 在当前时间点上增加一次实例的访问计数
   */
  public void inc(String key) throws Exception {
    inc(key, new Date());
  }


  public void inc(String key, Date d) throws Exception {
    transaction((j) -> {
      for (CTYPE c : allType) {
        j.incr("C."+ key +".D."+ c.num +"."+ c.f.format(d));
      }
    });
  }


  /**
   * 使用访问密钥打开查询
   * @param key 统计对象主键
   * @param pass 访问密钥
   * @return
   */
  public Search openSearch(String key, String pass) throws Exception {
    return new Search(key, pass);
  }


  private List<Object> transaction(IDO d) throws Exception {
    try (Jedis j = RedisMesmerizer.me().open();
         Transaction t = j.multi()) {
      int dbn = j.getDB().intValue();
      t.select(DB_NUM);
      d.o(t);
      t.select(dbn);
      return t.exec();
    }
  }


  private Object openRedis(IDO2 d) throws Exception {
    int dbn = 0;
    Jedis j = null;
    try {
      j = RedisMesmerizer.me().open();
      dbn = j.getDB().intValue();
      j.select(DB_NUM);
      return d.o(j);
    } finally {
      if (j != null) {
        try {
          j.select(dbn);
        } finally {
          j.close();
        }
      }
    }
  }


  interface IDO {
    void o(Transaction t) throws Exception;
  }


  interface IDO2 {
    Object o(Jedis j) throws Exception;
  }


  public class CTYPE {
    private int num;
    private String p;
    private SimpleDateFormat f;

    private CTYPE(int n, String pattern) {
      num = n;
      f = new SimpleDateFormat(pattern);
      p = pattern;
      allType[num] = this;
    }

    private synchronized String key(String key, Date d) {
      return "C."+ key +".D."+ num +"."+ f.format(d);
    }
  }


  public class Search {
    private String key;


    private Search(String key, String pass) throws Exception {
      checkPass(key, pass);
      this.key = key;
    }


    private void checkPass(String key, String pass) throws Exception {
      String p = passCache.get(key);

      if (p == null) {
        openRedis((j) -> {
          String enc = j.get("C."+ key + ".ENC");
          String val = j.get("C."+ key + ".VAL");
          String dec = new String(new AES2(pass).decrypt(enc), IConstant.CHARSET);

          if (!val.equals(dec)) {
            throw new PermissionException("bad password");
          }
          passCache.put(key, pass);
          return null;
        });
      } else if (!p.equals(pass)) {
        throw new PermissionException("bad password");
      }
    }


    /**
     * 返回 type 类型的计数器的值, 精确匹配
     */
    public Object get(CTYPE type, Date d) throws Exception {
      return openRedis((j) -> {
        return j.get( type.key(key, d) );
      });
    }


    /**
     * 返回 type 类型的计数器的值, 范围匹配
     */
    public Object range(CTYPE type, Date d) throws Exception {
      String searchKey;
      switch (type.num) {
        case 0:
        case 1:
          searchKey = "C."+ key +".D.1.*";
          break;

        case 2:
        case 3:
        case 4:
          searchKey = "C."+ key +".D."+ type.num +"."
                    + allType[type.num-1].f.format(d) +"*";
          break;

        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
          searchKey = "C."+ key +".D."+ type.num +".*";
          break;

        default:
          throw new Exception("Unknow type");
      }

      final String sk = searchKey;
      return openRedis((j) -> {
        Set<String> keys = j.keys(sk);
        Map<String, String> ret = new HashMap<>(keys.size());
        int split = key.length() + 7;
        if (type.num > 9) ++split;

        for (String n : keys) {
          ret.put(n.substring(split), j.get(n));
        }
        return ret;
      });
    }
  }
}
