package klearn

import klearn.linalg.Vector

interface DataFrame {
    val dim: Dimension
    operator fun get(name: String): Column<*>
    operator fun get(index: Int): Column<*>
    operator fun get(vararg columns: String): DataFrame
    operator fun <T> plus(column: Column<T>): DataFrame
    operator fun plus(other: DataFrame): DataFrame
    fun drop(name: String): DataFrame
    fun union(other: DataFrame): DataFrame
    fun transform(f: (Row) -> Row): DataFrame
    fun row(index: Int): Row
}

data class Dimension(val rows: Int, val cols: Int)

interface RowItem {
    fun <T> cast(): T
}

interface Row {
    operator fun get(name: String): RowItem
    operator fun get(index: Int): RowItem
    fun <T: Number> toVector(): Vector<T>
}

interface Column<out T> {
    val size: Int
    val name: String

    fun <R> map(f: (T) -> R): Column<R>
    fun alias(name: String): Column<T>
    fun toList(): List<T>
    fun <T> cast(): Column<T>
}

fun DataFrame.split(fraction: Double): Pair<DataFrame, DataFrame> = TODO()