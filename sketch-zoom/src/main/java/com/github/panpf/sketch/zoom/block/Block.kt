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
package com.github.panpf.sketch.zoom.block

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.zoom.block.internal.KeyCounter

/**
 * 碎片
 */
class Block {
    @JvmField
    var drawRect = Rect()

    @JvmField
    var srcRect = Rect()

    @JvmField
    var inSampleSize = 0

    @JvmField
    var scale = -1f

    @JvmField
    var decoder: ImageRegionDecoder? = null

    @JvmField
    var bitmap: Bitmap? = null

    @JvmField
    var bitmapDrawSrcRect = Rect()

    // 用来取消解码任务，开始解码这个碎片的时候会获取当时的key
    // 然后在解码过程的各个环节都会检验key是否已经失效
    // 因此如果想取消解码这个碎片，只需刷新key即可
    private val keyCounter = KeyCounter()
    val isEmpty: Boolean
        get() = bitmap == null || bitmap!!.isRecycled || isDecodeParamEmpty
    val isDecodeParamEmpty: Boolean
        get() = (drawRect.isEmpty || drawRect.isEmpty
                || srcRect.isEmpty || srcRect.isEmpty
                || inSampleSize == 0 || scale == -1f)

    fun isExpired(key: Int): Boolean {
        return keyCounter.key != key
    }

    fun clean(bitmapPoolHelper: BitmapPoolHelper) {
        if (bitmap != null) {
            bitmapPoolHelper.freeBitmapToPool(bitmap)
            bitmap = null
        }
        bitmapDrawSrcRect.setEmpty()
        srcRect.setEmpty()
        drawRect.setEmpty()
        inSampleSize = 0
        scale = -1f
        decoder = null
    }

    val key: Int
        get() = keyCounter.key

    fun refreshKey() {
        keyCounter.refresh()
    }

    val info: String
        get() {
            val builder = StringBuilder()
            builder.append("(")
            builder.append("drawRect:").append(drawRect.toShortString())
            builder.append(",")
            builder.append("srcRect:").append(srcRect.toShortString())
            builder.append(",")
            builder.append("inSampleSize:").append(inSampleSize)
            builder.append(",")
            builder.append("scale:").append(scale)
            builder.append(",")
            builder.append("key:").append(keyCounter.key)
            builder.append(",")
            builder.append("hashCode:").append(Integer.toHexString(hashCode()))
            builder.append(")")
            return builder.toString()
        }

    companion object {
        /**
         * 将一个碎片列表转换成字符串
         */
        @JvmStatic
        fun blockListToString(blockList: List<Block>?): String? {
            if (blockList == null) {
                return null
            }
            val builder = StringBuilder()
            builder.append("[")
            for (block in blockList) {
                if (builder.length > 1) {
                    builder.append(",")
                }
                builder.append("\"")
                builder.append(block.drawRect.left).append(",")
                builder.append(block.drawRect.top).append(",")
                builder.append(block.drawRect.right).append(",")
                builder.append(block.drawRect.bottom)
                builder.append("\"")
            }
            builder.append("]")
            return builder.toString()
        }
    }
}