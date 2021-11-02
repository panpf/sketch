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
import com.github.panpf.sketch.shaper.ImageShaper
import com.github.panpf.sketch.shaper.RoundRectImageShaper
import com.github.panpf.sketch.request.ShapeSize

/**
 * 圆角矩形的绘制时图片整形器，还可以有描边
 */
class RoundRectImageShaper(radiis: FloatArray?) : ImageShaper {

    val outerRadii: FloatArray
    private val boundsCached = Rect()
    private val bitmapPath = Path()
    private var innerStrokePath: Path? = null
    private var outerStrokePath: Path? = null
    private var clipPath: Path? = null
    var strokeWidth = 0
        private set
    var strokeColor = 0
        private set
    private var strokePaint: Paint? = null
    private var boundsBack: Rect? = null
    private var rectF: RectF? = null
    private var path: Path? = null

    constructor(
        topLeftRadii: Float,
        topRightRadii: Float,
        bottomLeftRadii: Float,
        bottomRightRadii: Float
    ) : this(
        floatArrayOf(
            topLeftRadii, topLeftRadii,
            topRightRadii, topRightRadii,
            bottomLeftRadii, bottomLeftRadii,
            bottomRightRadii, bottomRightRadii
        )
    )

    constructor(radii: Float) : this(radii, radii, radii, radii)

    init {
        if (radiis == null || radiis.size < 8) {
            throw ArrayIndexOutOfBoundsException("outer radii must have >= 8 values")
        }
        outerRadii = radiis
    }

    fun setStroke(strokeColor: Int, strokeWidth: Int): RoundRectImageShaper {
        this.strokeColor = strokeColor
        this.strokeWidth = strokeWidth
        updatePaint()
        return this
    }

    private fun updatePaint() {
        if (hasStroke()) {
            if (strokePaint == null) {
                strokePaint = Paint()
                strokePaint!!.style = Paint.Style.STROKE
                strokePaint!!.isAntiAlias = true
            }
            strokePaint!!.color = strokeColor
            strokePaint!!.strokeWidth = strokeWidth.toFloat()
            if (innerStrokePath == null) {
                innerStrokePath = Path()
            }
            if (outerStrokePath == null) {
                outerStrokePath = Path()
            }
            if (clipPath == null) {
                clipPath = Path()
            }
        }
    }

    private fun hasStroke(): Boolean {
        return strokeColor != 0 && strokeWidth > 0
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
            this@RoundRectImageShaper.path = this
        }
        path.reset()
        val rectF = rectF ?: RectF().apply {
            this@RoundRectImageShaper.rectF = this
        }
        rectF.set(boundsBack)
        path.addRoundRect(rectF, outerRadii, Path.Direction.CW)
        return path
    }

    override fun onUpdateShaderMatrix(
        matrix: Matrix, bounds: Rect, bitmapWidth: Int, bitmapHeight: Int,
        shapeSize: ShapeSize?, srcRect: Rect
    ) {
    }

    override fun draw(canvas: Canvas, paint: Paint, bounds: Rect) {
        if (boundsCached != bounds) {
            val rectF = RectF(bounds)
            bitmapPath.reset()
            bitmapPath.addRoundRect(rectF, outerRadii, Path.Direction.CW)

            // 假如描边宽度是10，那么会是5个像素在图片外面，5个像素在图片里面
            // 因为描边会有一半是在图片外面，所以如果图片被紧紧（没有缝隙）包括在Layout中，那么描边就会丢失一半
            if (hasStroke()) {
                // 内圈，往图片里面偏移描边宽度的一半，让描边都在图片里面，都在里面导致圆角部分会露出来一些
                val offset = strokeWidth / 2f
                rectF[bounds.left + offset, bounds.top + offset, bounds.right - offset] =
                    bounds.bottom - offset
                innerStrokePath!!.reset()
                innerStrokePath!!.addRoundRect(rectF, outerRadii, Path.Direction.CW)

                // 外圈，主要用来盖住内圈描边无法覆盖导致露出的圆角部分
                outerStrokePath!!.reset()
                rectF[bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat()] =
                    bounds.bottom.toFloat()
                outerStrokePath!!.addRoundRect(rectF, outerRadii, Path.Direction.CW)
                rectF.set(bounds)
                clipPath!!.addRoundRect(rectF, outerRadii, Path.Direction.CW)
            }
        }
        paint.isAntiAlias = true
        canvas.drawPath(bitmapPath, paint)
        if (hasStroke() && strokePaint != null) {
            // 裁掉外圈跑出图片的部分
            canvas.clipPath(clipPath)
            canvas.drawPath(innerStrokePath, strokePaint)
            canvas.drawPath(outerStrokePath, strokePaint)
        }
    }
}