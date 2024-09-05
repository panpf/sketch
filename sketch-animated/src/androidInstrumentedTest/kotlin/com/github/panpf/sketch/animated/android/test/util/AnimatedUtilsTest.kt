package com.github.panpf.sketch.animated.android.test.util

import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test

class AnimatedUtilsTest {

    @Test
    fun testRequiredMainThread() = runTest {
        assertThrow(IllegalStateException::class) {
            requiredMainThread()
        }
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
    }
}