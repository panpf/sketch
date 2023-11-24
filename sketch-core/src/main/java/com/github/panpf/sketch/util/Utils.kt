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

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Build
import android.os.Looper
import android.os.Process
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.coroutines.resume


inline fun <R> ifOrNull(value: Boolean, block: () -> R?): R? = if (value) block() else null

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Convert to the type specified by the generic
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

internal fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

internal fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

internal fun requiredWorkThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This method must be executed in the work thread"
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Deferred<T>.getCompletedOrNull(): T? {
    return try {
        getCompleted()
    } catch (_: Throwable) {
        null
    }
}

/** Suspend until [Lifecycle.currentState] is at least [Lifecycle.State.STARTED] */
@MainThread
internal suspend fun Lifecycle.awaitStarted() {
    // Fast path: we're already started.
    if (currentState.isAtLeast(Lifecycle.State.STARTED)) return

    // Slow path: observe the lifecycle until we're started.
    var observer: LifecycleObserver? = null
    try {
        suspendCancellableCoroutine { continuation ->
            observer = object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    continuation.resume(Unit)
                }
            }
            addObserver(observer!!)
        }
    } finally {
        // 'observer' will always be null if this method is marked as 'inline'.
        observer?.let(::removeObserver)
    }
}

/** Remove and re-add the observer to ensure all its lifecycle callbacks are invoked. */
@MainThread
internal fun Lifecycle.removeAndAddObserver(observer: LifecycleObserver) {
    removeObserver(observer)
    addObserver(observer)
}

internal fun getTrimLevelName(level: Int): String = when (level) {
    ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "COMPLETE"
    ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "MODERATE"
    ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "BACKGROUND"
    ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI_HIDDEN"
    ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "RUNNING_CRITICAL"
    ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "RUNNING_LOW"
    ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "RUNNING_MODERATE"
    else -> "UNKNOWN"
}

internal fun Any.toHexString(): String = Integer.toHexString(this.hashCode())

internal fun fileNameCompatibilityMultiProcess(context: Context, file: File): File {
    val processNameSuffix = getProcessNameSuffix(context)
    return if (processNameSuffix != null) {
        File(file.parent, "${file.name}-$processNameSuffix")
    } else {
        file
    }
}

// The getRunningAppProcesses() method is a privacy method and cannot be called before agreeing to the privacy agreement,
// so the process name can only be obtained in this way
@SuppressLint("PrivateApi")
internal fun getProcessNameCompat(context: Context): String? {
    if (Build.VERSION.SDK_INT >= 28) {
        return Application.getProcessName()
    }

    if (Build.VERSION.SDK_INT >= 18) {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val method = activityThreadClass.getMethod("currentProcessName").apply {
                isAccessible = true
            }
            val processName = method.invoke(null)?.toString()
            if (!processName.isNullOrEmpty()) {
                return processName
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    val myPid = Process.myPid()
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val processInfoList = activityManager.runningAppProcesses
    if (processInfoList != null) {
        for (runningAppProcessInfo in processInfoList) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.processName
            }
        }
    }

    return null
}

internal fun getProcessNameSuffix(context: Context, processName: String? = null): String? {
    val packageName = context.packageName
    val finalProcessName = processName ?: getProcessNameCompat(context) ?: return null
    return if (
        finalProcessName.length > packageName.length
        && finalProcessName.startsWith(packageName)
        && finalProcessName[packageName.length] == ':'
    ) {
        finalProcessName.substring(packageName.length + 1)
    } else {
        null
    }
}

internal fun Float.format(newScale: Int): Float =
    BigDecimal(toDouble()).setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()

/**
 * Returns the formatted file length that can be displayed, up to EB
 *
 * @receiver              File size
 * @param decimalPlacesLength   Keep a few decimal places
 * @param decimalPlacesFillZero Use 0 instead when the number of decimal places is insufficient
 * @param compact               If true, returns 150KB, otherwise returns 150 KB
 * @return For example: 300 B, 150.25 KB, 500.46 MB, 300 GB
 */
