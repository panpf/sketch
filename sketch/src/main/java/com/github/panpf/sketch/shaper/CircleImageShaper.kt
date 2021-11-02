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
package com.github.panpf.sketch.shaper

import android.graphics.*
import com.github.panpf.sketch.request.ShapeSize

/**
 * 圆形的绘制时图片整形器，还可以有描边
 */
class CircleImageShaper : ImageShaper {

    var strokeWidth = 0
        private set
    var strokeColor = 0
        private set
    private var strokePaint: Paint? = null
    private var boundsBack: Rect? = null
    private var path: Path? = null

    fun setStroke(strokeColor: Int, strokeWidth: Int): CircleImageShaper {
        this.strokeColor = strokeColor
        this.strokeWidth = strokeWidth
        updatePaint()
        return this
    }

    private fun updatePaint() {
        if (strokeColor != 0 && strokeWidth > 0) {
            if (strokePaint == null) {
                strokePaint = Paint()
                strokePaint!!.style = Paint.Style.STROKE
                strokePaint!!.isAntiAlias = true
            }
            strokePaint!!.color = strokeColor
            strokePaint!!.strokeWidth = strokeWidth.toFloat()
        }
    }

    override fun getPath(bounds: Rect): Path {
        val cachePath = path
        if (cachePath != null && boundsBack != null && boundsBack == bounds) {
            return cachePath
        }
        if (boundsBack == null) {
            boundsBack = Rect()
        }
        boundsBack!!.set(bounds)
        val path = path ?: Path().apply {
            this@CircleImageShaper.path = this
        }
        path.reset()
        val centerX = bounds.centerX()
        val centerY = bounds.centerX()
        val radius = (centerX - bounds.left).coerceAtLeast(centerY - bounds.top)
        path.addCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), Path.Direction.CW)
        return path
    }

    override fun onUpdateShaderMatrix(
        matrix: Matrix,
        bounds: Rect,
        bitmapWidth: Int,
        bitmapHeight: Int,
        shapeSize: ShapeSize?,
        srcRect: Rect
    ) {
    }

    override fun draw(canvas: Canvas, paint: Paint, bounds: Rect) {
        val widthRadius = bounds.width() / 2f
        val heightRadius = bounds.height() / 2f
        val cx = bounds.left + widthRadius
        val cy = bounds.top + heightRadius
        val radius = Math.min(widthRadius, heightRadius)
        paint.isAntiAlias = true
        canvas.drawCircle(cx, cy, radius, paint)
        val strokePaint = strokePaint
        if (strokeColor != 0 && strokeWidth > 0 && strokePaint != null) {
            // 假如描边宽度是10，那么会是5个像素在图片外面，5哥像素在图片里面，
            // 所以往图片里面偏移描边宽度的一半，让描边都在图片里面
            val offset = strokeWidth / 2f
            canvas.drawCircle(cx, cy, radius - offset, strokePaint)
        }
    }
}