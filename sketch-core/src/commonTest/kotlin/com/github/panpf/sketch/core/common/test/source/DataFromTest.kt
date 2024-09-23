package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.source.DataFrom
import kotlin.test.Test
import kotlin.test.assertEquals

class DataFromTest {

    @Test
    fun test() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "NETWORK, DOWNLOAD_CACHE, LOCAL, RESULT_CACHE, MEMORY_CACHE, MEMORY",
            actual = DataFrom.values().joinToString()
        )
    }
}