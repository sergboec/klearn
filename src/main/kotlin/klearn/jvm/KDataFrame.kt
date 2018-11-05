package klearn.jvm

import klearn.*
import klearn.linalg.Vector
import java.lang.IllegalArgumentException

internal class KDataFrame(build: () -> List<KAbstractColumn<*>>): DataFrame() {
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
        return KDataFrame { data.filter { colSet.contains(it.name) } }
    }

    override fun <T> cbind(column: Column<T>): DataFrame {
        if (column is KAbstractColumn) {
            return KDataFrame { data + column }
        } else throw IllegalArgumentException("column must be ${KAbstractColumn::class}")
    }

    override fun cbind(other: DataFrame): DataFrame {
        if (other is KDataFrame) {
            return KDataFrame { data + other.data }
        } else throw IllegalArgumentException("other must have type KDataFrame buf found ${other.javaClass}")
    }

    override fun drop(name: String): DataFrame {
        return KDataFrame { data.filter { it.name != name } }
    }

    override fun rbind(other: DataFrame): DataFrame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun row(index: Int): Row {
        return KRow(index)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        data.forEach { sb.append(it.name + " ") }
        sb.appendln()
        for (r in 0 until dim.rows) {
            data.forEach {
                sb.append(it[r].toString() + " ")
            }
            sb.appendln()
        }
        return sb.toString()
    }

    override fun <T> filter(col: String, condition: (T) -> Boolean): DataFrame {
        val i = this[col].cast<T>().iterator()
        return filter(i) { condition(i.next()) }
    }

    override fun <T1, T2> filter(col1: String, col2: String, condition: (T1, T2) -> Boolean): DataFrame {
        val i1 = this[col1].cast<T1>().iterator()
        val i2 = this[col2].cast<T2>().iterator()
        return filter(i1) { condition(i1.next(), i2.next()) }
    }

    override fun <T1, T2, T3> filter(col1: String, col2: String, col3: String, condition: (T1, T2, T3) -> Boolean): DataFrame {
        val i1 = this[col1].cast<T1>().iterator()
        val i2 = this[col2].cast<T2>().iterator()
        val i3 = this[col2].cast<T3>().iterator()
        return filter(i1) { condition(i1.next(), i2.next(), i3.next()) }
    }

    private inline fun filter(iterator: Iterator<*>, condition: () -> Boolean): DataFrame {
        val indexes = ArrayList<Int>()
        var i = 0
        while (iterator.hasNext()) {
            if (condition()) {
                indexes.add(i)
            }
            i ++
        }
        return KDataFrame { data.map { ColumnView(it, indexes) } }
    }

    inner class KRow(val rowIndex: Int): Row {
        override fun getDouble(index: Int): Double? {
            val col = data[index].cast<Double>()
            return col[rowIndex].unpack(Double.NaN)
        }

        override fun getInt(index: Int): Int? {
            val col = data[index].cast<Int>()
            return col[rowIndex].unpack(Int.MIN_VALUE)
        }

        override fun getLong(index: Int): Long? {
            val col = data[index].cast<Long>()
            return col[rowIndex].unpack(Long.MIN_VALUE)
        }

        override fun getString(index: Int): String? {
            val col = data[index].cast<String?>()
            return col[rowIndex]
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

class ColumnView<T>(val column: KAbstractColumn<T>, val indexes: List<Int>): KAbstractColumn<T>() {
    override fun iterator(): Iterator<T> {
        return ViewIterator()
    }

    override val size: Int
        get() = indexes.size

    override val name: String
        get() = column.name

    override val type: Type<T>
        get() = column.type

    override operator fun get(index: Int): T {
        return column[indexes[index]]
    }

    override fun alias(name: String): KAbstractColumn<T> {
        return ColumnView(column.alias(name), indexes)
    }

    inner class ViewIterator: Iterator<T> {
        private val parent = indexes.iterator()

        override fun hasNext(): Boolean {
            return parent.hasNext()
        }

        override fun next(): T {
            val x = parent.next()
            return column[x]
        }
    }
}

abstract class KAbstractColumn<T>: Column<T> {
    @Suppress("UNCHECKED_CAST")
    override fun <R> map(type: Type<R>, f: (T) -> R): Column<R> {
        when (type) {
            is DoubleType -> {
                val store = DoubleArray(size)
                var index = 0
                iterator().forEach {
                    store[index++] = f(it) as? Double ?: Double.NaN
                }
                return KDoubleColumn(name, store) as Column<R>
            }
            is IntType -> {
                val store = IntArray(size)
                var index = 0
                iterator().forEach {
                    store[index ++] = f(it) as? Int ?: Int.MIN_VALUE
                }
               return KIntColumn(name, store) as Column<R>
            }
            is LongType -> {
                val store = LongArray(size)
                var index = 0
                iterator().forEach {
                    store[index++] = f(it) as? Long ?: Long.MIN_VALUE
                }
                return KLongColumn(name, store) as Column<R>
            }
            else -> {
                val store = mutableListOf<Any?>()
                iterator().forEach { store.add(f(it)) }
                return KObjectColumn(name, store) as Column<R>
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> cast(): Column<T> {
        return this as Column<T>
    }

    override fun asDataFrame(): DataFrame {
        return KDataFrame { listOf(this) }
    }

    abstract override fun alias(name: String): KAbstractColumn<T>
}

class KObjectColumn(override val name: String, private val data: List<*>): KAbstractColumn<Any?>() {
    override fun get(i: Int): Any? {
        return data[i]
    }

    override val type: Type<Any?>
        get() = ObjectType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<Any?> {
        return KObjectColumn(name, data)
    }

    override fun iterator(): Iterator<Any?> = data.iterator()
}

class KDoubleColumn(override val name: String, private val data: DoubleArray): KAbstractColumn<Double>(), DoubleColumn {
    override fun get(index: Int): Double {
        return data[index]
    }

    override val type: Type<Double>
        get() = DoubleType

    override fun toVector(): Vector<Double> {
        return DoubleVector(data.size, isColumnVector = true, data = data)
    }

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<Double> {
        return KDoubleColumn(name, data)
    }

    override fun iterator(): Iterator<Double> = data.iterator()
}

class KIntColumn(override val name: String, private val data: IntArray): KAbstractColumn<Int>(), IntColumn {
    override fun get(index: Int): Int {
        return data[index]
    }

    override fun iterator(): Iterator<Int> {
        return data.iterator()
    }

    override val type: Type<Int>
        get() = IntType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<Int> {
        return KIntColumn(name, data)
    }
}

class KLongColumn(override val name: String, private val data: LongArray): KAbstractColumn<Long>(), LongColumn {
    override fun get(index: Int): Long {
       return data[index]
    }

    override fun iterator(): Iterator<Long> {
        return data.iterator()
    }

    override val type: Type<Long>
        get() = LongType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<Long> {
        return KLongColumn(name, data)
    }
}

class KStringColumn(override val name: String, private val data: List<String?>): KAbstractColumn<String?>() {
    override fun get(index: Int): String? {
        return data[index]
    }

    override fun iterator(): Iterator<String?> {
        return data.iterator()
    }

    override val type: Type<String?>
        get() = StringType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<String?> {
        return KStringColumn(name, data)
    }
}

class KDataFrameInPlaceBuilder(private val header: List<String>): DataFrameInPlaceBuilder(header) {
    interface UntypedConsumer {
        fun consume(value: Any?)
        fun asColumn(): KAbstractColumn<*>
    }

    override fun build(data: List<Any?>): DataFrame {
        var list = data
        val firstRow = list.take(header.size)
        list = list.drop(header.size)
        val consumers = inferSchema(firstRow)
        while (list.isNotEmpty()) {
            val row = list.take(header.size)
            list = list.drop(header.size)
            row.zip(consumers).forEach { (value, consumer) -> consumer.consume(value) }
        }
        return KDataFrame { consumers.map { it.asColumn() } }
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

        override fun asColumn(): KAbstractColumn<*> {
            return KDoubleColumn(name, data.toDoubleArray())
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

        override fun asColumn(): KAbstractColumn<*> {
            return KIntColumn(name, data.toIntArray())
        }
    }

    private class StringConsumer(val name: String, value: String?): UntypedConsumer {
        val data = mutableListOf(value)

        override fun consume(value: Any?) {
            data.add(value as? String)
        }

        override fun asColumn(): KAbstractColumn<*> {
            return KStringColumn(name, data)
        }
    }

    private class ObjectConsumer(val name: String, value: Any?): UntypedConsumer {
        val data = mutableListOf(value)

        override fun consume(value: Any?) {
            data.add(value)
        }

        override fun asColumn(): KAbstractColumn<*> {
            return KObjectColumn(name, data)
        }
    }
}

fun <T> T.unpack(na: T): T? {
    return if (this == na) null else this
}

fun <T> T.pack(na: T): T {
    return this ?: na
}

