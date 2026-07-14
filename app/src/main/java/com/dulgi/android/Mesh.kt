package com.dulgi.android

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Mesh(
    val vertices: FloatArray,
    val indices: ShortArray
) {
    companion object {
        const val STRIDE = 10
        const val POS_OFF = 0
        const val NRM_OFF = 3
        const val CLR_OFF = 6
        const val SZ = 4
    }

    private val vBuf: FloatBuffer
    private val iBuf: ShortBuffer

    init {
        vBuf = ByteBuffer.allocateDirect(vertices.size * SZ)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vBuf.put(vertices).position(0)

        iBuf = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        iBuf.put(indices).position(0)
    }

    fun draw(program: Int) {
        val aPos = GLES20.glGetAttribLocation(program, "aPosition")
        val aNrm = GLES20.glGetAttribLocation(program, "aNormal")
        val aClr = GLES20.glGetAttribLocation(program, "aColor")

        val stride = STRIDE * SZ

        vBuf.position(POS_OFF)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, stride, vBuf)
        GLES20.glEnableVertexAttribArray(aPos)

        vBuf.position(NRM_OFF)
        GLES20.glVertexAttribPointer(aNrm, 3, GLES20.GL_FLOAT, false, stride, vBuf)
        GLES20.glEnableVertexAttribArray(aNrm)

        vBuf.position(CLR_OFF)
        GLES20.glVertexAttribPointer(aClr, 4, GLES20.GL_FLOAT, false, stride, vBuf)
        GLES20.glEnableVertexAttribArray(aClr)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, iBuf)

        GLES20.glDisableVertexAttribArray(aPos)
        GLES20.glDisableVertexAttribArray(aNrm)
        GLES20.glDisableVertexAttribArray(aClr)
    }
}
