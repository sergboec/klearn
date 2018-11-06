package klearn.models

import junit.framework.TestCase.*
import klearn.*
import org.junit.Test

abstract class DataFrameTest {
    @Test
    fun testDataFrameOf() {
        val df = dataFrameOf("c1", "c2", "c3", "c4", "c5")(
                0, 1, 2, 3, "hello",
                1, null, null, 3.0, "world",
                2, 0, 0L, null, "test"
        )
        assertEquals(IntType, df["c1"].type)
        assertEquals(NullableIntType, df["c2"].type)
        assertEquals(NullableLongType, df["c3"].type)
        assertEquals(NullableDoubleType, df["c4"].type)
        assertEquals(StringType, df["c5"].type)
    }

    @Test
    fun testCBind() {
        val df = dataFrameOf("name", "birthYear")(
                "john", 1979,
                "jane", 1992
        )

        val yearNow = 2018
        val df1 = df.cbind(df["birthYear", IntType].map(IntType) { c: Int -> yearNow - c }.alias("age"))
        assertEquals(listOf(2018 - 1979, 2018 - 1992), df1["age", IntType].iterator().toList())
    }

    @Test
    fun testFilter() {
        val df = dataFrameOf("year", "name") (
                1979, "Vitaly",
                1980, "Nikita",
                1996, "Petya",
                1979, "Sasha"
        )
        val res = df.filter("year") { year: Int -> year >= 1980 }
        assertEquals(2, res.dim.rows)
        assertEquals(listOf(1980, 1996), res["year"].iterator().toList())
        assertEquals(listOf("Nikita", "Petya"), res["name"].iterator().toList())
    }

    @Test
    fun testNull() {
        val df = dataFrameOf("c1", "c2", "c3") (
                10, 1.0, 0L,
                null, 2.0, 1L,
                11, null, null
        )

        // row-wise check
        assertNull(df.row(1).getInt(0))
        assertNull(df.row(2).getDouble(1))
        assertNull(df.row(2).getLong(2))

        // column-wise check
        assertNull(df[0].cast<Int?>()[1])
    }

    @Test
    fun testTypeNullability() {
        assertTrue(NullableLongType.isNullable())
        assertFalse(LongType.isNullable())
    }

    private fun <T> Iterator<T>.toList(): List<T> {
        val res = mutableListOf<T>()
        while (hasNext()) {
            res.add(next())
        }
        return res
    }

    abstract fun dataFrameOf(vararg header: String): DataFrameInPlaceBuilder
}
