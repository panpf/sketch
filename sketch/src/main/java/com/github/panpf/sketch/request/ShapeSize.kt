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
package com.github.panpf.sketch.request

import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable
import java.util.*

/**
 * 用来搭配 [SketchShapeBitmapDrawable] 在绘制时修改图片的尺寸，用来替代大多数情况下对 [Resize] 的依赖
 *
 * 当多张图片的 inSampleSize 一样，那么读到内存里的 [android.graphics.Bitmap] 尺寸就一样，但是因为 [Resize] 不一样，导致会产生多个差别很小的 [android.graphics.Bitmap]，这样就降低了内存缓存利用率
 *
 * 当使用 [ShapeSize] 时，就可以使用同一个 [android.graphics.Bitmap] 在绘制时显示出不同的尺寸，避免了产生多个差别很小的 [android.graphics.Bitmap]，提高了内存缓存利用率
 */
data class ShapeSize(
    val width: Int,
    val height: Int,
    var scaleType: ScaleType? = null
) {

    constructor(width: Int, height: Int) : this(width, height, null)

    override fun toString(): String {
        return String.format(Locale.US, "ShapeSize(%dx%d)", width, height)
    }

    companion object {
        /**
         * 使用 [ImageView] 的固定尺寸作为 [ShapeSize]
         */
        @JvmStatic
        val BY_VIEW_FIXED_SIZE = ShapeSize(0, 0)

        @JvmStatic
        fun byViewFixedSize() = BY_VIEW_FIXED_SIZE
    }
}