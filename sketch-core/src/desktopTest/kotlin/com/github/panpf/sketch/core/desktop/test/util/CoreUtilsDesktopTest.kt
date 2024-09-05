package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoreUtilsDesktopTest {

    @Test
    fun testIsMainThread() = runTest {
        withContext(Dispatchers.IO) {
            assertFalse(isMainThread())
        }
        withContext(Dispatchers.Main) {
            assertTrue(isMainThread())
        }
    }

    @Test
    fun testRequiredMainThread() = runTest {
        withContext(Dispatchers.IO) {
            assertFailsWith(IllegalStateException::class) {
                requiredMainThread()
            }
        }
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testRequiredWorkThread() = runTest {
        withContext(Dispatchers.IO) {
            requiredWorkThread()
        }
        withContext(Dispatchers.Main) {
            assertFailsWith(IllegalStateException::class) {
                requiredWorkThread()
            }
        }
    }
}