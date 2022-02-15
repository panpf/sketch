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
package com.github.panpf.sketch.zoom.internal

import android.widget.ImageView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.getLastDrawable

class Sizes {
    var viewSize = Size() // ImageView 尺寸
    var imageSize = Size() // 原始图尺寸
    var drawableSize = Size() // 预览图尺寸

    val isEmpty: Boolean
        get() = viewSize.isEmpty || imageSize.isEmpty || drawableSize.isEmpty

    fun resetSizes(imageView: ImageView) {
        val imageViewWidth = imageView.width - imageView.paddingLeft - imageView.paddingRight
        val imageViewHeight = imageView.height - imageView.paddingTop - imageView.paddingBottom
        if (imageViewWidth == 0 || imageViewHeight == 0) {
            return
        }
        val drawable = imageView.drawable?.getLastDrawable() ?: return
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        if (drawableWidth == 0 || drawableHeight == 0) {
            return
        }
        viewSize[imageViewWidth] = imageViewHeight
        drawableSize[drawableWidth] = drawableHeight
        if (drawable is SketchDrawable) {
            val sketchDrawable = drawable as SketchDrawable
            imageSize[sketchDrawable.imageInfo.width] = sketchDrawable.imageInfo.height
        } else {
            imageSize[drawableWidth] = drawableHeight
        }
    }

    fun clean() {
        viewSize[0] = 0
        imageSize[0] = 0
        drawableSize[0] = 0
    }
}