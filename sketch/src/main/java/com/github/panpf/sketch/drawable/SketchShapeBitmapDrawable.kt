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
package com.github.panpf.sketch.drawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ResizeCalculator
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.request.ShapeSize
import com.github.panpf.sketch.shaper.ImageShaper
import com.github.panpf.sketch.util.ExifInterface

/**
 * 可以改变 [BitmapDrawable] 的形状和尺寸
 */
class SketchShapeBitmapDrawable(
    context: Context,
    bitmapDrawable: BitmapDrawable,
    shapeSize: ShapeSize?,
    shaper: ImageShaper?
) : Drawable(), SketchRefDrawable {

    val bitmapDrawable: BitmapDrawable
    private var shapeSize: ShapeSize? = null
    private var shaper: ImageShaper? = null
    private val paint: Paint
    private val srcRect: Rect
    private var bitmapShader: BitmapShader? = null
    private var refDrawable: SketchRefDrawable? = null
    private var sketchDrawable: SketchDrawable? = null
    private val resizeCalculator: ResizeCalculator

    constructor(context: Context, bitmapDrawable: BitmapDrawable, shapeSize: ShapeSize?) : this(
        context,
        bitmapDrawable,
        shapeSize,
        null
    )

    constructor(context: Context, bitmapDrawable: BitmapDrawable, shaper: ImageShaper?) : this(
        context,
        bitmapDrawable,
        null,
        shaper
    ) {
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val bitmap = bitmapDrawable.bitmap
        if (bounds.isEmpty || bitmap == null || bitmap.isRecycled) {
            return
        }
        if (shaper != null && bitmapShader != null) {
            shaper!!.draw(canvas, paint, bounds)
        } else {
            canvas.drawBitmap(bitmap, if (!srcRect.isEmpty) srcRect else null, bounds, paint)
        }
    }

    override fun getIntrinsicWidth(): Int {
        return if (shapeSize != null) shapeSize!!.width else bitmapDrawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return if (shapeSize != null) shapeSize!!.height else bitmapDrawable.intrinsicHeight
    }

    override fun getAlpha(): Int {
        return paint.alpha
    }

    override fun setAlpha(alpha: Int) {
        val oldAlpha = paint.alpha
        if (alpha != oldAlpha) {
            paint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun getColorFilter(): ColorFilter {
        return paint.colorFilter
    }

    override fun setColorFilter(cf: ColorFilter) {
        paint.colorFilter = cf
        invalidateSelf()
    }

    override fun setDither(dither: Boolean) {
        paint.isDither = dither
        invalidateSelf()
    }

    override fun setFilterBitmap(filter: Boolean) {
        paint.isFilterBitmap = filter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        val bitmap = bitmapDrawable.bitmap
        return if (bitmap.hasAlpha() || paint.alpha < 255) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        val bitmapWidth = bitmapDrawable.bitmap.width
        val bitmapHeight = bitmapDrawable.bitmap.height
        if (boundsWidth == 0 || boundsHeight == 0 || bitmapWidth == 0 || bitmapHeight == 0) {
            srcRect.setEmpty()
        } else if (bitmapWidth.toFloat() / bitmapHeight.toFloat() == boundsWidth.toFloat() / boundsHeight.toFloat()) {
            srcRect[0, 0, bitmapWidth] = bitmapHeight
        } else {
            val scaleType = if (shapeSize != null) shapeSize!!.scaleType else ScaleType.FIT_CENTER
            val mapping = resizeCalculator.calculator(
                bitmapWidth,
                bitmapHeight,
                boundsWidth,
                boundsHeight,
                scaleType,
                true
            )
            srcRect.set(mapping.srcRect)
        }
        if (shaper != null && bitmapShader != null) {
            val widthScale = boundsWidth.toFloat() / bitmapWidth
            val heightScale = boundsHeight.toFloat() / bitmapHeight

            // 缩放图片充满bounds
            val shaderMatrix = Matrix()
            val scale = Math.max(widthScale, heightScale)
            shaderMatrix.postScale(scale, scale)

            // 显示图片中间部分
            if (!srcRect.isEmpty) {
                shaderMatrix.postTranslate(-srcRect.left * scale, -srcRect.top * scale)
            }
            shaper!!.onUpdateShaderMatrix(
                shaderMatrix,
                bounds,
                bitmapWidth,
                bitmapHeight,
                shapeSize,
                srcRect
            )
            bitmapShader!!.setLocalMatrix(shaderMatrix)
            paint.shader = bitmapShader
        }
    }

    fun getShapeSize(): ShapeSize? {
        return shapeSize
    }

    fun setShapeSize(shapeSize: ShapeSize?) {
        this.shapeSize = shapeSize
        invalidateSelf()
    }

    fun getShaper(): ImageShaper? {
        return shaper
    }

    fun setShaper(shaper: ImageShaper?) {
        this.shaper = shaper
        if (this.shaper != null) {
            if (bitmapShader == null) {
                bitmapShader = BitmapShader(
                    bitmapDrawable.bitmap,
                    Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT
                )
                paint.shader = bitmapShader
            }
        } else {
            if (bitmapShader != null) {
                bitmapShader = null
                paint.shader = null
            }
        }
        invalidateSelf()
    }

    override val key: String?
        get() = sketchDrawable?.key
    override val uri: String?
        get() = sketchDrawable?.uri
    override val originWidth: Int
        get() = sketchDrawable?.originWidth ?: 0
    override val originHeight: Int
        get() = sketchDrawable?.originHeight ?: 0
    override val mimeType: String?
        get() = sketchDrawable?.mimeType
    override val exifOrientation: Int
        get() = sketchDrawable?.exifOrientation
            ?: ExifInterface.ORIENTATION_UNDEFINED
    override val byteCount: Int
        get() = sketchDrawable?.byteCount ?: 0
    override val bitmapConfig: Bitmap.Config?
        get() = sketchDrawable?.bitmapConfig
    override val imageFrom: ImageFrom?
        get() = sketchDrawable?.imageFrom
    override val info: String?
        get() = sketchDrawable?.info

    override fun setIsDisplayed(callingStation: String, displayed: Boolean) {
        refDrawable?.setIsDisplayed(callingStation, displayed)
    }

    override fun setIsWaitingUse(callingStation: String, waitingUse: Boolean) {
        refDrawable?.setIsWaitingUse(callingStation, waitingUse)
    }

    override val isRecycled: Boolean
        get() = refDrawable?.isRecycled != false

    companion object {
        private const val DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG
    }

    init {
        val bitmap = bitmapDrawable.bitmap
        require(!(bitmap == null || bitmap.isRecycled)) { if (bitmap == null) "bitmap is null" else "bitmap recycled" }
        require(!(shapeSize == null && shaper == null)) { "shapeSize is null and shapeImage is null" }
        this.bitmapDrawable = bitmapDrawable
        paint = Paint(DEFAULT_PAINT_FLAGS)
        srcRect = Rect()
        resizeCalculator = Sketch.with(context).configuration.resizeCalculator
        setShapeSize(shapeSize)
        setShaper(shaper)
        if (bitmapDrawable is SketchRefDrawable) {
            refDrawable = bitmapDrawable
        }
        if (bitmapDrawable is SketchDrawable) {
            sketchDrawable = bitmapDrawable
        }
    }
}