package klearn

import kotlin.reflect.KClass

interface DataFrame {
    operator fun get(name: String): Column
    operator fun get(index: Int): Column
    operator fun <T> plus(column: TypedColumn<T>): DataFrame
    operator fun plus(other: DataFrame): DataFrame
    fun union(other: DataFrame): DataFrame
    fun select(vararg columns: String): DataFrame
    fun transform(f: (Row) -> Row): DataFrame
}

interface RowItem {
    fun <T: Any> typed(type: KClass<T>): T
    fun <T: Number> typed(type: KClass<T>): T
}

interface Row {
    operator fun get(name: String): RowItem
    operator fun get(index: Int): RowItem
}

interface Column {
    fun <T: Any> typed(type: KClass<T>): TypedColumn<T>
    fun <T: Number> typed(type: KClass<T>): NumericColumn<T>
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
