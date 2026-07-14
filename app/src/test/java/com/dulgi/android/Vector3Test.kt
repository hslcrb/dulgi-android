package com.dulgi.android

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class Vector3Test {

    @Test
    fun testAddition() {
        val a = Vector3(1f, 2f, 3f)
        val b = Vector3(4f, 5f, 6f)
        val c = a + b
        assertEquals(5f, c.x)
        assertEquals(7f, c.y)
        assertEquals(9f, c.z)
    }

    @Test
    fun testSubtraction() {
        val a = Vector3(4f, 5f, 6f)
        val b = Vector3(1f, 2f, 3f)
        val c = a - b
        assertEquals(3f, c.x)
        assertEquals(3f, c.y)
        assertEquals(3f, c.z)
    }

    @Test
    fun testScalarMultiply() {
        val a = Vector3(1f, 2f, 3f)
        val b = a * 2f
        assertEquals(2f, b.x)
        assertEquals(4f, b.y)
        assertEquals(6f, b.z)
    }

    @Test
    fun testScalarDivide() {
        val a = Vector3(2f, 4f, 6f)
        val b = a / 2f
        assertEquals(1f, b.x)
        assertEquals(2f, b.y)
        assertEquals(3f, b.z)
    }

    @Test
    fun testLength() {
        val a = Vector3(3f, 4f, 0f)
        assertEquals(5f, a.len(), 0.0001f)
    }

    @Test
    fun testNormalize() {
        val a = Vector3(3f, 4f, 0f)
        val n = a.norm()
        assertEquals(0.6f, n.x, 0.0001f)
        assertEquals(0.8f, n.y, 0.0001f)
        assertEquals(0f, n.z, 0.0001f)
    }

    @Test
    fun testDot() {
        val a = Vector3(1f, 0f, 0f)
        val b = Vector3(0f, 1f, 0f)
        assertEquals(0f, a.dot(b), 0.0001f)

        val c = Vector3(1f, 2f, 3f)
        val d = Vector3(4f, 5f, 6f)
        assertEquals(32f, c.dot(d), 0.0001f)
    }

    @Test
    fun testCross() {
        val a = Vector3(1f, 0f, 0f)
        val b = Vector3(0f, 1f, 0f)
        val c = a.cross(b)
        assertEquals(0f, c.x, 0.0001f)
        assertEquals(0f, c.y, 0.0001f)
        assertEquals(1f, c.z, 0.0001f)
    }

    @Test
    fun testZeroVector() {
        val z = Vector3()
        assertEquals(0f, z.x)
        assertEquals(0f, z.y)
        assertEquals(0f, z.z)
        assertEquals(0f, z.len(), 0.0001f)
    }

    @Test
    fun testCopy() {
        val a = Vector3(1f, 2f, 3f)
        val b = a.copy(x = 10f)
        assertEquals(10f, b.x)
        assertEquals(2f, b.y)
        assertEquals(3f, b.z)
    }
}
