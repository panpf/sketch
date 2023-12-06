package com.github.panpf.sketch.sample.ui.photo.pexels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.mimeTypeLogo(state: MimeTypeLogoState, margin: Dp = 0.dp): Modifier {
    return this.then(MimeTypeLogoElement(state, margin))
}

@Composable
fun rememberMimeTypeLogoState(block: () -> Map<String, Painter>): MimeTypeLogoState {
    return remember { MimeTypeLogoState(block()) }
}

@Stable
class MimeTypeLogoState(val painterMap: Map<String, Painter>) {
    var mimeType by mutableStateOf<String?>(null)

    init {
        painterMap.entries.forEach {
            require(it.value.intrinsicSize.isSpecified) { "Painter.intrinsicSize must be specified: ${it.key}" }
        }
    }
}

internal data class MimeTypeLogoElement(
    val state: MimeTypeLogoState,
    val margin: Dp
) : ModifierNodeElement<MimeTypeLogoNode>() {

    override fun create(): MimeTypeLogoNode {
        return MimeTypeLogoNode(state, margin)
    }

    override fun update(node: MimeTypeLogoNode) {
        node.update(state, margin)
    }
}

internal class MimeTypeLogoNode(
    var state: MimeTypeLogoState,
    var margin: Dp
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    override fun ContentDrawScope.draw() {
        drawContent()

        val mimeType = state.mimeType
        val painter = mimeType?.let { state.painterMap[it] }
        if (painter != null) {
            val painterSize = painter.intrinsicSize
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

    fun update(state: MimeTypeLogoState, margin: Dp) {
        this.state = state
        this.margin = margin
        invalidateDraw()
    }
}