package klearn.linalg

import org.junit.Assert.assertEquals
import org.junit.Test

abstract class MatrixTest {
    val eps = 1e-6

    @Test
    fun testMatrixOf() {
        val m = matrixOf(2, 3) (
                0.0, 1.0, 2.0,
                3.0, 4.0, 5.0
        )

        assertEquals(2, m.dim.rows)
        assertEquals(3, m.dim.cols)

        assertEquals(0.0, m[0, 0], eps)
        assertEquals(1.0, m[0, 1], eps)
        assertEquals(2.0, m[0, 2], eps)

        assertEquals(3.0, m[1, 0], eps)
        assertEquals(4.0, m[1, 1], eps)
        assertEquals(5.0, m[1, 2], eps)
    }

    @Test
    fun testPlusMinus() {
        val a = matrixOf(2, 3) (
                0.0, 1.0, 2.0,
                3.0, 4.0, 5.0
        )
        val b = matrixOf(2, 3) (
                0.1, 0.2, 0.3,
                0.4, 0.5, 0.6
        )
        val c = a + b
        val d = a - b

        for (i in 0 until c.dim.rows) {
            for (j in 0 until c.dim.cols) {
                assertEquals(c[i, j], a[i, j] + b[i, j], eps)
                assertEquals(d[i, j], a[i, j] - b[i, j], eps)
            }
        }
    }

    @Test
    fun testTranspose() {
        val a = matrixOf(2, 3) (
                0.0, 1.0, 2.0,
                3.0, 4.0, 5.0
        )
        val b = matrixOf(3, 2) (
                0.0, 3.0,
                1.0, 4.0,
                2.0, 5.0
        )
        val at = a.t

        for (i in 0 until at.dim.rows) {
            for (j in 0 until at.dim.cols) {
                assertEquals(b[i, j], at[i, j], eps)
            }
        }
    }

    abstract fun matrixOf(rows: Int, cols: Int): MatrixBuilder<Double>
}
