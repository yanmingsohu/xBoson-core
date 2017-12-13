////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-8 下午12:19
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/SourceFix.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix;

import com.xboson.app.fix.state.*;
import com.xboson.been.XBosonException;
import com.xboson.util.StringBufferOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 代码修正
 */
public class SourceFix {


  /**
   * 去掉脚本的前后特殊符号 "<%...%>",
   * 如果执行了修正操作返回 true
   */
  public static boolean fixBeginEnd(byte[] content) {
    int len = content.length - 1;
    if (content[0] == 60
            && content[1] == 37
            && content[len-1] == 37
            && content[len] == 62)
    {
      content[0    ] = 32; // 13 = CR (carriage return) 回车键
      content[1    ] = 32; // 32 = (space) 空格
      content[len-1] = 13;
      content[len  ] = 13;
      return true;
    }
    return false;
  }


  /**
   * 修正 beetl 中 for 循环与 js 不兼容.
   *
   * "for (row in MAP) {"
   * 变换为:
   * "for (var _index_ in MAP) { var row = MAP[_index_];"
   */
  public static byte[] fixFor(byte[] content) {
    int size = (int) (content.length * 1.7);
    StringBufferOutputStream buf = new StringBufferOutputStream(size);

    SState[] all_state = new SState[] {
            new S_for(),
            new S_Space(),
            new S_BeginBrackets(),
            new S_Space(),
            new S_KeyVar(),
            new S_Symbol("key"),
            new S_Space(),
            new S_KeyIN(),
            new S_Space(),
            new S_Expression("exp"),
            new S_EndBrackets(),
            new S_SpaceEnter(),
            new S_BeginScope(),
            new S_For_Output("key", "exp"),
    };

    JsParser.rewrite(content, buf, all_state);
    return buf.toBytes();
  }


}
