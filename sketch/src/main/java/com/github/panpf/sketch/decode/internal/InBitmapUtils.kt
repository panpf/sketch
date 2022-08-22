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
package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isAndSupportHardware

private const val MODULE = "InBitmapUtils"


fun setInBitmap(
    bitmapPool: BitmapPool,
    logger: Logger,
    options: BitmapFactory.Options,
    imageSize: Size,
    imageMimeType: String?,
): Boolean {
    if (imageSize.isEmpty) {
        logger.e(MODULE, "setInBitmap. error. imageSize is empty: $imageSize")
        return false
    }
    if (options.inPreferredConfig?.isAndSupportHardware() == true) {
        logger.e(MODULE) {
            "setInBitmap. error. inPreferredConfig is HARDWARE does not support inBitmap"
        }
        return false
    }

    options.inMutable = true

    val inSampleSize = options.inSampleSize.coerceAtLeast(1)
    if (!isSupportInBitmap(imageMimeType, inSampleSize)) {
        logger.w(MODULE) {
            "setInBitmap. error. The current configuration does not support the use of inBitmap in BitmapFactory. " +
                    "imageMimeType=$imageMimeType, inSampleSize=${options.inSampleSize}. " +
                    "For details, please refer to 'DecodeUtils.isSupportInBitmap()'"
        }
        return false
    }
    val sampledBitmapSize =
        calculateSampledBitmapSize(imageSize, inSampleSize, imageMimeType)
    val inBitmap: Bitmap? =
        bitmapPool.get(
            sampledBitmapSize.width,
            sampledBitmapSize.height,
            options.inPreferredConfig
        )
    if (inBitmap != null) {
        logger.d(MODULE) {
            "setInBitmap. successful. imageSize=$imageSize, inSampleSize=$inSampleSize, imageMimeType=$imageMimeType. " +
                    "inBitmap=${inBitmap.logString}"
        }
    } else {
        logger.d(MODULE) {
            "setInBitmap. failed. imageSize=$imageSize, inSampleSize=$inSampleSize, imageMimeType=$imageMimeType"
        }
    }

    // IllegalArgumentException("Problem decoding into existing bitmap") is thrown when inSampleSize is 0 but inBitmap is not null
    options.inSampleSize = inSampleSize
    options.inBitmap = inBitmap
    return inBitmap != null
}

fun setInBitmapForRegion(
    bitmapPool: BitmapPool,
    logger: Logger,
    options: BitmapFactory.Options,
    regionSize: Size,
    imageMimeType: String?,
    imageSize: Size,
): Boolean {
    if (regionSize.isEmpty) {
        logger.e(MODULE, "setInBitmapForRegion. error. regionSize is empty: $regionSize")
        return false
    }
    if (options.inPreferredConfig?.isAndSupportHardware() == true) {
        logger.e(MODULE) {
            "setInBitmapForRegion. error. inPreferredConfig is HARDWARE does not support inBitmap"
        }
        return false
    }
    if (!isSupportInBitmapForRegion(imageMimeType)) {
        logger.w(MODULE) {
            "setInBitmapForRegion. error. The current configuration does not support the use of inBitmap in BitmapFactory. " +
                    "imageMimeType=$imageMimeType. For details, please refer to 'DecodeUtils.isSupportInBitmapForRegion()'"
        }
        return false
    }

    val inSampleSize = options.inSampleSize.coerceAtLeast(1)
    val sampledBitmapSize = calculateSampledBitmapSizeForRegion(
        regionSize, inSampleSize, imageMimeType, imageSize
    )
    // BitmapRegionDecoder does not support inMutable, so creates Bitmap
    var newCreate = false
    val inBitmap = bitmapPool.get(
        sampledBitmapSize.width, sampledBitmapSize.height, options.inPreferredConfig
    ) ?: Bitmap.createBitmap(
        sampledBitmapSize.width, sampledBitmapSize.height, options.inPreferredConfig
    )!!.apply {
        newCreate = true
    }
    logger.d(MODULE) {
        "setInBitmapForRegion. successful. newCreate $newCreate. regionSize=$regionSize, inSampleSize=$inSampleSize, imageSize=$imageSize. " +
                "inBitmap=${inBitmap.logString}"
    }

    // IllegalArgumentException("Problem decoding into existing bitmap") is thrown when inSampleSize is 0 but inBitmap is not null
    options.inSampleSize = inSampleSize
    options.inBitmap = inBitmap
    return true
}

fun freeBitmap(
    bitmapPool: BitmapPool,
    logger: Logger,
    bitmap: Bitmap?,
    caller: String? = null,
): Boolean {
    if (bitmap == null || bitmap.isRecycled) return false

    val success = bitmapPool.put(bitmap, caller)
    if (success) {
        logger.d(MODULE) {
            "free. successful. $caller. ${bitmap.logString}"
        }
    } else {
        bitmap.recycle()
        logger.w(MODULE) {
            "free. failed. execute recycle. $caller. ${bitmap.logString}"
        }
    }
    return success
}