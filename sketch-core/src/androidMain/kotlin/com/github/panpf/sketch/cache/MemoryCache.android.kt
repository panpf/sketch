/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.cache

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import com.github.panpf.sketch.PlatformContext
import kotlin.math.roundToLong


/**
 * Returns the default memory cache size
 *
 * @see com.github.panpf.sketch.core.android.test.cache.MemoryCacheAndroidTest.testDefaultMemoryCacheSize
 */
internal actual fun PlatformContext.defaultMemoryCacheSize(): Long {
    val standardMemoryPercent = 0.30
    val lowMemoryPercent = 0.20
    var memoryPercent: Double
    var memoryClassMegabytes: Int
    try {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val isLargeHeap =
            (applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
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
    return (maxMemory * memoryPercent).roundToLong()
}