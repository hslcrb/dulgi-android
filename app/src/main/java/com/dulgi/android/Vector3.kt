package com.dulgi.android

import kotlin.math.sqrt

data class Vector3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {
    operator fun plus(o: Vector3) = Vector3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vector3) = Vector3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vector3(x * s, y * s, z * s)
    operator fun div(s: Float) = Vector3(x / s, y / s, z / s)
    fun len() = sqrt(x * x + y * y + z * z)
    fun norm() = if (len() > 0f) this / len() else Vector3()
    fun dot(o: Vector3) = x * o.x + y * o.y + z * o.z
    fun cross(o: Vector3) = Vector3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )
}
