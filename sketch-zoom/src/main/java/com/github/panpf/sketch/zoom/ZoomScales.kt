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
package com.github.panpf.sketch.zoom

import android.content.Context
import android.widget.ImageView.ScaleType

interface ZoomScales {

    /**
     * 最小缩放比例
     */
    val minZoomScale: Float

    /**
     * 最大缩放比例
     */
    val maxZoomScale: Float

    /**
     * 最大初始缩放比例
     */
    val initZoomScale: Float

    /**
     * 能够看到图片全貌的缩放比例
     */
    val fullZoomScale: Float

    /**
     * 获取能够宽或高能够填满屏幕的缩放比例
     */
    val fillZoomScale: Float

    /**
     * 能够让图片按照真实尺寸一比一显示的缩放比例
     */
    val originZoomScale: Float

    /**
     * 双击缩放所的比例组
     */
    val zoomScales: FloatArray?

    /**
     * 重置
     */
    fun reset(
        context: Context,
        sizes: Sizes,
        scaleType: ScaleType?,
        rotateDegrees: Float,
        readMode: Boolean
    )

    /**
     * 清理一下
     */
    fun clean()
}