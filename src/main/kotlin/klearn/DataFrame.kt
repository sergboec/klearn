package klearn

import klearn.linalg.Matrix
import klearn.linalg.Vector
import kotlin.reflect.KClass

interface DataFrame {
    val dim: Dimension
    operator fun get(name: String): Column
    operator fun get(index: Int): Column
    operator fun get(vararg columns: String): DataFrame
    operator fun <T> plus(column: TypedColumn<T>): DataFrame
    operator fun plus(other: DataFrame): DataFrame
    fun drop(name: String): DataFrame
    fun union(other: DataFrame): DataFrame
    fun transform(f: (Row) -> Row): DataFrame
    fun row(index: Int): Row
    fun <T: Number> asMatrix(): Matrix<T>
}

data class Dimension(val rows: Int, val cols: Int)

interface RowItem {
    fun <T: Any> typed(type: KClass<T>): T
    fun <T: Number> typed(type: KClass<T>): T
}

interface Row {
    operator fun get(name: String): RowItem
    operator fun get(index: Int): RowItem
    fun <T: Number> asVector(): Vector<T>
}

interface Column: DataFrame {
    fun <T: Any> typed(type: KClass<T>): TypedColumn<T>
    fun <T: Number> typed(type: KClass<T>): NumericColumn<T>
    fun <T: Number> asVector(): Vector<T>
}

interface TypedColumn<T> {
    fun <R> map(f: (T) -> R): TypedColumn<R>
    fun alias(name: String): TypedColumn<T>
    fun toList(): List<T>
}

interface NumericColumn<T: Number>: TypedColumn<T> {
    val min: T?
    val max: T?
    val avg: Double?
}

fun DataFrame.split(fraction: Double): Pair<DataFrame, DataFrame> = TODO()