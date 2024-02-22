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
package com.github.panpf.sketch.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.PlatformContext

private const val STANDARD_MEMORY_MULTIPLIER = 0.33
private const val LOW_MEMORY_MULTIPLIER = 0.25

/**
 * Return the default percent of the application's total memory to use for the memory cache.
 */
internal actual fun PlatformContext.defaultMemoryCacheSizePercent(): Double {
    return try {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val isLowRamDevice =
            VERSION.SDK_INT < VERSION_CODES.KITKAT || activityManager.isLowRamDevice
        if (isLowRamDevice) LOW_MEMORY_MULTIPLIER else STANDARD_MEMORY_MULTIPLIER
    } catch (_: Exception) {
        STANDARD_MEMORY_MULTIPLIER
    }
}

private const val DEFAULT_MEMORY_CLASS_MEGABYTES = 128

/**
 * Return the application's total memory in bytes.
 */
internal actual fun PlatformContext.totalAvailableMemoryBytes(): Long {
    val memoryClassMegabytes = try {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val isLargeHeap =
            (applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
        if (isLargeHeap) activityManager.largeMemoryClass else activityManager.memoryClass
    } catch (e: Throwable) {
        e.printStackTrace()
        DEFAULT_MEMORY_CLASS_MEGABYTES
    }
    return memoryClassMegabytes * 1024L * 1024L
}