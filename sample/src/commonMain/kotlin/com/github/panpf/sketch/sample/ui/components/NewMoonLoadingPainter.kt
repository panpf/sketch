package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.withSaveLayer
import com.github.panpf.sketch.painter.AnimatablePainter

class NewMoonLoadingPainter(override val intrinsicSize: Size) : Painter(), AnimatablePainter {

    private var rotation by mutableFloatStateOf(0f)
    private val paint = Paint()
    private var running = false

    private var invalidateTick by mutableIntStateOf(0)

    override fun DrawScope.onDraw() {
        invalidateTick

        rotate(rotation) {
            drawIntoCanvas {
                it.withSaveLayer(
                    bounds = Rect(0f, 0f, size.width, size.height),
                    paint = paint
                ) {
                    drawCircle(
                        color = Color.Black,
                    )
                    drawCircle(
                        color = Color.Gray,
                        center = center.copy(y = center.y - (size.height * 0.05f)),
                        blendMode = BlendMode.DstOut,
                    )
                }
            }
        }

        if (running) {
            rotation = (rotation + 5) % 360
        }
    }

    override fun start() {
        if (running) return
        running = true
        invalidateDraw()
    }

    override fun stop() {
        if (!running) return
        running = false
        invalidateDraw()
    }

    override fun isRunning(): Boolean {
        return running
    }

    fun invalidateDraw() {
        invalidateTick = (invalidateTick + 1) % Int.MAX_VALUE
    }
}