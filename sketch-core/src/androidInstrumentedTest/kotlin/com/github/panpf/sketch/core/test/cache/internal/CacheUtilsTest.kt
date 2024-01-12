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
package com.github.panpf.sketch.core.test.cache.internal

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.defaultMemoryCacheBytes
import com.github.panpf.sketch.cache.internal.getAppMemoryClassBytes
import com.github.panpf.sketch.cache.internal.isLowRamDevice
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToLong

@RunWith(AndroidJUnit4::class)
class CacheUtilsTest {

    @Test
    fun testGetAppMemoryClassBytes() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLargeHeap =
            (context.applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
        val appMemoryClassBytes = when {
            activityManager != null && isLargeHeap -> activityManager.largeMemoryClass * 1024 * 1024
            activityManager != null && !isLargeHeap -> activityManager.memoryClass * 1024 * 1024
            else -> 16 * 1024 * 1024
        }
        Assert.assertEquals(appMemoryClassBytes, context.getAppMemoryClassBytes())
    }

    @Test
    fun testIsLowMemoryDevice() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLowRamDevice =
            VERSION.SDK_INT < VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
        Assert.assertEquals(isLowRamDevice, context.isLowRamDevice())
    }

    @Test
    fun testDefaultMemoryCacheBytes() {
        val context = getTestContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val isLowRamDevice =
            VERSION.SDK_INT < VERSION_CODES.KITKAT || activityManager?.isLowRamDevice == true
        val isLargeHeap =
            (context.applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP) != 0
        val appMemoryClassBytes = when {
            activityManager != null && isLargeHeap -> activityManager.largeMemoryClass * 1024 * 1024
            activityManager != null && !isLargeHeap -> activityManager.memoryClass * 1024 * 1024
            else -> 16 * 1024 * 1024
        }
        val maxCacheBytes =
            ((if (isLowRamDevice) 0.25f else 0.33f) * appMemoryClassBytes).roundToLong()
        val displayMetrics = context.resources.displayMetrics
        val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
        // Memory is expected to cache images for up to six screens
        val expectCacheBytes = (screenBytes * 6).toLong()
        val defaultMemoryCacheBytes = expectCacheBytes.coerceAtMost(maxCacheBytes)
        Assert.assertEquals(defaultMemoryCacheBytes, context.defaultMemoryCacheBytes())
    }
}