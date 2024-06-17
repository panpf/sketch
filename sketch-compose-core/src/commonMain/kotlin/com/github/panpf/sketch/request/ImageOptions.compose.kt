package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable


/**
 * Build and set the [ImageOptions]
 */
@Composable
fun ComposableImageOptions(
    configBlock: @Composable (ImageOptions.Builder.() -> Unit)? = null
): ImageOptions = ImageOptions.Builder().apply {
    configBlock?.invoke(this)
}.build()