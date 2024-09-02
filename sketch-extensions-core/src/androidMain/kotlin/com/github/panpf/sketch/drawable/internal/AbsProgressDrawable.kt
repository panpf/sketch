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

package com.github.panpf.sketch.drawable.internal

import android.graphics.Canvas
import androidx.annotation.FloatRange
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.ProgressDrawable
import com.github.panpf.sketch.internal.format
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

/**
 * Abstract definition of progress Drawable
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.drawable.internal.AbsProgressDrawableTest
 */
abstract class AbsProgressDrawable(
    val hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    val hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    val stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
) : ProgressDrawable() {

    private var stepAnimationRunning: Boolean = false
    private var stepAnimationStartProgress: Float? = null
    private var stepAnimationProgress: Float? = null
    private var stepAnimationEndProgress: Float? = null
    private var stepAnimationStartTimeMark: ValueTimeMark? = null
    private var hidden = false

    final override var progress: Float = 0f
        set(value) {
            val oldValue = field
            val newValue = value.coerceIn(-1f, 1f).format(1)
            field = newValue
            if (newValue != oldValue) {
                hidden = false
                if (oldValue <= 0f && newValue == 1f && hiddenWhenCompleted) {
                    // The progress goes directly from 0 to 1f, and hidden the content after completion,
                    // skip the animation and hidden the content directly.
                    hidden = true
                    stepAnimationRunning = false
                    stepAnimationProgress = null
                } else if (newValue < 0f) {
                    // Hide content now
                    stepAnimationRunning = false
                    stepAnimationProgress = null
                } else if (oldValue < 0f) {
                    // Show new progress now
                    stepAnimationRunning = false
                    stepAnimationProgress = null
                } else if (newValue > oldValue) {
                    // The progress increases and the animation starts from the current progress
                    val stepAnimationProgress = stepAnimationProgress
                    val newStepAnimationStartProgress =
                        if (stepAnimationRunning && stepAnimationProgress != null)
                            stepAnimationProgress else oldValue
                    stepAnimationStartProgress = newStepAnimationStartProgress
                    stepAnimationEndProgress = newValue
                    stepAnimationStartTimeMark = TimeSource.Monotonic.markNow()
                    stepAnimationRunning = true
                } else {
                    // The progress decreases, no animation is needed
                    stepAnimationRunning = false
                    stepAnimationProgress = null
                }
                invalidateSelf()
            }
        }

    override fun draw(canvas: Canvas) {
        if (hidden || (hiddenWhenIndeterminate && progress == 0f)) return

        val stepAnimationDone: Boolean
        val drawProgress: Float
        if (stepAnimationRunning) {
            val stepAnimationStartTimeMark = stepAnimationStartTimeMark!!
            val elapsedTime = stepAnimationStartTimeMark.elapsedNow().inWholeMilliseconds
            val stepProgress = (elapsedTime / stepAnimationDuration.toDouble()).coerceIn(0.0, 1.0)
            stepAnimationDone = stepProgress >= 1.0
            val animationStartProgress = stepAnimationStartProgress!!
            val animationEndProgress = stepAnimationEndProgress!!
            val addDrawProgress =
                ((animationEndProgress - animationStartProgress) * stepProgress).toFloat()
            drawProgress = animationStartProgress + addDrawProgress
            stepAnimationProgress = drawProgress
        } else {
            stepAnimationDone = false
            drawProgress = progress
            stepAnimationProgress = null
        }

        if (drawProgress in 0f..1f) {
            drawProgress(canvas, drawProgress)
        }

        if (stepAnimationRunning) {
            if (stepAnimationDone) {
                stepAnimationRunning = false
                stepAnimationProgress = null
            } else {
                invalidateSelf()
            }
        }

        if (!stepAnimationRunning && drawProgress >= 1f && hiddenWhenCompleted) {
            hidden = true
            invalidateSelf()
        }
    }

    abstract fun drawProgress(
        canvas: Canvas,
        @FloatRange(from = 0.0, to = 1.0) drawProgress: Float
    )

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        val changed = super.setVisible(visible, restart)
        if (changed && !visible) {
            if (stepAnimationRunning) {
                stepAnimationRunning = false
                stepAnimationProgress = null
                invalidateSelf()
            }
        }
        return changed
    }
}