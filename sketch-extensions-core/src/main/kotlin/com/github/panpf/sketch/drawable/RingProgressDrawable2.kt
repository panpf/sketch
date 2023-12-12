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
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.withSave

/**
 * Ring Progress Drawable
 */
class RingProgressDrawable(
    private val size: Int = defaultSize(),
    private val ringWidth: Float = size * 0.1f,
    @ColorInt private val ringColor: Int = Color.WHITE,
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
        color = ColorUtils.setAlphaComponent(ringColor, 60)
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
}