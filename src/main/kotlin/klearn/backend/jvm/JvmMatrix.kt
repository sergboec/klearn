package klearn.backend.jvm

import klearn.Dimension
import klearn.Column
import klearn.linalg.Matrix
import klearn.linalg.MatrixBuilder
import klearn.linalg.Vector
import java.lang.IllegalArgumentException


internal open class DoubleMatrix(rows: Int, cols: Int): Matrix<Double> {
    private val a = DoubleArray(rows * cols)

    private val size = Dimension(rows, cols)

    override val dim: Dimension
        get() = size

    override val t: Matrix<Double>
        get() = transpose()

    inline fun fill(init: (Int) -> Double): DoubleMatrix {
        for (index in 0 until a.size) {
            a[index] = init(index)
        }
        return this
    }

    override fun get(r: Int, c: Int): Double {
        return a[r * size.cols + c]
    }

    override fun set(r: Int, c: Int, value: Double) {
        a[r * size.cols + c] = value
    }

    override fun plus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        if (other is DoubleMatrix) {
            return DoubleMatrix(dim.rows, dim.cols).fill { index -> a[index] + other.a[index] }
        } else throw IllegalArgumentException("Parameter must have type DoubleMatrix")
    }

    override fun times(other: Matrix<Double>): Matrix<Double> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun times(k: Double): Matrix<Double> {
        return DoubleMatrix(dim.rows, dim.cols).fill { index -> a[index] * k }
    }

    override fun minus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        if (other is DoubleMatrix) {
            return DoubleMatrix(dim.rows, dim.cols).fill { index -> a[index] - other.a[index] }
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
        val res = DoubleMatrix(dim.cols, dim.rows)
        for (r in 0 until dim.rows) {
            for (c in 0 until dim.cols) {
                res[c, r] = this[r, c]
            }
        }
        return res
    }
}

internal class DoubleVector(n: Int, isColumnVector: Boolean, init: (Int) -> Double): DoubleMatrix(
        if (isColumnVector) n else 1,
        if (isColumnVector) 1 else n
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

fun <T> Column<T>.toVector(): Vector<T> {
    TODO()
}

class DoubleMatrixBuilder(rows: Int, cols: Int): MatrixBuilder<Double>(rows, cols) {
    override fun build(list: List<Double>): Matrix<Double> {
        return DoubleMatrix(rows, cols).fill { index -> list[index] }
    }
}
