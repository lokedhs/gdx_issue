package com.dhsdevelopments.gdxtest2

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.utils.Disposable

class ShaderTest : Disposable {

    val shaderProgram = makeShaderProgram()
    val mesh = makeMesh()

    private fun makeShaderProgram(): ShaderProgram {
        //language=GLSL
        val vertexShader = "attribute vec4 a_position;\nattribute vec2 a_texCoord;\nvarying vec2 v_texCoord;\n\nuniform mat4 u_projTrans;\n\nmat4 projectionx = mat4(1,0,0,0,\n                        0,1,0,0,\n                        0,0,1,0,\n                        0,0,0,1);\n\nvoid main()\n{\n    gl_Position = a_position * u_projTrans;\n    v_texCoord = a_texCoord;\n}\n"
        //language=GLSL
        val fragmentShader = "#version 120\n\n#ifdef GL_ES\nprecision mediump float;\n#endif\n\nvarying vec2 v_texCoord;\nuniform mat3 transform;\n\nvoid main()\n{\n    vec2 startPos = v_texCoord * mat2(transform);\n    vec2 pos = startPos;\n\n    int n = 0;\n    while(length(pos) < 2.0 && n++ < 50) {\n        pos.xy = vec2((pos.x * pos.x) - (pos.y * pos.y) + startPos.x,\n                      (2.0 * pos.x * pos.y) + startPos.y);\n    }\n    gl_FragColor = vec4(0, float(n) / 50.0, 0.5, 1);\n}\n"

        val p = ShaderProgram(vertexShader, fragmentShader)
        if (!p.log.isNullOrEmpty()) {
            println(p.log)
        }
        return p
    }

    private fun makeMesh(): Mesh {
        return Mesh(true, 4, 6,
                    VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                    VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord")).apply {
            setVertices(floatArrayOf(-0.5f, 0.5f, // Position 0
                                     -2.5f, -1.0f, // TexCoord 0

                                     -0.5f, -0.5f, // Position 1
                                     -2.5f, 1.0f, // TexCoord 1

                                     0.5f, -0.5f, // Position 2
                                     1.0f, 1.0f, // TexCoord 2

                                     0.5f, 0.5f, // Position 3
                                     1.0f, -1.0f // TexCoord 3
                                    ))
            setIndices(shortArrayOf(0, 1, 2, 0, 2, 3))
        }
    }

    val m = Matrix3()

    fun render(camera: Camera) {
        m.rotate(0.5f)

        shaderProgram.begin()
        shaderProgram.setUniformMatrix("u_projTrans", camera.combined)
        shaderProgram.setUniformMatrix("transform", m)
        mesh.render(shaderProgram, GL20.GL_TRIANGLES)
        shaderProgram.end()
    }

    override fun dispose() {
        disposeAll(shaderProgram, mesh)
    }
}
