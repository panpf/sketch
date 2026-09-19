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

@file:JvmName("Platform_contexts_desktopKt")  // For binary compatibility
package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File

/**
 * Return the application's total memory in bytes.
 *
 * @see com.github.panpf.sketch.core.jvm.test.util.PlatformContextsJvmTest.testMaxMemory
 */
actual fun PlatformContext.maxMemory(): Long {
    return Runtime.getRuntime().maxMemory()
}

/**
 * Return the application's cache directory.
 *
 * @see com.github.panpf.sketch.core.jvm.test.util.PlatformContextsJvmTest.testAppCacheDirectory
 */
actual fun PlatformContext.appCacheDirectory(): Path? {
    val appId = getJarPath(PlatformContext::class.java)
        ?.substringBefore("${File.separator}build${File.separator}")    // Debug
        ?.substringBefore("${File.separator}app${File.separator}")  // Release
        ?.md5()
        ?.let { "SketchImageLoader${File.separator}${it}" }
        ?: return null
    return AppDirs.getCacheDir(appId).toOkioPath()
}

/**
 * Return the screen size.
 *
 * @see com.github.panpf.sketch.core.jvm.test.util.PlatformContextsJvmTest.testScreenSize
 */
actual fun PlatformContext.screenSize(): Size {
    return java.awt.Toolkit.getDefaultToolkit().screenSize
        .let { Size(it.width, it.height) }
}