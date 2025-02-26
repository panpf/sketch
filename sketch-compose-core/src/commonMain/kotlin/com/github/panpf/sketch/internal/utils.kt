/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a new [ImageRequest] with the given [uri] and [context], then remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.internal.UtilsTest.testRequestOf
 */
@Composable
fun requestOf(context: PlatformContext, uri: String?): ImageRequest {
    return remember(context, uri) { ImageRequest(context, uri) }
}

/**
 * Create a new [ImageRequest] with the given [uri], then remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.internal.UtilsTest.testRequestOf2
 */
@Composable
fun requestOf(uri: String?): ImageRequest {
    val context = LocalPlatformContext.current
    return remember(uri) { ImageRequest(context, uri) }
}