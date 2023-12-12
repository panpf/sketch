package com.github.panpf.sketch.sample.ui.photo.pexels

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw

fun Modifier.progressIndicator(state: ProgressIndicatorState): Modifier {
    return this.then(ProgressIndicatorElement(state))
}

@Composable
fun rememberProgressIndicatorState(progressPainter: ProgressPainter): ProgressIndicatorState {
    val progressIndicatorState =
        remember(progressPainter) { ProgressIndicatorState(progressPainter) }
    LaunchedEffect(progressIndicatorState) {
        snapshotFlow { progressIndicatorState.progress }.collect {
            progressIndicatorState.progressPainter.progress = it
        }
    }
    return progressIndicatorState
}

@Stable
class ProgressIndicatorState(val progressPainter: ProgressPainter) {

    var progress by mutableFloatStateOf(0f)
}

abstract class ProgressPainter : Painter() {
    abstract var progress: Float
    var drawInvalidateTick by mutableIntStateOf(0)
}

internal data class ProgressIndicatorElement(
    val state: ProgressIndicatorState,
) : ModifierNodeElement<ProgressIndicatorNode>() {

    override fun create(): ProgressIndicatorNode {
        return ProgressIndicatorNode(state)
    }

    override fun update(node: ProgressIndicatorNode) {
        node.update(state)
    }
}

internal class ProgressIndicatorNode(
    var state: ProgressIndicatorState,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    override fun ContentDrawScope.draw() {
        drawContent()

        val progressPainter = state.progressPainter
        // Reading this ensures that we invalidate when invalidateDrawable() is called
        progressPainter.drawInvalidateTick
        val progressPainterSize = progressPainter.intrinsicSize
            .takeIf { it.isSpecified && !it.isEmpty() }
            ?: size
        translate(
            left = (size.width - progressPainterSize.width) / 2,
            top = (size.height - progressPainterSize.height) / 2,
        ) {
            with(progressPainter) {
                draw(progressPainterSize)
            }
        }
    }

    fun update(state: ProgressIndicatorState) {
        this.state = state
        invalidateDraw()
    }
}