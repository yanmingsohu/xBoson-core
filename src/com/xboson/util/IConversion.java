package com.xboson.util;

public interface IConversion<IN, OUT> {
  /**
   * 转换数据
   * @param obj
   * @return 返回转换后的值, 如果不许要转换应返回 obj 本身
   */
  OUT value(IN obj);

  /**
   * 类型转换
   * @param _class
   * @return 返回转换后的类型, 如果不需要转换应返回 _class 本身
   */
  Class<?> type(Class<?> _class);
}
