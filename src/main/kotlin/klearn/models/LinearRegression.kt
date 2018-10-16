package klearn.models

import klearn.Column
import klearn.DataFrame
import klearn.Model
import klearn.linalg.Matrix
import klearn.linalg.Vector
import klearn.linalg.zeros


class LinearRegression(private val normalize: Boolean = false,
                       private val maxIter: Int = 50,
                       private val alpha: Double = 0.01,
                       private val threshold: Double = 1e-4): Model {

    lateinit var theta: Vector<Double>

    override fun fit(df: DataFrame, col: Column) {
        val x = df.asMatrix<Double>()
        val y = col.asVector<Double>()
        if (normalize) normalizeFeatures(x)
        val (m, n) = x.dim
        theta = zeros(n + 1)
        val theta1 = theta
        theta[n] = 1.0
        val m1 = 1.0 / m
        var iter = 0
        while (iter++ < maxIter && theta.equalsWithThreshold(theta1, threshold)) {
            theta = theta1
            for (j in 0 .. n) {
                var s = 0.0
                for (i in 0 .. m) {
                    s += (theta.dot(x.row(i)) - y[i]) * x[i, j]
                }
                theta1[j] = theta[j] - alpha * m1 * s
            }
        }
    }

    private fun normalizeFeatures(x: Matrix<Double>) {
        TODO("not implemented")
    }

    override fun predict(data: DataFrame): Column {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}