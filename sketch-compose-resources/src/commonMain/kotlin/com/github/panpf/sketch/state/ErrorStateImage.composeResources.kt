package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.state.ErrorStateImage.Builder
import com.github.panpf.sketch.state.ErrorStateImage.Condition
import org.jetbrains.compose.resources.DrawableResource


/**
 * Create an ErrorStateImage
 */
@Composable
fun ComposableErrorStateImage(
    defaultResource: DrawableResource? = null,
    configBlock: @Composable (Builder.() -> Unit)? = null
): ErrorStateImage =
    Builder(defaultResource?.let { rememberPainterStateImage(it) }).apply {
        configBlock?.invoke(this)
    }.build()

/**
 * Add a StateImage dedicated to the empty uri error
 */
@Composable
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    resource: DrawableResource
): ErrorStateImage.Builder = apply {
    addState(condition, rememberPainterStateImage(resource))
}