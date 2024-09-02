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

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.name

/**
 * Display a dataFrom logo in the upper right corner of the component.
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.DataFromLogoModifierTest.testModifier
 */
fun Modifier.dataFromLogo(state: AsyncImageState, size: Dp = dataFromDefaultSize.dp): Modifier {
    return this.then(DataFromLogoElement(state, size))
}

/**
 * DataFromLogo Modifier Element
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.DataFromLogoModifierTest.testElement
 */
internal data class DataFromLogoElement(
    val state: AsyncImageState,
    val size: Dp,
) : ModifierNodeElement<DataFromLogoNode>() {

    override fun create(): DataFromLogoNode {
        return DataFromLogoNode(state, size)
    }

    override fun update(node: DataFromLogoNode) {
        node.update(state, size)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "DataFromLogo"
        properties["dataFrom"] = state.result
            ?.let { it as? ImageResult.Success }
            ?.dataFrom?.name
            ?: "null"
        properties["size"] = size
        properties["loadState"] = state.loadState?.name ?: "null"
    }
}

/**
 * DataFromLogo Modifier Node
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.DataFromLogoModifierTest.testNode
 */
internal class DataFromLogoNode(
    private var state: AsyncImageState,
    private var size: Dp,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private var drawSize: Size? = null
    private var path: Path? = null

    private fun getPath(drawSize: Size): Path {
        val path = this.path
        if (drawSize == this.drawSize && path != null) {
            return path
        }

        val density = currentValueOf(LocalDensity)
        val newPath = Path().apply {
            val viewWidth = drawSize.width
            val paddingRight = 0
            val paddingTop = 0
            val realSize = with(density) { size.toPx() }
            moveTo(
                viewWidth - paddingRight - realSize,
                paddingTop.toFloat()
            )
            lineTo(
                viewWidth - paddingRight.toFloat(),
                paddingTop.toFloat()
            )
            lineTo(
                viewWidth - paddingRight.toFloat(),
                paddingTop.toFloat() + realSize
            )
            close()
        }
        this.path = newPath
        return newPath
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val result = state.result
        if (result is ImageResult.Success) {
            val path = getPath(this@draw.size)
            val dataFrom = result.dataFrom
            val color = Color(dataFromColor(dataFrom))
            drawPath(path, color)
        }
    }

    fun update(state: AsyncImageState, size: Dp) {
        this.state = state
        this.size = size
        invalidateDraw()
    }
}