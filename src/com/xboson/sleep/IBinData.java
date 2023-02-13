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
// 文件创建日期: 17-11-11 上午11:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/IBinData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import com.xboson.been.IBean;

import java.io.Serializable;

/**
 * 需要休眠的数据实现该接口, 休眠数据为二进制且只能由 java 恢复.
 * 数据对象必须显示的继承 Serializable 才能工作
 */
public interface IBinData extends Serializable, ISleepwalker, IBean {
}
