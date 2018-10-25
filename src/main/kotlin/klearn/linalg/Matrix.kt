package klearn.linalg

import klearn.Dimension

interface Matrix<T: Number> {
    val dim: Dimension
    val t: Matrix<T>
    operator fun plus(other: Matrix<T>): Matrix<T>
    operator fun times(other: Matrix<T>): Matrix<T>
    operator fun times(k: Double): Matrix<T>
    operator fun minus(other: Matrix<T>): Matrix<T>
    operator fun get(r: Int, c: Int): T
    operator fun set(r: Int, c: Int, value: T)
    fun col(index: Int): Vector<T>
    fun row(index: Int): Vector<T>
    fun almostTheSame(other: Matrix<T>, threshold: T): Boolean
}

interface Vector<T: Number>: Matrix<T> {
    operator fun get(index: Int): T
    operator fun set(index: Int, value: T)
    fun dot(other: Vector<T>): T
}

fun <T: Number> vectorOf(vararg x: T): Vector<T> {
    TODO()
}

fun <T: Number> zeros(n: Int): Vector<T> = TODO()


operator fun <T: Number> Double.times(matrix: Matrix<T>): Matrix<T> = matrix.times(this)

