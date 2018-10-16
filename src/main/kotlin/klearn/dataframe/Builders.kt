package klearn.dataframe

import klearn.DataFrame
import klearn.Row

fun dataFrameOf(vararg header: String) = DataFrameInplaceBuilder(header.toList())

fun rowOf(vararg data: Any?): Row = TODO()

class DataFrameInplaceBuilder(private val header: List<String>) {
    operator fun invoke(vararg data: Any?): DataFrame {
        TODO()
    }
}

fun fromCSV(path: String, header: Boolean = true, sep: Char = ','): DataFrame = TODO()