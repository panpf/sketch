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

import android.content.Context
import android.graphics.RectF
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.zoom.Zoomer
import kotlin.math.roundToInt

internal class FlingHelper(
    val context: Context,
    private val zoomer: Zoomer,
    private val scaleDragHelper: ScaleDragHelper
) {

    private val view = zoomer.view
    private var flingTask: FlingTask? = null

    val isRunning: Boolean
        get() = flingTask?.isRunning == true

    fun start(velocityX: Int, velocityY: Int) {
        cancel()

        val drawRectF = RectF().apply {
            setEmpty()
            scaleDragHelper.getDrawRect(this)
        }.takeIf { !it.isEmpty } ?: return
        val viewSize = zoomer.viewSize
        val viewWidth = viewSize.width
        val viewHeight = viewSize.height

        val minX: Int
        val maxX: Int
        val startX = (-drawRectF.left).roundToInt()
        if (viewWidth < drawRectF.width()) {
            minX = 0
            maxX = (drawRectF.width() - viewWidth).roundToInt()
        } else {
            maxX = startX
            minX = maxX
        }

        val minY: Int
        val maxY: Int
        val startY = (-drawRectF.top).roundToInt()
        if (viewHeight < drawRectF.height()) {
            minY = 0
            maxY = (drawRectF.height() - viewHeight).roundToInt()
        } else {
            maxY = startY
            minY = maxY
        }

        if (startX != maxX || startY != maxY) {
            val flingTask = FlingTask(
                context,
                onTranslateBy = { dx: Float, dy: Float ->
                    scaleDragHelper.translateBy(dx, dy)
                    ViewCompat.postOnAnimation(view, flingTask)
                },
                onFinished = {
                    this@FlingHelper.flingTask = null
                }
            )
            this@FlingHelper.flingTask = flingTask
            flingTask.start(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
            ViewCompat.postOnAnimation(view, flingTask)
        }
    }

    fun cancel() {
        val flingTask = this@FlingHelper.flingTask
        if (flingTask != null) {
            flingTask.stop()
            view.removeCallbacks(flingTask)
            this@FlingHelper.flingTask = null
        }
    }

    private class FlingTask(
        context: Context,
        val onTranslateBy: (dx: Float, dy: Float) -> Unit,
        val onFinished: () -> Unit,
    ) : Runnable {

        private val scroller: OverScroller = OverScroller(context)
        private var currentX: Int = 0
        private var currentY: Int = 0

        val isRunning: Boolean
            get() = !scroller.isFinished

        override fun run() {
            if (scroller.isFinished || !scroller.computeScrollOffset()) {
                onFinished()
            } else {
                val newX = scroller.currX
                val newY = scroller.currY
                val dx = (currentX - newX).toFloat()
                val dy = (currentY - newY).toFloat()
                currentX = newX
                currentY = newY
                onTranslateBy(dx, dy)
            }
        }

        fun start(
            startX: Int, startY: Int, velocityX: Int, velocityY: Int,
            minX: Int, maxX: Int, minY: Int, maxY: Int
        ) {
            currentX = startX
            currentY = startY
            scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0)
        }

        fun stop() {
            scroller.forceFinished(true)
        }
    }
}