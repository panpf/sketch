package com.github.panpf.sketch.http.ktor3.js.test

import com.github.panpf.sketch.http.KtorStack
import kotlin.test.Test
import kotlin.test.assertEquals

class EngineTest {

    @Test
    fun test() {
        val engine = KtorStack().client.engine
        assertEquals("class JsClientEngine", engine::class.toString())
    }
}