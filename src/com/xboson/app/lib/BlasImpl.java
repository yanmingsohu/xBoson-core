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
import org.bouncycastle.pqc.math.linearalgebra.Matrix;
import org.jblas.*;
import org.jblas.ranges.*;


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

  public final EigenImpl        eigen         = new EigenImpl();
  public final GeometryImpl     geometry      = new GeometryImpl();
  public final SolveImpl        solve         = new SolveImpl();
  public final SingularImpl     singular      = new SingularImpl();
  public final TrigonometryImpl trigonometry  = new TrigonometryImpl();
  public final MatrixFuncImpl   mf            = new MatrixFuncImpl();


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


  public DoubleMatrix concatHorizontally(DoubleMatrix a, DoubleMatrix b) {
    return DoubleMatrix.concatHorizontally(a, b);
  }


  public DoubleMatrix concatVertically(DoubleMatrix a, DoubleMatrix b) {
    return DoubleMatrix.concatVertically(a, b);
  }


  public Range rangeAll() {
    return new AllRange();
  }


  public Range rangeIndices(int[] i) {
    return new IndicesRange(i);
  }


  public Range rangeIndices(DoubleMatrix i) {
    return new IndicesRange(i);
  }


  public Range rangeInterval(int begin, int end) {
    return new IntervalRange(begin, end);
  }


  public Range rangePoint(int i) {
    return new PointRange(i);
  }


  public class EigenImpl implements IVisitByScript {

    public DoubleMatrix symmetricEigenvalues(DoubleMatrix A) {
      return Eigen.symmetricEigenvalues(A);
    }

    public DoubleMatrix[] symmetricEigenvectors(DoubleMatrix A) {
      return Eigen.symmetricEigenvectors(A);
    }

    public ComplexDoubleMatrix eigenvalues(DoubleMatrix A) {
      return Eigen.eigenvalues(A);
    }

    public ComplexDoubleMatrix[] eigenvectors(DoubleMatrix A) {
      return Eigen.eigenvectors(A);
    }

    public DoubleMatrix symmetricGeneralizedEigenvalues(DoubleMatrix A,
                                                        DoubleMatrix B) {
      return Eigen.symmetricGeneralizedEigenvalues(A, B);
    }

    public DoubleMatrix[] symmetricGeneralizedEigenvectors(DoubleMatrix A,
                                                           DoubleMatrix B) {
      return Eigen.symmetricGeneralizedEigenvectors(A, B);
    }

    public DoubleMatrix symmetricGeneralizedEigenvalues(DoubleMatrix A,
                                                        DoubleMatrix B,
                                                        double vl,
                                                        double vu) {
      return Eigen.symmetricGeneralizedEigenvalues(A, B, vl, vu);
    }

    public DoubleMatrix symmetricGeneralizedEigenvalues(DoubleMatrix A,
                                                        DoubleMatrix B,
                                                        int il,
                                                        int iu) {
      return Eigen.symmetricGeneralizedEigenvalues(A, B, il, iu);
    }

    public DoubleMatrix[] symmetricGeneralizedEigenvectors(DoubleMatrix A,
                                                           DoubleMatrix B,
                                                           double vl,
                                                           double vu) {
      return Eigen.symmetricGeneralizedEigenvectors(A, B, vl, vu);
    }

    public DoubleMatrix[] symmetricGeneralizedEigenvectors(DoubleMatrix A,
                                                           DoubleMatrix B,
                                                           int il,
                                                           int iu) {
      return Eigen.symmetricGeneralizedEigenvectors(A, B, il, iu);
    }
  }


  public class GeometryImpl implements IVisitByScript {

    public DoubleMatrix pairwiseSquaredDistances(DoubleMatrix X,
                                                 DoubleMatrix Y) {
      return Geometry.pairwiseSquaredDistances(X, Y);
    }

    public DoubleMatrix center(DoubleMatrix x) {
      return Geometry.center(x);
    }

    public DoubleMatrix centerRows(DoubleMatrix x) {
      return Geometry.centerRows(x);
    }

    public DoubleMatrix centerColumns(DoubleMatrix x) {
      return Geometry.centerColumns(x);
    }

    public DoubleMatrix normalize(DoubleMatrix x) {
      return Geometry.normalize(x);
    }

    public DoubleMatrix normalizeRows(DoubleMatrix x) {
      return Geometry.normalizeRows(x);
    }

    public DoubleMatrix normalizeColumns(DoubleMatrix x) {
      return Geometry.normalizeColumns(x);
    }
  }


  public class SolveImpl implements IVisitByScript {

    public DoubleMatrix solve(DoubleMatrix A, DoubleMatrix B) {
      return Solve.solve(A, B);
    }

    public DoubleMatrix solveSymmetric(DoubleMatrix A, DoubleMatrix B) {
      return Solve.solveSymmetric(A, B);
    }

    public DoubleMatrix solvePositive(DoubleMatrix A, DoubleMatrix B) {
      return Solve.solvePositive(A, B);
    }

    public DoubleMatrix solveLeastSquares(DoubleMatrix A, DoubleMatrix B) {
      return Solve.solveLeastSquares(A, B);
    }

    public DoubleMatrix pinv(DoubleMatrix A) {
      return Solve.pinv(A);
    }
  }


  public class SingularImpl implements IVisitByScript {

    public DoubleMatrix[] fullSVD(DoubleMatrix A) {
      return Singular.fullSVD(A);
    }

    public DoubleMatrix[] sparseSVD(DoubleMatrix A) {
      return Singular.sparseSVD(A);
    }

    public ComplexDoubleMatrix[] sparseSVD(ComplexDoubleMatrix A) {
      return Singular.sparseSVD(A);
    }

    public ComplexDoubleMatrix[] fullSVD(ComplexDoubleMatrix A) {
      return Singular.fullSVD(A);
    }

    public DoubleMatrix SVDValues(DoubleMatrix A) {
      return Singular.SVDValues(A);
    }

    public DoubleMatrix SVDValues(ComplexDoubleMatrix A) {
      return Singular.SVDValues(A);
    }
  }


  public class MatrixFuncImpl implements IVisitByScript {

    public DoubleMatrix absi(DoubleMatrix x) {
      return MatrixFunctions.absi(x);
    }

    public ComplexDoubleMatrix absi(ComplexDoubleMatrix x) {
      return MatrixFunctions.absi(x);
    }

    public double abs(double d) {
      return MatrixFunctions.abs(d);
    }

    public DoubleMatrix abs(DoubleMatrix x) {
      return MatrixFunctions.abs(x);
    }

    public DoubleMatrix acosi(DoubleMatrix x) {
      return MatrixFunctions.acosi(x);
    }

    public DoubleMatrix	acos(DoubleMatrix x) {
      return MatrixFunctions.acos(x);
    }

    public double acos(double d) {
      return MatrixFunctions.acos(d);
    }

    public DoubleMatrix asini(DoubleMatrix x) {
      return MatrixFunctions.asini(x);
    }

    public double asin(double x) {
      return MatrixFunctions.asin(x);
    }

    public DoubleMatrix asin(DoubleMatrix x) {
      return MatrixFunctions.asin(x);
    }

    public DoubleMatrix atani(DoubleMatrix x) {
      return MatrixFunctions.atani(x);
    }

    public double atan(double x) {
      return MatrixFunctions.atan(x);
    }

    public DoubleMatrix atan(DoubleMatrix x) {
      return MatrixFunctions.atan(x);
    }

    public DoubleMatrix cbrti(DoubleMatrix x) {
      return MatrixFunctions.cbrti(x);
    }

    public double cbrt(double x) {
      return MatrixFunctions.cbrt(x);
    }

    public DoubleMatrix cbrt(DoubleMatrix x) {
      return MatrixFunctions.cbrt(x);
    }

    public DoubleMatrix ceili(DoubleMatrix x) {
      return MatrixFunctions.ceili(x);
    }

    public double ceil(double x) {
      return MatrixFunctions.ceil(x);
    }

    public DoubleMatrix ceil(DoubleMatrix x) {
      return MatrixFunctions.ceil(x);
    }

    public DoubleMatrix cosi(DoubleMatrix x) {
      return MatrixFunctions.cosi(x);
    }

    public double cos(double x) {
      return MatrixFunctions.cos(x);
    }

    public DoubleMatrix cos(DoubleMatrix x) {
      return MatrixFunctions.cos(x);
    }

    public DoubleMatrix coshi(DoubleMatrix x) {
      return MatrixFunctions.coshi(x);
    }

    public double cosh(double x) {
      return MatrixFunctions.cosh(x);
    }

    public DoubleMatrix cosh(DoubleMatrix x) {
      return MatrixFunctions.cosh(x);
    }

    public DoubleMatrix expi(DoubleMatrix x) {
      return MatrixFunctions.expi(x);
    }

    public double exp(double x) {
      return MatrixFunctions.exp(x);
    }

    public DoubleMatrix exp(DoubleMatrix x) {
      return MatrixFunctions.exp(x);
    }

    public DoubleMatrix expm(DoubleMatrix A) {
      return MatrixFunctions.expm(A);
    }

    public DoubleMatrix floori(DoubleMatrix x) {
      return MatrixFunctions.floori(x);
    }

    public double floor(double x) {
      return MatrixFunctions.floor(x);
    }

    public DoubleMatrix floor(DoubleMatrix x) {
      return MatrixFunctions.floor(x);
    }

    public DoubleMatrix logi(DoubleMatrix x) {
      return MatrixFunctions.logi(x);
    }

    public double log(double x) {
      return MatrixFunctions.log(x);
    }

    public DoubleMatrix log(DoubleMatrix x) {
      return MatrixFunctions.log(x);
    }

    public DoubleMatrix log10i(DoubleMatrix x) {
      return MatrixFunctions.log10i(x);
    }

    public double log10(double x) {
      return MatrixFunctions.log10(x);
    }

    public DoubleMatrix log10(DoubleMatrix x) {
      return MatrixFunctions.log10(x);
    }

    public DoubleMatrix powi(DoubleMatrix x, DoubleMatrix e) {
      return MatrixFunctions.powi(x, e);
    }

    public double pow(double x, double y) {
      return MatrixFunctions.pow(x, y);
    }

    public DoubleMatrix pow(double b, DoubleMatrix x) {
      return MatrixFunctions.pow(b, x);
    }

    public DoubleMatrix pow(DoubleMatrix x, double e) {
      return MatrixFunctions.pow(x, e);
    }

    public DoubleMatrix powi(DoubleMatrix x, double d) {
      return MatrixFunctions.powi(x, d);
    }

    public DoubleMatrix powi(double base, DoubleMatrix x) {
      return MatrixFunctions.powi(base, x);
    }

    public DoubleMatrix signumi(DoubleMatrix x) {
      return MatrixFunctions.signumi(x);
    }

    public double signum(double x) {
      return MatrixFunctions.signum(x);
    }

    public DoubleMatrix signum(DoubleMatrix x) {
      return MatrixFunctions.signum(x);
    }

    public DoubleMatrix sini(DoubleMatrix x) {
      return MatrixFunctions.sini(x);
    }

    public double sin(double x) {
      return MatrixFunctions.sin(x);
    }

    public DoubleMatrix sin(DoubleMatrix x) {
      return MatrixFunctions.sin(x);
    }

    public DoubleMatrix sinhi(DoubleMatrix x) {
      return MatrixFunctions.sinhi(x);
    }

    public double sinh(double x) {
      return MatrixFunctions.sinh(x);
    }

    public DoubleMatrix sinh(DoubleMatrix x) {
      return MatrixFunctions.sinh(x);
    }

    public DoubleMatrix sqrti(DoubleMatrix x) {
      return MatrixFunctions.sqrti(x);
    }

    public double sqrt(double x) {
      return MatrixFunctions.sqrt(x);
    }

    public DoubleMatrix sqrt(DoubleMatrix x) {
      return MatrixFunctions.sqrt(x);
    }

    public DoubleMatrix tani(DoubleMatrix x) {
      return MatrixFunctions.tani(x);
    }

    public double tan(double x) {
      return MatrixFunctions.tan(x);
    }

    public DoubleMatrix tan(DoubleMatrix x) {
      return MatrixFunctions.tan(x);
    }

    public DoubleMatrix tanhi(DoubleMatrix x) {
      return MatrixFunctions.tanhi(x);
    }

    public double tanh(double x) {
      return MatrixFunctions.tanh(x);
    }

    public DoubleMatrix tanh(DoubleMatrix x) {
      return MatrixFunctions.tanh(x);
    }
  }


  /**
   * 没有对应实现
   */
  public class TrigonometryImpl implements IVisitByScript {}
}
