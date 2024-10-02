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

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.util.toSketchSize

/**
 * Build and set the [ImageOptions]
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ComposableImageOptions(
    crossinline configBlock: @Composable (ImageOptions.Builder.() -> Unit)
): ImageOptions = ImageOptions.Builder().apply {
    configBlock.invoke(this)
}.build()


/**
 * Set how to resize image
 *
 * @param size Expected Bitmap size
 * @param precision precision of size, default is [Precision.LESS_PIXELS]
 * @param scale Which part of the original image to keep when [precision] is
 * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.request.ImageOptionsComposeCommonTest.testResize
 */
fun ImageOptions.Builder.resize(
    size: IntSize,
    precision: Precision? = null,
    scale: Scale? = null
): ImageOptions.Builder = resize(size.toSketchSize(), precision, scale)

/**
 * Set the resize size
 *
 * @see com.github.panpf.sketch.compose.core.common.test.request.ImageOptionsComposeCommonTest.testSize
 */
fun ImageOptions.Builder.size(size: IntSize): ImageOptions.Builder = size(size.toSketchSize())

/**
 * Use window size as resize size
 *
 * @see com.github.panpf.sketch.compose.core.android.test.request.ImageOptionsComposeAndroidTest.testSizeWithWindow
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.request.ImageOptionsComposeNonAndroidTest.testSizeWithWindow
 */
@Composable
expect fun ImageOptions.Builder.sizeWithWindow(): ImageOptions.Builder


/**
 * Set Color placeholder image when loading
 *
 * @see com.github.panpf.sketch.compose.core.common.test.request.ImageOptionsComposeCommonTest.testPlaceholder
 */
fun ImageOptions.Builder.placeholder(color: Color): ImageOptions.Builder =
    placeholder(ColorPainterStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.compose.core.common.test.request.ImageOptionsComposeCommonTest.testFallback
 */
fun ImageOptions.Builder.fallback(color: Color): ImageOptions.Builder =
    fallback(ColorPainterStateImage(color))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.compose.core.common.test.request.ImageOptionsComposeCommonTest.testError
 */
fun ImageOptions.Builder.error(color: Color): ImageOptions.Builder =
    error(ColorPainterStateImage(color))