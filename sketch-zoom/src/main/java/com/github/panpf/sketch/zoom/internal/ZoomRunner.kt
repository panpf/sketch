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

import androidx.core.view.ViewCompat.postOnAnimation

internal class ZoomRunner(
    private val imageZoomer: ImageZoomer,
    private val scaleDragHelper: ScaleDragHelper,
    currentZoom: Float,
    targetZoom: Float,
    private val mFocalX: Float,
    private val mFocalY: Float
) : Runnable {

    private val mStartTime: Long = System.currentTimeMillis()
    private val mZoomStart: Float = currentZoom
    private val mZoomEnd: Float = targetZoom
    private val logger by lazy {
        imageZoomer.imageView.sketch.logger
    }

    override fun run() {
        if (!imageZoomer.isWorking) {
            logger.w(ImageZoomer.MODULE, "not working. zoom run")
            return
        }
        val t = interpolate()
        val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
        val deltaScale = scale / scaleDragHelper.zoomScale
        val continueZoom = t < 1f
        scaleDragHelper.isZooming = continueZoom
        scaleDragHelper.onScale(deltaScale, mFocalX, mFocalY)

        // We haven't hit our target scale yet, so post ourselves again
        if (continueZoom) {
            postOnAnimation(imageZoomer.getImageView(), this)
        } else {
            logger.v(ImageZoomer.MODULE) { "finished. zoom run" }
        }
    }

    private fun interpolate(): Float {
        var t = 1f * (System.currentTimeMillis() - mStartTime) / imageZoomer.zoomDuration
        t = 1f.coerceAtMost(t)
        t = imageZoomer.zoomInterpolator.getInterpolation(t)
        return t
    }

    fun zoom() {
        imageZoomer.getImageView().post(this)
    }
}