package klearn.linalg

import klearn.Dimension

interface Matrix<T> {
    val dim: Dimension
    val t: Matrix<T>
    operator fun plus(other: Matrix<T>): Matrix<T>
    operator fun times(other: Matrix<T>): Matrix<T>
    operator fun times(k: Double): Matrix<T>
    operator fun minus(other: Matrix<T>): Matrix<T>
    operator fun get(r: Int, c: Int): T
    operator fun set(r: Int, c: Int, value: T)
    fun col(index: Int, copy: Boolean = true, preserveOrientation: Boolean = true): Vector<T>
    fun row(index: Int, copy: Boolean = true, preserveOrientation: Boolean = true): Vector<T>
    fun almostTheSame(other: Matrix<T>, threshold: T): Boolean
}

interface Vector<T>: Matrix<T> {
    val size: Int
    operator fun get(index: Int): T
    operator fun set(index: Int, value: T)
    fun dot(other: Vector<T>): T
}

abstract class MatrixBuilder<T>(val rows: Int, val cols: Int) {
    abstract fun build(list: List<T>): Matrix<T>
    operator fun invoke(vararg elements: T): Matrix<T> = build(elements.toList())
}

fun <T: Number> zeros(n: Int): Vector<T> = TODO()


operator fun <T: Number> Double.times(matrix: Matrix<T>): Matrix<T> = matrix.times(this)

