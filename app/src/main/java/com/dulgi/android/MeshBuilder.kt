package com.dulgi.android

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MeshBuilder {

    private fun V(c: FloatArray) = c.toList()

    fun createSphere(r: Float, lat: Int, lon: Int, c: FloatArray): Mesh {
        val verts = mutableListOf<Float>()
        val idxs = mutableListOf<Short>()
        for (l in 0..lat) {
            val theta = PI * l / lat
            val st = sin(theta).toFloat()
            val ct = cos(theta).toFloat()
            for (n in 0..lon) {
                val phi = 2.0 * PI * n / lon
                val sp = sin(phi).toFloat()
                val cp = cos(phi).toFloat()
                val nx = cp * st; val ny = ct; val nz = sp * st
                verts += nx * r; verts += ny * r; verts += nz * r
                verts += nx; verts += ny; verts += nz
                verts += V(c)
            }
        }
        for (l in 0 until lat) {
            for (n in 0 until lon) {
                val a = (l * (lon + 1) + n).toShort()
                val b = (a + lon + 1).toShort()
                idxs += a; idxs += b; idxs += (a + 1).toShort()
                idxs += b; idxs += (b + 1).toShort(); idxs += (a + 1).toShort()
            }
        }
        return Mesh(verts.toFloatArray(), idxs.toShortArray())
    }

    fun createEllipsoid(rx: Float, ry: Float, rz: Float, lat: Int, lon: Int, c: FloatArray): Mesh {
        val verts = mutableListOf<Float>()
        val idxs = mutableListOf<Short>()
        for (l in 0..lat) {
            val theta = PI * l / lat
            val st = sin(theta).toFloat()
            val ct = cos(theta).toFloat()
            for (n in 0..lon) {
                val phi = 2.0 * PI * n / lon
                val sp = sin(phi).toFloat()
                val cp = cos(phi).toFloat()
                val nx = cp * st; val ny = ct; val nz = sp * st
                val px = nx / (rx * rx); val py = ny / (ry * ry); val pz = nz / (rz * rz)
                val len = sqrt(px * px + py * py + pz * pz)
                val nnx = px / len; val nny = py / len; val nnz = pz / len
                verts += nx * rx; verts += ny * ry; verts += nz * rz
                verts += nnx; verts += nny; verts += nnz
                verts += V(c)
            }
        }
        for (l in 0 until lat) {
            for (n in 0 until lon) {
                val a = (l * (lon + 1) + n).toShort()
                val b = (a + lon + 1).toShort()
                idxs += a; idxs += b; idxs += (a + 1).toShort()
                idxs += b; idxs += (b + 1).toShort(); idxs += (a + 1).toShort()
            }
        }
        return Mesh(verts.toFloatArray(), idxs.toShortArray())
    }

    fun createCone(br: Float, h: Float, segs: Int, c: FloatArray): Mesh {
        val verts = mutableListOf<Float>()
        val idxs = mutableListOf<Short>()
        val hh = h / 2

        verts += 0f; verts += hh; verts += 0f; verts += 0f; verts += 1f; verts += 0f; verts += V(c)
        verts += 0f; verts += -hh; verts += 0f; verts += 0f; verts += -1f; verts += 0f; verts += V(c)

        for (i in 0..segs) {
            val a = 2.0 * PI * i / segs
            val ca = cos(a).toFloat()
            val sa = sin(a).toFloat()
            verts += ca * br; verts += -hh; verts += sa * br
            verts += ca; verts += 0.3f; verts += sa
            verts += V(c)
        }

        for (i in 1 until segs) {
            idxs += 1; idxs += (i + 2).toShort(); idxs += (i + 1).toShort()
        }
        idxs += 1; idxs += 2; idxs += (segs + 1).toShort()

        for (i in 0 until segs) {
            idxs += 0; idxs += (i + 2).toShort(); idxs += (i + 3).toShort()
        }
        idxs += 0; idxs += (segs + 1).toShort(); idxs += 2

        return Mesh(verts.toFloatArray(), idxs.toShortArray())
    }

    fun createBox(w: Float, h: Float, d: Float, c: FloatArray): Mesh {
        val hw = w / 2; val hh = h / 2; val hd = d / 2
        val verts = floatArrayOf(
            -hw, -hh, -hd,  0f, 0f, -1f,  c[0],c[1],c[2],c[3],
             hw, -hh, -hd,  0f, 0f, -1f,  c[0],c[1],c[2],c[3],
             hw,  hh, -hd,  0f, 0f, -1f,  c[0],c[1],c[2],c[3],
            -hw,  hh, -hd,  0f, 0f, -1f,  c[0],c[1],c[2],c[3],

            -hw, -hh,  hd,  0f, 0f,  1f,  c[0],c[1],c[2],c[3],
             hw, -hh,  hd,  0f, 0f,  1f,  c[0],c[1],c[2],c[3],
             hw,  hh,  hd,  0f, 0f,  1f,  c[0],c[1],c[2],c[3],
            -hw,  hh,  hd,  0f, 0f,  1f,  c[0],c[1],c[2],c[3],

            -hw, -hh, -hd,  0f, -1f, 0f,  c[0],c[1],c[2],c[3],
             hw, -hh, -hd,  0f, -1f, 0f,  c[0],c[1],c[2],c[3],
             hw, -hh,  hd,  0f, -1f, 0f,  c[0],c[1],c[2],c[3],
            -hw, -hh,  hd,  0f, -1f, 0f,  c[0],c[1],c[2],c[3],

            -hw,  hh, -hd,  0f,  1f, 0f,  c[0],c[1],c[2],c[3],
             hw,  hh, -hd,  0f,  1f, 0f,  c[0],c[1],c[2],c[3],
             hw,  hh,  hd,  0f,  1f, 0f,  c[0],c[1],c[2],c[3],
            -hw,  hh,  hd,  0f,  1f, 0f,  c[0],c[1],c[2],c[3],

            -hw, -hh, -hd, -1f, 0f, 0f,  c[0],c[1],c[2],c[3],
            -hw, -hh,  hd, -1f, 0f, 0f,  c[0],c[1],c[2],c[3],
            -hw,  hh,  hd, -1f, 0f, 0f,  c[0],c[1],c[2],c[3],
            -hw,  hh, -hd, -1f, 0f, 0f,  c[0],c[1],c[2],c[3],

             hw, -hh, -hd,  1f, 0f, 0f,  c[0],c[1],c[2],c[3],
             hw, -hh,  hd,  1f, 0f, 0f,  c[0],c[1],c[2],c[3],
             hw,  hh,  hd,  1f, 0f, 0f,  c[0],c[1],c[2],c[3],
             hw,  hh, -hd,  1f, 0f, 0f,  c[0],c[1],c[2],c[3],
        )
        val idxs = shortArrayOf(
            0,1,2, 0,2,3,
            4,5,6, 4,6,7,
            8,9,10, 8,10,11,
            12,13,14, 12,14,15,
            16,17,18, 16,18,19,
            20,21,22, 20,22,23
        )
        return Mesh(verts, idxs)
    }

    fun createGrid(w: Float, d: Float, divs: Int, c1: FloatArray, c2: FloatArray): Mesh {
        val verts = mutableListOf<Float>()
        val idxs = mutableListOf<Short>()
        val hw = w / 2; val hd = d / 2
        var idx: Short = 0

        for (z in 0..divs) {
            for (x in 0..divs) {
                val px = -hw + x * (w / divs)
                val pz = -hd + z * (d / divs)
                val c = if (((x + z) % 2) == 0) c1 else c2
                verts += px; verts += 0f; verts += pz
                verts += 0f; verts += 1f; verts += 0f
                verts += V(c)
                idx++
            }
        }

        for (z in 0 until divs) {
            for (x in 0 until divs) {
                val a = (z * (divs + 1) + x).toShort()
                val b = ((z + 1) * (divs + 1) + x).toShort()
                idxs += a; idxs += b; idxs += (a + 1).toShort()
                idxs += b; idxs += (b + 1).toShort(); idxs += (a + 1).toShort()
            }
        }

        return Mesh(verts.toFloatArray(), idxs.toShortArray())
    }
}
