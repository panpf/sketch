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

import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.ProcessedResultCacheProcessor
import com.github.panpf.sketch.decode.ThumbnailModeDecodeHelper
import com.github.panpf.sketch.process.ImageProcessor
import com.github.panpf.sketch.util.SketchUtils

open class LoadOptions : DownloadOptions {
    /**
     * The size of the desired bitmap
     */
    var resize: Resize? = null

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    var maxSize: MaxSize? = null

    /**
     * Support gif images
     */
    var isDecodeGifImage = false

    /**
     * Prioritize low quality [Bitmap.Config] when creating bitmaps, the priority is lower than the [.bitmapConfig] attribute
     */
    var isLowQualityImage = false

    /**
     * Priority is given to speed or quality when decoding. Applied to the [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
     */
    var isInPreferQualityOverSpeed = false

    /**
     * Thumbnail mode, together with the [.resize] property, gives a sharper thumbnail, see [ThumbnailModeDecodeHelper]
     */
    var isThumbnailMode = false

    /**
     * Modify Bitmap after decoding the image, If the [.resize] attribute is not null, the default is [com.github.panpf.sketch.process.ResizeImageProcessor]
     */
    var processor: ImageProcessor? = null

    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     * With priority higher than [.lowQualityImage] Property.
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    var bitmapConfig: Bitmap.Config? = null
        set(value) {
            field = if (value == Bitmap.Config.ARGB_4444 && SketchUtils.isDisabledARGB4444) {
                Bitmap.Config.ARGB_8888
            } else {
                value
            }
        }

    /**
     * In order to speed up, save the image processed by [.processor], [.resize] or [.thumbnailMode] to the disk cache,
     * read it directly next time, refer to [ProcessedResultCacheProcessor]
     */
    var isCacheProcessedImageInDisk = false

    /**
     * Disabled get reusable bitmap from [BitmapPool]
     */
    var isBitmapPoolDisabled = false

    /**
     * Disabled correcting picture orientation
     */
    var isCorrectImageOrientationDisabled = false

    constructor()

    @Suppress("unused")
    constructor(from: LoadOptions) {
        copy(from)
    }

    open fun maxSize(maxWidth: Int, maxHeight: Int): LoadOptions {
        maxSize = MaxSize(maxWidth, maxHeight)
        return this
    }

    open fun resize(reWidth: Int, reHeight: Int): LoadOptions {
        resize = Resize(reWidth, reHeight)
        return this
    }

    open fun resize(reWidth: Int, reHeight: Int, scaleType: ScaleType?): LoadOptions {
        resize = Resize(reWidth, reHeight, scaleType)
        return this
    }

    override fun reset() {
        super.reset()
        maxSize = null
        resize = null
        isLowQualityImage = false
        processor = null
        isDecodeGifImage = false
        bitmapConfig = null
        isInPreferQualityOverSpeed = false
        isThumbnailMode = false
        isCacheProcessedImageInDisk = false
        isBitmapPoolDisabled = false
        isCorrectImageOrientationDisabled = false
    }

    fun copy(options: LoadOptions) {
        super.copy(options as DownloadOptions)
        maxSize = options.maxSize
        resize = options.resize
        isLowQualityImage = options.isLowQualityImage
        processor = options.processor
        isDecodeGifImage = options.isDecodeGifImage
        bitmapConfig = options.bitmapConfig
        isInPreferQualityOverSpeed = options.isInPreferQualityOverSpeed
        isThumbnailMode = options.isThumbnailMode
        isCacheProcessedImageInDisk = options.isCacheProcessedImageInDisk
        isBitmapPoolDisabled = options.isBitmapPoolDisabled
        isCorrectImageOrientationDisabled = options.isCorrectImageOrientationDisabled
    }

    override fun makeKey(): String {
        val builder = StringBuilder()
        if (maxSize != null) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append(maxSize!!.key)
        }
        if (resize != null) {
            // TODO: 2019/1/23 这里计算的时候 resize 有可能是 ByViewFixedSizeResize
            if (builder.isNotEmpty()) builder.append('-')
            builder.append(resize!!.key)
            if (isThumbnailMode) {
                if (builder.isNotEmpty()) builder.append('-')
                builder.append("thumbnailMode")
            }
        }
        if (isCorrectImageOrientationDisabled) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append("ignoreOrientation")
        }
        if (isLowQualityImage) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append("lowQuality")
        }
        if (isInPreferQualityOverSpeed) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append("preferQuality")
        }
        if (bitmapConfig != null) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append(bitmapConfig!!.name)
        }
        if (processor != null) {
            // 旋转图片处理器在旋转0度或360度时不用旋转处理，因此也不会返回key，因此这里过滤一下
            val processorKey = processor!!.key
            if (!TextUtils.isEmpty(processorKey)) {
                if (builder.isNotEmpty()) builder.append('-')
                builder.append(processorKey)
            }
        }
        return builder.toString()
    }

    override fun makeStateImageKey(): String {
        val builder = StringBuilder()
        if (resize != null) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append(resize!!.key)
        }
        if (isLowQualityImage) {
            if (builder.isNotEmpty()) builder.append('-')
            builder.append("lowQuality")
        }
        if (processor != null) {
            // 旋转图片处理器在旋转0度或360度时不用旋转处理，因此也不会返回key，因此这里过滤一下
            val processorKey = processor!!.key
            if (!TextUtils.isEmpty(processorKey)) {
                if (builder.isNotEmpty()) builder.append('-')
                builder.append(processorKey)
            }
        }
        return builder.toString()
    }
}