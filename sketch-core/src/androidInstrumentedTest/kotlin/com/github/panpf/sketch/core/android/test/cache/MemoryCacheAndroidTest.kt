package com.github.panpf.sketch.core.android.test.cache

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.github.panpf.sketch.cache.defaultMemoryCacheSizePercent
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheAndroidTest {

    @Test
    fun testDefaultMemoryCacheSizePercent() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLowRamDevice =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
        assertEquals(
            expected = if (isLowRamDevice) 0.20 else 0.30,
            actual = context.defaultMemoryCacheSizePercent(),
            absoluteTolerance = 0.0
        )
    }
}