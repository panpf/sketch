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
import okio.Path

/**
 * Return the application's total memory in bytes.
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.PlatformContextsJsCommonTest.testMaxMemory
 */
actual fun PlatformContext.maxMemory(): Long {
    // TODO Get the accurate max memory on the js platform
    return 512L * 1024L * 1024L // 512 MB
}

/**
 * Return the application's cache directory.
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.PlatformContextsJsCommonTest.testAppCacheDirectory
 */
actual fun PlatformContext.appCacheDirectory(): Path? = null

/**
 * Return the application's cache directory.
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.PlatformContextsJsCommonTest.testScreenSize
 */
actual fun PlatformContext.screenSize(): Size {
    return Size(1920, 1080)
}