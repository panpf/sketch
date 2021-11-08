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
package com.github.panpf.sketch.viewfun

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import com.github.panpf.sketch.SLog.Companion.em
import com.github.panpf.sketch.request.DisplayCache
import com.github.panpf.sketch.shaper.ImageShaper

/**
 * 显示按下状态，按下后会在图片上显示一个黑色半透明的蒙层，此功能需要注册点击事件或设置 Clickable 为 true
 */
class ShowPressedFunction(private val view: FunctionPropertyView) : ViewFunction() {

    companion object {
        const val DEFAULT_MASK_COLOR = 0x33000000
        private const val NAME = "ShowPressedFunction"
    }

    private var maskShaper: ImageShaper? = null
    private var maskColor = DEFAULT_MASK_COLOR
    private var showProcessed = false
    private var singleTapUp = false
    private var maskPaint: Paint? = null
    private val gestureDetector: GestureDetector
    private var bounds: Rect? = null

    init {
        gestureDetector = GestureDetector(view.context, PressedStatusManager())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (view.isClickable) {
            gestureDetector.onTouchEvent(event)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> if (showProcessed && !singleTapUp) {
                    showProcessed = false
                    view.invalidate()
                }
            }
        }
        return false
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (!showProcessed) {
            return
        }
        val shaper = getMaskShaper()
        if (shaper != null) {
            canvas.save()
            try {
                val bounds = bounds ?: Rect().apply {
                    this@ShowPressedFunction.bounds = this
                }
                bounds.set(
                    view.paddingLeft,
                    view.paddingTop,
                    view.width - view.paddingRight,
                    view.height - view.paddingBottom
                )
                val maskPath = shaper.getPath(bounds)
                canvas.clipPath(maskPath)
            } catch (e: UnsupportedOperationException) {
                em(
                    NAME,
                    "The current environment doesn't support clipPath has shut down automatically hardware acceleration"
                )
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                e.printStackTrace()
            }
        }
        val maskPaint = maskPaint ?: Paint().apply {
            color = maskColor
            isAntiAlias = true
            this@ShowPressedFunction.maskPaint = this
        }
        canvas.drawRect(
            view.paddingLeft.toFloat(),
            view.paddingTop.toFloat(),
            (view.width - view.paddingRight).toFloat(),
            (view.height - view.paddingBottom).toFloat(),
            maskPaint
        )
        if (shaper != null) {
            canvas.restore()
        }
    }

    fun setMaskColor(@ColorInt maskColor: Int): Boolean {
        if (this.maskColor == maskColor) {
            return false
        }
        this.maskColor = maskColor
        maskPaint?.color = maskColor
        return true
    }

    private fun getMaskShaper(): ImageShaper? {
        if (maskShaper != null) {
            return maskShaper
        }
        val displayCache: DisplayCache? = view.displayCache
        val shaperFromCacheOptions = displayCache?.options?.shaper
        if (shaperFromCacheOptions != null) {
            return shaperFromCacheOptions
        }
        return view.options.shaper
    }

    fun setMaskShaper(maskShaper: ImageShaper?): Boolean {
        if (this.maskShaper === maskShaper) {
            return false
        }
        this.maskShaper = maskShaper
        return true
    }

    private inner class PressedStatusManager : SimpleOnGestureListener() {
        private val runnable = Runnable {
            showProcessed = false
            view.invalidate()
        }

        override fun onDown(event: MotionEvent): Boolean {
            showProcessed = false
            singleTapUp = false
            view.removeCallbacks(runnable)
            return true
        }

        override fun onShowPress(e: MotionEvent) {
            super.onShowPress(e)
            showProcessed = true
            view.invalidate()
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            singleTapUp = true
            if (!showProcessed) {
                showProcessed = true
                view.invalidate()
            }
            view.postDelayed(runnable, 120)
            return super.onSingleTapUp(e)
        }
    }
}