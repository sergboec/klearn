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
    operator fun <T> get(name: String, type: Type<T>): T
    operator fun <T> get(index: Int, type: Type<T>): T
    fun <T: Number> toVector(): Vector<T>
}

interface Column<out T> {
    val size: Int
    val name: String

    fun <R> map(type: Type<R>, f: (T) -> R): Column<R>
    fun alias(name: String): Column<T>
    fun toList(): List<T>
    fun <T> cast(): Column<T>
}

@Suppress("UNUSED")
sealed class Type<T>
object IntType : Type<Int>()
object LongType : Type<Long>()
object DoubleType: Type<Double>()
object StringType : Type<String>()
object ObjectType : Type<Any>()

interface DoubleColumn: Column<Double>
interface IntColumn: Column<Int>
interface LongColumn: Column<Long>

fun DataFrame.split(fraction: Double): Pair<DataFrame, DataFrame> = TODO()