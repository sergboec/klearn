package klearn.backend.jvm

import klearn.Dimension
import klearn.Column
import klearn.linalg.Matrix
import klearn.linalg.MatrixBuilder
import klearn.linalg.Vector
import java.lang.IllegalArgumentException


internal open class DoubleMatrix(rows: Int, cols: Int): Matrix<Double> {
    internal val a = DoubleArray(rows * cols)

    private val size = Dimension(rows, cols)

    override val dim: Dimension
        get() = size

    override val t: Matrix<Double>
        get() = transpose()

    override fun get(r: Int, c: Int): Double {
        return a[r * size.cols + c]
    }

    override fun set(r: Int, c: Int, value: Double) {
        a[r * size.cols + c] = value
    }

    override fun plus(other: Matrix<Double>): Matrix<Double> {
        cast(other) { m ->
            assert(dim == other.dim)
            val res = DoubleMatrix(dim.rows, dim.cols)
            var index = 0
            while (index < a.size) {
                res.a[index] = a[index] + m.a[index]
                index ++
            }
            return res
        }
    }

    private inline fun <T> cast(m: Matrix<Double>, body: (DoubleMatrix) -> T): T {
        if (m is DoubleMatrix) {
            return body(m)
        } else throw IllegalArgumentException("Parameter must have type ${this::class.java}")
    }

    override fun times(other: Matrix<Double>): Matrix<Double> {
        cast(other) { m ->
            assert(dim.cols == m.dim.rows)
            val res = DoubleMatrix(dim.rows, m.dim.cols)
            for (i in 0 until dim.rows) {
                for (j in 0 until m.dim.cols) {
                    var v = 0.0
                    for (k in 0 until dim.cols) {
                        v += this[i, k] * m[k, j]
                    }
                    res[i, j] = v
                }
            }
            return res
        }
    }

    override fun times(k: Double): Matrix<Double> {
        val res = DoubleMatrix(dim.rows, dim.cols)
        var index = 0
        while (index < a.size) {
            res.a[index] = a[index] * k
            index ++
        }
        return res
    }

    override fun minus(other: Matrix<Double>): Matrix<Double> {
        cast(other) { m ->
            assert(dim == other.dim)
            val res = DoubleMatrix(dim.rows, dim.cols)
            var index = 0
            while (index < a.size) {
                res.a[index] = a[index] - m.a[index]
                index ++
            }
            return res
        }
    }

    override fun col(index: Int): Vector<Double> {
        val res = DoubleVector(dim.cols, true)
        var i = 0
        var offset = index
        while (index < dim.cols) {
            res.a[i ++] = a[offset]
            offset += dim.rows
        }
        return res
    }

    override fun row(index: Int): Vector<Double> {
        val res = DoubleVector(dim.cols, false)
        var i = 0
        var offset = index * dim.cols
        while (i < dim.cols) {
            res.a[i ++] = a[offset ++]
        }
        return res
    }

    override fun almostTheSame(other: Matrix<Double>, threshold: Double): Boolean {
        if (dim != other.dim) return false
        for (i in 0 until dim.rows) {
            for (j in 0 until dim.cols) {
                if (Math.abs(this[i, j] - other[i, j]) < threshold) return false
            }
        }
        return true
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

internal class DoubleVector(n: Int, isColumnVector: Boolean): DoubleMatrix(
        if (isColumnVector) n else 1,
        if (isColumnVector) 1 else n
), Vector<Double> {
    override fun get(index: Int): Double {
        return if (dim.cols == 1) this[index, 0] else this[0, index]
    }

    override fun set(index: Int, value: Double) {
        if (dim.cols == 1) {
            this[index, 0] = value
        } else {
            this[0, index] = value
        }
    }

    override fun dot(other: Vector<Double>): Double {
        cast(other) { v ->
            assert(dim == other.dim)
            var res = 0.0
            var index = 0
            val size = Math.max(dim.rows, dim.cols)
            while (index < size) {
                res += a[index] * v.a[index]
                index ++
            }
            return res
        }
    }

    private inline fun <T> cast(m: Vector<Double>, body: (DoubleVector) -> T): T {
        if (m is DoubleVector) {
            return body(m)
        } else throw IllegalArgumentException("Parameter must have type ${this::class.java}")
    }
}

fun <T> Column<T>.toVector(): Vector<T> {
    TODO()
}

class DoubleMatrixBuilder(rows: Int, cols: Int): MatrixBuilder<Double>(rows, cols) {
    override fun build(list: List<Double>): Matrix<Double> {
        val res = DoubleMatrix(rows, cols)
        list.forEachIndexed { index, value -> res.a[index] = value }
        return res
    }
}
