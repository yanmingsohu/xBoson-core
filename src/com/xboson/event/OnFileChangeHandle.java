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
// 文件创建日期: 17-12-15 下午3:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/OnFileChangeHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.been.XBosonException;
import com.xboson.util.Tool;

import javax.naming.event.NamingEvent;

import java.util.HashSet;
import java.util.Set;

import static com.xboson.event.Names.volatile_file_change_prifix;


/**
 * 封装了文件修改事件相关操作.
 * 文件修改事件必须使用该方法来封装, 因为事件名称相同而文件名不同.
 */
public abstract class OnFileChangeHandle extends GLHandle {

  private final Set<String> eventNames;


  /**
   * 构造文件修改事件句柄
   * 实现自行增加文件(系统)类型前缀, 防止不同系统中同名文件冲突.
   */
  public OnFileChangeHandle() {
    eventNames = new HashSet<>();
  }


  @Override
  public void objectChanged(NamingEvent namingEvent) {
    String file_name = (String) namingEvent.getNewBinding().getObject();
    try {
      onFileChange(file_name);
    } catch (Exception e) {
      getLog().error("OnFileChange fail", Tool.allStack(e));
    }
  }


  /**
   * 注册文件修改消息, 当文件修改后, onFileChange() 被调用,
   * 该方法可以多次调用, 在多个文件上监听修改.
   *
   * @param file_name 由于全局都会使用这个事件来注册文件消息,
   *                  不同的类型应该自定义一个前缀.
   */
  protected void regFileChange(String file_name) {
    String eventName = getEventName(file_name);
    if (eventNames.contains(eventName)) {
      throw new XBosonException("File has been monitored " + file_name);
    }
    GlobalEventBus.me().on(eventName, this);
  }


  /**
   * 文件被修改
   * @param file_name
   */
  protected abstract void onFileChange(String file_name);


  /**
   * 移除所有注册过的监听器
   */
  public void removeFileListener() {
    for (String eventName : eventNames) {
      boolean rm = GlobalEventBus.me().off(eventName, this);
      assert rm : "must removed";
    }
    eventNames.clear();
  }


  protected static String getEventName(String file) {
    if (file == null)
      throw new XBosonException.NullParamException("String file_name");

    return volatile_file_change_prifix + file;
  }


  /**
   * 方便发送文件修改消息,
   * 由于全局都会使用这个事件来注册文件消息, 不同的类型应该自定义一个前缀.
   * @param file 文件名
   */
  public static void sendChange(String file) {
    String eventName = getEventName(file);
    GlobalEventBus.me().emit(eventName, file);
  }
}
