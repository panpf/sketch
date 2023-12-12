///*
// * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch.drawable
//
//import android.graphics.Canvas
//import android.graphics.ColorFilter
//import android.graphics.Paint
//import android.graphics.PixelFormat
//import android.os.SystemClock
//import androidx.annotation.ColorInt
//import com.github.panpf.sketch.util.format
//
///**
// * Mask Progress Drawable
// */
//class MaskProgressDrawable(
//    @ColorInt private val maskColor: Int = DEFAULT_MASK_COLOR
//) : ProgressDrawable() {
//
//    companion object {
//        const val DEFAULT_MASK_COLOR = 0x22000000
//        private const val DEFAULT_DURATION = 300
//    }
//
//    private val paint = Paint().apply {
//        color = maskColor
//        isAntiAlias = true
//    }
//
//    private var animationRunning: Boolean = false
//    private var animationStartProgress: Float? = null
//    private var animationTargetProgress: Float? = null
//    private var animationStartTimeMillis = 0L
//    private var _progress: Float = 0f
//        set(value) {
//            field = value
//            if (value >= 1f) {
//                onProgressEnd?.invoke()
//            }
//        }
//    override var progress: Float
//        get() = _progress
//        set(value) {
//            val newValue = value.format(1).coerceIn(0f, 1f)
//            if (newValue != _progress) {
//                if (_progress == 0f && newValue == 1f) {
//                    // Here is the loading of the local image, no loading progress, quickly complete
//                    _progress = newValue
//                    animationRunning = false
//                } else if (newValue > _progress) {
//                    animationStartProgress = _progress
//                    animationTargetProgress = newValue
//                    animationStartTimeMillis = SystemClock.uptimeMillis()
//                    animationRunning = true
//                } else {
//                    // If newValue is less than _progress, you can reset it quickly
//                    _progress = newValue
//                    animationRunning = false
//                }
//                invalidateSelf()
//            }
//        }
//
//    override var onProgressEnd: (() -> Unit)? = null
//
//    override fun draw(canvas: Canvas) {
//        var animationDone = false
//        if (animationRunning) {
//            val percent =
//                (SystemClock.uptimeMillis() - animationStartTimeMillis) / DEFAULT_DURATION.toDouble()
//            animationDone = percent >= 1
//            _progress =
//                (animationStartProgress!! + ((animationTargetProgress!! - animationStartProgress!!) * percent)).toFloat()
//        }
//
//        val progress = _progress.takeIf { it >= 0f } ?: return
//        val bounds = bounds.takeIf { !it.isEmpty } ?: return
//        val saveCount = canvas.save()
//        canvas.drawRect(
//            bounds.left.toFloat(),
//            bounds.top + ((progress * bounds.height())),
//            bounds.right.toFloat(),
//            bounds.bottom.toFloat(),
//            paint
//        )
//        canvas.restoreToCount(saveCount)
//
//        if (animationRunning) {
//            if (animationDone) {
//                animationRunning = false
//            } else {
//                invalidateSelf()
//            }
//        }
//    }
//
//    override fun setAlpha(alpha: Int) {
//        paint.alpha = alpha
//    }
//
//    override fun setColorFilter(colorFilter: ColorFilter?) {
//        paint.colorFilter = colorFilter
//    }
//
//    @Deprecated(
//        "Deprecated in Java. This method is no longer used in graphics optimizations",
//        ReplaceWith("")
//    )
//    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
//
//    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
//        val changed = super.setVisible(visible, restart)
//        if (changed && !visible) {
//            animationRunning = false
//            invalidateSelf()
//        }
//        return changed
//    }
//
//    override fun getIntrinsicWidth(): Int = -1
//
//    override fun getIntrinsicHeight(): Int = -1
//
//    override fun mutate(): ProgressDrawable {
//        return this
//    }
//}