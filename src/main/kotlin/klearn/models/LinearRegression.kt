package klearn.models

import klearn.DataFrame
import klearn.Model
import klearn.Column
import klearn.backend.jvm.DoubleVector
import klearn.linalg.Matrix
import klearn.linalg.Vector
import klearn.linalg.zeros
import klearn.linalg.vectorOf
import klearn.toVector


class LinearRegression(private val maxIter: Int = 10000,
                       private val alpha: Double = 0.01,
                       private val threshold: Double = 1e-3): Model {

    lateinit var theta: Vector<Double>

    override fun fit(df: DataFrame, col: Column<*>) {
        TODO("not implemented")
    }

    fun fit(_x: Matrix<Double>, y: Vector<Double>) {
        val x = ones(y.size).cbind(_x)
        val (m, n) = x.dim
        theta = zeros(n)
        for (k in 0..maxIter) {
            val m1 = 1.0 / m
            for (j in 0 until n) {
                val xj = x.col(j)
                val s = xj.mul(x * theta - y).sum()
                theta[j] -= s * alpha * m1
            }
        }
    }

    fun ones(n: Int): Vector<Double> {
        val arr = DoubleArray(n) { 1.0 }
        return DoubleVector(n, true, arr)
    }

    fun DataFrame.normalize(): DataFrame {
        TODO("not implemented")
    }

    override fun predict(data: DataFrame): Column<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}