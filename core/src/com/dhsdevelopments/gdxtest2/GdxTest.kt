package com.dhsdevelopments.gdxtest2

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController


class GdxTest : ApplicationAdapter() {

    private lateinit var camController: CameraInputController

    private lateinit var shaderTest: ShaderTest

    override fun create() {
        val cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
            position.set(10f, 10f, 10f)
            lookAt(0f, 0f, 0f)
            near = 1f
            far = 300f
            update()
        }

        camController = CameraInputController(cam)
        Gdx.input.inputProcessor = camController

        shaderTest = ShaderTest()
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        camController.update()

        shaderTest.render(camController.camera)
    }

    override fun dispose() {
        disposeAll(shaderTest)
    }
}
