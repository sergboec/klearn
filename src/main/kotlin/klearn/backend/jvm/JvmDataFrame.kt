package klearn.backend.jvm

import klearn.*
import klearn.linalg.Vector
import java.lang.IllegalArgumentException

internal class JvmDataFrame(build: () -> List<Column<*>>): DataFrame() {
    val data = build()
    val header = data.map { it.name }

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
        return DFRow(index)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        data.forEach { sb.append(it.name + " ") }
        sb.appendln()
        for (r in 0 until dim.rows) {
            data.forEach { sb.append(it.toList()[r].toString() + " ") }
            sb.appendln()
        }
        return sb.toString()
    }

    inner class DFRow(val rowIndex: Int): Row {
        override fun getDouble(index: Int): Double {
            val col = data[index]
            return if (col is JvmDoubleColumn) col.data[rowIndex] else throw IllegalArgumentException("Column $col is not double column")
        }

        override fun getInt(index: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getLong(index: Int): Long {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getString(index: Int): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAny(index: Int): Any {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun resolve(name: String): Int {
            val index = header.withIndex().find { (_, colName) -> colName == name }
            return index?.index ?: throw IllegalArgumentException("No column $name")
        }
    }
}


abstract class JvmAbstractColumn<T>: Column<T> {
    abstract val iterator: Iterator<T>

    @Suppress("UNCHECKED_CAST")
    override fun <R> map(type: Type<R>, f: (T) -> R): Column<R> {
        when (type) {
            is DoubleType -> {
                val store = DoubleArray(size)
                var index = 0
                iterator.forEach {
                    store[index++] = f(it) as? Double ?: Double.NaN
                }
                return JvmDoubleColumn(name, store) as Column<R>
            }
            is IntType -> {
                val store = IntArray(size)
                var index = 0
                iterator.forEach {
                    store[index ++] = f(it) as? Int ?: Int.MIN_VALUE
                }
               return JvmIntColumn(name, store) as Column<R>
            }
            is LongType -> {
                val store = LongArray(size)
                var index = 0
                iterator.forEach {
                    store[index++] = f(it) as? Long ?: Long.MIN_VALUE
                }
                return JvmLongColumn(name, store) as Column<R>
            }
            else -> {
                val store = mutableListOf<Any?>()
                iterator.forEach { store.add(f(it)) }
                return JvmObjectColumn(name, store) as Column<R>
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> cast(): Column<T> {
        return this as Column<T>
    }

    override fun asDataFrame(): DataFrame {
        return JvmDataFrame { listOf(this) }
    }
}

class JvmObjectColumn(override val name: String, private val data: List<*>): JvmAbstractColumn<Any?>() {
    override val iterator: Iterator<Any?>
        get() = data.iterator()

    override val size: Int
        get() = data.size

    override fun alias(name: String): Column<Any?> {
        return JvmObjectColumn(name, data)
    }

    override fun toList(): List<Any?> = data
}

class JvmDoubleColumn(override val name: String, internal val data: DoubleArray): JvmAbstractColumn<Double>(), DoubleColumn {
    override fun toVector(): Vector<Double> {
        return DoubleVector(data.size, isColumnVector = true, data = data)
    }

    override val iterator: Iterator<Double>
        get() = data.iterator()

    override val size: Int
        get() = data.size

    override fun alias(name: String): Column<Double> {
        return JvmDoubleColumn(name, data)
    }

    override fun toList(): List<Double> {
        return data.asList()
    }
}

class JvmIntColumn(override val name: String, private val data: IntArray): JvmAbstractColumn<Int>(), IntColumn {
    override val iterator: Iterator<Int>
        get() = data.iterator()

    override val size: Int
        get() = data.size

    override fun alias(name: String): Column<Int> {
        return JvmIntColumn(name, data)
    }

    override fun toList(): List<Int> {
        return data.asList()
    }
}

class JvmLongColumn(override val name: String, private val data: LongArray): JvmAbstractColumn<Long>(), LongColumn {
    override val iterator: Iterator<Long>
        get() = data.iterator()

    override val size: Int
        get() = data.size

    override fun alias(name: String): Column<Long> {
        return JvmLongColumn(name, data)
    }

    override fun toList(): List<Long> {
        return data.asList()
    }
}

class JvmStringColumn(override val name: String, private val data: List<String?>): JvmAbstractColumn<String?>() {
    override val iterator: Iterator<String?>
        get() = data.iterator()

    override val size: Int
        get() = data.size

    override fun alias(name: String): Column<String?> {
        return JvmStringColumn(name, data)
    }

    override fun toList(): List<String?> {
        return data
    }
}

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
            return JvmDoubleColumn(name, data.toDoubleArray())
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
            return JvmIntColumn(name, data.toIntArray())
        }
    }

    private class StringConsumer(val name: String, value: String?): UntypedConsumer {
        val data = mutableListOf(value)

        override fun consume(value: Any?) {
            data.add(value as? String)
        }

        override fun asColumn(): Column<*> {
            return JvmStringColumn(name, data)
        }
    }

    private class ObjectConsumer(val name: String, value: Any?): UntypedConsumer {
        val data = mutableListOf(value)

        override fun consume(value: Any?) {
            data.add(value)
        }

        override fun asColumn(): Column<*> {
            return JvmObjectColumn(name, data)
        }
    }
}
