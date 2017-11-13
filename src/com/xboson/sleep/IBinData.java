////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
