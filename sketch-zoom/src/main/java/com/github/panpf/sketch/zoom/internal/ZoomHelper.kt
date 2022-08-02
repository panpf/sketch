/*
 * Copyright 2011, 2012 Chris Banes.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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

internal class ZoomHelper(
    private val zoomer: Zoomer,
    private val scaleDragHelper: ScaleDragHelper,
) {

    private var lastTask: ZoomTask? = null

    fun start(targetScale: Float, scaleFocalX: Float, scaleFocalY: Float) {
        lastTask?.stop()
        lastTask = ZoomTask(
            zoomer, scaleDragHelper, zoomer.scale, targetScale, scaleFocalX, scaleFocalY
        )
        lastTask?.start()
    }

    fun stop() {
        lastTask?.stop()
    }

    class ZoomTask(
        private val zoomer: Zoomer,
        private val scaleDragHelper: ScaleDragHelper,
        private val startScale: Float,
        private val endScale: Float,
        private val scaleFocalX: Float,
        private val scaleFocalY: Float
    ) : Runnable {

        private val view = zoomer.view
        private val startTime: Long = System.currentTimeMillis()

        fun start() {
            view.post(this)
        }

        fun stop() {
            view.removeCallbacks(this)
        }

        override fun run() {
            val t = interpolate()
            val scale = startScale + t * (endScale - startScale)
            val deltaScale = scale / scaleDragHelper.scale
            val continueZoom = t < 1f
            scaleDragHelper.isZooming = continueZoom
            scaleDragHelper.scale(deltaScale, scaleFocalX, scaleFocalY, 0f, 0f)

            // We haven't hit our target scale yet, so post ourselves again
            if (continueZoom) {
                ViewCompat.postOnAnimation(view, this)
            }
        }

        private fun interpolate(): Float {
            var t = 1f * (System.currentTimeMillis() - startTime) / zoomer.zoomAnimationDuration
            t = 1f.coerceAtMost(t)
            t = zoomer.zoomInterpolator.getInterpolation(t)
            return t
        }
    }
}