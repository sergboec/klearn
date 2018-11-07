package klearn.typed

import klearn.Column
import klearn.DoubleColumn

/**
 * User: vitaly.khudobakhshov
 * Date: 2018-11-07
 */
operator fun <T: Number> Column<Double>.plus(other: Column<T>): Column<Double> {
    return (this as DoubleColumn).plus(other)
}