package klearn.models.klearn.backend.jvm

import klearn.backend.jvm.DoubleMatrixBuilder
import klearn.backend.jvm.DoubleVector
import klearn.linalg.MatrixBuilder
import klearn.linalg.MatrixTest
import klearn.linalg.Vector

class JvmMatrixTest: MatrixTest() {
    override fun vectorOf(vararg x: Double): Vector<Double> {
        val elems = x.toList()
        val res = DoubleVector(elems.size, false)
        elems.forEachIndexed { index, value -> res.a[index] = value }
        return res
    }

    override fun matrixOf(rows: Int, cols: Int): MatrixBuilder<Double> = DoubleMatrixBuilder(rows, cols)
}