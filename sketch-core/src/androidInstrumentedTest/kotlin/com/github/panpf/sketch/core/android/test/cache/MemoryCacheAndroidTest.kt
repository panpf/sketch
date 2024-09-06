package com.github.panpf.sketch.core.android.test.cache

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import com.github.panpf.sketch.cache.defaultMemoryCacheSize
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheAndroidTest {

    @Test
    fun testDefaultMemoryCacheSize() {
        val context = getTestContext()
        val standardMemoryPercent = 0.30
        val lowMemoryPercent = 0.20
        var memoryPercent: Double
        var memoryClassMegabytes: Int
        try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val isLargeHeap =
                (context.applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
            memoryClassMegabytes =
                if (isLargeHeap) activityManager.largeMemoryClass else activityManager.memoryClass
            memoryPercent =
                if (activityManager.isLowRamDevice) lowMemoryPercent else standardMemoryPercent
        } catch (e: Throwable) {
            e.printStackTrace()
            memoryClassMegabytes = 128
            memoryPercent = standardMemoryPercent
        }
        val maxMemory = memoryClassMegabytes * 1024L * 1024L
        val memoryCacheSize = (maxMemory * memoryPercent).roundToLong()

        assertEquals(
            expected = memoryCacheSize,
            actual = context.defaultMemoryCacheSize(),
        )
    }
}