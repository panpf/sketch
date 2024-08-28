package com.github.panpf.sketch.cache

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.github.panpf.sketch.PlatformContext

private const val STANDARD_MEMORY_MULTIPLIER = 0.33
private const val LOW_MEMORY_MULTIPLIER = 0.25


/**
 * Return the default percent of the application's total memory to use for the memory cache.
 *
 * @see com.github.panpf.sketch.core.android.test.cache.MemoryCacheAndroidTest.testPlatformDefaultMemoryCacheSizePercent
 */
internal actual fun PlatformContext.platformDefaultMemoryCacheSizePercent(): Double {
    return try {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val isLowRamDevice =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || activityManager.isLowRamDevice
        if (isLowRamDevice) LOW_MEMORY_MULTIPLIER else STANDARD_MEMORY_MULTIPLIER
    } catch (_: Exception) {
        STANDARD_MEMORY_MULTIPLIER
    }
}