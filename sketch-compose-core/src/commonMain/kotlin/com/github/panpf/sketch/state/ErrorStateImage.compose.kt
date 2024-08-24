package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Create an ErrorStateImage
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ComposableErrorStateImage(
    defaultImage: StateImage? = null,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ErrorStateImage = ErrorStateImage.Builder(defaultImage).apply {
    configBlock.invoke(this)
}.build()

/**
 * Add a custom error state
 */
fun ErrorStateImage.Builder.addState(
    condition: ErrorStateImage.Condition,
    color: Color
): ErrorStateImage.Builder = apply {
    addState(condition, ColorPainterStateImage(color))
}