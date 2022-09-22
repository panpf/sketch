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

import android.content.Context
import android.graphics.RectF
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import kotlin.math.roundToInt

internal class FlingRunnable(
    context: Context,
    private val zoomerHelper: ZoomerHelper,
    private val scaleDragHelper: ScaleDragHelper,
    private val velocityX: Int,
    private val velocityY: Int,
) : Runnable {

    private val scroller: OverScroller = OverScroller(context)
    private var currentX: Int = 0
    private var currentY: Int = 0

    @Suppress("unused")
    val isRunning: Boolean
        get() = !scroller.isFinished

    fun start() {
        cancel()

        val drawRectF = RectF()
            .apply { scaleDragHelper.getDrawRect(this) }
            .takeIf { !it.isEmpty }
            ?: return
        val (viewWidth, viewHeight) = zoomerHelper.viewSize

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

        currentX = startX
        currentY = startY
        if (startX != maxX || startY != maxY) {
            scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0)
            zoomerHelper.view.post(this)
        }
    }

    fun cancel() {
        zoomerHelper.view.removeCallbacks(this)
        scroller.forceFinished(true)
    }

    override fun run() {
        if (scroller.isFinished) {
            return  // remaining post that should not be handled
        }

        if (scroller.computeScrollOffset()) {
            val newX = scroller.currX
            val newY = scroller.currY
            val dx = (currentX - newX).toFloat()
            val dy = (currentY - newY).toFloat()
            scaleDragHelper.translateBy(dx, dy)
            currentX = newX
            currentY = newY
            // Post On animation
            ViewCompat.postOnAnimation(zoomerHelper.view, this)
        }
    }
}