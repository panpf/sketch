package com.github.panpf.sketch.http.ktor3.android.test

import com.github.panpf.sketch.http.KtorStack
import kotlin.test.Test
import kotlin.test.assertEquals

class EngineTest {

    @Test
    fun test() {
        val engine = KtorStack().client.engine
        assertEquals(
            "class io.ktor.client.engine.android.AndroidClientEngine",
            engine::class.toString()
        )
    }
}