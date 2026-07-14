package com.dulgi.android

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PigeonRenderer : GLSurfaceView.Renderer {

    private val pigeon = Pigeon()
    private val ground = Ground()

    private var program = 0
    private var uMVP = 0
    private var uModel = 0
    private var uLightDir = 0
    private var uLightColor = 0
    private var uAmbColor = 0

    private val viewMatrix = FloatArray(16)
    private val projMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)

    private val VERTEX_SHADER = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        attribute vec4 aPosition;
        attribute vec3 aNormal;
        attribute vec4 aColor;
        varying vec4 vColor;
        varying vec3 vNormal;
        varying vec3 vFragPos;
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vec4 wp = uModelMatrix * aPosition;
            vFragPos = wp.xyz;
            vNormal = normalize(mat3(uModelMatrix) * aNormal);
            vColor = aColor;
        }
    """.trimIndent()

    private val FRAGMENT_SHADER = """
        precision mediump float;
        uniform vec3 uLightDir;
        uniform vec4 uLightColor;
        uniform vec4 uAmbientColor;
        varying vec4 vColor;
        varying vec3 vNormal;
        varying vec3 vFragPos;
        void main() {
            vec3 normal = normalize(vNormal);
            vec3 lightDir = normalize(uLightDir);
            float diff = max(dot(normal, lightDir), 0.0);
            vec4 ambient = uAmbientColor * vColor;
            vec4 diffuse = uLightColor * vColor * diff;
            gl_FragColor = ambient + diffuse;
        }
    """.trimIndent()

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.65f, 0.78f, 0.95f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)

        program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        uMVP = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        uModel = GLES20.glGetUniformLocation(program, "uModelMatrix")
        uLightDir = GLES20.glGetUniformLocation(program, "uLightDir")
        uLightColor = GLES20.glGetUniformLocation(program, "uLightColor")
        uAmbColor = GLES20.glGetUniformLocation(program, "uAmbientColor")

        pigeon.create()
        ground.create()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projMatrix, 0, 45f, aspect, 0.1f, 100f)
        Matrix.setLookAtM(viewMatrix, 0,
            3.5f, 3.5f, 5f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )
        Matrix.multiplyMM(vpMatrix, 0, projMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        GLES20.glUniform3f(uLightDir, -0.5f, 1.0f, 0.5f)
        GLES20.glUniform4f(uLightColor, 1f, 1f, 1f, 1f)
        GLES20.glUniform4f(uAmbColor, 0.5f, 0.5f, 0.5f, 1f)

        ground.draw(vpMatrix, program, uMVP, uModel)
        pigeon.draw(vpMatrix, program, uMVP, uModel)
        pigeon.update(0.016f)
    }

    private fun createProgram(vSource: String, fSource: String): Int {
        val vShader = compileShader(GLES20.GL_VERTEX_SHADER, vSource)
        val fShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fSource)
        val prog = GLES20.glCreateProgram()
        GLES20.glAttachShader(prog, vShader)
        GLES20.glAttachShader(prog, fShader)
        GLES20.glLinkProgram(prog)
        return prog
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }
}
