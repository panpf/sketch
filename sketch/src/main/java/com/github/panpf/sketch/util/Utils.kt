package com.github.panpf.sketch.util

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.ContextWrapper
import android.os.Looper
import android.os.Process
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.coroutines.resume


/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
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

internal fun Context?.getLifecycle(): Lifecycle? {
    var context: Context? = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            is ContextWrapper -> context = context.baseContext
            else -> return null
        }
    }
}

internal fun <T> Deferred<T>.getCompletedOrNull(): T? {
    return try {
        getCompleted()
    } catch (_: Throwable) {
        null
    }
}

/** Suspend until [Lifecycle.getCurrentState] is at least [Lifecycle.State.STARTED] */
@MainThread
internal suspend fun Lifecycle.awaitStarted() {
    // Fast path: we're already started.
    if (currentState.isAtLeast(Lifecycle.State.STARTED)) return

    // Slow path: observe the lifecycle until we're started.
    var observer: LifecycleObserver? = null
    try {
        suspendCancellableCoroutine<Unit> { continuation ->
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

internal fun Context.fileNameCompatibilityMultiProcess(file: File): File {
    val processName = getProcessName()
    val processNameSuffix = parseProcessNameSuffix(processName)
    return if (processNameSuffix != null) {
        File(file.parent, "${file.name}-$processNameSuffix")
    } else {
        file
    }
}

internal fun Context.getProcessName(): String {
    val pid = Process.myPid()
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE).asOrNull<ActivityManager>()
    val processInfo = activityManager?.runningAppProcesses?.find { it.pid == pid }
    return processInfo?.processName ?: return packageName
}

internal fun Context.parseProcessNameSuffix(processName: String): String? {
    val packageName = packageName
    return if (
        processName.length > packageName.length
        && processName.startsWith(packageName)
        && processName[packageName.length] == ':'
    ) {
        processName.substring(packageName.length + 1)
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
        @Suppress("CascadeIf")
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