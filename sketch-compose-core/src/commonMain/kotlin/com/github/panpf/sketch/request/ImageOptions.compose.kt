package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ComposableErrorStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.StateImage


/**
 * Build and set the [ImageOptions]
 */
@Composable
fun ComposableImageOptions(
    configBlock: @Composable (ImageOptions.Builder.() -> Unit)? = null
): ImageOptions = ImageOptions.Builder().apply {
    configBlock?.invoke(this)
}.build()

/**
 * Set Color placeholder image when loading
 */
fun ImageOptions.Builder.placeholder(color: Color): ImageOptions.Builder =
    placeholder(ColorPainterStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 */
fun ImageOptions.Builder.fallback(color: Color): ImageOptions.Builder =
    fallback(ColorPainterStateImage(color))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageOptions.Builder.error(
    color: Color,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ColorPainterStateImage(color), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
@Composable
fun ImageOptions.Builder.composableError(
    stateImage: StateImage,
    configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ComposableErrorStateImage(stateImage, configBlock))


/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
@Composable
fun ImageOptions.Builder.composableError(
    color: Color,
    configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ComposableErrorStateImage(ColorPainterStateImage(color), configBlock))