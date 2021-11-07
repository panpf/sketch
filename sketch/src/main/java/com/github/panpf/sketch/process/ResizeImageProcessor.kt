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
package com.github.panpf.sketch.process

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Resize

open class ResizeImageProcessor : ImageProcessor {
    override fun process(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {
        if (bitmap.isRecycled) {
            return bitmap
        }
        if (resize == null || resize.width == 0 || resize.height == 0 ||
            bitmap.width == resize.width && bitmap.height == resize.height
        ) {
            return bitmap
        }
        val resizeCalculator = sketch.configuration.resizeCalculator
        val mapping = resizeCalculator.calculator(
            bitmap.width, bitmap.height,
            resize.width, resize.height, resize.scaleType, resize.mode == Resize.Mode.EXACTLY_SAME
        )
        var config = bitmap.config
        if (config == null) {
            config = if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
        }
        val bitmapPool = sketch.configuration.bitmapPool
        val resizeBitmap = bitmapPool.getOrMake(mapping.imageWidth, mapping.imageHeight, config)
        val canvas = Canvas(resizeBitmap)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null)
        return resizeBitmap
    }

    override fun toString(): String {
        return "ResizeImageProcessor"
    }

    override val key: String?
        get() = "Resize"
}