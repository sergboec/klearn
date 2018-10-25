package klearn.backend.jvm

import klearn.*
import klearn.dataframe.DataFrameInplaceBuilder
import klearn.linalg.Matrix
import klearn.linalg.Vector

class JvmBackend {
    inline fun <reified T: Number> zeros(): Vector<T> {
        TODO()
    }

    fun dataFrameInplaceBuilder(header: List<String>): DataFrameInplaceBuilder {
        TODO()
    }

    inline fun <reified T: Number> matrixOf(rows: Int, cols: Int): MatrixInplaceBuilder {
        TODO()
    }
}

class MatrixInplaceBuilder {
    operator fun invoke(vararg data: Double) {
        TODO()
    }
}
