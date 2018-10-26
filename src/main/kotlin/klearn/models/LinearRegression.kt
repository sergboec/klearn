package klearn.models

import klearn.DataFrame
import klearn.Model
import klearn.Column
import klearn.linalg.Vector
import klearn.linalg.zeros
import klearn.backend.jvm.*


class LinearRegression(private val maxIter: Int = 50,
                       private val alpha: Double = 0.01,
                       private val threshold: Double = 1e-4): Model {

    lateinit var theta: Vector<Double>

    override fun fit(df: DataFrame, col: Column<*>) {
        val y = col.cast<Double>().toVector()
        val (m, n) = df.dim
        theta = zeros(n + 1)
        val theta1 = theta
        theta[n] = 1.0
        val m1 = 1.0 / m
        var iter = 0
        while (iter++ < maxIter && theta.almostTheSame(theta1, threshold)) {
            theta = theta1
            for (j in 0 until n) {
                var s = 0.0
                for (i in 0 until m) {
                    val xi = df.row(i).toVector<Double>()
                    s += (theta.dot(xi) - y[i]) * xi[j]
                }
                theta1[j] = theta[j] - alpha * m1 * s
            }
        }
    }

    fun DataFrame.normalize(): DataFrame {
        TODO("not implemented")
    }

    override fun predict(data: DataFrame): Column<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}