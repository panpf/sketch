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
 * 用于绘制时改变图片的形状
 */
interface ImageShaper {
    /**
     * 获取形状 Path
     */
    fun getPath(bounds: Rect): Path

    /**
     * [Shader] 的 [Matrix] 更新时回调
     *
     * @param matrix       [Shader] 的 [Matrix]
     * @param bounds       [Rect]. 绘制区域的边界位置
     * @param bitmapWidth  bitmap 宽
     * @param bitmapHeight bitmap 高
     * @param shapeSize    [ShapeSize]
     * @param srcRect      [Rect]. 原图中的位置
     */
    fun onUpdateShaderMatrix(
        matrix: Matrix,
        bounds: Rect,
        bitmapWidth: Int,
        bitmapHeight: Int,
        shapeSize: ShapeSize?,
        srcRect: Rect
    )

    /**
     * 绘制
     *
     * @param canvas [Canvas]
     * @param paint  [Paint]
     * @param bounds [Rect]. 绘制区域的边界位置
     */
    fun draw(canvas: Canvas, paint: Paint, bounds: Rect)
}