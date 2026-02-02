package com.github.panpf.sketch.sample.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun rememberTextPainter(
    text: String,
    textStyle: TextStyle = TextStyle.Default,
    paddingValues: PaddingValues? = null,
    background: Background? = null,
    size: Size = Size.Zero,
): TextPainter {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    return remember(text, textStyle, paddingValues, background, size, layoutDirection, density) {
        TextPainter(
            text = text,
            textMeasurer = textMeasurer,
            textStyle = textStyle,
            paddingValues = paddingValues,
            background = background,
            size = size,
            layoutDirection = layoutDirection,
            density = density
        )
    }
}

@Stable
data class Background internal constructor(
    val color: Color,
    val brush: Brush?,
    val alpha: Float,
    val shape: Shape
) {
    constructor(
        color: Color,
        shape: Shape = RectangleShape,
        alpha: Float = 1.0f,
    ) : this(color, null, alpha, shape)

    constructor(
        brush: Brush,
        shape: Shape = RectangleShape,
        alpha: Float = 1.0f,
    ) : this(Color.Unspecified, brush, alpha, shape)
}

class TextPainter(
    val text: String,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle = TextStyle.Default,
    val paddingValues: PaddingValues? = null,
    val background: Background? = null,
    val size: Size = Size.Zero,
    val layoutDirection: LayoutDirection,
    val density: Density,
) : Painter() {

    private val result = textMeasurer.measure(text, textStyle)

    // naive cache outline calculation if size is the same
    private var lastSize: Size? = null
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastOutline: Outline? = null
    private var lastShape: Shape? = null

    override val intrinsicSize: Size = if (size.isUnspecified) {
        size
    } else {
        val paddingSize = paddingValues?.let {
            Size(
                width = with(density) {
                    val startSize = it.calculateStartPadding(layoutDirection).toPx()
                    val endSize = it.calculateEndPadding(layoutDirection).toPx()
                    startSize + endSize
                },
                height = with(density) {
                    val topSize = it.calculateTopPadding().toPx()
                    val bottomSize = it.calculateBottomPadding().toPx()
                    topSize + bottomSize
                },
            )
        } ?: Size.Zero
        result.size.let { Size(it.width + paddingSize.width, it.height + paddingSize.height) }
    }

    override fun DrawScope.onDraw() {
        val background = background
        if (background != null) {
            if (background.shape === RectangleShape) {
                // shortcut to avoid Outline calculation and allocation
                drawRect(background.color, background.brush, background.alpha)
            } else {
                drawOutline(background.color, background.brush, background.alpha, background.shape)
            }
        }
        val paddingLeft = paddingValues?.calculateStartPadding(layoutDirection)?.toPx() ?: 0f
        val paddingTop = paddingValues?.calculateTopPadding()?.toPx() ?: 0f
        translate(left = paddingLeft, top = paddingTop) {
            drawText(result)
        }
    }

    private fun DrawScope.drawRect(color: Color, brush: Brush?, alpha: Float) {
        if (color != Color.Unspecified) drawRect(color = color)
        brush?.let { drawRect(brush = it, alpha = alpha) }
    }

    private fun DrawScope.drawOutline(
        color: Color,
        brush: Brush?,
        alpha: Float,
        shape: Shape = RectangleShape
    ) {
        val outline =
            if (size == lastSize && layoutDirection == lastLayoutDirection && lastShape == shape) {
                lastOutline!!
            } else {
                shape.createOutline(size, layoutDirection, this)
            }
        if (color != Color.Unspecified) drawOutline(outline, color = color)
        brush?.let { drawOutline(outline, brush = it, alpha = alpha) }
        lastOutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
        lastShape = shape
    }
}