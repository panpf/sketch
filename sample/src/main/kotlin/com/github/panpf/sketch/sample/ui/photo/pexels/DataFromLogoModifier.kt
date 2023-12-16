package com.github.panpf.sketch.sample.ui.photo.pexels

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
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.compose.AsyncImageState
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.DisplayResult

// todo Move to separate module
fun Modifier.dataFromLogo(state: AsyncImageState): Modifier {
    return this.then(DataFromLogoElement(state))
}

internal data class DataFromLogoElement(
    val state: AsyncImageState
) : ModifierNodeElement<DataFromLogoNode>() {

    override fun create(): DataFromLogoNode {
        return DataFromLogoNode(state)
    }

    override fun update(node: DataFromLogoNode) {
        node.update(state)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "DataFromLogo"
        properties["dataFrom"] = state.result
            ?.let { it as? DisplayResult.Success }
            ?.dataFrom?.name
            ?: "null"
        properties["loadState"] = state.loadState?.name ?: "null"
    }
}

internal class DataFromLogoNode(
    private var state: AsyncImageState,
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
            val realSize = with(density) { 20.dp.toPx() }
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
        if (result is DisplayResult.Success) {
            val path = getPath(size)
            val dataFrom = result.dataFrom
            val color = Color(dataFrom2Color(dataFrom))
            drawPath(path, color)
        }
    }

    fun update(state: AsyncImageState) {
        this.state = state
        invalidateDraw()
    }

    companion object {
        private const val FROM_FLAG_COLOR_MEMORY = 0x77008800   // dark green
        private const val FROM_FLAG_COLOR_MEMORY_CACHE = 0x7700FF00   // green
        private const val FROM_FLAG_COLOR_RESULT_CACHE = 0x77FFFF00 // yellow
        private const val FROM_FLAG_COLOR_LOCAL = 0x771E90FF   // dodger blue
        private const val FROM_FLAG_COLOR_DOWNLOAD_CACHE = 0x77FF8800 // dark yellow
        private const val FROM_FLAG_COLOR_NETWORK = 0x77FF0000  // red

        fun dataFrom2Color(dataFrom: DataFrom): Int {
            return when (dataFrom) {
                DataFrom.MEMORY_CACHE -> FROM_FLAG_COLOR_MEMORY_CACHE
                DataFrom.MEMORY -> FROM_FLAG_COLOR_MEMORY
                DataFrom.RESULT_CACHE -> FROM_FLAG_COLOR_RESULT_CACHE
                DataFrom.DOWNLOAD_CACHE -> FROM_FLAG_COLOR_DOWNLOAD_CACHE
                DataFrom.LOCAL -> FROM_FLAG_COLOR_LOCAL
                DataFrom.NETWORK -> FROM_FLAG_COLOR_NETWORK
            }
        }
    }
}