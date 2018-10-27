package klearn.backend.jvm

import klearn.IntType
import org.junit.Assert.*
import org.junit.Test

class JvmDataFrameTest {
    @Test
    fun testEmptyCol() {
        val col = JvmIntColumn("test", IntArray(0))
        assertEquals(0, col.map(IntType) { _ -> 1 }.size)
    }
}