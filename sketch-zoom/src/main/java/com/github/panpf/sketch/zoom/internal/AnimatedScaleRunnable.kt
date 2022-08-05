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
package com.github.panpf.sketch.zoom.internal

import androidx.core.view.ViewCompat
import com.github.panpf.sketch.zoom.Zoomer

internal class AnimatedScaleRunnable(
    private val zoomer: Zoomer,
    private val scaleDragHelper: ScaleDragHelper,
    private val startScale: Float,
    private val endScale: Float,
    private val scaleFocalX: Float,
    private val scaleFocalY: Float
) : Runnable {

    private val startTime: Long = System.currentTimeMillis()

    var isRunning = false
        private set

    fun start() {
        isRunning = true
        zoomer.view.post(this)
    }

    fun cancel() {
        zoomer.view.removeCallbacks(this)
        isRunning = false
    }

    override fun run() {
        val t = interpolate()
        val scale = startScale + t * (endScale - startScale)
        val deltaScale = scale / scaleDragHelper.scale
        isRunning = t < 1f
        scaleDragHelper.doScale(deltaScale, scaleFocalX, scaleFocalY, 0f, 0f)
        // We haven't hit our target scale yet, so post ourselves again
        if (isRunning) {
            ViewCompat.postOnAnimation(zoomer.view, this)
        }
    }

    private fun interpolate(): Float {
        var t = 1f * (System.currentTimeMillis() - startTime) / zoomer.zoomAnimationDuration
        t = 1f.coerceAtMost(t)
        t = zoomer.zoomInterpolator.getInterpolation(t)
        return t
    }
}