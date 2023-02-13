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
// 文件创建日期: 17-12-13 下午12:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/SomeThing.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

/**
 * Http json 应答要求把一个数据对象放在一个 list 中, 这个数据对象就是 SomeThing,
 * 在很多时候找不到合适的应答对象, 就把属性塞在这里;
 *
 * 这些属性看起来将是毫无关联的.
 */
public class SomeThing {
  public String userid;
}
