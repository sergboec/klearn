package klearn

import klearn.backend.jvm.JvmContext
import klearn.linalg.Vector

/**
 * User: vitaly.khudobakhshov
 * Date: 2018-10-31
 */
interface Context {
    fun zeros(n: Int): Vector<Double>
    fun vectorOf(list: List<Double>): Vector<Double>

    companion object {
        fun getContext(): Context {
            return JvmContext
        }
    }
}