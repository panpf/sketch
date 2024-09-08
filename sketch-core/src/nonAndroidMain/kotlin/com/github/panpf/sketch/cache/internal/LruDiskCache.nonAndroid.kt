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

package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.PlatformContext
import okio.Path

/**
 * Check whether the disk cache directory meets the requirements of the platform.
 * If it does not meet the requirements, it will try to repair it.
 *
 * Implementation of non-Android platforms does not require any processing
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.cache.internal.LruDiskCacheNonAndroidTest.testCheckDiskCacheDirectory
 */
actual fun checkDiskCacheDirectory(context: PlatformContext, directory: Path): Path = directory