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
//package com.github.panpf.sketch.sample.widget
//
//import android.graphics.Canvas
//import android.os.SystemClock
//import androidx.annotation.FloatRange
//import com.github.panpf.sketch.drawable.ProgressDrawable
//import com.github.panpf.sketch.util.format
//
///**
// * Sector Progress Drawable
// */
//abstract class AbsProgressDrawable : ProgressDrawable() {
//
//    companion object {
//        private const val DEFAULT_DURATION = 300
//    }
//
//    private var animationRunning: Boolean = false
//    private var animationStartProgress: Float? = null
//    private var animationEndProgress: Float? = null
//    private var animationStartTimeMillis = 0L
//
//    final override var progress: Float = 0f
//        set(value) {
//            val newValue = value.format(1)
//            require(newValue in -1f..1f) {
//                "progress must be in [-1, 1]"
//            }
//            val oldValue = field
//            field = value
//            if (newValue != oldValue) {
//                if (oldValue == 0f && (newValue == -1f || newValue == 1f)) {
//                    // Here is the loading of the local image, no loading progress, quickly complete
//                    animationRunning = false
//                } else {
//                    animationStartProgress = oldValue
//                    animationEndProgress = newValue
//                    animationStartTimeMillis = SystemClock.uptimeMillis()
//                    animationRunning = true
//                }
//                invalidateSelf()
//            }
//        }
//
//    private val drawProgress: Float = progress
////        set(value) {
////            field = value
////            if (value >= 1f) {
////                onProgressEnd?.invoke()
////            }
////        }
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
//                (animationStartProgress!! + ((animationEndProgress!! - animationStartProgress!!) * percent)).toFloat()
//        }
//
//        val drawProgress = _progress.takeIf { it >= 0f } ?: return
//
//        drawProgress(canvas, drawProgress)
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
//    abstract fun drawProgress(
//        canvas: Canvas,
//        @FloatRange(from = -1.0, to = 1.0) drawProgress: Float
//    )
//
//    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
//        val changed = super.setVisible(visible, restart)
//        if (changed && !visible) {
//            animationRunning = false
//            _progress = progress
//            invalidateSelf()
//        }
//        return changed
//    }
//}