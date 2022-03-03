/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.zoom.new.scale

import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Scroller
import androidx.core.view.ViewCompat.postOnAnimation
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.zoom.internal.ImageZoomer
import com.github.panpf.sketch.zoom.new.Zoomer

/**
 * 定位执行器
 */
internal class NewLocationRunner(
    val context: Context,
    private val zoomer: Zoomer,
    private val scaleDragHelper: NewScaleDragHelper
) : Runnable {

    private val scroller = Scroller(context, AccelerateDecelerateInterpolator())
    private var currentX = 0
    private var currentY = 0
    private val logger = context.sketch.logger

    val isRunning: Boolean
        get() = !scroller.isFinished

    /**
     * 定位到预览图上指定的位置
     */
    fun location(startX: Int, startY: Int, endX: Int, endY: Int) {
        currentX = startX
        currentY = startY
        scroller.startScroll(startX, startY, endX - startX, endY - startY, 300)
        zoomer.view.removeCallbacks(this)
        zoomer.view.post(this)
    }

    override fun run() {
        // remaining post that should not be handled
        if (scroller.isFinished) {
            logger.v(ImageZoomer.MODULE) { "finished. location run" }
            return
        }
        if (!scroller.computeScrollOffset()) {
            logger.v(ImageZoomer.MODULE) { "scroll finished. location run" }
            return
        }
        val newX = scroller.currX
        val newY = scroller.currY
        val dx = (currentX - newX).toFloat()
        val dy = (currentY - newY).toFloat()
        scaleDragHelper.translateBy(dx, dy)
        currentX = newX
        currentY = newY

        postOnAnimation(zoomer.view, this)
    }

    fun cancel() {
        scroller.forceFinished(true)
        zoomer.view.removeCallbacks(this)
    }
}