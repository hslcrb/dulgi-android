package com.dulgi.android

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.app.Activity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glView = GLSurfaceView(this)
        glView.setEGLContextClientVersion(2)
        glView.setRenderer(PigeonRenderer())
        setContentView(glView)
    }
}
