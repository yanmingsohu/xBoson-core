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
// 文件创建日期: 18-6-7 下午3:48
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/c0nst/IXML.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.c0nst;

public interface IXML {

  String PF_SOAP_TAG    = "soap:";

  String NS             = "xmlns";
  String NS_PF          = NS +":";

  String NS_XSI         = "xmlns:xsi";
  String NS_XSI_URI     = "http://www.w3.org/2001/XMLSchema-instance";

  String NS_XSD         = "xmlns:xsd";
  String NS_XSD_URI     = "http://www.w3.org/2001/XMLSchema";

  String NS_SOAP        = "xmlns:soap";
  String NS_SOAP_URI    = "http://schemas.xmlsoap.org/soap/envelope/";
  String NS_SOAP12_URI  = "http://www.w3.org/2003/05/soap-envelope";

  String TAG_S_ENVELOPE = PF_SOAP_TAG +"Envelope";
  String TAG_S_BODY     = PF_SOAP_TAG +"Body";
  String TAG_S_HEADER   = PF_SOAP_TAG +"Header";
  String TAG_S_FAULT    = PF_SOAP_TAG +"Fault";

  String XML_HEAD       = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

}
