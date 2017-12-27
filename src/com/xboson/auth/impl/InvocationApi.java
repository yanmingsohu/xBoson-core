////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-26 下午1:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/impl/InvocationApi.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth.impl;

import com.xboson.app.AppContext;
import com.xboson.app.lib.IApiConstant;
import com.xboson.auth.IAResource;
import com.xboson.auth.IAWhere;
import com.xboson.auth.IAWho;
import com.xboson.auth.PermissionException;
import com.xboson.been.LoginUser;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.JavaConverter;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


public class InvocationApi implements IAWhere {

  public static final String H_KEY
          = IApiConstant._R_KEY_PREFIX_
          + IApiConstant._CACHE_REGION_SYS_AUTHORITY_;

  public static final String APP_NORMAL = "application_normal:";
  public static final String APP_PUB    = "application_pub:";

  /** 将用户与权限直接映射, 加快速度 */
  private Map<String, Boolean> user_api_cache;
  /** 不做权限检查的接口集合, 内容为资源描述字符串 */
  private Set<String> skip_check;
  private Log log;


  public InvocationApi() {
    this.log = LogFactory.create();
    this.user_api_cache = new WeakHashMap<>();
    this.skip_check = createSkip();
  }


  private Set<String> createSkip() {
    return JavaConverter.arr2set(new String[]{
            "zyapp_loginzymodule_logingetuserorg",
    });
  }


  @Override
  public boolean apply(IAWho who, IAResource res) {
    if (skip_check.contains(res.description())) {
      return true;
    }

    String res_desc = ":" + res.description();
    LoginUser user  = (LoginUser) who;
    String org      = AppContext.me().originalOrg() + ':';
    String hasAuth  = null;
    String q_key    = user.userid +'/'+ user.loginTime;

    Boolean has = user_api_cache.get(q_key);
    if (has != null) {
      if (!has) throw new ApiPermission(user, res);
      return true;
    }

    try (Jedis client = RedisMesmerizer.me().open()) {
      for (String roleid : user.roles) {
        //
        // 缓存系统角色 API 信息
        // orgid : roleid : appid + moduleid + apiid + sysid
        //
        hasAuth = client.hget(H_KEY, org + roleid + res_desc);
        if (hasAuth != null) break;

        //
        // 缓存已发布应用（普通）角色 API 信息
        // "application_normal" : roleid : appid + moduleid + apiid
        //
        hasAuth = client.hget(H_KEY, APP_NORMAL + roleid + res_desc);
        if (hasAuth != null) break;

        //
        // 缓存已发布应用（公共）角色 API 信息，机构ID作为角色ID，
        // 同机构所有公共应用使用同一角色ID即机构ID
        // "application_pub" : orgid : appid + moduleid + apiid
        //
        hasAuth = client.hget(H_KEY, APP_PUB + roleid + res_desc);
        if (hasAuth != null) break;
      }
    }

    //
    // 不要利用 has 变量, 拆箱/装箱不如直接比较.
    //
    user_api_cache.put(q_key, hasAuth != null);
    if (hasAuth == null) throw new ApiPermission(user, res);
    return true;
  }


  private class ApiPermission extends PermissionException {
    public ApiPermission(LoginUser user, IAResource res) {
      super(user, InvocationApi.this, res, 1101);

      log.debug("No api auth", user.userid,
              "Roles:", user.roles, "Api Description:", res.description());
    }
  }
}