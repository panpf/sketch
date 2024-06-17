package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.rememberPainterStateImage
import org.jetbrains.compose.resources.DrawableResource

/**
 * Set Drawable placeholder image when loading
 */
@Composable
fun ImageRequest.Builder.placeholder(resource: DrawableResource): ImageRequest.Builder =
    placeholder(rememberPainterStateImage(resource))

/**
 * Set Drawable placeholder image when uri is empty
 */
@Composable
fun ImageRequest.Builder.fallback(resource: DrawableResource): ImageRequest.Builder =
    fallback(rememberPainterStateImage(resource))

/**
 * Set Drawable image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
@Composable
fun ImageRequest.Builder.error(
    defaultResource: DrawableResource,
    configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(ErrorStateImage(defaultResource, configBlock))