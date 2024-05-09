package com.github.panpf.sketch.core.android.test.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.defaultMemoryCacheSizePercent
import com.github.panpf.sketch.util.totalAvailableMemoryBytes
import org.junit.Assert
import org.junit.Test

class PlatformContextsAndroidTest {

    @Test
    fun testDefaultMemoryCacheSizePercent() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLowRamDevice =
            VERSION.SDK_INT < VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
        Assert.assertEquals(
            /* expected = */ if (isLowRamDevice) 0.25 else 0.33,
            /* actual = */ context.defaultMemoryCacheSizePercent(),
            /* delta = */ 0.0
        )
    }

    @Test
    fun testTotalAvailableMemoryBytes() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLargeHeap =
            (context.applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
        val appMemoryClassBytes = when {
            activityManager != null && isLargeHeap -> activityManager.largeMemoryClass * 1024L * 1024L
            activityManager != null && !isLargeHeap -> activityManager.memoryClass * 1024L * 1024L
            else -> 16 * 1024L * 1024L
        }
        Assert.assertEquals(appMemoryClassBytes, context.totalAvailableMemoryBytes())
    }
}