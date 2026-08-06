package com.github.panpf.sketch.core.apple.test.util

import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.platformIsMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.util.setMainThreadChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoreUtilsAppleTest {

    @Test
    fun testIsMainThread() = runTest {
        withContext(Dispatchers.Main) {
            assertTrue(isMainThread())
        }
        withContext(Dispatchers.IO) {
            assertFalse(isMainThread())

            setMainThreadChecker { true }
            try {
                assertTrue(isMainThread())
            } finally {
                setMainThreadChecker(null)
            }

            assertFalse(isMainThread())
        }
    }

    @Test
    fun testPlatformIsMainThread() = runTest {
        withContext(Dispatchers.Main) {
            assertTrue(platformIsMainThread())
        }
        withContext(Dispatchers.IO) {
            assertFalse(platformIsMainThread())

            setMainThreadChecker { true }
            try {
                assertFalse(platformIsMainThread())
            } finally {
                setMainThreadChecker(null)
            }

            assertFalse(platformIsMainThread())
        }
    }

    @Test
    fun testRequiredMainThread() = runTest {
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
        withContext(Dispatchers.IO) {
            assertFailsWith(IllegalStateException::class) {
                requiredMainThread()
            }

            setMainThreadChecker { true }
            try {
                requiredMainThread()
            } finally {
                setMainThreadChecker(null)
            }

            assertFailsWith(IllegalStateException::class) {
                requiredMainThread()
            }
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

            setMainThreadChecker { false }
            try {
                requiredWorkThread()
            } finally {
                setMainThreadChecker(null)
            }

            assertFailsWith(IllegalStateException::class) {
                requiredWorkThread()
            }
        }
    }
}
