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
package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Scroller
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.zoom.Zoomer

internal class ScrollHelper(
    val context: Context,
    zoomer: Zoomer,
    private val scaleDragHelper: ScaleDragHelper
) {

    private val view = zoomer.view
    private var scrollTask: ScrollTask? = null

    val isRunning: Boolean
        get() = scrollTask?.isRunning == true

    fun start(startX: Int, startY: Int, endX: Int, endY: Int) {
        cancel()

        val locationTask = ScrollTask(
            context,
            onTranslateBy = { dx: Float, dy: Float ->
                scaleDragHelper.translateBy(dx, dy)
                ViewCompat.postOnAnimation(view, scrollTask)
            },
            onFinished = {
                this@ScrollHelper.scrollTask = null
            }
        )
        this@ScrollHelper.scrollTask = locationTask
        locationTask.start(startX, startY, endX, endY)
        ViewCompat.postOnAnimation(view, locationTask)
    }

    fun cancel() {
        val locationTask = this@ScrollHelper.scrollTask
        if (locationTask != null) {
            locationTask.stop()
            view.removeCallbacks(locationTask)
            this@ScrollHelper.scrollTask = null
        }
    }

    private class ScrollTask(
        context: Context,
        val onTranslateBy: (dx: Float, dy: Float) -> Unit,
        val onFinished: () -> Unit,
    ) : Runnable {

        private val scroller = Scroller(context, AccelerateDecelerateInterpolator())
        private var currentX = 0
        private var currentY = 0

        val isRunning: Boolean
            get() = !scroller.isFinished

        override fun run() {
            if (scroller.isFinished || !scroller.computeScrollOffset()) {
                onFinished()
                return
            }
            val newX = scroller.currX
            val newY = scroller.currY
            val dx = (currentX - newX).toFloat()
            val dy = (currentY - newY).toFloat()
            currentX = newX
            currentY = newY
            onTranslateBy(dx, dy)
        }

        fun start(startX: Int, startY: Int, endX: Int, endY: Int) {
            currentX = startX
            currentY = startY
            scroller.startScroll(startX, startY, endX - startX, endY - startY, 300)
        }

        fun stop() {
            scroller.forceFinished(true)
        }
    }
}