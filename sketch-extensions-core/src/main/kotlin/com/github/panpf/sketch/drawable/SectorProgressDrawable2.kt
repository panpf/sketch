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

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import androidx.core.graphics.withSave

/**
 * Sector Progress Drawable
 */
class SectorProgressDrawable(
    private val size: Int = defaultSize(),
    private val backgroundColor: Int = 0x44000000,
    private val strokeColor: Int = Color.WHITE,
    private val progressColor: Int = Color.WHITE,
    private val strokeWidth: Float = size * 0.02f,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
    hideWhenCompleted: Boolean = true,
) : AbsProgressDrawable(stepAnimationDuration, hideWhenCompleted) {

    companion object {
        fun defaultSize(): Int {
            return (50f * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        }
    }

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
}