internal fun Long.formatFileSize(
    decimalPlacesLength: Int = 2,
    decimalPlacesFillZero: Boolean = false,
    compact: Boolean = true,
): String {
    // Multiplied by 999 to avoid 1000 KB, 1000 MB
    // Why is appendSuffix required to be true when calling the format method, because DecimalFormat encounters '#.##EB' and throws an exception 'IllegalArgumentException: Malformed exponential pattern "#.##EB"'
    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
    val finalFileSize = Math.max(this, 0)
    return if (finalFileSize <= 999) {
        finalFileSize.toString() + if (compact) "B" else " B"
    } else {
        val value: Double
        val suffix: String
        if (finalFileSize <= 1024L * 999) {
            value = (finalFileSize / 1024f).toDouble()
            suffix = if (compact) "KB" else " KB"
        } else if (finalFileSize <= 1024L * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f).toDouble()
            suffix = if (compact) "MB" else " MB"
        } else if (finalFileSize <= 1024L * 1024 * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "GB" else " GB"
        } else if (finalFileSize <= 1024L * 1024 * 1024 * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "TB" else " TB"
        } else if (finalFileSize <= 1024L * 1024 * 1024 * 1024 * 1024 * 999) {
            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "PB" else " PB"
        } else {
            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f / 1024f / 1024f).toDouble()
            suffix = if (compact) "EB" else " EB"
        }
        val buffString = StringBuilder()
        buffString.append("#")
        if (decimalPlacesLength > 0) {
            buffString.append(".")
            for (w in 0 until decimalPlacesLength) {
                buffString.append(if (decimalPlacesFillZero) "0" else "#")
            }
        }
        val format = DecimalFormat(buffString.toString())
        format.roundingMode = RoundingMode.HALF_UP
        format.format(value).toString() + suffix
    }
}

internal fun Int.formatFileSize(): String = toLong().formatFileSize()

/**
 * Modified from [MimeTypeMap.getFileExtensionFromUrl] to be more permissive
 * with special characters.
 */
internal fun getMimeTypeFromUrl(url: String?): String? =
    MimeTypeMap.getSingleton().getMimeTypeFromUrl(url)

/**
 * Modified from [MimeTypeMap.getFileExtensionFromUrl] to be more permissive
 * with special characters.
 */
internal fun MimeTypeMap.getMimeTypeFromUrl(url: String?): String? {
    if (url.isNullOrBlank()) {
        return null
    }

    val extension = url
        .substringBeforeLast('#') // Strip the fragment.
        .substringBeforeLast('?') // Strip the query.
        .substringAfterLast('/') // Get the last path segment.
        .substringAfterLast('.', missingDelimiterValue = "") // Get the file extension.

    return getMimeTypeFromExtension(extension)
}

internal val ImageView.fitScale: Boolean
    get() = when (scaleType) {
        ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER_INSIDE -> true
        else -> false
    }

internal fun intMerged(highInt: Int, lowInt: Int): Int {
    require(highInt in 0.rangeTo(Short.MAX_VALUE)) {
        "The value range for 'highInt' is 0 to ${Short.MAX_VALUE}"
    }
    require(lowInt in 0.rangeTo(Short.MAX_VALUE)) {
        "The value range for 'lowInt' is 0 to ${Short.MAX_VALUE}"
    }
    val high2 = highInt shl 16
    val low2 = (lowInt shl 16) shr 16
    return high2 or low2
}

internal fun intSplit(value: Int): Pair<Int, Int> {
    return (value shr 16) to ((value shl 16) shr 16)
}

@WorkerThread
@Throws(IOException::class)
internal fun getCacheFileFromStreamDataSource(
    sketch: Sketch,
    request: ImageRequest,
    streamDataSource: BasedStreamDataSource,
): File = runBlocking {
    val resultCache = sketch.resultCache
    val resultCacheKey = request.uriString + "_data_source"
    resultCache.editLock(resultCacheKey).withLock {
        val snapshot = resultCache[resultCacheKey]
        if (snapshot != null) {
            snapshot
        } else {
            val editor = resultCache.edit(resultCacheKey)
                ?: throw IOException("Disk cache cannot be used")
            try {
                streamDataSource.newInputStream().use { inputStream ->
                    editor.newOutputStream().buffered().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                editor.commit()
            } catch (e: Throwable) {
                editor.abort()
                throw e
            }
            resultCache[resultCacheKey]
                ?: throw IOException("Disk cache cannot be used after edit")
        }
    }.file
}