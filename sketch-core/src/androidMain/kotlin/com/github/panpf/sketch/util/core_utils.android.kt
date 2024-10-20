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

package com.github.panpf.sketch.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Build
import android.os.Looper
import android.os.Process
import java.io.File

/**
 * Returns true if currently on the main thread
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testIsMainThread
 */
internal actual fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()

/**
 * Throws an exception if not currently on the main thread
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testRequiredMainThread
 */
internal actual fun requiredMainThread() {
    check(isMainThread()) {
        "This method must be executed in the UI thread"
    }
}

/**
 * Throws an exception if not currently on the work thread
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testRequiredWorkThread
 */
actual fun requiredWorkThread() {
    check(!isMainThread()) {
        "This method must be executed in the work thread"
    }
}

/**
 * Get memory trim level name
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testGetTrimLevelName
 */
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

/**
 * Get the suffix of the current process name
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testFileNameCompatibilityMultiProcess
 */
internal fun fileNameCompatibilityMultiProcess(context: Context, file: File): File {
    val processNameSuffix = getProcessNameSuffix(context)
    return if (processNameSuffix != null) {
        File(file.parent, "${file.name}-$processNameSuffix")
    } else {
        file
    }
}

/**
 * Get the current process name
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testGetProcessNameCompat
 */
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

/**
 * Get the suffix of the current process name
 *
 * @see com.github.panpf.sketch.core.android.test.util.CoreUtilsAndroidTest.testGetProcessNameSuffix
 */
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