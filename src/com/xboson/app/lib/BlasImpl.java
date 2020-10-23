////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-10-23 上午6:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/BlasImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.script.IVisitByScript;
import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;


/**
 * 从 js 环境传入 array, 在 java 中函数参数是 double[] 类型时,
 * 如果 js 中是数字, 则自动 java 中也是数字;
 * 如果 js 是字符串, 则尝试转换为数字, 转换失败为 NaN;
 * 其他 js 类型尝试转换为字符串再转换为数字;
 *
 * java 参数是 double[][] 可以按照该逻辑处理;
 * java 参数是 ComplexDouble[] 可以正确处理;
 * java 函数重载不能同时有 double[] 和 double[][] 和 Object[]
 */
public class BlasImpl extends RuntimeUnitImpl implements IVisitByScript {

  public final EigenImpl eigen = new EigenImpl();
  public final GeometryImpl geometry = new GeometryImpl();
  public final SolveImpl solve = new SolveImpl();
  public final SingularImpl singular = new SingularImpl();
  public final TrigonometryImpl trigonometry = new TrigonometryImpl();
  public final MatrixFuncImpl mf = new MatrixFuncImpl();


  public BlasImpl() {
    super(null);
  }


  public ComplexDouble complex(double real) {
    return new ComplexDouble(real);
  }


  public ComplexDouble complex(double real, double imag) {
    return new ComplexDouble(real, imag);
  }


  public ComplexDoubleMatrix complexMatrix() {
    return new ComplexDoubleMatrix();
  }


  public ComplexDoubleMatrix complexMatrix(int row) {
    return new ComplexDoubleMatrix(row);
  }


  public ComplexDoubleMatrix complexMatrix(int row, int col) {
    return new ComplexDoubleMatrix(row, col);
  }


  public ComplexDoubleMatrix complexMatrixd(double[] data) {
    return new ComplexDoubleMatrix(data);
  }


  public ComplexDoubleMatrix complexMatrix(double[][] data) {
    return new ComplexDoubleMatrix(data);
  }


  public ComplexDoubleMatrix complexMatrixc(ComplexDouble[] data) {
    return new ComplexDoubleMatrix(data);
  }


  public ComplexDoubleMatrix complexMatrix(DoubleMatrix data) {
    return new ComplexDoubleMatrix(data);
  }


  public ComplexDoubleMatrix complexMatrix(DoubleMatrix real, DoubleMatrix imag) {
    return new ComplexDoubleMatrix(real, imag);
  }


  public ComplexDoubleMatrix complexMatrixOnes(int row, int col) {
    return ComplexDoubleMatrix.ones(row, col);
  }


  public ComplexDoubleMatrix complexMatrixOnes(int row) {
    return ComplexDoubleMatrix.ones(row);
  }


  public ComplexDoubleMatrix complexMatrixDiag(ComplexDoubleMatrix m) {
    return ComplexDoubleMatrix.diag(m);
  }


  public ComplexDoubleMatrix complexMatrixDiag(ComplexDoubleMatrix m, int r, int c) {
    return ComplexDoubleMatrix.diag(m, r, c);
  }


  public ComplexDoubleMatrix concatHorizontally(ComplexDoubleMatrix a, ComplexDoubleMatrix b) {
    return ComplexDoubleMatrix.concatHorizontally(a, b);
  }


  public ComplexDoubleMatrix concatVertically(ComplexDoubleMatrix a, ComplexDoubleMatrix b) {
    return ComplexDoubleMatrix.concatVertically(a, b);
  }


  public DoubleMatrix matrixd(double[] d) {
    return new DoubleMatrix(d);
  }


  public DoubleMatrix matrix(double[][] d) {
    return new DoubleMatrix(d);
  }


  public DoubleMatrix matrix(int row, int col) {
    return new DoubleMatrix(row, col);
  }


  public DoubleMatrix matrix(int row) {
    return new DoubleMatrix(row);
  }


  public DoubleMatrix matrixRand(int row, int col) {
    return DoubleMatrix.rand(row, col);
  }


  public DoubleMatrix matrixRand(int row) {
    return DoubleMatrix.rand(row);
  }


  public DoubleMatrix matrixRandn(int row, int col) {
    return DoubleMatrix.randn(row, col);
  }


  public DoubleMatrix matrixRandn(int row) {
    return DoubleMatrix.randn(row);
  }


  public DoubleMatrix matrixOnes(int row, int col) {
    return DoubleMatrix.ones(row, col);
  }


  public DoubleMatrix matrixOnes(int row) {
    return DoubleMatrix.ones(row);
  }


  public DoubleMatrix matrixEye(int n) {
    return DoubleMatrix.eye(n);
  }


  public DoubleMatrix matrixDiag(DoubleMatrix d) {
    return DoubleMatrix.diag(d);
  }


  public DoubleMatrix matrixDiag(DoubleMatrix d, int rows, int columns) {
    return DoubleMatrix.diag(d, rows, columns);
  }


  public DoubleMatrix matrixLinspace(int lower, int upper, int size) {
    return DoubleMatrix.linspace(lower, upper, size);
  }


  public DoubleMatrix matrixLogspace(double lower, double upper, int size) {
    return DoubleMatrix.logspace(lower, upper, size);
  }


  public class EigenImpl implements IVisitByScript {}
  public class GeometryImpl implements IVisitByScript {}
  public class SolveImpl implements IVisitByScript {}
  public class SingularImpl implements IVisitByScript {}
  public class MatrixFuncImpl implements IVisitByScript {}


  /**
   * 没有对应实现
   */
  public class TrigonometryImpl implements IVisitByScript {}
}
