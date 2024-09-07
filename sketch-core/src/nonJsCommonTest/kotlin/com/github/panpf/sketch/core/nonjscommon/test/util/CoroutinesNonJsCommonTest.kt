package com.github.panpf.sketch.core.nonjscommon.test.util

import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.test.Test
import kotlin.test.assertEquals

class CoroutinesNonJsCommonTest {

    @Test
    fun testIoCoroutineDispatcher() {
        assertEquals(
            expected = Dispatchers.IO,
            actual = ioCoroutineDispatcher()
        )
    }
}