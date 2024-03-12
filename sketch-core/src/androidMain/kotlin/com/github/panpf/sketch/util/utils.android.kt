package com.github.panpf.sketch.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Looper
import android.os.Process
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.resize.Scale
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt


internal actual fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

internal actual fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

internal actual fun requiredWorkThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This method must be executed in the work thread"
    }
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

internal val ScaleType.fitScale: Boolean
    get() = this == ScaleType.FIT_START
            || this == ScaleType.FIT_CENTER
            || this == ScaleType.FIT_END
            || this == ScaleType.CENTER_INSIDE

internal fun calculateBounds(srcSize: Size, dstSize: Size, scale: Scale): Rect {
    if (srcSize.isEmpty || dstSize.isEmpty) {
        return Rect(
            /* left = */ 0,
            /* top = */ 0,
            /* right = */ srcSize.width.takeIf { it > 0 } ?: dstSize.width,
            /* bottom = */ srcSize.height.takeIf { it > 0 } ?: dstSize.height
        )
    }

    val srcWidthScaleFactor = dstSize.width.toFloat() / srcSize.width
    val srcHeightScaleFactor = dstSize.height.toFloat() / srcSize.height
    val srcScaleFactor = max(srcWidthScaleFactor, srcHeightScaleFactor)
    val srcScaledWidth = (srcSize.width * srcScaleFactor).roundToInt()
    val srcScaledHeight = (srcSize.height * srcScaleFactor).roundToInt()
    return when (scale) {
        Scale.START_CROP -> {
            Rect(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ srcScaledWidth,
                /* bottom = */ srcScaledHeight
            )
        }

        Scale.CENTER_CROP -> {
            val left: Int = -(srcScaledWidth - dstSize.width) / 2
            val top: Int = -(srcScaledHeight - dstSize.height) / 2
            Rect(
                /* left = */ left,
                /* top = */ top,
                /* right = */ left + srcScaledWidth,
                /* bottom = */ top + srcScaledHeight,
            )
        }

        Scale.END_CROP -> {
            val left = -(srcScaledWidth - dstSize.width)
            val top = -(srcScaledHeight - dstSize.height)
            Rect(
                /* left = */ left,
                /* top = */ top,
                /* right = */ left + srcScaledWidth,
                /* bottom =*/ top + srcScaledHeight,
            )
        }

        Scale.FILL -> {
            Rect(
                /* left = */0,
                /* top = */0,
                /* right = */dstSize.width,
                /* bottom = */dstSize.height,
            )
        }
    }
}