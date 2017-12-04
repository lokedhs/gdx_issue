package com.dhsdevelopments.gdxtest2

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram


class GdxTestOld : ApplicationAdapter() {

    private lateinit var cam: PerspectiveCamera
    private lateinit var box: Model
    private lateinit var modelBatch: ModelBatch
    private lateinit var env: Environment
    private lateinit var camController: CameraInputController
    private lateinit var assetManager: AssetManager

    private var loading = true
    val instances = com.badlogic.gdx.utils.Array<ModelInstance>()

    override fun create() {
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(10f, 10f, 10f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()

        env = Environment()
        env.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        env.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        modelBatch = ModelBatch()

        val modelBuilder = ModelBuilder()
        box = modelBuilder.createBox(5f, 5f, 5f,
                                     Material(ColorAttribute.createDiffuse(Color.GREEN)),
                                     (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        instances.add(ModelInstance(box))

        camController = CameraInputController(cam)
        Gdx.input.inputProcessor = camController

        initAssets()
        initShaders()
    }

    private lateinit var shader: ShaderProgram
    private lateinit var mesh: Mesh

    private fun initShaders() {
        //language=GLSL
        val vertexShader = "attribute vec4 a_position;\nattribute vec2 a_texCoord;\nvarying vec2 v_texCoord;\n\nvoid main()\n{\n    gl_Position = a_position;\n    v_texCoord = a_texCoord;\n}\n"
        //language=GLSL
        val fragmentShader = "#ifdef GL_ES\nprecision mediump float;\n#endif\n\nvarying vec2 v_texCoord;\nuniform sampler2D s_texture0;\nuniform sampler2D s_texture1;\n\nvoid main()\n{\n  gl_FragColor = texture2D(s_texture0, v_texCoord ) * texture2D(s_texture1, v_texCoord);\n}\n"

        shader = ShaderProgram(vertexShader, fragmentShader)
        mesh = Mesh(true, 4, 6,
                    VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                    VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord")).apply {
            setVertices(floatArrayOf(-0.5f, 0.5f, // Position 0
                                     0.0f, 0.0f, // TexCoord 0
                                     -0.5f, -0.5f, // Position 1
                                     0.0f, 1.0f, // TexCoord 1
                                     0.5f, -0.5f, // Position 2
                                     1.0f, 1.0f, // TexCoord 2
                                     0.5f, 0.5f, // Position 3
                                     1.0f, 0.0f // TexCoord 3
                                    ))
            setIndices(shortArrayOf(0, 1, 2, 0, 2, 3))
        }

        createTextures()
    }

    private lateinit var texture0: Texture
    private lateinit var texture1: Texture

    private fun createTextures() {
        Pixmap(256, 256, Pixmap.Format.RGBA8888).apply {
            setColor(1f, 1f, 1f, 1f)
            fill()
            setColor(0f, 0f, 0f, 1f)
            drawLine(0, 0, 256, 256)
            drawLine(256, 0, 0, 256)
        }.use {
            texture0 = Texture(it)
        }

        Pixmap(256, 256, Pixmap.Format.RGBA8888).apply {
            setColor(1f, 1f, 1f, 1f)
            fill()
            setColor(0f, 0f, 0f, 1f)
            drawLine(128, 0, 128, 256)
        }.use {
            texture1 = Texture(it)
        }
    }

    private fun initAssets() {
        assetManager = AssetManager()
        assetManager.load("foo.g3db", Model::class.java)
    }

    override fun render() {
        if (loading && assetManager.update()) {
            assetsLoaded()
            loading = false
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        camController.update()

        modelBatch.begin(cam)
        modelBatch.render(instances, env)
        modelBatch.end()

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        texture0.bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        texture1.bind()

        if(true) {
            shader.begin()
            shader.setUniformi("s_texture0", 0)
            shader.setUniformi("s_texture1", 1)
            mesh.render(shader, GL20.GL_TRIANGLES)
            shader.end()
        }
    }

    private fun assetsLoaded() {
        val obj = assetManager.get("foo.g3db", Model::class.java)
        instances.add(ModelInstance(obj))
        println("Assets loaded")
    }

    override fun dispose() {
        box.dispose()
        assetManager.dispose()
    }
}
