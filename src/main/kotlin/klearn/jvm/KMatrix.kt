package klearn.jvm

import klearn.Dimension
import klearn.Column
import klearn.linalg.Matrix
import klearn.linalg.MatrixBuilder
import klearn.linalg.Vector

abstract class AbstractDoubleMatrix(rows: Int, cols: Int): Matrix<Double> {
    private val size = Dimension(rows, cols)

    override val t: Matrix<Double>
        get() = transpose()

    override val dim: Dimension
        get() = size

    override fun plus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        return op { i, j -> this[i, j] + other[i, j] }
    }

    override fun minus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        return op { i, j -> this[i, j] - other[i, j] }
    }

    override fun times(k: Double): Matrix<Double> {
        return op { i, j -> this[i, j] * k }
    }

    private inline fun op(f: (Int, Int) -> Double): Matrix<Double> {
        val res = DoubleMatrix(dim.rows, dim.cols)
        for (i in 0 until dim.rows) {
            for (j in 0 until dim.cols) {
                res[i, j] = f(i, j)
            }
        }
        return res
    }

    override fun times(other: Matrix<Double>): Matrix<Double> {
        assert(dim.cols == other.dim.rows) { "$dim ${other.dim}"}
        val res = DoubleMatrix(dim.rows, other.dim.cols)
        for (i in 0 until dim.rows) {
            for (j in 0 until other.dim.cols) {
                var v = 0.0
                for (k in 0 until dim.cols) {
                    v += this[i, k] * other[k, j]
                }
                res[i, j] = v
            }
        }
        return res
    }

    private fun transpose(): Matrix<Double> {
        val res = DoubleMatrix(dim.cols, dim.rows)
        val m = Math.min(dim.cols, dim.rows)
        for (r in 0 until m) {
            for (c in r + 1 until m) {
                res[c, r] = this[r, c]
                res[r, c] = this[c, r]
            }
            res[r, r] = this[r, r]
        }
        if (m == dim.rows) {
            for (c in m until dim.cols) {
                for (r in 0 until dim.rows) {
                    res[c, r] = this[r, c]
                }
            }
        } else {
            for (r in m until dim.rows) {
                for (c in 0 until dim.cols) {
                    res[c, r] = this[r, c]
                }
            }
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

    override fun col(index: Int, copy: Boolean, preserveOrientation: Boolean): Vector<Double> {
        return if (copy) {
            val res = DoubleVector(dim.rows, preserveOrientation)
            var i = 0
            while (i < dim.rows) {
                res.a[i] = this[i, index]
                i ++
            }
            res
        } else {
            ColView(this, index)
        }
    }

    override fun row(index: Int, copy: Boolean, preserveOrientation: Boolean): Vector<Double> {
        return if (copy) {
            val res = DoubleVector(dim.cols, !preserveOrientation)
            var i = 0
            while (i < dim.cols) {
                res.a[i] = this[index, i]
                i ++
            }
            res
        } else {
            RowView(this, index)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (r in 0 until dim.rows) {
            for (c in 0 until dim.cols) {
                sb.append(this[r, c])
                if (c != dim.cols - 1) sb.append(", ")
            }
            sb.appendln()
        }
        return sb.toString()
    }

    override fun mul(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        val res = DoubleMatrix(dim.rows, dim.cols)
        for (r in 0 until dim.rows) {
            for (c in 0 until dim.cols) {
                res[r, c] = this[r, c] * other[r, c]
            }
        }
        return res
    }

    override fun sum(): Double {
        var res = 0.0
        for (r in 0 until dim.rows) {
            for (c in 0 until dim.cols) {
                res += this[r, c]
            }
        }
        return res
    }

    override fun cbind(other: Matrix<Double>): Matrix<Double> {
        assert(dim.rows == other.dim.rows) { "$dim ${other.dim}" }
        val res = DoubleMatrix(dim.rows, dim.cols + other.dim.cols)
        for (r in 0 until dim.rows) {
            for (c in 0 until dim.cols) {
                res[r, c] = this[r, c]
            }
        }
        for (r in 0 until dim.rows) {
            for (c in 0 until other.dim.cols) {
                res[r, c + dim.cols] = other[r, c]
            }
        }
        return res
    }
}

internal open class DoubleMatrix(rows: Int, cols: Int, internal val a: DoubleArray = DoubleArray(rows * cols)): AbstractDoubleMatrix(rows, cols) {

    override fun get(r: Int, c: Int): Double {
        return a[r * dim.cols + c]
    }

    override fun set(r: Int, c: Int, value: Double) {
        a[r * dim.cols + c] = value
    }

    fun cols(vararg c: Int): Matrix<Double> {
        return DoubleMatrixViewIdx(this, rows = (0 until dim.rows).toList(), cols = c.asList())
    }

    override fun plus(other: Matrix<Double>): Matrix<Double> {
        assert(dim == other.dim)
        return when (other) {
             is DoubleMatrix -> {
                val res = DoubleMatrix(dim.rows, dim.cols)
                var index = 0
                while (index < a.size) {
                    res.a[index] = a[index] + other.a[index]
                    index++
                }
                return res
            }
            else -> super.plus(other)
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
        assert(dim == other.dim) { "${this.javaClass} $dim ${other.dim} "}
        return when (other) {
            is DoubleMatrix -> {
                val res = DoubleMatrix(dim.rows, dim.cols)
                var index = 0
                while (index < a.size) {
                    res.a[index] = a[index] - other.a[index]
                    index++
                }
                return res
            }
            else -> super.minus(other)
        }
    }

    override fun col(index: Int, copy: Boolean, preserveOrientation: Boolean): Vector<Double> {
        return if (copy) {
            val res = DoubleVector(dim.rows, preserveOrientation)
            var i = 0
            var offset = index
            while (i < dim.rows) {
                res.a[i ++] = a[offset]
                offset += dim.cols
            }
            res
        } else {
            ColView(this, index)
        }
    }

    override fun row(index: Int, copy: Boolean, preserveOrientation: Boolean): Vector<Double> {
        return if (copy) {
            val res = DoubleVector(dim.cols, !preserveOrientation)
            System.arraycopy(this.a, index * dim.cols, res.a, 0, dim.cols)
            res
        } else {
            RowView(this, index)
        }
    }
}

open class DoubleMatrixView(val parent: Matrix<Double>, val top: Int, val left: Int, bottom: Int, right: Int) :
        AbstractDoubleMatrix(bottom - top, right - left) {

    override fun get(r: Int, c: Int): Double {
        return parent[r + top, c + left]
    }

    override fun set(r: Int, c: Int, value: Double) {
        parent[r + top, c + left] = value
    }
}

open class DoubleMatrixViewIdx(val parent: Matrix<Double>, val rows: List<Int>, val cols: List<Int>) :
        AbstractDoubleMatrix(rows.size, cols.size) {

    override fun get(r: Int, c: Int): Double {
        return parent[rows[r], cols[c]]
    }

    override fun set(r: Int, c: Int, value: Double) {
        parent[rows[r], cols[c]] = value
    }
}

interface AbstractVectorView: Vector<Double> {
    override val size: Int
        get() = Math.max(dim.rows, dim.cols)

    override fun dot(other: Vector<Double>): Double {
        var index = 0
        var res = 0.0
        while (index ++ < size) {
            res += this[index] * other[index]
        }
        return res
    }
}

class RowView(parent: Matrix<Double>, row: Int): DoubleMatrixView(parent, row, 0, row, parent.dim.cols), AbstractVectorView {
    override fun get(index: Int): Double {
        return this[0, index]
    }

    override fun set(index: Int, value: Double) {
        this[0, index] = value
    }
}


class ColView(parent: Matrix<Double>, col: Int): DoubleMatrixView(parent, 0, col, parent.dim.rows, col), AbstractVectorView {
    override fun get(index: Int): Double {
        return this[index, 0]
    }

    override fun set(index: Int, value: Double) {
        this[index, 0] = value
    }
}

internal class DoubleVector(n: Int, isColumnVector: Boolean, data: DoubleArray = DoubleArray(n)): DoubleMatrix(
        if (isColumnVector) n else 1,
        if (isColumnVector) 1 else n,
        data
), AbstractVectorView {
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
        assert(size == other.size)
        return when (other) {
            is DoubleVector -> {
                var res = 0.0
                var index = 0
                while (index < size) {
                    res += a[index] * other.a[index]
                    index++
                }
                return res
            }
            else -> super.dot(other)
        }
    }
}

class DoubleMatrixBuilder(rows: Int, cols: Int): MatrixBuilder<Double>(rows, cols) {
    override fun build(list: List<Double>): Matrix<Double> {
        val res = DoubleMatrix(rows, cols)
        list.forEachIndexed { index, value -> res.a[index] = value }
        return res
    }
}
