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
import android.text.TextUtils
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPoolUtils
import com.github.panpf.sketch.request.Resize

/**
 * 用于组合两个 [ImageProcessor] 一起使用，可以无限嵌套
 */
abstract class WrappedImageProcessor protected constructor(
    val wrappedProcessor: WrappedImageProcessor?
) : ResizeImageProcessor() {

    override fun process(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {

        if (bitmap.isRecycled) {
            return bitmap
        }

        // resize
        var newBitmap = bitmap
        if (!isInterceptResize) {
            newBitmap = super.process(sketch, bitmap, resize, lowQualityImage)
        }

        // wrapped
        if (wrappedProcessor != null) {
            val wrappedBitmap = wrappedProcessor.process(sketch, newBitmap, resize, lowQualityImage)
            if (wrappedBitmap != newBitmap) {
                if (newBitmap != bitmap) {
                    val bitmapPool = sketch.configuration.bitmapPool
                    BitmapPoolUtils.freeBitmapToPool(newBitmap, bitmapPool)
                }
                newBitmap = wrappedBitmap
            }
        }
        return onProcess(sketch, newBitmap, resize, lowQualityImage)
    }

    abstract fun onProcess(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap

    protected open val isInterceptResize: Boolean
        get() = false

    override val key: String?
        get() {
            val selfKey = onGetKey()
            val wrappedKey = wrappedProcessor?.key
            return if (!TextUtils.isEmpty(selfKey)) {
                if (!TextUtils.isEmpty(wrappedKey)) {
                    String.format("%s->%s", selfKey, wrappedKey)
                } else {
                    selfKey
                }
            } else if (!TextUtils.isEmpty(wrappedKey)) {
                wrappedKey
            } else {
                null
            }
        }

    abstract fun onGetKey(): String?

    override fun toString(): String {
        val selfToString = onToString()
        val wrappedToString = wrappedProcessor?.toString()
        return if (TextUtils.isEmpty(wrappedToString)) {
            selfToString
        } else String.format("%s->%s", selfToString, wrappedToString)
    }

    abstract fun onToString(): String
}