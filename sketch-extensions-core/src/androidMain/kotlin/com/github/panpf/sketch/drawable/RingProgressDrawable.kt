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
package com.github.panpf.sketch.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.withSave
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.drawable.internal.AbsProgressDrawable
import com.github.panpf.sketch.drawable.internal.SketchDrawable
import com.github.panpf.sketch.internal.dp2Px
import kotlin.math.roundToInt

/**
 * Ring Progress Drawable
 */
class RingProgressDrawable constructor(
    private val size: Int = PROGRESS_INDICATOR_RING_SIZE.dp2Px(),
    private val ringWidth: Float = size * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
    @ColorInt private val ringColor: Int = PROGRESS_INDICATOR_RING_COLOR,
    @ColorInt private val backgroundColor: Int = ColorUtils.setAlphaComponent(
        ringColor,
        (PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT * 255).roundToInt()
    ),
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
) : AbsProgressDrawable(
    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
    hiddenWhenCompleted = hiddenWhenCompleted,
    stepAnimationDuration = stepAnimationDuration
), SketchDrawable {

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = this@RingProgressDrawable.backgroundColor
        strokeWidth = this@RingProgressDrawable.ringWidth
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = STROKE
        strokeWidth = this@RingProgressDrawable.ringWidth
        color = this@RingProgressDrawable.ringColor
        strokeCap = ROUND
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            alpha = this@RingProgressDrawable.alpha
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            colorFilter = this@RingProgressDrawable.colorFilter
        }
    }
    private val progressOval = RectF()

    override fun drawProgress(canvas: Canvas, drawProgress: Float) {
        val bounds = bounds.takeIf { !it.isEmpty } ?: return

        canvas.withSave {
            // background
            val widthRadius = bounds.width() / 2f
            val heightRadius = bounds.height() / 2f
            val radius = widthRadius.coerceAtMost(heightRadius)
            val cx = bounds.left + widthRadius
            val cy = bounds.top + heightRadius
            canvas.drawCircle(cx, cy, radius, backgroundPaint)

            // progress
            progressOval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            val sweepAngle = drawProgress * 360f
            canvas.drawArc(progressOval, 270f, sweepAngle, false, progressPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        progressPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        progressPaint.colorFilter = colorFilter
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun mutate(): ProgressDrawable {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RingProgressDrawable
        if (size != other.size) return false
        if (ringWidth != other.ringWidth) return false
        if (ringColor != other.ringColor) return false
        if (backgroundColor == other.backgroundColor) return false
        if (hiddenWhenIndeterminate != other.hiddenWhenIndeterminate) return false
        if (hiddenWhenCompleted != other.hiddenWhenCompleted) return false
        return stepAnimationDuration == other.stepAnimationDuration
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + ringWidth.hashCode()
        result = 31 * result + ringColor
        result = 31 * result + backgroundColor
        result = 31 * result + hiddenWhenIndeterminate.hashCode()
        result = 31 * result + hiddenWhenCompleted.hashCode()
        result = 31 * result + stepAnimationDuration.hashCode()
        return result
    }

    override fun toString(): String {
        return "RingProgressDrawable(size=$size, ringWidth=$ringWidth, ringColor=$ringColor, backgroundColor=$backgroundColor, hiddenWhenIndeterminate=$hiddenWhenIndeterminate, hiddenWhenCompleted=$hiddenWhenCompleted, stepAnimationDuration=$stepAnimationDuration)"
    }
}