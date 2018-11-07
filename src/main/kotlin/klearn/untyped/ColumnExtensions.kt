package klearn.untyped

import klearn.Column
import klearn.DoubleColumn
import klearn.IntColumn

/**
 * User: vitaly.khudobakhshov
 * Date: 2018-11-07
 */
operator fun Column<*>.plus(other: Column<*>): Column<*> {
    return when (this) {
        is DoubleColumn -> when (other) {
            is IntColumn -> this.plus(other)
            else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException()
    }
}