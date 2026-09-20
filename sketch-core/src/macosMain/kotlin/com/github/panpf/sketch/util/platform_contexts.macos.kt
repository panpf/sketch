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
import platform.Foundation.NSProcessInfo
import kotlin.math.roundToInt

/**
 * Return the machine's physical memory in bytes.
 *
 * @see com.github.panpf.sketch.core.macos.test.util.PlatformContextsMacosTest.testMaxMemory
 */
actual fun PlatformContext.maxMemory(): Long {
    return runCatching { NSProcessInfo.processInfo.physicalMemory.toLong() }
        .getOrDefault(0L)
        .takeIf { it > 0L }
        ?: (2L * 1024 * 1024 * 1024)
}

/**
 * Return the application's user cache directory.
 *
 * @see com.github.panpf.sketch.core.macos.test.util.PlatformContextsMacosTest.testAppCacheDirectory
 */
actual fun PlatformContext.appCacheDirectory(): Path? {
    // bundleIdentifier: Debug: ''
    // bundleIdentifier: Release: 'com.github.panpf.sketch.sample'
    // bundlePath: Debug: '/Users/panpf/Workspace/sketch/samples/macosApp/build/bin/macosArm64/debugExecutable'
    // bundlePath: Release: '/Users/panpf/Downloads/Sketch Sample.app'
    val appId = NSBundle.mainBundle.bundleIdentifier?.takeIf { it.isNotEmpty() }
        ?: NSBundle.mainBundle.bundlePath.takeIf { it.isNotEmpty() }
            ?.substringBefore("/build/")
            ?.md5()
            ?.let { "SketchImageLoader/${it}" }
        ?: return null
    return AppDirs.getCacheDir(appId).toPath()
}

/**
 * Return the main screen's pixel dimensions.
 *
 * @see com.github.panpf.sketch.core.macos.test.util.PlatformContextsMacosTest.testScreenSize
 */
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
