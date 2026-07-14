package com.dulgi.android

import android.opengl.GLES20
import android.opengl.Matrix

class Ground {

    private lateinit var grid: Mesh
    private lateinit var plane: Mesh

    fun create() {
        val gC1 = floatArrayOf(0.82f, 0.90f, 0.78f, 1f)
        val gC2 = floatArrayOf(0.75f, 0.85f, 0.70f, 1f)
        grid = MeshBuilder.createGrid(8f, 8f, 16, gC1, gC2)
        plane = MeshBuilder.createBox(8f, 0.02f, 8f, floatArrayOf(0.78f, 0.87f, 0.74f, 1f))
    }

    fun draw(vpMatrix: FloatArray, program: Int, uMVP: Int, uModel: Int) {
        val model = FloatArray(16)
        val mvp = FloatArray(16)

        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, 0f, -0.45f, 0f)
        Matrix.multiplyMM(mvp, 0, vpMatrix, 0, model, 0)
        GLES20.glUniformMatrix4fv(uMVP, 1, false, mvp, 0)
        GLES20.glUniformMatrix4fv(uModel, 1, false, model, 0)
        grid.draw(program)
    }
}
