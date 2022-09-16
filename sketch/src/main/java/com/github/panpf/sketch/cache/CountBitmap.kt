/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.decode.internal.freeBitmap
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.requiredMainThread

/**
 * Reference counts [Bitmap] and, when the count is 0, puts it into the BitmapPool
 */
class CountBitmap constructor(
    val cacheKey: String,
    bitmap: Bitmap,
    private val logger: Logger,
    private val bitmapPool: BitmapPool,
) {

    companion object {
        private const val MODULE = "CountBitmap"
    }

    private val bitmapLogString = bitmap.logString
    private var _bitmap: Bitmap? = bitmap
    private var cachedCount = 0
    private var displayedCount = 0
    private var pendingCount = 0

    val bitmap: Bitmap?
        get() = _bitmap

    val isRecycled: Boolean
        get() = _bitmap?.isRecycled ?: true

    val byteCount: Int
        get() = bitmap?.allocationByteCountCompat ?: 0

    @MainThread
    fun setIsPending(pending: Boolean, caller: String? = null) {
        // Pending is to prevent the Drawable from being recycled before it is not used by the target, so it does not need to trigger tryFree
        requiredMainThread()
        if (pending) {
            pendingCount++
            tryFree(caller = "$caller:pending:true", pending = true)
        } else {
            if (pendingCount > 0) {
                pendingCount--
            }
            tryFree(caller = "$caller:pending:false", pending = true)
        }
    }

    @Synchronized
    fun setIsCached(cached: Boolean, caller: String? = null) {
        if (cached) {
            cachedCount++
            tryFree(caller = "$caller:cached:true", pending = false)
        } else {
            if (cachedCount > 0) {
                cachedCount--
            }
            tryFree(caller = "$caller:cached:false", pending = false)
        }
    }

    @MainThread
    fun setIsDisplayed(displayed: Boolean, caller: String? = null) {
        requiredMainThread()
        if (displayed) {
            displayedCount++
            tryFree(caller = "$caller:displayed:true", pending = false)
        } else {
            if (displayedCount > 0) {
                displayedCount--
            }
            tryFree(caller = "$caller:displayed:false", pending = false)
        }
    }

    @MainThread
    fun getPendingCount(): Int {
        requiredMainThread()
        return pendingCount
    }

    @Synchronized
    fun getCachedCount(): Int {
        return cachedCount
    }

    @MainThread
    fun getDisplayedCount(): Int {
        requiredMainThread()
        return displayedCount
    }

    override fun toString(): String {
        return "CountBitmap(${bitmapLogString},$pendingCount/$cachedCount/$displayedCount,$cacheKey)"
    }

    private fun tryFree(caller: String, pending: Boolean) {
        val bitmap = this._bitmap
        if (bitmap == null) {
            logger.w(MODULE, "Bitmap freed. $caller. ${toString()}")
        } else if (isRecycled) {
            throw IllegalStateException("Bitmap recycled. $caller. ${toString()}")
        } else if (!pending && cachedCount == 0 && displayedCount == 0 && pendingCount == 0) {
            this._bitmap = null
            freeBitmap(bitmapPool, logger, bitmap, caller)
            logger.d(MODULE) { "freeBitmap. $caller. ${toString()}" }
        } else {
            logger.d(MODULE) { "keep. $caller. ${toString()}" }
        }
    }
}