/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.ability

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.painter.ProgressPainter
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.name
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Display a progress indicator on the surface of the component,
 * which shows the user the progress of the download.
 * The style of the indicator is provided by [progressPainter]
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.ProgressIndicatorModifierTest.testModifier
 */
fun Modifier.progressIndicator(
    state: AsyncImageState,
    progressPainter: ProgressPainter
): Modifier {
    return this.then(ProgressIndicatorElement(state, progressPainter))
}

/**
 * ProgressIndicator Modifier Element
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.ProgressIndicatorModifierTest.testElement
 */
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

/**
 * ProgressIndicator Modifier Node
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.ProgressIndicatorModifierTest.testNode
 */
internal class ProgressIndicatorNode(
    private var state: AsyncImageState,
    private var progressPainter: ProgressPainter,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var lastProgressCollectJob: Job? = null

    override fun onAttach() {
        super.onAttach()
        recollectProgress()
    }

    override fun onDetach() {
        super.onDetach()
        lastProgressCollectJob?.cancel()
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val progressPainter = progressPainter
        // Reading this ensures that we invalidate when progressPainter.invalidateDraw() is called
        progressPainter.drawInvalidateTick.value
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
            recollectProgress()
        }
        invalidateDraw()
    }

    private fun recollectProgress() {
        lastProgressCollectJob?.cancel()
        lastProgressCollectJob = coroutineScope.launch {
            combine(
                flows = listOf(
                    snapshotFlow { state.loadState },
                    snapshotFlow { state.progress },
                ),
                transform = { it[0] as? LoadState to it[1] as Progress? }
            ).collect { (loadState, progress) ->
                when (loadState) {
                    is LoadState.Started -> {
                        progressPainter.progress = progress?.decimalProgress ?: 0f
                    }

                    is LoadState.Success -> progressPainter.progress = 1f
                    is LoadState.Error -> progressPainter.progress = -1f
                    is LoadState.Canceled -> progressPainter.progress = -1f
                    else -> progressPainter.progress = -1f
                }
            }
        }
    }
}