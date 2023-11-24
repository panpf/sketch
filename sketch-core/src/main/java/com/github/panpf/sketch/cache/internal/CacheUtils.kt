/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.cache.internal

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import kotlin.math.min
import kotlin.math.roundToLong

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

internal fun Context.defaultMemoryCacheBytes(): Long {
    val appMemoryClassBytes = getAppMemoryClassBytes()
    val lowRamDevice = isLowRamDevice()
    val maxCacheBytes = ((if (lowRamDevice) 0.25f else 0.33f) * appMemoryClassBytes).roundToLong()
    val screenBytes = resources.displayMetrics.let { it.widthPixels * it.heightPixels * 4 }
    // Memory is expected to cache images for up to six screens
    val expectCacheBytes = (screenBytes * 6).toLong()
    return min(expectCacheBytes, maxCacheBytes)
}