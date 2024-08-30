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
 * Return the global application context.
 *
 * @see com.github.panpf.sketch.core.android.test.util.PlatformContextsAndroidTest.testApplication
 * @see com.github.panpf.sketch.core.nonandroid.test.util.PlatformContextsNonAndroidTest.testApplication
 */
expect val PlatformContext.application: PlatformContext

/**
 * Return the application's total memory in bytes.
 *
 * @see com.github.panpf.sketch.core.android.test.util.PlatformContextsAndroidTest.testTotalAvailableMemoryBytes
 * @see com.github.panpf.sketch.core.nonandroid.test.util.PlatformContextsNonAndroidTest.testTotalAvailableMemoryBytes
 */
expect fun PlatformContext.totalAvailableMemoryBytes(): Long

/**
 * Return the application's cache directory.
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.PlatformContextsDesktopTest.testAppCacheDirectory
 * @see com.github.panpf.sketch.core.android.test.util.PlatformContextsAndroidTest.testAppCacheDirectory
 * @see com.github.panpf.sketch.core.jscommon.test.util.PlatformContextsJsCommonTest.testAppCacheDirectory
 * @see com.github.panpf.sketch.core.ios.test.util.PlatformContextsIosTest.testAppCacheDirectory
 */
expect fun PlatformContext.appCacheDirectory(): Path?

/**
 * Return the screen size.
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.PlatformContextsDesktopTest.testScreenSize
 * @see com.github.panpf.sketch.core.android.test.util.PlatformContextsAndroidTest.testScreenSize
 * @see com.github.panpf.sketch.core.jscommon.test.util.PlatformContextsJsCommonTest.testScreenSize
 * @see com.github.panpf.sketch.core.ios.test.util.PlatformContextsIosTest.testScreenSize
 */
expect fun PlatformContext.screenSize(): Size