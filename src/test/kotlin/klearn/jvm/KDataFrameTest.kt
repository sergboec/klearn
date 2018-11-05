package klearn.jvm

import klearn.DataFrameInPlaceBuilder
import klearn.models.DataFrameTest

/**
 * User: vitaly.khudobakhshov
 * Date: 2018-11-05
 */
class KDataFrameTest: DataFrameTest() {
    override fun dataFrameOf(vararg header: String): DataFrameInPlaceBuilder {
        return KDataFrameInPlaceBuilder(header.toList())
    }
}