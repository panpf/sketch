/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.painter

import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.times
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.TransitionPainter
import com.github.panpf.sketch.util.computeScaleMultiplierWithFit
import kotlin.js.JsName
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.TimeSource

/**
 * A [Painter] that crossfades from [start] to [end].
 *
 * NOTE: The animation can only be executed once as the [start]
 * painter is dereferenced at the end of the transition.
 *
 * @param start The [Painter] to crossfade from.
 * @param end The [Painter] to crossfade to.
 * @param durationMillis The duration of the crossfade animation.
 * @param fadeStart If false, the start painter will not fade out while the end painter fades in.
 * @param preferExactIntrinsicSize If true, this painter's intrinsic width/height will only be -1
 *  if [start] **and** [end] return -1 for that dimension. If false, the intrinsic width/height will
 *  be -1 if [start] **or** [end] return -1 for that dimension. This is useful for views that
 *  require an exact intrinsic size to scale the painter.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.CrossfadePainterTest
 */
@Stable
class CrossfadePainter constructor(
    @JsName("startPainter") val start: Painter?,
    @JsName("endPainter") val end: Painter?,
    val fitScale: Boolean = true,
    val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
    val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
    val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
) : Painter(), RememberObserver, AnimatablePainter, SketchPainter, TransitionPainter {

    companion object {
        private const val STATE_START = 0
        private const val STATE_RUNNING = 1
        private const val STATE_DONE = 2
    }

    private var invalidateTick by mutableIntStateOf(0)

    private var startTime: TimeSource.Monotonic.ValueTimeMark? = null
    private var maxAlpha: Float by mutableFloatStateOf(DefaultAlpha)
    private var colorFilter: ColorFilter? by mutableStateOf(null)
    private var state = STATE_START
    private var startPainter1: Painter? = start
    private val endPainter1: Painter? = end

    override val intrinsicSize: Size = computeIntrinsicSize()

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
    }

    override fun DrawScope.onDraw() {
        invalidateTick // Invalidate the scope when invalidateTick changes.

        if (state == STATE_START) {
            drawPainter(startPainter1, maxAlpha)
            return
        }
        if (state == STATE_DONE) {
            drawPainter(endPainter1, maxAlpha)
            return
        }

        val startTime = startTime
        val percent = if (startTime != null) {
            startTime.elapsedNow().inWholeMilliseconds / durationMillis.toFloat()
        } else {
            1.0f
        }
        val endAlpha = percent.coerceIn(0f, 1f) * maxAlpha
        val startAlpha = if (fadeStart) maxAlpha - endAlpha else maxAlpha
        val isDone = percent >= 1f

        // Draw the start painter.
        if (!isDone) {
            drawPainter(startPainter1, startAlpha)
        }

        // Draw the end painter.
        drawPainter(endPainter1, endAlpha)

        if (isDone) {
            markDone()
        } else {
            invalidateSelf()
        }
    }

    private fun invalidateSelf() {
        if (invalidateTick == Int.MAX_VALUE) {
            invalidateTick = 0
        } else {
            invalidateTick++
        }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.maxAlpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    private fun computeIntrinsicSize(): Size {
        if (startPainter1 == null && endPainter1 == null) return Size.Unspecified
        val startSize = startPainter1?.intrinsicSize ?: Size.Zero
        val endSize = endPainter1?.intrinsicSize ?: Size.Zero

        val isStartSpecified = startSize.isSpecified
        val isEndSpecified = endSize.isSpecified
        if (isStartSpecified && isEndSpecified) {
            return Size(
                width = max(startSize.width, endSize.width),
                height = max(startSize.height, endSize.height),
            )
        }
        if (preferExactIntrinsicSize) {
            if (isStartSpecified) return startSize
            if (isEndSpecified) return endSize
        }
        return Size.Unspecified
    }

    private fun DrawScope.drawPainter(painter: Painter?, alpha: Float) {
        if (painter == null || alpha <= 0) return

        with(painter) {
            val drawSize = this@drawPainter.size
            val painterScaledSize = computeScaledSize(this@with.intrinsicSize, drawSize)
            if (drawSize.isUnspecified || drawSize.isEmpty()) {
                draw(painterScaledSize, alpha, colorFilter)
            } else {
                inset(
                    horizontal = (drawSize.width - painterScaledSize.width) / 2,
                    vertical = (drawSize.height - painterScaledSize.height) / 2
                ) {
                    draw(painterScaledSize, alpha, colorFilter)
                }
            }
        }
    }

    private fun computeScaledSize(srcSize: Size, dstSize: Size): Size {
        if (srcSize.isUnspecified || srcSize.isEmpty()) return dstSize
        if (dstSize.isUnspecified || dstSize.isEmpty()) return dstSize
        val sizeMultiplier = computeScaleMultiplierWithFit(
            srcWidth = srcSize.width.roundToInt(),
            srcHeight = srcSize.height.roundToInt(),
            dstWidth = dstSize.width.roundToInt(),
            dstHeight = dstSize.height.roundToInt(),
            fitScale = fitScale
        )
        return srcSize * ScaleFactor(sizeMultiplier.toFloat(), sizeMultiplier.toFloat())
    }

    override fun onRemembered() {
        (startPainter1 as? RememberObserver)?.onRemembered()
        (endPainter1 as? RememberObserver)?.onRemembered()
        start()
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        (startPainter1 as? RememberObserver)?.onForgotten()
        (endPainter1 as? RememberObserver)?.onForgotten()
        stop()
    }

    override fun isRunning() = state == STATE_RUNNING

    override fun start() {
        (startPainter1 as? AnimatablePainter)?.start()
        (endPainter1 as? AnimatablePainter)?.start()

        if (state != STATE_START) {
            return
        }

        state = STATE_RUNNING
        startTime = TimeSource.Monotonic.markNow()

        invalidateSelf()
    }

    override fun stop() {
        (startPainter1 as? AnimatablePainter)?.stop()
        (endPainter1 as? AnimatablePainter)?.stop()

        if (state != STATE_DONE) {
            markDone()
        }
    }

    private fun markDone() {
        state = STATE_DONE
        (startPainter1 as? AnimatablePainter)?.stop()
        (startPainter1 as? RememberObserver)?.onForgotten()
        startPainter1 = null
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String = "CrossfadePainter(" +
            "start=${start?.toLogString()}, " +
            "end=${end?.toLogString()}, " +
            "fitScale=$fitScale, " +
            "durationMillis=$durationMillis, " +
            "fadeStart=$fadeStart, " +
            "preferExactIntrinsicSize=$preferExactIntrinsicSize" +
            ")"
}