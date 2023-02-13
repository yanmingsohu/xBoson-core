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
// 文件创建日期: 17-12-10 上午11:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/SqlKeyWords.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;

import com.xboson.util.JavaConverter;

import java.util.Set;


public class SqlKeyWords {

  static Set<String> key = JavaConverter.arr2setUpper(new String[] {
          "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ACCESSIBLE",
          "AND", "ANY", "AS", "ASC", "AT",

          "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH",
          "BY", "BYTE",

          "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER",
          "CHECK", "COLLATE", "COLUMN", "COMMENT", "COMMIT", "CONSTRAINT",

          "DATABASE", "DATABASES", "DECIMAL", "DECLARE", "DEFAULT", "DROP",
          "DELAYED", "DELETE", "DESC", "DESCRIBE", "DISTINCT", "DOUBLE",

          "EACH", "ELSE", "ELSEIF", "ENABLE", "END", "ENUM", "EXIT", "EXECUTE",

          "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FROM", "FULLTEXT",

          "GET", "GRANT", "GROUP",

          "HAVING",

          "IDENTIFIED", "IF", "IGNORE", "IN", "INDEX", "INNER", "INOUT", "INSERT",
          "INT", "INTEGER", "INTO", "EXISTS",

          "JOIN",

          "LIKE", "LIMIT", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LEFT",

          "MATCH",

          "NCHAR", "NOT", "NULL", "NVARCHAR", "NUMERIC", "NUMBER",

          "OFFSET", "ON", "ONE", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OWNER",

          "REAL", "REMOVE", "REPAIR", "REPEAT", "REPLACE", "RIGHT",

          "SCHEMA", "SELECT", "SET", "SMALLINT", "STRING",

          "TABLE", "TABLES", "TABLESPACE", "TEMPORARY", "TABLE_NAME", "TEXT",
          "THAN", "THEN", "TO", "TYPE", "TYPES",

          "UNION", "UNDEFINED", "UPDATE", "UPGRADE", "USE",

          "VALUE", "VALUES",

          "WHEN", "WHERE", "WHILE", "WITH", "WITHOUT",

          "XOR", "OJ",
  });


  /**
   * 该关键字不放入层级关系
   */
  static Set<String> skipParent = JavaConverter.arr2setUpper(new String[] {
          "IF", "NOT", "EXISTS", "LIKE", "LOW_PRIORITY", "DELAYED",
          "LOW_PRIORITY", "IGNORE", "PARTITION",
  });


  /**
   * 关键字表示表名称列表开始
   */
  static Set<String> beginTable = JavaConverter.arr2setUpper(new String[] {
          "TABLE", "INTO", "FROM", "REPLACE", "UPDATE",
          "JOIN", "OJ",
  });


  static Set<Character> notation = JavaConverter.arr2set(new Character[] {
          ' ', '\t', '\n', '(', ')', ',', '{', '}', ';',
  });

}
