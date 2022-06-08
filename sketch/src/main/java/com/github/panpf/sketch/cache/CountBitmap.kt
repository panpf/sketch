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
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.toHexString

/**
 * Reference counts [Bitmap] and, when the count is 0, puts it into the BitmapPool
 */
class CountBitmap constructor(
    initBitmap: Bitmap,
    val imageUri: String,
    val requestKey: String,
    val requestCacheKey: String,
    val imageInfo: ImageInfo,
    @ExifOrientation val imageExifOrientation: Int,
    val transformedList: List<Transformed>?,
    private val logger: Logger,
    private val bitmapPool: BitmapPool
) {

    companion object {
        private const val MODULE = "CountBitmap"
    }

    private var bitmapHolder: Bitmap? = initBitmap
    private var cachedCount = 0
    private var displayedCount = 0
    private var pendingCount = 0

    val bitmap: Bitmap?
        get() = bitmapHolder

    val isRecycled: Boolean
        get() = bitmapHolder?.isRecycled ?: true

    val byteCount: Int
        get() = bitmap?.allocationByteCountCompat ?: 0

    val info: String by lazy {
        "CountBitmap(ImageInfo=%dx%d/%s/%s,BitmapInfo=%dx%d/%s/%s/%s)".format(
            imageInfo.width,
            imageInfo.height,
            imageInfo.mimeType,
            exifOrientationName(imageExifOrientation),
            initBitmap.width,
            initBitmap.height,
            initBitmap.config,
            initBitmap.allocationByteCountCompat.formatFileSize(),
            initBitmap.toHexString(),
        )
    }

    @MainThread
    fun setIsDisplayed(displayed: Boolean, caller: String? = null) {
        requiredMainThread()
        if (displayed) {
            displayedCount++
            countChanged("$caller:displayed:true")
        } else if (displayedCount > 0) {
            displayedCount--
            countChanged("$caller:displayed:false")
        }
    }

    @Synchronized
    fun setIsCached(cached: Boolean, caller: String? = null) {
        if (cached) {
            cachedCount++
            countChanged("$caller:cached:true")
        } else if (cachedCount > 0) {
            cachedCount--
            countChanged("$caller:cached:false")
        }
    }

    @MainThread
    fun setIsPending(waitingUse: Boolean, caller: String? = null) {
        requiredMainThread()
        if (waitingUse) {
            pendingCount++
            countChanged("$caller:pending:true")
        } else if (pendingCount > 0) {
            pendingCount--
            countChanged("$caller:pending:false")
        }
    }

    @MainThread
    fun getPendingCount(): Int {
        requiredMainThread()
        return pendingCount
    }

    @MainThread
    fun getDisplayedCount(): Int {
        requiredMainThread()
        return displayedCount
    }

    @MainThread
    fun getCachedCount(): Int {
        requiredMainThread()
        return cachedCount
    }

    private fun countChanged(caller: String? = null) {
        val bitmapHolder = this.bitmapHolder
        if (bitmapHolder == null) {
            logger.w(
                MODULE,
                "Known Recycled. $caller. $cachedCount/$displayedCount/$pendingCount. $requestKey"
            )
        } else if (isRecycled) {
            throw IllegalStateException("Unexpected Recycled. $caller. $cachedCount/$displayedCount/$pendingCount. ${bitmapHolder.logString}. $requestKey")
        } else if (cachedCount == 0 && displayedCount == 0 && pendingCount == 0) {
            bitmapPool.free(bitmapHolder, caller)
            this.bitmapHolder = null
            logger.w(MODULE, "Free. $caller. ${bitmapHolder.logString}. $requestKey")
        } else {
            logger.d(MODULE) {
                "Keep. $caller. $cachedCount/$displayedCount/$pendingCount. ${bitmapHolder.logString}. $requestKey"
            }
        }
    }
}