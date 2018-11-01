package klearn

import klearn.linalg.Vector

abstract class DataFrame {
    abstract val dim: Dimension

    abstract operator fun get(name: String): Column<*>
    abstract operator fun get(index: Int): Column<*>
    abstract operator fun get(vararg columns: String): DataFrame
    abstract operator fun <T> plus(column: Column<T>): DataFrame
    abstract operator fun plus(other: DataFrame): DataFrame
    abstract fun drop(name: String): DataFrame
    abstract fun union(other: DataFrame): DataFrame
    abstract fun transform(f: (Row) -> Row): DataFrame
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
    fun getDouble(name: String): Double = getDouble(resolve(name))
    fun getInt(name: String): Int = getInt(resolve(name))
    fun getLong(name: String): Long = getLong(resolve(name))
    fun getString(name: String): String = getString(resolve(name))
    fun getAny(name: String): Any = getAny(resolve(name))

    fun getDouble(index: Int): Double
    fun getInt(index: Int): Int
    fun getLong(index: Int): Long
    fun getString(index: Int): String
    fun getAny(index: Int): Any

    fun resolve(name: String): Int
}

interface Column<T> {
    val size: Int
    val name: String

    fun <R> map(type: Type<R>, f: (T) -> R): Column<R>
    fun alias(name: String): Column<T>
    fun toList(): List<T>
    fun <T> cast(): Column<T>
    fun asDataFrame(): DataFrame
}

fun Column<Double>.toVector(): Vector<Double> {
    return (this as DoubleColumn).toVector()
}

@Suppress("UNUSED")
sealed class Type<T>
object IntType : Type<Int>()
object LongType : Type<Long>()
object DoubleType: Type<Double>()
object StringType : Type<String>()
object ObjectType : Type<Any>()

interface DoubleColumn: Column<Double> {
    fun toVector(): Vector<Double>
}

interface IntColumn: Column<Int>
interface LongColumn: Column<Long>

fun DataFrame.split(fraction: Double): Pair<DataFrame, DataFrame> = TODO()