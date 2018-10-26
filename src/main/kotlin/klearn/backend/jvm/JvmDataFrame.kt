package klearn.backend.jvm

import klearn.*
import java.lang.IllegalArgumentException

internal class JvmDataFrame(build: () -> List<Column<*>>): DataFrame {
    val data = build()

    override val dim: Dimension = Dimension(data[0].size, data.size)

    override fun get(name: String): Column<*> {
        val t = data.filter { it.name == name }
        if (t.isNotEmpty()) {
            return t[0]
        } else throw IllegalArgumentException("No such column $name")
    }

    override fun get(index: Int): Column<*> {
        return data[index]
    }

    override fun get(vararg columns: String): DataFrame {
        val colSet = columns.toSet()
        return JvmDataFrame { data.filter { colSet.contains(it.name) } }
    }

    override fun <T> plus(column: Column<T>): DataFrame {
        return JvmDataFrame { data + column }
    }

    override fun plus(other: DataFrame): DataFrame {
        if (other is JvmDataFrame) {
            return JvmDataFrame { data + other.data }
        } else throw IllegalArgumentException("other must have type JvmDataFrame buf found ${other.javaClass}")
    }

    override fun drop(name: String): DataFrame {
        return JvmDataFrame { data.filter { it.name != name } }
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
}

open class JvmColumn<out T>(override val name: String, build: () -> List<T>): Column<T> {
    @Suppress("UNCHECKED_CAST")
    override fun <R> cast(): Column<R> {
        return this as Column<R>
    }

    val data = build()

    override val size: Int
        get() = data.size

    override fun <R> map(f: (T) -> R): Column<R> {
        return JvmColumn(name) { data.map(f) }
    }

    override fun alias(name: String): Column<T> {
        return JvmColumn(name) { data }
    }

    override fun toList(): List<T> = data
}

class DoubleColumn(name: String, build: () -> List<Double>): JvmColumn<Double>(name, build)

class IntColumn(name: String, build: () -> List<Int>): JvmColumn<Int>(name, build)


fun dataFrameOf(vararg header: String) = DataFrameInplaceBuilder(header.toList())

fun rowOf(vararg data: Any?): Row = TODO()

class DataFrameInplaceBuilder(private val header: List<String>) {
    interface UntypedConsumer {
        fun consume(value: Any?)
        fun asColumn(): Column<*>
    }

    operator fun invoke(vararg data: Any?): DataFrame {
        var list = data.toList()
        val firstRow = list.take(header.size)
        list = list.drop(header.size)
        val consumers = inferSchema(firstRow)
        while (list.isNotEmpty()) {
            val row = list.take(header.size)
            list = list.drop(header.size)
            row.zip(consumers).forEach { (value, consumer) -> consumer.consume(value) }
        }
        return JvmDataFrame { consumers.map { it.asColumn() } }
    }

    private fun inferSchema(row: List<*>): List<UntypedConsumer> {
        return row.zip(header).map { (value, col) ->
            when (value) {
                is Double -> DoubleConsumer(col, value)
                is Int -> IntConsumer(col, value)
                is String -> StringConsumer(col, value)
                else -> ObjectConsumer(col, value)
            }
        }
    }

    private class DoubleConsumer(val name: String, value: Double?): UntypedConsumer {
        val data = mutableListOf<Double>()

        init {
            consume(value)
        }

        override fun consume(value: Any?) {
            data.add(value as? Double ?: Double.NaN)
        }

        override fun asColumn(): Column<*> {
            return JvmColumn(name) { data }
        }
    }

    private class IntConsumer(val name: String, value: Int?): UntypedConsumer {
        val data = mutableListOf<Int>()

        init {
            consume(value)
        }

        override fun consume(value: Any?) {
            data.add(value as? Int ?: Int.MIN_VALUE)
        }

        override fun asColumn(): Column<*> {
            return JvmColumn(name) { data }
        }
    }

    private class StringConsumer(val name: String, value: String?): UntypedConsumer {
        val data = mutableListOf(value)

        override fun consume(value: Any?) {
            data.add(value as? String)
        }

        override fun asColumn(): Column<*> {
            return JvmColumn(name) { data }
        }
    }

    private class ObjectConsumer(val name: String, value: Any?): UntypedConsumer {
        val data = mutableListOf(value)
        override fun consume(value: Any?) {
            data.add(value)
        }

        override fun asColumn(): Column<*> {
            return JvmColumn(name) { data }
        }
    }
}
