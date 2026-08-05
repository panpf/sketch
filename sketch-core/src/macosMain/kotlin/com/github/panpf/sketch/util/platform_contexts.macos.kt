/*
 * Copyright (C) 2026 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import okio.Path
import okio.Path.Companion.toPath
import platform.AppKit.NSScreen
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import kotlin.math.roundToInt

/** Return the machine's physical memory in bytes. */
actual fun PlatformContext.maxMemory(): Long {
    return runCatching { NSProcessInfo.processInfo.physicalMemory.toLong() }
        .getOrDefault(0L)
        .takeIf { it > 0L }
        ?: (2L * 1024 * 1024 * 1024)
}

/** Return the application's user cache directory. */
actual fun PlatformContext.appCacheDirectory(): Path? {
    val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
    val cachesDirectory = (paths.firstOrNull() as? String)?.toPath() ?: return null
    val applicationId = NSBundle.mainBundle.bundleIdentifier
        ?.takeIf { it.isNotBlank() }
        ?: NSProcessInfo.processInfo.processName.takeIf { it.isNotBlank() }
        ?: "com.github.panpf.sketch"
    return cachesDirectory.resolve(applicationId)
}

/** Return the main screen's pixel dimensions. */
@OptIn(ExperimentalForeignApi::class)
actual fun PlatformContext.screenSize(): Size {
    val screen = NSScreen.mainScreen ?: return Size(1920, 1080)
    val scale = screen.backingScaleFactor
    val width = screen.frame.useContents { size.width }
    val height = screen.frame.useContents { size.height }
    return Size(
        width = (width * scale).roundToInt(),
        height = (height * scale).roundToInt(),
    ).takeIf { it.isNotEmpty } ?: Size(1920, 1080)
}
