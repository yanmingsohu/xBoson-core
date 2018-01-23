////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-11 下午12:25
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ErrorCodeMessage.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import java.util.HashMap;
import java.util.Map;


/**
 * 系统消息整数值与消息内容映射
 */
public class ErrorCodeMessage {

  public final static String UNKNOW_MSG = "错误代码:";


  private final static Map<Integer, String> mapping
          = new HashMap<Integer, String>() {{
    put(0, "成功");
    put(1, "缺少必要请求参数");
    put(2, "请求参数错误");
    put(3, "协议不合法");
    put(4, "接口不存在");
    put(5, "接口执行失败");
    put(6, "数据已被其他用户添加或修改");
    put(7, "文件操作失败");
    put(8, "数据已存在");
    put(9, "API 内可能出现死循环");
    put(10, "验证码错误");
    put(11, "数据不存在");

    put(900, "本服务未经产品授权，请联系系统管理员");
    put(997, "服务节点拒绝访问");
    put(998, "服务器繁忙");
    put(999, "系统异常，后台服务处理异常");

    put(1000, "用户没有登录状态");
    put(1001, "用户登陆失败");
    put(1002, "用户已登录");
    put(1003, "该用户未注册或不存在");
    put(1004, "该用户已经停用");
    put(1005, "用户已在其他设备登录");
    put(1006, "登录超时");
    put(1007, "用户已注销");
    put(1008, "用户多设备同时在线");
    put(1009, "用户登录异常");
    put(1010, "用户ID未绑定到平台账号");
    put(1011, "用户名已存在");
    put(1012, "OpenID 不合法");
    put(1013, "用户不匹配，无法操作此模块");
    put(1014, "用户不存在");

    put(1100, "非法操作，通常是进行了不被授权的操作");
    put(1101, "无API访问权限");
    put(1102, "IP没有权限，应用需排查是否对错误提示中的IP进行了授权");
    put(1110, "无页面访问权限");
    put(1111, "页面不存在");

    put(1201, "该应用注册状态正常");
    put(1202, "该应用未注册或不存在");
    put(1203, "该应用已经停用");
    put(1301, "该系统注册状态正常");
    put(1302, "该系统未注册或不存在");
    put(1303, "该系统已经停用");
  }};


  /**
   * 通过错误码返回错误消息, 永远不返回 null
   */
  public static String get(int code) {
    String msg = mapping.get(code);
    return msg == null ? UNKNOW_MSG+code : msg;
  }


  /**
   * 如果错误码未定义返回 null
   */
  public static String getNul(int code) {
    return mapping.get(code);
  }
}
