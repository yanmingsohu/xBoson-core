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

package com.xboson.app;

import com.xboson.been.XBosonException;
import com.xboson.util.IConstant;
import com.xboson.util.StringBufferOutputStream;

import java.io.IOException;
import java.io.OutputStream;
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
    int state = 0;
    int resetIndex = 0;
    int stateIndex = 0;

    SState[] all_state = new SState[] {
            new S_for(),
            new S_Space(),
            new S_BeginBrackets(),
            new S_Space(),
            new S_KeyVar(),
            new S_Symbol("keyName"),
            new S_Space(),
            new S_KeyIN(),
            new S_Space(),
//            new S_Symbol("objectName"),
            new S_Expression(),
            new S_EndBrackets(),
            new S_SpaceEnter(),
            new S_BeginScope(),
            new S_Output(),
    };

    Map<String, String> data = new HashMap<>();
    ISState currentState = all_state[0];

    for (int i=0; i<all_state.length; ++i) {
      all_state[i].setOutput(buf);
      all_state[i].setData(data);
    }

    try {
      for (int i = 0; i < content.length; ++i) {
        byte ch = content[i];
        int directive = currentState.read(ch);

        if (directive == SState.BEGIN) {
          stateIndex = i;
          resetIndex = i;
          continue;
        }
        else if (directive == SState.KEEP) {
          continue;
        }
        else if (directive >= SState.NEXT) {
          if (directive == SState.NEXT_AND_BACK_ALL) {
            i = stateIndex - 1;
          }
          else if (directive == SState.NEXT_AND_BACK) {
            --i;
          } else /* if is NEXT */ {
            stateIndex = i+1;
          }

          if (++state >= all_state.length) {
            state = 0;
          }
          currentState = all_state[state];
          continue;
        }
        else if (directive == SState.RESET) {
          while (resetIndex < i) {
            buf.write(content[resetIndex]);
            ++resetIndex;
          }
          state = 0;
          currentState = all_state[state];
        }

        buf.write(ch);
      }
    } catch(IOException e) {
      throw new XBosonException(e);
    }

    return buf.toBytes();
  }


  /**
   * 前向状态机接口
   */
  interface ISState {
    /** 返回初始状态, 并把之前的字符序列写入输出. */
    int RESET = 0;
    /** 进入状态机, 一旦发生 RESET 将重制到 BEGIN 发生的点. */
    int BEGIN = 1;
    /** 保持当前状态机 */
    int KEEP  = 2;
    /** 没有特别的动作 */
    int NOTHING = 3;
    /** 进入下一个状态 */
    int NEXT  = 1000;
    /** 进入下一个状态, 并退回一个字符 */
    int NEXT_AND_BACK = 1001;
    /** 进入下一个状态, 并将字符退回到状态机的起始位置 */
    int NEXT_AND_BACK_ALL = 1002;

    /**
     * 读取一个字符, 返回下一次状态
     */
    int read(byte ch);
    void setData(Map<String, String> d);
    void setOutput(OutputStream out);
  }


  static abstract class SState implements ISState {
    protected Map<String, String> data;
    protected OutputStream out;

    @Override
    public void setData(Map<String, String> d) {
      this.data = d;
    }

    @Override
    public void setOutput(OutputStream out) {
      this.out = out;
    }
  }


  static class S_for extends SState {
    int s = 0;

    public int read(byte ch) {
      if (s == 0) {
        if (ch == 'f') {
          s = 1;
          return BEGIN;
        } else {
          return NOTHING;
        }
      }
      if (s == 1 && ch == 'o') {
        s = 2;
        return KEEP;
      }
      if (s == 2 && ch == 'r') {
        s = 0;
        return NEXT;
      }
      s = 0;
      return RESET;
    }
  }


  static class S_Space extends SState {
    public int read(byte ch) {
      if (ch == ' ' || ch == '\t') {
        return KEEP;
      }
      return NEXT_AND_BACK;
    }
  }


  static class S_SpaceEnter extends SState {
    public int read(byte ch) {
      if (ch == ' ' || ch == '\t' || ch == '\n') {
        return KEEP;
      }
      return NEXT_AND_BACK;
    }
  }


  static class S_BeginScope extends SState {
    public int read(byte ch) {
      if (ch == '{') return NEXT;
      return RESET;
    }
  }


  static class S_BeginBrackets extends SState {
    public int read(byte ch) {
      if (ch == '(') return NEXT;
      return RESET;
    }
  }


  static class S_EndBrackets extends SState {
    public int read(byte ch) {
      if (ch == ')') return NEXT;
      return RESET;
    }
  }


  static class S_Symbol extends SState {
    private int state = 0;
    private StringBuilder symbol;
    private String name;

    S_Symbol(String name) {
      this.name = name;
    }

    public int read(byte ch) {
      if (state == 0 && Character.isJavaIdentifierStart(ch)) {
        state = 1;
        symbol = new StringBuilder();
        symbol.append((char)ch);
        return KEEP;
      }
      if (state == 1) {
        if (Character.isJavaIdentifierPart(ch)) {
          symbol.append((char)ch);
          return KEEP;
        } else {
          state = 0;
          data.put(name, symbol.toString());
          return NEXT_AND_BACK;
        }
      }
      state = 0;
      return RESET;
    }
  }


  static class S_KeyVar extends SState {
    private int state = 0;

    public int read(byte ch) {
      if (state == 0 && ch == 'v') {
        ++state;
        return KEEP;
      }
      if (state == 1 && ch == 'a') {
        ++state;
        return KEEP;
      }
      if (state == 2 && ch == 'r') {
        state = 0;
        return NEXT;
      }
      state = 0;
      return NEXT_AND_BACK_ALL;
    }
  }


  static class S_KeyIN extends SState {
    private int state = 0;

    public int read(byte ch) {
      if (state == 0 && ch == 'i') {
        ++state;
        return KEEP;
      }
      if (state == 1 && ch == 'n') {
        state = 0;
        return NEXT;
      }
      return RESET;
    }
  }


  static class S_Output extends SState {
    static int id = 0;

    @Override
    public int read(byte ch) {
      try {
        StringBuffer buf = new StringBuffer(50);
        String indexName = "__index_" + (++id) + "_";
        String keyName = data.get("keyName");
        String objName = data.get("exp");

        buf.append("for (var ");
        buf.append(indexName);
        buf.append(" in ");
        buf.append(objName);
        buf.append(") { var ");
        buf.append(keyName);
        buf.append(" = ");
        buf.append(objName);
        buf.append("[");
        buf.append(indexName);
        buf.append("];");

        out.write(buf.toString().getBytes(IConstant.CHARSET));

      } catch (IOException e) {
        throw new XBosonException(e);
      }
      return NEXT_AND_BACK;
    }
  }


  static class S_Expression extends SState {
    private StringBuilder exp;
    private int state = 0;

    @Override
    public int read(byte ch) {
      if (state == 0) {
        exp = new StringBuilder();
        state = 1;
      }
      if (ch == ')') {
        state = 0;
        data.put("exp", exp.toString());
        return NEXT_AND_BACK;
      }
      if (ch == '\n') {
        state = 0;
        return RESET;
      }
      exp.append((char)ch);
      return KEEP;
    }
  }
}
