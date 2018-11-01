package klearn.models

import klearn.DoubleType
import klearn.IntType
import klearn.backend.jvm.dataFrameOf
import klearn.backend.jvm.rowOf
import org.junit.Assert
import org.junit.Test

class DataFrameTest {
    @Test
    fun testDataFrameOf() {
        val df = dataFrameOf("name", "birthYear")(
                "john", 1979,
                "jane", 1992
        )

        val yearNow = 2018
        val df1 = df + df["birthYear", IntType].map(IntType) { c: Int -> yearNow - c }.alias("age")
        Assert.assertEquals(listOf(2018 - 1979, 2018 - 1992), df1["age", IntType].toList())
    }
}