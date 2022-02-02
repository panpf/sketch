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
package com.github.panpf.sketch.cache

import android.graphics.Bitmap
import androidx.annotation.MainThread
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.toHexString

/**
 * Reference counts [Bitmap] and, when the count is 0, puts it into the BitmapPool
 */
class CountBitmap constructor(
    initBitmap: Bitmap,
    val requestKey: String,
    val imageUri: String,
    val imageInfo: ImageInfo,
    val transformedList: List<Transformed>?,
    private val logger: Logger,
    private val bitmapPool: BitmapPool
) {

    companion object {
        private const val MODULE = "CountBitmap"
    }

    private var bitmapHolder: Bitmap? = initBitmap
    private var cacheCount = 0  // Memory cache count
    private var displayCount = 0 // View display count
    private var waitingCount = 0 // Waiting count

    val bitmap: Bitmap?
        get() = bitmapHolder

    val isRecycled: Boolean
        get() = bitmapHolder?.isRecycled ?: true

    val byteCount: Int
        get() = bitmap?.byteCountCompat ?: 0

    val info: String by lazy {
        "CountBitmap(ImageInfo=%dx%d/%s/%s,BitmapInfo=%dx%d/%s/%s/%s)".format(
            imageInfo.width,
            imageInfo.height,
            imageInfo.mimeType,
            exifOrientationName(imageInfo.exifOrientation),
            initBitmap.width,
            initBitmap.height,
            initBitmap.config,
            initBitmap.byteCountCompat.toLong().formatFileSize(),
            initBitmap.toHexString(),
        )
    }

    @MainThread
    fun setIsDisplayed(callingStation: String, displayed: Boolean) {
        if (displayed) {
            displayCount++
            countChanged(callingStation)
        } else if (displayCount > 0) {
            displayCount--
            countChanged(callingStation)
        }
    }

    @MainThread
    fun setIsCached(callingStation: String, cached: Boolean) {
        if (cached) {
            cacheCount++
            countChanged(callingStation)
        } else if (cacheCount > 0) {
            cacheCount--
            countChanged(callingStation)
        }
    }

    @MainThread
    fun setIsWaiting(callingStation: String, waitingUse: Boolean) {
        if (waitingUse) {
            waitingCount++
            countChanged(callingStation)
        } else if (waitingCount > 0) {
            waitingCount--
            countChanged(callingStation)
        }
    }

    private fun countChanged(callingStation: String) {
        val bitmapHolder = this.bitmapHolder
        if (bitmapHolder == null) {
            logger.e(MODULE, "Recycled. $callingStation. $requestKey")
        } else if (isRecycled) {
            logger.e(MODULE, "Recycle. $callingStation. ${bitmapHolder.toHexString()}. $requestKey")
        } else if (cacheCount == 0 && displayCount == 0 && waitingCount == 0) {
            bitmapPool.free(bitmapHolder)
            this.bitmapHolder = null
            logger.d(MODULE) {
                "Free bitmap. $callingStation. ${bitmapHolder.toHexString()}. $requestKey"
            }
        } else {
            logger.d(MODULE) {
                "Can't free bitmap. $callingStation. " +
                        "cacheCount $cacheCount, displayCount $displayCount, waitingCount $waitingCount. " +
                        "${bitmapHolder.toHexString()}. $requestKey"
            }
        }
    }
}