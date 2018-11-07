package klearn

import klearn.linalg.Vector

abstract class DataFrame {
    abstract val dim: Dimension

    abstract operator fun get(name: String): Column<*>
    abstract operator fun get(index: Int): Column<*>
    abstract operator fun get(vararg columns: String): DataFrame
    abstract fun drop(name: String): DataFrame
    abstract fun <T> cbind(column: Column<T>): DataFrame
    abstract fun cbind(other: DataFrame): DataFrame
    abstract fun rbind(other: DataFrame): DataFrame
    abstract fun <T> filter(col: String, condition: (T) -> Boolean): DataFrame
    abstract fun <T1, T2> filter(col1: String, col2: String, condition: (T1, T2) -> Boolean): DataFrame
    abstract fun <T1, T2, T3> filter(col1: String, col2: String, col3: String, condition: (T1, T2, T3) -> Boolean): DataFrame

    abstract fun row(index: Int): Row

    @Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")
    inline operator fun <reified T> get(name: String, type: Type<T>): Column<T> {
        return get(name) as Column<T>
    }

    @Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")
    inline operator fun <reified T> get(index: Int, type: Type<T>): Column<T> {
        return get(index) as Column<T>
    }
}

data class Dimension(val rows: Int, val cols: Int)

interface Row {
    fun getDouble(name: String): Double? = getDouble(resolve(name))
    fun getInt(name: String): Int? = getInt(resolve(name))
    fun getLong(name: String): Long? = getLong(resolve(name))
    fun getString(name: String): String? = getString(resolve(name))
    fun getAny(name: String): Any? = getAny(resolve(name))

    fun getDouble(index: Int): Double?
    fun getInt(index: Int): Int?
    fun getLong(index: Int): Long?
    fun getString(index: Int): String?
    fun getAny(index: Int): Any?

    fun resolve(name: String): Int
}

interface Column<out T> {
    val size: Int
    val name: String
    val type: Type<T>

    operator fun get(index: Int): T
    fun <R> map(type: Type<R>, f: (T) -> R): Column<R>
    fun alias(name: String): Column<T>
//    fun toList(): List<T>
    fun iterator(): Iterator<T>
    fun <T> cast(): Column<T>
    fun asDataFrame(): DataFrame
}

fun Column<Double>.toVector(): Vector<Double> {
    return (this as DoubleColumn).toVector()
}

@Suppress("UNUSED")
sealed class Type<out T> {
    open fun mkNullable(): Type<T?> = this
    fun isNullable(): Boolean = mkNullable() == this
}

object IntType : Type<Int>() {
    override fun mkNullable(): Type<Int?> {
        return NullableIntType
    }
}

object LongType : Type<Long>() {
    override fun mkNullable(): Type<Long?> {
        return NullableLongType
    }
}

object DoubleType: Type<Double>() {
    override fun mkNullable(): Type<Double?> {
        return NullableDoubleType
    }
}

object StringType : Type<String>() {
    override fun mkNullable(): Type<String?> {
        return NullableStringType
    }
}

object ObjectType : Type<Any>() {
    override fun mkNullable(): Type<Any?> {
        return NullableObjectType
    }
}

object NullableIntType: Type<Int?>()
object NullableLongType: Type<Long?>()
object NullableDoubleType: Type<Double?>()
object NullableStringType: Type<String?>()
object NullableObjectType: Type<Any?>()


interface DoubleColumn: Column<Double> {
    fun toVector(): Vector<Double>
    operator fun <T: Number> plus(other: Column<T>): Column<Double>
}

interface IntColumn: Column<Int> {
    operator fun plus(other: Column<Long>): Column<Long>
}

fun DataFrame.split(fraction: Double): Pair<DataFrame, DataFrame> = TODO()

abstract class DataFrameInPlaceBuilder(private val header: List<String>) {
    abstract fun build(data: List<Any?>): DataFrame
    operator fun invoke(vararg data: Any?): DataFrame = build(data.toList())
}

fun dataFrameOf(vararg header: String) = Context.getContext().dataFrameOf(*header)