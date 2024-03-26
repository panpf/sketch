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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.getMimeTypeFromImageResult
import com.github.panpf.sketch.compose.AsyncImageState
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.name

/**
 * Display a MimeType logo in the lower right corner of the component. The icon is provided by [mimeTypeIconMap]
 */
fun Modifier.mimeTypeLogo(
    state: AsyncImageState,
    mimeTypeIconMap: Map<String, Painter>,
    margin: Dp = 0.dp
): Modifier {
    return this.then(MimeTypeLogoElement(state, mimeTypeIconMap, margin))
}

internal data class MimeTypeLogoElement(
    val state: AsyncImageState,
    val mimeTypeIconMap: Map<String, Painter>,
    val margin: Dp
) : ModifierNodeElement<MimeTypeLogoNode>() {

    override fun create(): MimeTypeLogoNode {
        mimeTypeIconMap.entries.forEach {
            require(it.value.intrinsicSize.isSpecified) { "Painter.intrinsicSize must be specified: ${it.key}" }
        }
        return MimeTypeLogoNode(state, mimeTypeIconMap, margin)
    }

    override fun update(node: MimeTypeLogoNode) {
        mimeTypeIconMap.entries.forEach {
            require(it.value.intrinsicSize.isSpecified) { "Painter.intrinsicSize must be specified: ${it.key}" }
        }
        node.update(state, mimeTypeIconMap, margin)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "MimeTypeLogo"
        properties["mimeType"] = state.result
            ?.let { (it as? ImageResult.Success) }
            ?.imageInfo?.mimeType
            ?: "null"
        properties["loadState"] = state.loadState?.name ?: "null"
    }
}

internal class MimeTypeLogoNode(
    private var state: AsyncImageState,
    private var mimeTypeIconMap: Map<String, Painter>,
    private var margin: Dp
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    override fun ContentDrawScope.draw() {
        drawContent()

        val mimeType = getMimeTypeFromImageResult(state.result, state.request?.uriString)
        if (mimeType != null) {
            val painter = mimeTypeIconMap[mimeType]
            if (painter != null) {
                val painterSize =
                    painter.intrinsicSize.takeIf { it.isSpecified && !it.isEmpty() } ?: size
                translate(
                    left = size.width - painterSize.width - margin.toPx(),
                    top = size.height - painterSize.height - margin.toPx()
                ) {
                    with(painter) {
                        draw(painterSize)
                    }
                }
            }
        }
    }

    fun update(state: AsyncImageState, mimeTypeIconMap: Map<String, Painter>, margin: Dp) {
        this.state = state
        this.mimeTypeIconMap = mimeTypeIconMap
        this.margin = margin
        invalidateDraw()
    }
}