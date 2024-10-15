package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.getValue
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
import kotlin.time.TimeSource

class NewMoonLoadingPainter(override val intrinsicSize: Size) : Painter(), AnimatablePainter {

    private var rotation = 0f
    private val paint = Paint()
    private var running: Boolean? = null
    private var startTime: TimeSource.Monotonic.ValueTimeMark? = null

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

        if (running == null) {
            running = true
        }
        if (running == true) {
            val startTime = startTime ?: TimeSource.Monotonic.markNow().apply {
                this@NewMoonLoadingPainter.startTime = this
            }
            val elapsedTime = startTime.elapsedNow().inWholeMilliseconds
            rotation = ((elapsedTime % 1000) / 1000f) * 360f
            invalidateDraw()
        }
    }

    override fun start() {
        if (running == true) return
        running = true
        invalidateDraw()
    }

    override fun stop() {
        if (running != true) return
        running = false
        invalidateDraw()
    }

    override fun isRunning(): Boolean {
        return running == true
    }

    private fun invalidateDraw() {
        invalidateTick = (invalidateTick + 1) % Int.MAX_VALUE
    }
}