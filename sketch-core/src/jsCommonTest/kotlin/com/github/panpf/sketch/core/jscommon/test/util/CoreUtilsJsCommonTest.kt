package com.github.panpf.sketch.core.jscommon.test.util

import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertTrue

class CoreUtilsJsCommonTest {

    @Test
    fun testIsMainThread() = runTest {
        withContext(Dispatchers.Default) {
            assertTrue(isMainThread())
        }
        withContext(Dispatchers.Main) {
            assertTrue(isMainThread())
        }
    }

    @Test
    fun testRequiredMainThread() = runTest {
        withContext(Dispatchers.Default) {
            requiredMainThread()
        }
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testRequiredWorkThread() = runTest {
        withContext(Dispatchers.Default) {
            requiredWorkThread()
        }
        withContext(Dispatchers.Main) {
            requiredWorkThread()
        }
    }
}