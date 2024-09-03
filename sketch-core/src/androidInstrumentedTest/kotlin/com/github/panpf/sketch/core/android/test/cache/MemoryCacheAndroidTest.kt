package com.github.panpf.sketch.core.android.test.cache

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.github.panpf.sketch.cache.platformDefaultMemoryCacheSizePercent
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheAndroidTest {

    @Test
    fun testPlatformDefaultMemoryCacheSizePercent() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLowRamDevice =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
        assertEquals(
            if (isLowRamDevice) 0.25 else 0.33,
            context.platformDefaultMemoryCacheSizePercent(),
            0.0
        )
    }
}