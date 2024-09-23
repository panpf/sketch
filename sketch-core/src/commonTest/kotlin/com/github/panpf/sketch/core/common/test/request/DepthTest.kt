package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.request.Depth
import kotlin.test.Test
import kotlin.test.assertEquals

class DepthTest {

    @Test
    fun test() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "NETWORK, LOCAL, MEMORY",
            actual = Depth.values().joinToString()
        )
    }
}