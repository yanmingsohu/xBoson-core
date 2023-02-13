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
// 文件创建日期: 2017年11月2日 下午5:19:38
// 原始文件路径: xBoson/src/com/xboson/log/Level.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;


/**
 * 枚举的局限太多, 麻烦,
 * 该对象的实例可以直接比较 '=='
 */
public class Level {

	public static final Level DEBUG	= new Level("DEBUG",  5);
	public static final Level INFO	= new Level(" INFO", 10);
	public static final Level WARN  = new Level(" WARN", 15);
	public static final Level ERR		= new Level("ERROR", 20);
	public static final Level FATAL	= new Level("FATAL", 25);
	
	public static final Level ALL		= new Level("ALL",    0);
	public static final Level OFF		= new Level("OFF", 9999);

	/** 当 Log 的等级设置为继承, 则使用全局配置 */
	public static final Level INHERIT = new Level("INHERIT", -1);

	
	private Level(String l, int n) {
		str = l;
		num = n;
	}
	
	
	public String toString() {
		return str;
	}


	public String getName() {
		return str;
	}
	
	
	/**
	 * this - 当前日志级别
	 * @param l - 要检测的日志级别
	 * @return 阻止在当前日志级别显示 l 的日志返回 true
	 */
	public boolean blocking(Level l) {
		return num > l.num;
	}
	
	
	public void checknull() {
	}
	
	
	public static Level find(String name) {
		if (name != null) {
			switch(name.toUpperCase()) {
				case "ON": 
				case "ALL": 	  return ALL;
				case "CLOSE":
				case "OFF":     return OFF;
				case "INFO":	  return INFO;
				case "DEBUG":	  return DEBUG;
				case "WARN":    return WARN;
				case "ERR":
				case "ERROR":   return ERR;
				case "FATAL":   return FATAL;
        case "INHERIT": return INHERIT;
			}
		}
		return ALL;
	}
	
	
	private int num;
	private String str;
}
