package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.state.ErrorStateImage.Builder
import com.github.panpf.sketch.state.ErrorStateImage.Condition

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

/**
 * Add a custom error state
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    color: Color
): ErrorStateImage.Builder = apply {
    addState(condition, ColorPainterStateImage(color))
}