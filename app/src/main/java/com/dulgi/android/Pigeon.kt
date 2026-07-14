package com.dulgi.android

import android.opengl.GLES20
import android.opengl.Matrix
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Pigeon {

    private lateinit var body: Mesh
    private lateinit var head: Mesh
    private lateinit var beak: Mesh
    private lateinit var eyeL: Mesh
    private lateinit var eyeR: Mesh
    private lateinit var wingL: Mesh
    private lateinit var wingR: Mesh
    private lateinit var legL: Mesh
    private lateinit var legR: Mesh
    private lateinit var tail: Mesh

    var position = Vector3()
    var facingAngle = 0f

    private var animTime = 0f
    private var wingTimer = 0f
    private var targetAngle = 0f
    private var turnTimer = 2f
    private var idleTimer = 0f
    private var isIdle = false

    companion object {
        private val BODY_C = floatArrayOf(0.55f, 0.55f, 0.50f, 1f)
        private val HEAD_C = floatArrayOf(0.62f, 0.62f, 0.57f, 1f)
        private val BEAK_C = floatArrayOf(0.95f, 0.70f, 0.15f, 1f)
        private val EYE_C  = floatArrayOf(0.00f, 0.00f, 0.00f, 1f)
        private val WING_C = floatArrayOf(0.40f, 0.40f, 0.38f, 1f)
        private val LEG_C  = floatArrayOf(0.90f, 0.40f, 0.30f, 1f)
        private val TAIL_C = floatArrayOf(0.42f, 0.42f, 0.40f, 1f)
    }

    fun create() {
        body   = MeshBuilder.createEllipsoid(0.35f, 0.25f, 0.22f, 16, 16, BODY_C)
        head   = MeshBuilder.createSphere(0.18f, 12, 12, HEAD_C)
        beak   = MeshBuilder.createCone(0.04f, 0.1f, 6, BEAK_C)
        eyeL   = MeshBuilder.createSphere(0.022f, 6, 6, EYE_C)
        eyeR   = MeshBuilder.createSphere(0.022f, 6, 6, EYE_C)
        wingL  = MeshBuilder.createBox(0.22f, 0.015f, 0.08f, WING_C)
        wingR  = MeshBuilder.createBox(0.22f, 0.015f, 0.08f, WING_C)
        legL   = MeshBuilder.createBox(0.03f, 0.13f, 0.03f, LEG_C)
        legR   = MeshBuilder.createBox(0.03f, 0.13f, 0.03f, LEG_C)
        tail   = MeshBuilder.createCone(0.09f, 0.13f, 6, TAIL_C)
    }

    fun update(dt: Float) {
        if (isIdle) {
            idleTimer -= dt
            if (idleTimer <= 0f) {
                isIdle = false
                turnTimer = 1f
            }
            return
        }

        animTime += dt * 1.2f
        wingTimer += dt

        turnTimer -= dt
        if (turnTimer <= 0f) {
            if (Random.nextFloat() < 0.2f) {
                isIdle = true
                idleTimer = 0.5f + Random.nextFloat() * 1.5f
                return
            }
            targetAngle = Random.nextFloat() * 360f
            turnTimer = 2f + Random.nextFloat() * 4f
        }

        var diff = targetAngle - facingAngle
        while (diff > 180f) diff -= 360f
        while (diff < -180f) diff += 360f
        facingAngle += diff * dt * 2.5f

        val rad = Math.toRadians(facingAngle.toDouble()).toFloat()
        position = Vector3(
            x = position.x + sin(rad) * dt * 0.35f,
            y = position.y,
            z = position.z + cos(rad) * dt * 0.35f
        )

        val bound = 2.8f
        if (position.x < -bound) { position = position.copy(x = -bound); targetAngle = 180f - facingAngle }
        if (position.x > bound) { position = position.copy(x = bound); targetAngle = 180f - facingAngle }
        if (position.z < -bound) { position = position.copy(z = -bound); targetAngle = -facingAngle }
        if (position.z > bound) { position = position.copy(z = bound); targetAngle = -facingAngle }
    }

    fun draw(vpMatrix: FloatArray, program: Int, uMVP: Int, uModel: Int) {
        val bounce = sin(animTime * 4f) * 0.025f
        val legSwing = sin(animTime * 4f) * 25f
        val flapCycle = wingTimer % 6f
        val wingAngle = if (flapCycle < 1f) {
            sin(flapCycle / 1f * PI.toFloat()) * 45f
        } else {
            0f
        }

        val model = FloatArray(16)
        val mvp = FloatArray(16)

        fun drawPart(
            mesh: Mesh, tx: Float, ty: Float, tz: Float,
            rx: Float = 0f, ry: Float = 0f, rz: Float = 0f,
            sx: Float = 1f, sy: Float = 1f, sz: Float = 1f
        ) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, position.x + tx, position.y + ty, position.z + tz)
            Matrix.rotateM(model, 0, facingAngle + ry, 0f, 1f, 0f)
            if (rx != 0f) Matrix.rotateM(model, 0, rx, 1f, 0f, 0f)
            if (rz != 0f) Matrix.rotateM(model, 0, rz, 0f, 1f, 1f)
            Matrix.scaleM(model, 0, sx, sy, sz)
            Matrix.multiplyMM(mvp, 0, vpMatrix, 0, model, 0)
            GLES20.glUniformMatrix4fv(uMVP, 1, false, mvp, 0)
            GLES20.glUniformMatrix4fv(uModel, 1, false, model, 0)
            mesh.draw(program)
        }

        drawPart(body, 0f, bounce, 0f)

        drawPart(head, 0.35f, 0.18f + bounce, 0f)
        drawPart(beak, 0.54f, 0.22f + bounce, 0f)
        drawPart(eyeL, 0.38f, 0.25f + bounce, -0.10f)
        drawPart(eyeR, 0.38f, 0.25f + bounce, 0.10f)

        drawPart(wingL, 0f, 0.02f + bounce, -0.22f, rx = wingAngle)
        drawPart(wingR, 0f, 0.02f + bounce, 0.22f, rx = -wingAngle)

        drawPart(legL, -0.05f, -0.25f, -0.07f, rz = legSwing)
        drawPart(legR, -0.05f, -0.25f, 0.07f, rz = -legSwing)

        drawPart(tail, -0.30f, -0.05f + bounce, 0f)
    }
}
