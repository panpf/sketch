package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.state.ErrorStateImage.Builder

/**
 * Create an ErrorStateImage
 */
@Composable
fun ComposableErrorStateImage(
    defaultImage: StateImage? = null,
    configBlock: @Composable (Builder.() -> Unit)? = null
): ErrorStateImage = Builder(defaultImage).apply {
    configBlock?.invoke(this)
}.build()