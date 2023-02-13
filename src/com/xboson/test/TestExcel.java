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
// 文件创建日期: 18-1-7 下午2:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestExcel.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.util.StringBufferOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class TestExcel extends Test {


  @Override
  public void test() throws Throwable {
    testWrite();
  }


  public void testWrite() throws Exception{
    // 第一步 创建文档对象
    Workbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet("分区信息");
    Row headRow = sheet.createRow(0);
    String[] heads = {"省","市","区（县）","定区编码","关键字",
            "起始号","结束号","单双号","省市区编码"};
    for(int i=0;i<heads.length;i++){
      Cell cell = headRow.createCell(i);
      cell.setCellValue(heads[i]);
    }

    for(int i=1; i<10; ++i){
      //从第二行开始
      Row row = sheet.createRow(i);

      for(int j=0;j<9;j++){
        Cell cell = row.createCell(j);
        cell.setCellValue(i + " " + j);
      }
    }

    StringBufferOutputStream buf = new StringBufferOutputStream();
    wb.write(buf);

    TestTool.printArr(buf.toBytes());
  }


  public static void main(String[] a) {
    new TestExcel();
  }
}
