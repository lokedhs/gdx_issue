package com.dhsdevelopments.gdxtest2

import com.badlogic.gdx.utils.Disposable

infix fun <T : Disposable> T.use(fn: (T) -> Unit) {
    try {
        fn(this)
    }
    finally {
        dispose()
    }
}

fun disposeAll(vararg elements: Disposable) {
    elements.forEach(Disposable::dispose)
}
