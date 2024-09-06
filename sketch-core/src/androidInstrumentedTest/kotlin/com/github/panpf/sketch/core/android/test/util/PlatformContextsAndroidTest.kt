package com.github.panpf.sketch.core.android.test.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.maxMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformContextsAndroidTest {

    @Test
    fun testApplication() {
        // TODO test
    }

    @Test
    fun testMaxMemory() {
        val context = getTestContext()
        val memoryClassMegabytes = try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val isLargeHeap =
                (context.applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
            if (isLargeHeap) activityManager.largeMemoryClass else activityManager.memoryClass
        } catch (e: Throwable) {
            e.printStackTrace()
            128
        }
        val appMemoryClassBytes = memoryClassMegabytes * 1024L * 1024L
        assertEquals(appMemoryClassBytes, context.maxMemory())
    }

    @Test
    fun testAppCacheDirectory() {
        // TODO test
    }

    @Test
    fun testScreenSize() {
        // TODO test
    }
}