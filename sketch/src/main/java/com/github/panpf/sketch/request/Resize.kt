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
import com.github.panpf.sketch.Key
import com.github.panpf.sketch.request.Resize.Mode
import java.util.*

/**
 * 将图片加载到内存中之后根据 [Resize] 进行调整尺寸
 *
 *
 * 修正的原则就是最终返回的图片的比例一定是跟 [Resize] 一样的，但尺寸小于等于 [Resize] ，如果需要必须同 [Resize] 一致可以设置 [Mode.ASPECT_RATIO_SAME]
 */
data class Resize(
    var width: Int,
    var height: Int,
    var scaleType: ScaleType? = null,
    var mode: Mode = Mode.ASPECT_RATIO_SAME,
) : Key {

    constructor(sourceResize: Resize) : this(
        width = sourceResize.width,
        height = sourceResize.height,
        mode = sourceResize.mode,
        scaleType = sourceResize.scaleType,
    )

    constructor(width: Int, height: Int) : this(width, height, null, Mode.ASPECT_RATIO_SAME)

    constructor(width: Int, height: Int, scaleType: ScaleType) : this(width, height, scaleType, Mode.ASPECT_RATIO_SAME)

    override val key: String
        get() = toString()

    override fun toString(): String {
        return String.format(
            Locale.US,
            "Resize(%dx%d-%s-%s)",
            width,
            height,
            if (scaleType != null) scaleType!!.name else "null",
            mode.name
        )
    }

    enum class Mode {
        /**
         * 新图片的尺寸不会比 [Resize] 大，但宽高比一定会一样
         */
        ASPECT_RATIO_SAME,

        /**
         * 即使原图尺寸比 [Resize] 小，也会得到一个跟 [Resize] 尺寸一样的 [android.graphics.Bitmap]
         */
        EXACTLY_SAME
    }

    companion object {
        @JvmStatic
        val BY_VIEW_FIXED_SIZE = Resize(0, 0)

        @JvmStatic
        val BY_VIEW_FIXED_SIZE_EXACTLY_SAME = Resize(0, 0, null, Mode.EXACTLY_SAME)

        /**
         * 使用 [ImageView] 的固定尺寸作为 [Resize]
         */
        @JvmStatic
        fun byViewFixedSize(mode: Mode): Resize = if (mode == Mode.EXACTLY_SAME) {
            BY_VIEW_FIXED_SIZE_EXACTLY_SAME
        } else {
            BY_VIEW_FIXED_SIZE
        }

        /**
         * 使用 [ImageView] 的固定尺寸作为 [Resize]
         */
        @JvmStatic
        fun byViewFixedSize(): Resize = BY_VIEW_FIXED_SIZE
    }
}