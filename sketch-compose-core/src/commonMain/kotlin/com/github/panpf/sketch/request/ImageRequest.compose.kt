package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ComposableErrorStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.StateImage

@Composable
fun ComposableImageRequest(
    context: PlatformContext,
    uri: String?,
    configBlock: @Composable (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(context, uri).apply {
    configBlock?.invoke(this)
}.build()

@Composable
fun ComposableImageRequest(
    uri: String?,
    configBlock: @Composable (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(LocalPlatformContext.current, uri).apply {
    configBlock?.invoke(this)
}.build()

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
 */
@Composable
fun ImageRequest.Builder.composableError(
    stateImage: StateImage,
    configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(ComposableErrorStateImage(stateImage, configBlock))


/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
@Composable
fun ImageRequest.Builder.composableError(
    color: Color,
    configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder =
    error(ComposableErrorStateImage(ColorPainterStateImage(color), configBlock))