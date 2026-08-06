package com.github.panpf.sketch.core.jscommon.test.util

import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.platformIsMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.util.setMainThreadChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoreUtilsJsCommonTest {

    @Test
    fun testIsMainThread() = runTest {
        withContext(Dispatchers.Main) {
            assertTrue(isMainThread())
        }
        withContext(Dispatchers.Default) {
            assertTrue(isMainThread())

            setMainThreadChecker { false }
            try {
                assertFalse(isMainThread())
            } finally {
                setMainThreadChecker(null)
            }

            assertTrue(isMainThread())
        }
    }

    @Test
    fun testPlatformIsMainThread() = runTest {
        withContext(Dispatchers.Main) {
            assertTrue(platformIsMainThread())
        }
        withContext(Dispatchers.Default) {
            assertTrue(platformIsMainThread())

            setMainThreadChecker { false }
            try {
                assertTrue(platformIsMainThread())
            } finally {
                setMainThreadChecker(null)
            }

            assertTrue(platformIsMainThread())
        }
    }

    @Test
    fun testRequiredMainThread() = runTest {
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
        withContext(Dispatchers.Default) {
            requiredMainThread()

            setMainThreadChecker { false }
            try {
                requiredMainThread()
            } finally {
                setMainThreadChecker(null)
            }

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

            setMainThreadChecker { true }
            try {
                requiredWorkThread()
            } finally {
                setMainThreadChecker(null)
            }

            requiredWorkThread()
        }
    }
}