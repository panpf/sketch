package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.LocalPlatformContext

@Composable
fun ImageRequest(
    uri: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(LocalPlatformContext.current, uri).apply {
    configBlock?.invoke(this)
}.build()