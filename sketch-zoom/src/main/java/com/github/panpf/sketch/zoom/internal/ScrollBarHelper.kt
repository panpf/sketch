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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.roundToInt

internal class ScrollBarHelper(context: Context, private val imageZoomer: ImageZoomer) {

    private val scrollBarPaint: Paint = Paint()
    private val scrollBarSize: Int
    private val scrollBarMargin: Int
    private val scrollBarRadius: Int
    private val scrollBarAlpha = 51
    private val scrollBarRectF = RectF()
    private val tempDisplayRectF = RectF()
    private val handler: Handler
    private val hiddenScrollBarRunner: HiddenScrollBarRunner
    private val fadeScrollBarRunner: FadeScrollBarRunner
    private val logger by lazy {
        imageZoomer.imageView.sketch.logger
    }

    init {
        scrollBarPaint.color = Color.parseColor("#000000")
        scrollBarPaint.alpha = scrollBarAlpha
        scrollBarSize = 3.dp2px
        scrollBarMargin = 3.dp2px
        scrollBarRadius = (scrollBarSize / 2).toFloat().roundToInt()
        handler = Handler(Looper.getMainLooper())
        hiddenScrollBarRunner = HiddenScrollBarRunner()
        fadeScrollBarRunner = FadeScrollBarRunner(context)
    }

    fun onDraw(canvas: Canvas) {
        val drawRectF = tempDisplayRectF
        imageZoomer.getDrawRect(drawRectF)
        if (drawRectF.isEmpty) {
            logger.v(ImageZoomer.MODULE) {
                "displayRectF is empty. drawScrollBar. drawRectF=drawRectF.toString()"

            }
            return
        }
        val viewSize = imageZoomer.viewSize
        val viewWidth = viewSize.width
        val viewHeight = viewSize.height
        val displayWidth = drawRectF.width()
        val displayHeight = drawRectF.height()
        if (viewWidth <= 0 || viewHeight <= 0 || displayWidth == 0f || displayHeight == 0f) {
            logger.v(ImageZoomer.MODULE) {
                "size is 0. drawScrollBar. viewSize=%dx%d, displaySize=%sx%s"
                    .format(viewWidth, viewHeight, displayWidth, displayHeight)
            }
            return
        }
        val imageView = imageZoomer.getImageView()
        val finalViewWidth = viewWidth - scrollBarMargin * 2
        val finalViewHeight = viewHeight - scrollBarMargin * 2
        if (displayWidth.toInt() > viewWidth) {
            val widthScale = viewWidth.toFloat() / displayWidth
            val horScrollBarWidth = (finalViewWidth * widthScale).toInt()
            val horScrollBarRectF = scrollBarRectF
            horScrollBarRectF.setEmpty()
            horScrollBarRectF.left =
                (imageView.paddingLeft + scrollBarMargin + if (drawRectF.left < 0) (abs(
                    drawRectF.left
                ) / drawRectF.width() * finalViewWidth).toInt() else 0).toFloat()
            horScrollBarRectF.top =
                (imageView.paddingTop + scrollBarMargin + finalViewHeight - scrollBarSize).toFloat()
            horScrollBarRectF.right = horScrollBarRectF.left + horScrollBarWidth
            horScrollBarRectF.bottom = horScrollBarRectF.top + scrollBarSize
            canvas.drawRoundRect(
                horScrollBarRectF,
                scrollBarRadius.toFloat(),
                scrollBarRadius.toFloat(),
                scrollBarPaint
            )
        }
        if (displayHeight.toInt() > viewHeight) {
            val heightScale = viewHeight.toFloat() / displayHeight
            val verScrollBarHeight = (finalViewHeight * heightScale).toInt()
            val verScrollBarRectF = scrollBarRectF
            verScrollBarRectF.setEmpty()
            verScrollBarRectF.left =
                (imageView.paddingLeft + scrollBarMargin + finalViewWidth - scrollBarSize).toFloat()
            verScrollBarRectF.top =
                (imageView.paddingTop + scrollBarMargin + if (drawRectF.top < 0) (abs(drawRectF.top) / drawRectF.height() * finalViewHeight).toInt() else 0).toFloat()
            verScrollBarRectF.right = verScrollBarRectF.left + scrollBarSize
            verScrollBarRectF.bottom = verScrollBarRectF.top + verScrollBarHeight
            canvas.drawRoundRect(
                verScrollBarRectF,
                scrollBarRadius.toFloat(),
                scrollBarRadius.toFloat(),
                scrollBarPaint
            )
        }
    }

    /**
     * 此方法里没有执行 imageView.invalidate()，因为回调的地方会有执行
     */
    fun onMatrixChanged() {
        scrollBarPaint.alpha = scrollBarAlpha
        if (fadeScrollBarRunner.isRunning) {
            fadeScrollBarRunner.abort()
        }
        handler.removeCallbacks(hiddenScrollBarRunner)
        handler.postDelayed(hiddenScrollBarRunner, 800)
    }

    private fun invalidateView() {
        val imageView = imageZoomer.getImageView()
        imageView.invalidate()
    }

    private inner class HiddenScrollBarRunner : Runnable {
        override fun run() {
            fadeScrollBarRunner.start()
        }
    }

    private inner class FadeScrollBarRunner(context: Context) : Runnable {
        private val scroller: Scroller = Scroller(context, DecelerateInterpolator())

        init {
            scroller.forceFinished(true)
        }

        fun start() {
            scroller.startScroll(scrollBarAlpha, 0, -scrollBarAlpha, 0, 300)
            handler.post(this)
        }

        val isRunning: Boolean
            get() = !scroller.isFinished

        fun abort() {
            scroller.forceFinished(true)
        }

        override fun run() {
            if (scroller.computeScrollOffset()) {
                scrollBarPaint.alpha = scroller.currX
                invalidateView()
                handler.postDelayed(this, 60)
            }
        }
    }
}