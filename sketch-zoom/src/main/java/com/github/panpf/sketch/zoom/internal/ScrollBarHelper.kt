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
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.zoom.Zoomer
import kotlin.math.abs
import kotlin.math.roundToInt

internal class ScrollBarHelper(context: Context, private val zoomer: Zoomer) {

    private val scrollBarSize: Float = 3f * Resources.getSystem().displayMetrics.density
    private val scrollBarMargin: Float = 3f * Resources.getSystem().displayMetrics.density
    private val scrollBarRadius: Int = (scrollBarSize / 2).roundToInt()
    private val scrollBarAlpha: Int = 51
    private val scrollBarPaint: Paint = Paint().apply {
        color = Color.parseColor("#000000")
        alpha = scrollBarAlpha
    }
    private val view = zoomer.view
    private val drawRectF = RectF()
    private val scrollBarRectF = RectF()
    private val fadeRunnable: FadeRunnable = FadeRunnable(context, this)
    private val delayFadeRunnable: DelayFadeRunnable = DelayFadeRunnable(this, fadeRunnable)

    fun onDraw(canvas: Canvas) {
        val drawRectF = drawRectF.apply {
            zoomer.getDrawRect(this)
        }.takeIf { !it.isEmpty } ?: return
        val (viewWidth, viewHeight) = zoomer.viewSize.takeIf { !it.isEmpty } ?: return
        val drawWidth = drawRectF.width()
        val drawHeight = drawRectF.height()
        val viewAvailableWidth =
            viewWidth - (scrollBarMargin * 2) - view.paddingLeft - view.paddingRight
        val viewAvailableHeight =
            viewHeight - (scrollBarMargin * 2) - view.paddingTop - view.paddingBottom

        // draw hor scroll bar
        if (drawWidth.toInt() > viewWidth) {
            val widthScale = viewWidth.toFloat() / drawWidth
            val horScrollBarWidth =
                (viewAvailableWidth * widthScale).coerceAtLeast(scrollBarSize).toInt()
            val horScrollBarRectF = scrollBarRectF.apply {
                val mapLeft = if (drawRectF.left < 0) {
                    (abs(drawRectF.left) / drawRectF.width() * viewAvailableWidth).toInt()
                } else 0
                left = (view.paddingLeft + scrollBarMargin + mapLeft)
                right = left + horScrollBarWidth
                top = (view.paddingTop + scrollBarMargin + viewAvailableHeight - scrollBarSize)
                bottom = top + scrollBarSize
            }
            canvas.drawRoundRect(
                horScrollBarRectF,
                scrollBarRadius.toFloat(),
                scrollBarRadius.toFloat(),
                scrollBarPaint
            )
        }

        // draw ver scroll bar
        if (drawHeight.toInt() > viewHeight) {
            val heightScale = viewHeight.toFloat() / drawHeight
            val verScrollBarHeight =
                (viewAvailableHeight * heightScale).coerceAtLeast(scrollBarSize).toInt()
            val verScrollBarRectF = scrollBarRectF.apply {
                val mapTop = if (drawRectF.top < 0) {
                    (abs(drawRectF.top) / drawRectF.height() * viewAvailableHeight).toInt()
                } else 0
                left = (view.paddingLeft + scrollBarMargin + viewAvailableWidth - scrollBarSize)
                right = left + scrollBarSize
                top = (view.paddingTop + scrollBarMargin + mapTop)
                bottom = top + verScrollBarHeight
            }
            canvas.drawRoundRect(
                verScrollBarRectF,
                scrollBarRadius.toFloat(),
                scrollBarRadius.toFloat(),
                scrollBarPaint
            )
        }
    }

    fun onMatrixChanged() {
        scrollBarPaint.alpha = scrollBarAlpha
        if (fadeRunnable.isRunning) {
            fadeRunnable.cancel()
        }
        delayFadeRunnable.start()
    }

    private class DelayFadeRunnable(
        val scrollBarHelper: ScrollBarHelper,
        val fadeRunnable: FadeRunnable
    ) : Runnable {

        override fun run() {
            fadeRunnable.start()
        }

        fun start() {
            scrollBarHelper.view.removeCallbacks(this)
            scrollBarHelper.view.postDelayed(this, 800)
        }
    }

    private class FadeRunnable(context: Context, val scrollBarHelper: ScrollBarHelper) : Runnable {

        private val scroller: Scroller = Scroller(context, DecelerateInterpolator())

        val isRunning: Boolean
            get() = !scroller.isFinished

        fun start() {
            cancel()

            val startX = scrollBarHelper.scrollBarAlpha
            val dx = -startX
            scroller.startScroll(startX, 0, dx, 0, 300)
            scrollBarHelper.view.post(this)
        }

        fun cancel() {
            scrollBarHelper.view.removeCallbacks(this)
            scroller.forceFinished(true)
        }

        override fun run() {
            if (!scroller.isFinished && scroller.computeScrollOffset()) {
                scrollBarHelper.scrollBarPaint.alpha = scroller.currX
                scrollBarHelper.view.invalidate()
                ViewCompat.postOnAnimation(scrollBarHelper.view, this)
            }
        }
    }
}