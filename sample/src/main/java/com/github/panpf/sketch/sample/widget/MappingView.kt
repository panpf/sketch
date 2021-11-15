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

package com.github.panpf.sketch.sample.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.zoom.BlockDisplayer
import com.github.panpf.sketch.zoom.Size

class MappingView : SketchImageView {

    private var depBlockDisplayer: BlockDisplayer? = null

    private var visibleMappingRect: Rect? = null
    private var visiblePaint: Paint? = null
    private var drawBlockPaint: Paint? = null
    private var realSrcRectPaint: Paint? = null
    private var originSrcRectPaint: Paint? = null
    private var loadingBlockPaint: Paint? = null

    private val drawableSize = Size()
    private val visibleRect = Rect()

    private var detector: GestureDetector? = null
    private var onSingleClickListener: OnSingleClickListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun init(context: Context) {
        visibleMappingRect = Rect()
        visiblePaint = Paint()
        visiblePaint!!.color = Color.RED
        visiblePaint!!.style = Paint.Style.STROKE
        visiblePaint!!.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()

        drawBlockPaint = Paint()
        drawBlockPaint!!.color = Color.parseColor("#88A020F0")
        drawBlockPaint!!.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()
        drawBlockPaint!!.style = Paint.Style.STROKE

        loadingBlockPaint = Paint()
        loadingBlockPaint!!.color = Color.parseColor("#880000CD")
        loadingBlockPaint!!.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()
        loadingBlockPaint!!.style = Paint.Style.STROKE

        realSrcRectPaint = Paint()
        realSrcRectPaint!!.color = Color.parseColor("#8800CD00")
        realSrcRectPaint!!.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()
        realSrcRectPaint!!.style = Paint.Style.STROKE

        originSrcRectPaint = Paint()
        originSrcRectPaint!!.color = Color.parseColor("#88FF7F24")
        originSrcRectPaint!!.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()
        originSrcRectPaint!!.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (depBlockDisplayer != null && depBlockDisplayer!!.isReady) {
            val widthScale = depBlockDisplayer!!.imageSize!!.x.toFloat() / width
            val heightScale = depBlockDisplayer!!.imageSize!!.y.toFloat() / height

            for (block in depBlockDisplayer!!.blockList) {
                if (!block.isEmpty) {
                    canvas.drawRect(
                        (block.srcRect.left + 1) / widthScale,
                        (block.srcRect.top + 1) / heightScale,
                        (block.srcRect.right - 1) / widthScale,
                        (block.srcRect.bottom - 1) / heightScale, drawBlockPaint!!
                    )
                } else if (!block.isDecodeParamEmpty) {
                    canvas.drawRect(
                        (block.srcRect.left + 1) / widthScale,
                        (block.srcRect.top + 1) / heightScale,
                        (block.srcRect.right - 1) / widthScale,
                        (block.srcRect.bottom - 1) / heightScale, loadingBlockPaint!!
                    )
                }
            }

            val drawSrcRect = depBlockDisplayer!!.drawSrcRect
            if (!drawSrcRect.isEmpty) {
                canvas.drawRect(
                    drawSrcRect.left / widthScale,
                    drawSrcRect.top / heightScale,
                    drawSrcRect.right / widthScale,
                    drawSrcRect.bottom / heightScale, originSrcRectPaint!!
                )
            }

            val decodeSrcRect = depBlockDisplayer!!.decodeSrcRect
            if (!decodeSrcRect.isEmpty) {
                canvas.drawRect(
                    decodeSrcRect.left / widthScale,
                    decodeSrcRect.top / heightScale,
                    decodeSrcRect.right / widthScale,
                    decodeSrcRect.bottom / heightScale, realSrcRectPaint!!
                )
            }
        }

        if (!visibleMappingRect!!.isEmpty) {
            canvas.drawRect(visibleMappingRect!!, visiblePaint!!)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        recover()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        resetViewSize()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (detector == null) {
            return super.onTouchEvent(event)
        }

        detector!!.onTouchEvent(event)
        return true
    }

    private val imageUri: String?
        get() {
            return displayCache?.uri
        }

    fun update(newDrawableSize: Size, newVisibleRect: Rect) {
        if (newDrawableSize.width == 0 || newDrawableSize.height == 0 || newVisibleRect.isEmpty) {
            SLog.wmf(
                "MappingView",
                "update. drawableWidth is 0 or newVisibleRect is empty. %s. drawableSize=%s, newVisibleRect=%s",
                imageUri ?: "",
                newDrawableSize.toString(),
                newVisibleRect.toShortString()
            )

            drawableSize.set(0, 0)
            visibleRect.setEmpty()

            if (!visibleMappingRect!!.isEmpty) {
                visibleMappingRect!!.setEmpty()
                invalidate()
            }
            return
        }

        drawableSize.set(newDrawableSize.width, newDrawableSize.height)
        visibleRect.set(newVisibleRect)

        if (!isUsableDrawable || width == 0 || height == 0) {
            if (SLog.isLoggable(SLog.VERBOSE)) {
                SLog.vmf(
                    "MappingView",
                    "update. view size is 0 or getDrawable() is null. %s",
                    imageUri!!
                )
            }

            if (!visibleMappingRect!!.isEmpty) {
                visibleMappingRect!!.setEmpty()
                invalidate()
            }
            return
        }

        if (resetViewSize()) {
            return
        }
        resetVisibleMappingRect()
        invalidate()
    }

    fun blockChanged(blockDisplayer: BlockDisplayer) {
        this.depBlockDisplayer = blockDisplayer
        invalidate()
    }

    fun setOnSingleClickListener(onSingleClickListener: OnSingleClickListener?) {
        this.onSingleClickListener = onSingleClickListener
        isClickable = onSingleClickListener != null
        if (detector == null) {
            detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return this@MappingView.onSingleClickListener!!.onSingleClick(e.x, e.y)
                }
            })
        }
    }

    private fun recover() {
        if (!visibleRect.isEmpty) {
            update(drawableSize, visibleRect)
        }
    }

    val isUsableDrawable: Boolean
        get() {
            val drawable = drawable
            return drawable != null && drawable !is SketchLoadingDrawable
        }

    private fun resetViewSize(): Boolean {
        val drawable = drawable ?: return true

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val maxWidth: Int
        val maxHeight: Int
        if (Math.max(drawableWidth, drawableHeight).toFloat() / Math.min(
                drawableWidth,
                drawableHeight
            ) >= 4
        ) {
            maxWidth = Math.round(resources.displayMetrics.widthPixels / 2f)
            maxHeight = Math.round(resources.displayMetrics.heightPixels / 2f)
        } else {
            maxWidth = Math.round(resources.displayMetrics.widthPixels / 4f)
            maxHeight = Math.round(resources.displayMetrics.heightPixels / 4f)
        }
        val newViewWidth: Int
        val newViewHeight: Int
        if (drawableWidth > maxWidth || drawableHeight > maxHeight) {
            val finalScale =
                Math.min(maxWidth.toFloat() / drawableWidth, maxHeight.toFloat() / drawableHeight)
            newViewWidth = Math.round(drawableWidth * finalScale)
            newViewHeight = Math.round(drawableHeight * finalScale)
        } else {
            newViewWidth = drawableWidth
            newViewHeight = drawableHeight
        }

        val layoutParams = layoutParams!!
        if (newViewWidth != layoutParams.width || newViewHeight != layoutParams.height) {
            layoutParams.width = newViewWidth
            layoutParams.height = newViewHeight
            setLayoutParams(layoutParams)

            return true
        } else {
            return false
        }
    }

    private fun resetVisibleMappingRect() {
        val selfWidth = width
        val selfHeight = height
        val widthScale = selfWidth.toFloat() / drawableSize.width
        val heightScale = selfHeight.toFloat() / drawableSize.height
        this.visibleMappingRect!!.set(
            Math.round(visibleRect.left * widthScale),
            Math.round(visibleRect.top * heightScale),
            Math.round(visibleRect.right * widthScale),
            Math.round(visibleRect.bottom * heightScale)
        )
    }

    fun interface OnSingleClickListener {
        fun onSingleClick(x: Float, y: Float): Boolean
    }
}
