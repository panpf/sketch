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
 * @see com.github.panpf.sketch.compose.resources.common.test.request.ImageRequestComposeResourcesTest.testPlaceholder
 */
fun ImageRequest.Builder.blurHashPlaceholder(blurHash: String): ImageRequest.Builder =
    placeholder(BlurHashStateImage(blurHash))

/**
 * Set Drawable placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.request.ImageRequestComposeResourcesTest.testFallback
 */
fun ImageRequest.Builder.blurHashFallback(blurHash: String): ImageRequest.Builder =
    fallback(BlurHashStateImage(blurHash))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.request.ImageRequestComposeResourcesTest.testError
 */
fun ImageRequest.Builder.blurHashError(blurHash: String): ImageRequest.Builder =
    error(BlurHashStateImage(blurHash))