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
            return col[rowIndex].unpack(NA.double)
        }

        override fun getInt(index: Int): Int? {
            val col = data[index].cast<Int>()
            return col[rowIndex].unpack(NA.int)
        }

        override fun getLong(index: Int): Long? {
            val col = data[index].cast<Long>()
            return col[rowIndex].unpack(NA.long)
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
        return when (type) {
            is DoubleType, NullableDoubleType -> {
                val store = DoubleArray(size)
                var index = 0
                iterator().forEach {
                    store[index ++] = f(it) as? Double ?: NA.double
                }
                if (type.isNullable()) KNullableDoubleColumn(name, store) else KDoubleColumn(name, store)
            }
            is IntType, NullableDoubleType -> {
                val store = IntArray(size)
                var index = 0
                iterator().forEach {
                    store[index ++] = f(it) as? Int ?: NA.int
                }
               if (type.isNullable()) KNullableIntColumn(name, store) else KIntColumn(name, store)
            }
            is LongType, NullableLongType -> {
                val store = LongArray(size)
                var index = 0
                iterator().forEach {
                    store[index ++] = f(it) as? Long ?: NA.long
                }
                if (type.isNullable()) KNullableLongColumn(name, store) else KLongColumn(name, store)
            }
            else -> {
                val store = mutableListOf<Any?>()
                iterator().forEach { store.add(f(it)) }
                KObjectColumn(name, store) as Column<R>
            }
        } as Column<R>
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

class KNullableDoubleColumn(override val name: String, private val data: DoubleArray): KAbstractColumn<Double?>() {
    override fun get(index: Int): Double? {
        return data[index].unpack(NA.double)
    }

    override val type: Type<Double?>
        get() = NullableDoubleType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<Double?> {
        return KNullableDoubleColumn(name, data)
    }

    override fun iterator(): Iterator<Double?> = IteratorWithUnpacking(data.iterator(), NA.double)
}

class KIntColumn(override val name: String, private val data: IntArray): KAbstractColumn<Int>() {
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

class KNullableIntColumn(override val name: String, private val data: IntArray): KAbstractColumn<Int?>() {
    override fun alias(name: String): KAbstractColumn<Int?> {
        return KNullableIntColumn(name, data)
    }

    override val size: Int
        get() = data.size

    override val type: Type<Int?>
        get() = NullableIntType

    override fun get(index: Int): Int? {
        return data[index].unpack(NA.int)
    }

    override fun iterator(): Iterator<Int?> {
        return IteratorWithUnpacking(data.iterator(), NA.int)
    }
}

class KLongColumn(override val name: String, private val data: LongArray): KAbstractColumn<Long>() {
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

class KNullableLongColumn(override val name: String, private val data: LongArray): KAbstractColumn<Long?>() {
    override fun alias(name: String): KAbstractColumn<Long?> {
        return KNullableLongColumn(name, data)
    }

    override val size: Int
        get() = data.size

    override val type: Type<Long?>
        get() = NullableLongType

    override fun get(index: Int): Long? {
        return data[index].unpack(NA.long)
    }

    override fun iterator(): Iterator<Long?> {
        return IteratorWithUnpacking(data.iterator(), NA.long)
    }
}

class KStringColumn(override val name: String, private val data: List<String>): KAbstractColumn<String>() {
    override fun get(index: Int): String {
        return data[index]
    }

    override fun iterator(): Iterator<String> {
        return data.iterator()
    }

    override val type: Type<String>
        get() = StringType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<String> {
        return KStringColumn(name, data)
    }
}

class KNullableStringColumn(override val name: String, private val data: List<String?>): KAbstractColumn<String?>() {
    override fun get(index: Int): String? {
        return data[index]
    }

    override fun iterator(): Iterator<String?> {
        return data.iterator()
    }

    override val type: Type<String?>
        get() = NullableStringType

    override val size: Int
        get() = data.size

    override fun alias(name: String): KAbstractColumn<String?> {
        return KNullableStringColumn(name, data)
    }
}

class KDataFrameInPlaceBuilder(private val header: List<String>): DataFrameInPlaceBuilder(header) {
    override fun build(data: List<Any?>): DataFrame {
        val cwData = columnWise(data)
        val cols = cwData.zip(header).map { (rawCol, name) ->
            val type = inferType(rawCol)
            when (type) {
                is IntType -> KIntColumn(name, rawCol.map { (it as Number).toInt() }.toIntArray())
                is NullableIntType -> KNullableIntColumn(name, rawCol.map { (it as Int?)?.toInt().pack(NA.int) }.toIntArray())
                is LongType -> KLongColumn(name, rawCol.map { (it as Number).toLong() }.toLongArray())
                is NullableLongType -> KNullableLongColumn(name, rawCol.map { (it as Number?)?.toLong().pack(NA.long) }.toLongArray())
                is DoubleType -> KDoubleColumn(name, rawCol.map { (it as Number).toDouble() }.toDoubleArray())
                is NullableDoubleType -> KNullableDoubleColumn(name, rawCol.map { (it as Number?)?.toDouble().pack(NA.double) }.toDoubleArray())
                is StringType -> KStringColumn(name, rawCol.map { it as String } )
                is NullableStringType -> KNullableStringColumn(name, rawCol.map { it as String? } )
                else -> throw IllegalArgumentException(type.toString())
            }
        }
        return KDataFrame { cols }
    }

    private fun columnWise(data: List<*>): List<List<*>> {
        val res = mutableListOf<MutableList<Any?>>()
        header.forEach { res.add(mutableListOf()) }
        var list = data
        while (list.isNotEmpty()) {
            val row = list.take(header.size)
            list = list.drop(header.size)
            row.zip(res).forEach { (value, column) ->
                column.add(value)
            }
        }
        return res
    }

    private fun inferType(column: List<Any?>): Type<*> {
        var nullable = false
        var type = ColumnTypes.INT
        for (d in column) {
            if (d == null) {
                nullable = true
            } else {
                val t = when (d) {
                    is Int -> ColumnTypes.INT
                    is Long -> ColumnTypes.LONG
                    is Double -> ColumnTypes.DOUBLE
                    is String -> ColumnTypes.STRING
                    else -> ColumnTypes.OBJECT
                }
                type = if (t > type) t else type
            }
        }
        return if (nullable) type.type.mkNullable() else type.type
    }

    private enum class ColumnTypes(val type: Type<*>): Comparable<ColumnTypes> {
        INT(IntType),
        LONG(LongType),
        DOUBLE(DoubleType),
        STRING(StringType),
        OBJECT(ObjectType)
    }
}

object NA {
    val double = Double.NaN
    const val int = Int.MIN_VALUE
    const val long = Long.MIN_VALUE
}

fun <T> T.unpack(na: T): T? {
    return if (this == na) null else this
}

fun <T> T?.pack(na: T): T {
    return this ?: na
}

class IteratorWithUnpacking<T>(val iterator: Iterator<T>, private val na: T): Iterator<T?> {
    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun next(): T? {
        return iterator.next().unpack(na)
    }
}
