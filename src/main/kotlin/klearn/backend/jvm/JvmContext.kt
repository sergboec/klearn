package klearn.backend.jvm

import klearn.Context
import klearn.linalg.Vector

/**
 * User: vitaly.khudobakhshov
 * Date: 2018-10-31
 */
object JvmContext: Context {
    override fun vectorOf(list: List<Double>): Vector<Double> {
        val arr = DoubleArray(list.size)
        var i = 0
        list.forEach { arr[i++] = it }
        return DoubleVector(list.size, isColumnVector = false, data = arr)
    }

    override fun zeros(n: Int): Vector<Double> {
        return DoubleVector(n, true)
    }
}