package klearn.backend.jvm

import klearn.*
import klearn.linalg.Matrix

internal class JvmDataFrame: DataFrame {
    override val dim: Dimension
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun get(name: String): Column {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(index: Int): Column {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(vararg columns: String): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> plus(column: TypedColumn<T>): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun plus(other: DataFrame): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun drop(name: String): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun union(other: DataFrame): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transform(f: (Row) -> Row): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun row(index: Int): Row {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Number> asMatrix(): Matrix<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
