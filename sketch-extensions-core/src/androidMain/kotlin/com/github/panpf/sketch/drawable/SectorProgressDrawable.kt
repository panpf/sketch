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

package com.github.panpf.sketch.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.core.graphics.withSave
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_STROKE_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.internal.AbsProgressDrawable
import com.github.panpf.sketch.internal.dp2Px
import com.github.panpf.sketch.internal.format

/**
 * Sector Progress Drawable
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.drawable.SectorProgressDrawableTest
 */
class SectorProgressDrawable constructor(
    private val size: Int = PROGRESS_INDICATOR_SECTOR_SIZE.dp2Px(),
    @ColorInt private val backgroundColor: Int = PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR,
    @ColorInt private val strokeColor: Int = PROGRESS_INDICATOR_SECTOR_STROKE_COLOR,
    @ColorInt private val progressColor: Int = PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR,
    private val strokeWidth: Float = size * PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT,
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
        color = this@SectorProgressDrawable.backgroundColor
    }
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        color = this@SectorProgressDrawable.strokeColor
        strokeWidth = this@SectorProgressDrawable.strokeWidth
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = this@SectorProgressDrawable.progressColor
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

            // stroke
            canvas.drawCircle(cx, cy, radius, strokePaint)

            // progress
            val space = strokeWidth * 3
            val progressOval = progressOval.apply {
                set(
                    cx - radius + space,
                    cy - radius + space,
                    cx + radius - space,
                    cy + radius - space,
                )
            }
            val sweepAngle = drawProgress * 360f
            canvas.drawArc(progressOval, 270f, sweepAngle, true, progressPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        backgroundPaint.alpha = alpha
        strokePaint.alpha = alpha
        progressPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        backgroundPaint.colorFilter = colorFilter
        strokePaint.colorFilter = colorFilter
        progressPaint.colorFilter = colorFilter
    }

    @Deprecated(
        "Deprecated in Java. This method is no longer used in graphics optimizations",
        ReplaceWith("")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun mutate(): ProgressDrawable {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SectorProgressDrawable
        if (size != other.size) return false
        if (backgroundColor != other.backgroundColor) return false
        if (strokeColor != other.strokeColor) return false
        if (progressColor != other.progressColor) return false
        if (strokeWidth != other.strokeWidth) return false
        if (hiddenWhenIndeterminate != other.hiddenWhenIndeterminate) return false
        if (hiddenWhenCompleted != other.hiddenWhenCompleted) return false
        if (stepAnimationDuration != other.stepAnimationDuration) return false
        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + backgroundColor
        result = 31 * result + strokeColor
        result = 31 * result + progressColor
        result = 31 * result + strokeWidth.hashCode()
        result = 31 * result + hiddenWhenIndeterminate.hashCode()
        result = 31 * result + hiddenWhenCompleted.hashCode()
        result = 31 * result + stepAnimationDuration.hashCode()
        return result
    }

    override fun toString(): String {
        return "SectorProgressDrawable(" +
                "size=$size, " +
                "backgroundColor=$backgroundColor, " +
                "strokeColor=$strokeColor, " +
                "progressColor=$progressColor, " +
                "strokeWidth=${strokeWidth.format(2)}, " +
                "hiddenWhenIndeterminate=$hiddenWhenIndeterminate, " +
                "hiddenWhenCompleted=$hiddenWhenCompleted, " +
                "stepAnimationDuration=$stepAnimationDuration" +
                ")"
    }
}