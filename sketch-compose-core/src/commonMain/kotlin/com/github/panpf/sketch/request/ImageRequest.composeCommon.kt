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
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ComposableErrorStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.util.toSketchSize

@Composable
fun ComposableImageRequest(
    context: PlatformContext,
    uri: String?,
): ImageRequest = ImageRequest.Builder(context, uri).build()

/**
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ComposableImageRequest(
    context: PlatformContext,
    uri: String?,
    crossinline configBlock: @Composable (ImageRequest.Builder.() -> Unit)
): ImageRequest = ImageRequest.Builder(context, uri).apply {
    configBlock.invoke(this)
}.build()

/**
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ComposableImageRequest(
    uri: String?,
    crossinline configBlock: @Composable (ImageRequest.Builder.() -> Unit)
): ImageRequest = ImageRequest.Builder(LocalPlatformContext.current, uri).apply {
    configBlock.invoke(this)
}.build()

@Composable
fun ComposableImageRequest(uri: String?): ImageRequest =
    ImageRequest.Builder(LocalPlatformContext.current, uri).build()


/**
 * Set how to resize image
 *
 * @param size Expected Bitmap size
 * @param precision precision of size, default is [Precision.LESS_PIXELS]
 * @param scale Which part of the original image to keep when [precision] is
 * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
 */
fun ImageRequest.Builder.resize(
    size: IntSize,
    precision: Precision? = null,
    scale: Scale? = null
): ImageRequest.Builder =
    apply {
        resize(size.toSketchSize(), precision, scale)
    }

/**
 * Set the resize size
 */
fun ImageRequest.Builder.size(size: IntSize): ImageRequest.Builder =
    apply {
        size(size.toSketchSize())
    }

/**
 * Set the resize size
 */
@Composable
expect fun ImageRequest.Builder.sizeWithWindow(): ImageRequest.Builder

/**
 * Set Color placeholder image when loading
 */
fun ImageRequest.Builder.placeholder(color: Color): ImageRequest.Builder =
    placeholder(ColorPainterStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 */
fun ImageRequest.Builder.fallback(color: Color): ImageRequest.Builder =
    fallback(ColorPainterStateImage(color))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageRequest.Builder.error(
    color: Color,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(ColorPainterStateImage(color), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ImageRequest.Builder.composableError(
    stateImage: StateImage,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ImageRequest.Builder = error(ComposableErrorStateImage(stateImage, configBlock))


/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ImageRequest.Builder.composableError(
    color: Color,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ImageRequest.Builder =
    error(ComposableErrorStateImage(ColorPainterStateImage(color), configBlock))