package klearn.backend.jvm

import klearn.Dimension
import klearn.Column
import klearn.linalg.Matrix
import klearn.linalg.Vector
import java.lang.IllegalArgumentException


internal open class DoubleMatrix(rows: Int, cols: Int, init: (Int) -> Double ): Matrix<Double> {
    private val a: Array<Double> = Array(rows * cols, init)

    private val size = Dimension(rows, cols)

    override val dim: Dimension
        get() = size

    override val t: Matrix<Double>
        get() = transpose()

    override fun get(r: Int, c: Int): Double {
        return a[c * size.rows + r]
    }

    override fun set(r: Int, c: Int, value: Double) {
        a[c * size.rows + r] = value
    }

    override fun plus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        if (other is DoubleMatrix) {
            return DoubleMatrix(dim.rows, dim.cols) { index -> a[index] + other.a[index] }
        } else throw IllegalArgumentException("Parameter must have type DoubleMatrix")
    }

    override fun times(other: Matrix<Double>): Matrix<Double> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun times(k: Double): Matrix<Double> {
        return DoubleMatrix(dim.rows, dim.cols) { index -> a[index] * k }
    }

    override fun minus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        if (other is DoubleMatrix) {
            return DoubleMatrix(dim.rows, dim.cols) { index -> a[index] - other.a[index] }
        } else throw IllegalArgumentException("Parameter must have type DoubleMatrix")
    }

    override fun col(index: Int): Vector<Double> {
        TODO() // copy array
    }

    override fun row(index: Int): Vector<Double> {
        var offset = index
        return DoubleVector(dim.cols, false) {
            offset += dim.rows
            a[offset]
        }
    }

    override fun almostTheSame(other: Matrix<Double>, threshold: Double): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun transpose(): Matrix<Double> {
        TODO()
    }
}

internal class DoubleVector(n: Int, isColumnVector: Boolean, init: (Int) -> Double): DoubleMatrix(
        if (isColumnVector) n else 1,
        if (isColumnVector) 1 else n,
        init
), Vector<Double> {
    override fun get(index: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(index: Int, value: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dot(other: Vector<Double>): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun <T: Number> Column<T>.toVector(): Vector<T> {
    TODO()
}