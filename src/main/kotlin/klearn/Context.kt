package klearn

import klearn.jvm.KContext
import klearn.linalg.Vector

/**
 * User: vitaly.khudobakhshov
 * Date: 2018-10-31
 */
interface Context {
    fun zeros(n: Int): Vector<Double>
    fun vectorOf(list: List<Double>): Vector<Double>
    fun dataFrameOf(vararg header: String): DataFrameInPlaceBuilder

    companion object {
        fun getContext(): Context {
            return KContext
        }
    }
}