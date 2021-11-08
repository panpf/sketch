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
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.view.View
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.util.SketchUtils

/**
 * 显示图片来源功能，会在 [android.widget.ImageView] 的左上角显示一个三角形的色块用于标识本次图片是从哪里来的
 *
 *  * 红色：网络
 *  * 黄色：磁盘缓存
 *  * 蓝色：本地
 *  * 绿色：内存缓存
 *  * 紫色：内存
 *
 */
class ShowImageFromFunction(private val view: View) : ViewFunction() {
    private var imageFromPath: Path? = null
    private var imageFromPaint: Paint? = null
    var imageFrom: ImageFrom? = null
    override fun onReadyDisplay(uri: String): Boolean {
        imageFrom = null
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        initImageFromPath()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (imageFrom == null) {
            return
        }
        if (imageFromPath == null) {
            initImageFromPath()
        }
        val imageFromPaint = imageFromPaint ?: Paint().apply {
            this@ShowImageFromFunction.imageFromPaint = this
            isAntiAlias = true
        }
        when (imageFrom) {
            ImageFrom.MEMORY_CACHE -> imageFromPaint.color = FROM_FLAG_COLOR_MEMORY_CACHE
            ImageFrom.DISK_CACHE -> imageFromPaint.color = FROM_FLAG_COLOR_DISK_CACHE
            ImageFrom.NETWORK -> imageFromPaint.color = FROM_FLAG_COLOR_NETWORK
            ImageFrom.LOCAL -> imageFromPaint.color = FROM_FLAG_COLOR_LOCAL
            ImageFrom.MEMORY -> imageFromPaint.color = FROM_FLAG_COLOR_MEMORY
            else -> return
        }
        canvas.drawPath(imageFromPath, imageFromPaint)
    }

    private fun initImageFromPath() {
        if (imageFromPath == null) {
            imageFromPath = Path()
        } else {
            imageFromPath!!.reset()
        }
        val x = view.width / 10
        val y = view.width / 10
        val left2 = view.paddingLeft
        val top2 = view.paddingTop
        imageFromPath!!.moveTo(left2.toFloat(), top2.toFloat())
        imageFromPath!!.lineTo((left2 + x).toFloat(), top2.toFloat())
        imageFromPath!!.lineTo(left2.toFloat(), (top2 + y).toFloat())
        imageFromPath!!.close()
    }

    override fun onDetachedFromWindow(): Boolean {
        // drawable都已经被清空了，图片来源标识当然要重置了
        imageFrom = null
        return false
    }

    override fun onDrawableChanged(
        callPosition: String,
        oldDrawable: Drawable?,
        newDrawable: Drawable?
    ): Boolean {
        val oldImageFrom = imageFrom
        var newImageFrom: ImageFrom? = null
        val lastDrawable = SketchUtils.getLastDrawable(newDrawable)
        if (lastDrawable !is SketchLoadingDrawable && lastDrawable is SketchDrawable) {
            val sketchDrawable = lastDrawable as SketchDrawable
            newImageFrom = sketchDrawable.imageFrom
        }
        imageFrom = newImageFrom
        return oldImageFrom != newImageFrom
    }

    companion object {
        private const val FROM_FLAG_COLOR_MEMORY = -0x775fdf10
        private const val FROM_FLAG_COLOR_MEMORY_CACHE = -0x77ff0100
        private const val FROM_FLAG_COLOR_LOCAL = -0x77ffff01
        private const val FROM_FLAG_COLOR_DISK_CACHE = -0x77000100
        private const val FROM_FLAG_COLOR_NETWORK = -0x77010000
    }
}