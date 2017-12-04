package com.dhsdevelopments.gdxtest2.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.dhsdevelopments.gdxtest2.GdxTest

fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration()
    LwjglApplication(GdxTest(), config)
}
