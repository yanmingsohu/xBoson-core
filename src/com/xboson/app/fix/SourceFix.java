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
// 文件创建日期: 17-12-8 下午12:19
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/SourceFix.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix;

import com.xboson.app.fix.state.*;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;


/**
 * 代码修正.
 */
public class SourceFix {

  public static final char[] STRICT_MODE = "\"use strict\"".toCharArray();


  /**
   * 自动给代码打补丁, 该方法返回的代码适合给机器运行.
   * 返回后参数 content 会被改动但不是最终代码, 应该丢弃.
   *
   * 补丁规则:
   *    1. 去掉首尾 "<%...%>".
   *    2. 是严格模式则不做代码补丁.
   *    3. 不是严格模式, 修正脚本中的方言.
   *    4. 多行字符串转义.
   */
  public static byte[] autoPatch(byte[] content) {
    SourceFix.fixBeginEnd(content);
    if (! SourceFix.isStrictMode(content)) {
      content = SourceFix.fixFor(content);
      content = SourceFix.fixJavaCall(content);
      content = SourceFix.fixVirtualAttr(content);
    }
    content = multiLineString(content);
    return content;
  }


  /**
   * 去掉脚本的前后特殊符号 "<%...%>",
   * 如果修改了代码返回 true;
   *
   * 该方法仅适合替换运行时的代码, 不加入换行另出错后的行数正确,
   * 但是输出到 ide 后多出的空格, 让开发者以为代码被改过.
   */
  public static boolean fixBeginEnd(byte[] content) {
    int len = content.length - 1;
    if (isDrag(content)) {
      content[0    ] = 32; // 13 = CR (carriage return) 回车键
      content[1    ] = 32; // 32 = (space) 空格
      content[len-1] = 13; // 10 = LF 换行
      content[len  ] = 10;
      return true;
    }
    return false;
  }


  /**
   * 带有前后 "<%...%>" 符号返回 true
   */
  public static boolean isDrag(byte[] content) {
    if (content == null || content.length < 4)
      return false;

    int len = content.length - 1;
    return (content[0] == 60
            && content[1] == 37
            && content[len-1] == 37
            && content[len] == 62);
  }


  /**
   * 如果代码使用了严格模式, 则返回 true,
   * 严格模式必须在第一行声明字符串 "use strict"
   */
  public static boolean isStrictMode(byte[] content) {
    int i = 0, g = 0;
    while (i < content.length && content[i] != '\n') {
      if (content[i] == STRICT_MODE[g]) {
        if (++g >= STRICT_MODE.length)
          return true;
      } else {
        g = 0;
      }
      ++i;
    }
    return false;
  }


  /**
   * 修正 beetl 中 for 循环与 js 不兼容.
   * @see com.xboson.app.fix.state.S_For_Output
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
            new S_Space(),
            new S_Symbol(0),
            new S_Space(),
            new S_KeyIN(),
            new S_Space(),
            new S_Expression(1),
            new S_EndBrackets(),
            new S_SpaceEnter(),
            new S_BeginScope(),
            new S_For_Output(0, 1),
    };

    JsParser.rewrite(content, buf, all_state, 2);
    return buf.toBytes();
  }


  /**
   * 修正 beetl 中对象的 java 函数调用语法,
   * 语法: @object.func(args0, args1)
   * 重写为: __inner_call("func-name:func", object, args0, args1)
   */
  public static byte[] fixJavaCall(byte[] content) {
    int size = (int) (content.length * 1.7);
    StringBufferOutputStream buf = new StringBufferOutputStream(size);

    SState[] all_state = new SState[] {
            new S_BeginNotation('@'),
            new S_Symbol(0),
            new S_Notation('.'),
            new S_Symbol(1),
            new S_BeginBrackets(),
            new S_DynArgument(2),
            new S_EndBrackets(),
            new S_JavaCallOutput(0, 1, 2),
    };

    JsParser.rewrite(content, buf, all_state, 3);
    return buf.toBytes();
  }


  /**
   * 修正 beetl 中虚拟属性
   * 语法: object.~size
   * 重写为: __virtual_attr(object, "attr_name:size")
   */
  public static byte[] fixVirtualAttr(byte[] content) {
    int size = (int) (content.length * 1.7);
    StringBufferOutputStream buf = new StringBufferOutputStream(size);

    SState[] all_state = new SState[] {
            new S_BeginSymbol(0),
            new S_Notation('.'),
            new S_Notation('~'),
            new S_Symbol(1),
            new S_VirtualAttr(0, 1),
    };

    JsParser.rewrite(content, buf, all_state, 2);
    return buf.toBytes();
  }


  /**
   * 支持 ES6 多行字符串简化语法, '`' 符号作为开始, '`' 作为结束,
   * 不支持动态变量 ${varName}.
   */
  public static byte[] multiLineString(byte[] content) {
    int size = (int) (content.length * 1.7);
    StringBufferOutputStream buf = new StringBufferOutputStream(size);

    S_BeginMultiLineString multiLine = new S_BeginMultiLineString();

    SState[] all_state = new SState[] {
            multiLine.createBegin(),
            multiLine,
    };

    JsParser.rewrite(content, buf, all_state, 2);
    return buf.toBytes();
  }
}
