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

package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext

/**
 * Get the default maximum size of the disk cache
 *
 * @see com.github.panpf.sketch.core.nonjscommon.test.cache.DiskCacheNonJsCommonTest.testDefaultDiskCacheMaxSize
 */
internal actual fun defaultDiskCacheMaxSize(context: PlatformContext): Long? = 512L * 1024 * 1024