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
// 文件创建日期: 18-6-7 下午3:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/c0nst/IHttp.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.c0nst;

public interface IHttp {

  String GET = "GET";
  String POST = "POST";

  String HEAD_CACHE = "Cache-Control";

  String VAL_CACHE_NO = "no-cache";

  String CONTENT_TYPE = "Content-Type";

  String CONTENT_UTF8 = "; charset=utf-8";

  String CONTENT_TYPE_HTML = "text/html";

  String CONTENT_TYPE_SOAP = "application/soap+xml"+ CONTENT_UTF8;

  String CONTENT_XML = "text/xml";

  String CONTENT_APP_XML = "application/xml"+ CONTENT_UTF8;

  String HEAD_SOAP = "SOAPAction";
}
