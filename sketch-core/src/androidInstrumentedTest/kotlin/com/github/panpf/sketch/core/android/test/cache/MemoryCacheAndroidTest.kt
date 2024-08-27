package com.github.panpf.sketch.core.android.test.cache

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.github.panpf.sketch.cache.platformDefaultMemoryCacheSizePercent
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test

class MemoryCacheAndroidTest {
    // TODO test Builder

    @Test
    fun testPlatformDefaultMemoryCacheSizePercent() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLowRamDevice =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
        Assert.assertEquals(
            /* expected = */ if (isLowRamDevice) 0.25 else 0.33,
            /* actual = */ context.platformDefaultMemoryCacheSizePercent(),
            /* delta = */ 0.0
        )
    }
}