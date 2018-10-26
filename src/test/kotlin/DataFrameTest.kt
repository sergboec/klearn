package klearn.models

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
        val df1 = df + df["birthYear"].cast<Int>().map { c: Int -> yearNow - c }.alias("age")
        Assert.assertEquals(listOf(2018 - 1979, 2018 - 1992), df1["age"].cast<Int>().toList())
    }

    @Test
    fun testSelectAndTransform() {
        val df = dataFrameOf("x", "y", "p")(
                10, 1, 0.1,
                20, 2, 0.2,
                30, 3, 0.3
        )

        df["y", "p"].transform {
            rowOf(it["y"].cast<Int>() * it["p"].cast<Double>())
        }
    }
}