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
import android.graphics.PixelFormat
import androidx.annotation.ColorInt
import androidx.core.graphics.withSave

/**
 * Mask Progress Drawable
 */
class MaskProgressDrawable(
    @ColorInt private val maskColor: Int = DEFAULT_MASK_COLOR,
    hiddenWhenIndeterminate: Boolean = false,
    hiddenWhenCompleted: Boolean = true,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
) : AbsProgressDrawable(hiddenWhenIndeterminate, hiddenWhenCompleted, stepAnimationDuration) {

    companion object {
        const val DEFAULT_MASK_COLOR = 0x22000000
    }

    private val paint = Paint().apply {
        color = maskColor
        isAntiAlias = true
    }

    override fun drawProgress(canvas: Canvas, drawProgress: Float) {
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        canvas.withSave {
            val progressHeight = drawProgress * bounds.height()
            drawRect(
                bounds.left.toFloat(),
                bounds.top + progressHeight,
                bounds.right.toFloat(),
                bounds.bottom.toFloat(),
                paint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated(
        "Deprecated in Java. This method is no longer used in graphics optimizations",
        ReplaceWith("")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = -1

    override fun getIntrinsicHeight(): Int = -1

    override fun mutate(): ProgressDrawable {
        return this
    }
}