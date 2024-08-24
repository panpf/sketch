package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ComposableErrorStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.StateImage
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
 */
fun ImageOptions.Builder.resize(
    size: IntSize,
    precision: Precision? = null,
    scale: Scale? = null
): ImageOptions.Builder =
    apply {
        resize(size.toSketchSize(), precision, scale)
    }

/**
 * Set the resize size
 */
fun ImageOptions.Builder.size(size: IntSize): ImageOptions.Builder =
    apply {
        size(size.toSketchSize())
    }

/**
 * Set the resize size
 */
@Composable
expect fun ImageOptions.Builder.sizeWithWindow(): ImageOptions.Builder


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
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ImageOptions.Builder.composableError(
    stateImage: StateImage,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ImageOptions.Builder = error(ComposableErrorStateImage(stateImage, configBlock))


/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ImageOptions.Builder.composableError(
    color: Color,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ImageOptions.Builder =
    error(ComposableErrorStateImage(ColorPainterStateImage(color), configBlock))