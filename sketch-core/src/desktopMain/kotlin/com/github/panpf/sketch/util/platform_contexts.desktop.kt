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

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import net.harawata.appdirs.AppDirsFactory
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

/**
 * Return the application's total memory in bytes.
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.PlatformContextsDesktopTest.testMaxMemory
 */
actual fun PlatformContext.maxMemory(): Long {
    return Runtime.getRuntime().maxMemory()
}

/**
 * Return the application's cache directory.
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.PlatformContextsDesktopTest.testAppCacheDirectory
 */
actual fun PlatformContext.appCacheDirectory(): Path? {
    val appFlag = (getComposeResourcesPath() ?: getJarPath(Sketch::class.java))
        ?.md5()
        ?: throw UnsupportedOperationException(
            "Unable to generate application aliases to automatically initialize downloadCache and resultCache, " +
                    "please configure them manually. Documentation address 'https://github.com/panpf/sketch/blob/main/docs/wiki/getting_started.md'"
        )
    val fakeAppName = "SketchImageLoader${File.separator}${appFlag}"
    val cacheDir = AppDirsFactory.getInstance()
        .getUserCacheDir(fakeAppName, /* appVersion = */ null,/* appAuthor = */ null)?.toPath()
    return requireNotNull(cacheDir) { "Failed to get the cache directory of the App" }
}

/**
 * Return the screen size.
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.PlatformContextsDesktopTest.testScreenSize
 */
actual fun PlatformContext.screenSize(): Size {
    return java.awt.Toolkit.getDefaultToolkit().screenSize
        .let { Size(it.width, it.height) }
}