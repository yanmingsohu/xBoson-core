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
// 文件创建日期: 18-1-30 上午11:48
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/Network.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * 网络相关辅助工具
 */
public final class Network {

  private Network() {}


  /**
   * 获取本机 ip 地址, 可以通过该地址在另一台主机上访问.
   */
  public static List<InetAddress> netWorkerInterfaces() {
    try {
      List<InetAddress> list = new ArrayList<>();
      see(list, NetworkInterface.getNetworkInterfaces());
      return list;
    } catch (SocketException e) {
      throw new XBosonException(e);
    }
  }


  public static String[] toAddressString(List<InetAddress> list) {
    String[] names = new String[list.size()];
    int i = 0;
    for (InetAddress aa : list) {
      names[i++] = aa.getHostAddress();
    }
    return names;
  }


  public static InetAddress parseIpv4(String ip) {
    try {
      if (Tool.isNulStr(ip)) return null;
      byte[] addr = new byte[4];
      String [] s = ip.split("\\.");
      for (int i = 0; i < 4; ++i) {
        addr[i] = Byte.parseByte(s[i]);
      }
      return InetAddress.getByAddress(addr);
    } catch (Exception e) {
      Tool.pl("Parse IP fail", e);
    }
    return null;
  }


  /**
   * 返回的列表中包含符合 mask 规则的地址
   * mask: 接受 '*' 匹配
   */
  public static String[] filter(String mask, String[] ip) {
    int[] pass = new int[ip.length];
    int count = 0;
    for (int i=0; i<ip.length; ++i) {
      if (filter(mask, ip[i])) {
        pass[i] = i;
        count++;
      } else {
        pass[i] = -1;
      }
    }
    String[] ret = new String[count];
    for (int i=0; i<pass.length; ++i) {
      if (pass[i] >= 0) {
        ret[--count] = ip[ pass[i] ];
      }
    }
    return ret;
  }


  /**
   * 匹配返回 true
   */
  public static boolean filter(String mask, String ip) {
    int pi = 0, i = 0;
    while (i<mask.length()) {
      char c = mask.charAt(i);
      switch (c) {
        case '*':
          // 处理连续 '*' 号的情况
          while (c == '*' && ++i < mask.length()) {
            c = mask.charAt(i);
          }
          // mask 最末是 '*' 则认为之后全部匹配
          if (i >= mask.length()) {
            return true;
          }
          ++pi;
          while (pi < ip.length() && ip.charAt(pi) != c) {
            ++pi;
          }
          break;

        default:
          if (pi >= ip.length()) return false;
          if (ip.charAt(pi) != c) {
            return false;
          }
          ++i;
          ++pi;
          break;
      }
    }
    return true;
  }


  private static void see(List<InetAddress> list,
                          Enumeration<NetworkInterface> e)
          throws SocketException
  {
    while (e.hasMoreElements()) {
      NetworkInterface nif = e.nextElement();
      if (nif.isVirtual() || nif.isLoopback())
        continue;

      for (InterfaceAddress taddr : nif.getInterfaceAddresses()) {
        InetAddress addr = taddr.getAddress();

        if (addr.isSiteLocalAddress()) {
          list.add(addr);
        }
      }

      Enumeration<NetworkInterface> sub = nif.getSubInterfaces();
      if (sub != null) {
        see(list, sub);
      }
    }
  }
}
