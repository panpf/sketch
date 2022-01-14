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

import android.graphics.RectF
import android.widget.OverScroller
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.vm
import com.github.panpf.sketch.SLog.Companion.vmf
import com.github.panpf.sketch.SLog.Companion.wm
import com.github.panpf.sketch.util.SketchUtils.Companion.postOnAnimation
import kotlin.math.roundToInt

internal class FlingRunner(
    private val imageZoomer: ImageZoomer,
    private val scaleDragHelper: ScaleDragHelper
) : Runnable {

    private val scroller: OverScroller = OverScroller(imageZoomer.imageView.context)
    private var currentX = 0
    private var currentY = 0

    fun fling(velocityX: Int, velocityY: Int) {
        if (!imageZoomer.isWorking) {
            wm(ImageZoomer.MODULE, "not working. fling")
            return
        }
        val drawRectF = RectF()
        scaleDragHelper.getDrawRect(drawRectF)
        if (drawRectF.isEmpty) {
            return
        }
        val viewSize = imageZoomer.viewSize
        val imageViewWidth = viewSize.width
        val imageViewHeight = viewSize.height
        val startX = (-drawRectF.left).roundToInt()
        val minX: Int
        val maxX: Int
        val minY: Int
        val maxY: Int
        if (imageViewWidth < drawRectF.width()) {
            minX = 0
            maxX = (drawRectF.width() - imageViewWidth).roundToInt()
        } else {
            maxX = startX
            minX = maxX
        }
        val startY = (-drawRectF.top).roundToInt()
        if (imageViewHeight < drawRectF.height()) {
            minY = 0
            maxY = (drawRectF.height() - imageViewHeight).roundToInt()
        } else {
            maxY = startY
            minY = maxY
        }
        if (isLoggable(SLog.VERBOSE)) {
            vmf(
                ImageZoomer.MODULE, "fling. start=%dx %d, min=%dx%d, max=%dx%d",
                startX, startY, minX, minY, maxX, maxY
            )
        }

        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            currentX = startX
            currentY = startY
            scroller.fling(
                startX, startY, velocityX, velocityY, minX,
                maxX, minY, maxY, 0, 0
            )
        }
        val imageView = imageZoomer.imageView
        imageView.removeCallbacks(this)
        imageView.post(this)
    }

    override fun run() {
        // remaining post that should not be handled
        if (scroller.isFinished) {
            if (isLoggable(SLog.VERBOSE)) {
                vm(ImageZoomer.MODULE, "finished. fling run")
            }
            return
        }
        if (!imageZoomer.isWorking) {
            wm(ImageZoomer.MODULE, "not working. fling run")
            return
        }
        if (!scroller.computeScrollOffset()) {
            if (isLoggable(SLog.VERBOSE)) {
                vm(ImageZoomer.MODULE, "scroll finished. fling run")
            }
            return
        }
        val newX = scroller.currX
        val newY = scroller.currY
        scaleDragHelper.translateBy((currentX - newX).toFloat(), (currentY - newY).toFloat())
        currentX = newX
        currentY = newY

        // Post On animation
        postOnAnimation(imageZoomer.imageView, this)
    }

    fun cancelFling() {
        if (isLoggable(SLog.VERBOSE)) {
            vm(ImageZoomer.MODULE, "cancel fling")
        }
        scroller.forceFinished(true)
        imageZoomer.imageView.removeCallbacks(this)
    }
}