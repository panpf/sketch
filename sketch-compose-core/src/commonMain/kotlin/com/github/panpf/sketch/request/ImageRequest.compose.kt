package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext

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