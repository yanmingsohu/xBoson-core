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
// 文件创建日期: 17-11-23 上午11:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/DateImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.script.IJSObject;
import com.xboson.script.JSObject;
import com.xboson.util.DateParserFactory;
import com.xboson.util.DateParserFactory.ScopeCalendar;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeDate;

import java.util.Calendar;
import java.util.Date;


public class DateImpl extends RuntimeUnitImpl implements IJSObject {


  public DateImpl(CallData cd) {
    super(cd);
  }

  public DateImpl() {
    super(null);
  }


  @Override
  public String env_name() {
    return "date";
  }


  @Override
  public boolean freeze() {
    return true;
  }


  @Override
  public void init() {}


  @Override
  public void destory() {}


  public Object currentDate() {
    return new Date();
  }


  public String currentTimeString() {
    return Tool.formatDate(new Date());
  }


  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }


  public long unixTimestampFromDate(Date d) {
    return d.getTime() / 1000;
  }


  public Date dateFromUnixTimestamp(long l) {
    return new Date(l * 1000);
  }


  public String formattedTime(Object date, String format) throws Exception {
    Date d = parse(date);
    try (DateParserFactory.DateParser dp = DateParserFactory.get(format)) {
      return dp.format(d);
    }
  }


  public Date parseDate(String dateString, String format) throws Exception {
    try (DateParserFactory.DateParser dp = DateParserFactory.get(format)) {
      return dp.parse(dateString);
    }
  }


  public Date plusDate(Date d, int num, String type) {
    Calendar c = Calendar.getInstance();
    c.setTime(d);
    switch (type) {
      case "y": c.add(Calendar.YEAR        , num); break;
      case "M": c.add(Calendar.MONTH       , num); break;
      case "w": c.add(Calendar.WEEK_OF_YEAR, num); break;
      case "d": c.add(Calendar.DAY_OF_MONTH, num); break;
      case "h": c.add(Calendar.HOUR_OF_DAY , num); break;
      case "m": c.add(Calendar.MONTH       , num); break;
      case "s": c.add(Calendar.SECOND      , num); break;
      case "S": c.add(Calendar.MILLISECOND , num); break;
    }
    return c.getTime();
  }


  public String plusString(String timeStr, String format, int num, String type)
          throws Exception {
    try (DateParserFactory.DateParser dp = DateParserFactory.get(format)) {
      Date d = dp.parse(timeStr);
      d = plusDate(d, num, type);
      return dp.format(d);
    }
  }


  public int compare(Date d1, Date d2) {
    return d1.compareTo(d2);
  }


  public int getYear(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.YEAR);
    }
  }


  public int getMonth(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.MONTH) + 1;
    }
  }


  public int getWeek(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.WEEK_OF_YEAR);
    }
  }


  public int getDayOfYear(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.DAY_OF_YEAR);
    }
  }


  public int getDayOfMonth(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.DAY_OF_MONTH);
    }
  }


  public int getDayOfWeek(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.DAY_OF_WEEK);
    }
  }


  public int getHourOfDay(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.HOUR_OF_DAY);
    }
  }


  public int getMinuteOfDay(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.MINUTE) + c.c.get(Calendar.HOUR_OF_DAY) * 60;
    }
  }


  public int getMinuteOfHour(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.MINUTE);
    }
  }


  public int getSecond(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.SECOND);
    }
  }


  public long getMillis(Date d) throws Exception {
    return d.getTime();
  }


  public long getMillisOfDay(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return  c.c.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000
            + c.c.get(Calendar.MINUTE) * 60 * 1000
            + c.c.get(Calendar.SECOND) * 1000
            + c.c.get(Calendar.MILLISECOND);
    }
  }


  public int getMillisOfSecond(Date d) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      return c.c.get(Calendar.MILLISECOND);
    }
  }


  public Date setYear(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.YEAR, n);
      return c.c.getTime();
    }
  }


  public Date setMonth(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.MONTH, n);
      return c.c.getTime();
    }
  }


  public Date setDayOfYear(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.DAY_OF_YEAR, n);
      return c.c.getTime();
    }
  }


  public Date setDayOfMonth(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.DAY_OF_MONTH, n);
      return c.c.getTime();
    }
  }


  public Date setDayOfWeek(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.DAY_OF_WEEK, n);
      return c.c.getTime();
    }
  }


  public Date setHourOfDay(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.HOUR_OF_DAY, n);
      return c.c.getTime();
    }
  }


  public Date setMinuteOfHour(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.MINUTE, n);
      return c.c.getTime();
    }
  }


  public Date setMillis(Date d, long n) throws Exception {
    d.setTime(n);
    return d;
  }


  public Date setMillisOfDay(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      int h = n / (60 * 60 * 1000);
      n -= h * (60 * 60 * 1000);
      int m = n / (60 * 1000);
      n -= m * (60 * 1000);
      int s = n / (1000);
      n -= s * 1000;

      c.c.set(Calendar.HOUR_OF_DAY, h);
      c.c.set(Calendar.MINUTE, m);
      c.c.set(Calendar.SECOND, s);
      c.c.set(Calendar.MILLISECOND, n);
      return c.c.getTime();
    }
  }


  public Date setMillisOfSecond(Date d, int n) throws Exception {
    try (ScopeCalendar c = new ScopeCalendar(d)) {
      c.c.set(Calendar.MILLISECOND, n);
      return c.c.getTime();
    }
  }


  public Date parse(Object o) {
    if (o == null) {
      throw new XBosonException.NullParamException("Object data");
    }
    if (o instanceof Date) {
      return (Date) o;
    }
    if (o instanceof NativeDate || o instanceof ScriptObjectMirror) {
      long t = (long) NativeDate.getTime(o);
      return new Date(t);
    }
    throw new XBosonException.BadParameter(
            "Object data", "Is not Date type " + o.getClass());
  }
}
