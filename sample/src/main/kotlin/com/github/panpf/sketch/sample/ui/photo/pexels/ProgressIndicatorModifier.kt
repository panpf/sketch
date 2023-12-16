package com.github.panpf.sketch.sample.ui.photo.pexels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.InspectorInfo
import com.github.panpf.sketch.compose.AsyncImageState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun Modifier.progressIndicator(
    state: AsyncImageState,
    progressPainter: ProgressPainter
): Modifier {
    return this.then(ProgressIndicatorElement(state, progressPainter))
}

abstract class ProgressPainter : Painter() {
    abstract var progress: Float
    var drawInvalidateTick by mutableIntStateOf(0)
}

internal data class ProgressIndicatorElement(
    val state: AsyncImageState,
    val progressPainter: ProgressPainter,
) : ModifierNodeElement<ProgressIndicatorNode>() {

    override fun create(): ProgressIndicatorNode {
        return ProgressIndicatorNode(state, progressPainter)
    }

    override fun update(node: ProgressIndicatorNode) {
        node.update(state, progressPainter)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "ProgressIndicator"
        properties["progress"] = progressPainter.progress
        properties["loadState"] = state.loadState?.name ?: "null"
    }
}

internal class ProgressIndicatorNode(
    private var state: AsyncImageState,
    private var progressPainter: ProgressPainter,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var lastJob: Job? = null

    override fun onAttach() {
        super.onAttach()
        againCollectProgress()
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val progressPainter = progressPainter
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

    fun update(state: AsyncImageState, progressPainter: ProgressPainter) {
        this.state = state
        this.progressPainter = progressPainter
        if (isAttached) {
            againCollectProgress()
        }
        invalidateDraw()
    }

    private fun againCollectProgress() {
        lastJob?.cancel()
        lastJob = coroutineScope.launch {
            snapshotFlow { state.progress }.collect {
                progressPainter.progress = it?.decimalProgress ?: -1f
            }
        }
    }
}