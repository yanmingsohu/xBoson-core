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
// 文件创建日期: 2017年12月14日 13:34
// 原始文件路径: xBoson/src/com/xboson/app/lib/pre-init.js
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

//
// 在系统模块初始化之后, 服务脚本执行之前, 执行预初始操作.
// 这些 java 模块需要 js 环境中的隐式全局变量, 只能在 js 环境中调用.
//
function __init_system_modules(sys) {
   sys.parseBody();
}
