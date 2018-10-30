package klearn.linalg

import klearn.Dimension
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

    @Test
    fun testDot() {
        val u = vectorOf(0.0, 1.0, 2.0)
        assertEquals(5.0, u.dot(u), eps)
    }

    @Test
    fun testTimes() {
        val a = matrixOf(2, 3) (
                0.0, 1.0, 2.0,
                3.0, 4.0, 5.0
        )
        val c = a * a.t

        assertEquals(a.row(0).dot(a.row(0)), c[0, 0], eps)
        assertEquals(a.row(0).dot(a.row(1)), c[0, 1], eps)
        assertEquals(a.row(1).dot(a.row(0)), c[1, 0], eps)
        assertEquals(a.row(1).dot(a.row(1)), c[1, 1], eps)
    }

    @Test
    fun testRow() {
        val a = matrixOf(2, 3) (
                0.0, 1.0, 2.0,
                3.0, 4.0, 5.0
        )
        val r = a.row(0)
        r[0] = 10.0

        assertEquals(10.0, r[0], eps)
        assertEquals(1.0, r[1], eps)
        assertEquals(2.0, r[2], eps)

        assertEquals(0.0, a[0, 0], eps)

        val v = a.row(1, copy = false)
        v[0] = 10.0
        v[1] = 11.0
        assertEquals(10.0, a[1, 0], eps)
        assertEquals(10.0, v[0], eps)

        assertEquals(11.0, a[1, 1], eps)
        assertEquals(11.0, v[1], eps)
    }

    @Test
    fun testCol() {
        val a = matrixOf(2, 3) (
                0.0, 1.0, 2.0,
                3.0, 4.0, 5.0
        )
        val r = a.col(0)
        r[0] = 10.0

        assertEquals(Dimension(2, 1), r.dim)

        assertEquals(10.0, r[0], eps)
        assertEquals(3.0, r[1], eps)

        assertEquals(0.0, a[0, 0], eps)

        val v = a.col(1, copy = false)
        v[0] = 10.0
        v[1] = 11.0

        assertEquals(10.0, a[0, 1], eps)
        assertEquals(10.0, v[0], eps)

        assertEquals(11.0, a[1, 1], eps)
        assertEquals(11.0, v[1], eps)
    }


    abstract fun matrixOf(rows: Int, cols: Int): MatrixBuilder<Double>
    abstract fun vectorOf(vararg x: Double): Vector<Double>
}
