package klearn.models.klearn.backend.jvm

import klearn.backend.jvm.DoubleMatrixBuilder
import klearn.linalg.MatrixBuilder
import klearn.linalg.MatrixTest

class JvmMatrixTest: MatrixTest() {
    override fun matrixOf(rows: Int, cols: Int): MatrixBuilder<Double> = DoubleMatrixBuilder(rows, cols)
}