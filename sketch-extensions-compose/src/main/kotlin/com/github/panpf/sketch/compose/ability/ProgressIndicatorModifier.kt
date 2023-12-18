/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.compose.ability

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
import com.github.panpf.sketch.compose.AsyncImageState
import com.github.panpf.sketch.compose.LoadState
import com.github.panpf.sketch.compose.internal.ignoreFirst
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Display a progress indicator on the surface of the component,
 * which shows the user the progress of the download.
 * The style of the indicator is provided by [progressPainter]
 */
fun Modifier.progressIndicator(
    state: AsyncImageState,
    progressPainter: ProgressPainter
): Modifier {
    return this.then(ProgressIndicatorElement(state, progressPainter))
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

    private var lastJob1: Job? = null
    private var lastJob2: Job? = null

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
        lastJob1?.cancel()
        lastJob1 = coroutineScope.launch {
            snapshotFlow { state.progress }.collect {
                if (state.loadState == LoadState.Started || state.loadState == LoadState.Success) {
                    progressPainter.progress = it?.decimalProgress ?: -1f
                } else {
                    progressPainter.progress = -1f
                }
            }
        }

        lastJob2?.cancel()
        lastJob2 = coroutineScope.launch {
            snapshotFlow { state.loadState }.ignoreFirst().collect {
                when (it) {
                    LoadState.Started -> progressPainter.progress = 0f
                    LoadState.Success -> progressPainter.progress = 1f
                    LoadState.Error -> progressPainter.progress = -1f
                    LoadState.Canceled -> progressPainter.progress = -1f
                    else -> {}
                }
            }
        }
    }
}