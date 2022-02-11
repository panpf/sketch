package com.github.panpf.sketch.cache.internal

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import kotlin.math.roundToInt

internal fun Context.getAppMemoryClassBytes(): Int {
    val activityManager =
        getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    val isLargeHeap =
        (applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
    val memoryClass = when {
        activityManager != null && isLargeHeap -> activityManager.largeMemoryClass
        activityManager != null && !isLargeHeap -> activityManager.memoryClass
        else -> 16
    }
    return memoryClass * 1024 * 1024
}

internal fun Context.isLowRamDevice(): Boolean {
    val activityManager =
        getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    return VERSION.SDK_INT < VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
}

internal fun Context.defaultMemoryCacheBytes(): Int {
    val maxCacheBytes =
        ((if (isLowRamDevice()) 0.25f else 0.33f) * getAppMemoryClassBytes()).roundToInt()
    val displayMetrics = resources.displayMetrics
    val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
    // Memory is expected to cache images for up to six screens
    val expectCacheBytes = screenBytes * 6
    return expectCacheBytes.coerceAtMost(maxCacheBytes)
}