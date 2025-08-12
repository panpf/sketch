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

package com.github.panpf.sketch.request

import com.github.panpf.sketch.state.BlurHashStateImage

/**
 * Set Drawable placeholder image when loading
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.request.ImageOptionsComposeResourcesTest.testPlaceholder
 */
fun ImageOptions.Builder.blurHashPlaceholder(blurHash: String): ImageOptions.Builder =
    placeholder(BlurHashStateImage(blurHash))

/**
 * Set Drawable placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.request.ImageOptionsComposeResourcesTest.testFallback
 */
fun ImageOptions.Builder.blurHashFallback(blurHash: String): ImageOptions.Builder =
    fallback(BlurHashStateImage(blurHash))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.request.ImageOptionsComposeResourcesTest.testError
 */
fun ImageOptions.Builder.blurHashError(blurHash: String): ImageOptions.Builder =
    error(BlurHashStateImage(blurHash))