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

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.em
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.decode.ImageType
import com.github.panpf.sketch.util.SketchUtils.Companion.calculateSamplingSize
import com.github.panpf.sketch.util.SketchUtils.Companion.calculateSamplingSizeForRegion
import com.github.panpf.sketch.util.SketchUtils.Companion.computeByteCount
import com.github.panpf.sketch.util.SketchUtils.Companion.getByteCount
import com.github.panpf.sketch.util.SketchUtils.Companion.toHexString

class BitmapPoolUtils {
    companion object {
        private const val NAME = "BitmapPoolUtils"

        /**
         * SDK版本是否支持inBitmap，适用于BitmapRegionDecoder
         */
        @JvmStatic
        fun sdkSupportInBitmapForRegionDecoder(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
        }

        /**
         * 从bitmap poo中取出可复用的Bitmap设置到inBitmap上，适用于BitmapFactory
         *
         * @param options     BitmapFactory.Options 需要用到inSampleSize以及inPreferredConfig属性
         * @param outWidth    图片原始宽
         * @param outHeight   图片原始高
         * @param outMimeType 图片类型
         * @param bitmapPool  BitmapPool 从这个池子里找可复用的Bitmap
         * @return true：找到了可复用的Bitmap
         */
        @JvmStatic
        fun setInBitmapFromPool(
            options: BitmapFactory.Options,
            outWidth: Int,
            outHeight: Int,
            outMimeType: String?,
            bitmapPool: BitmapPool
        ): Boolean {
            if (outWidth == 0 || outHeight == 0) {
                em(NAME, "outWidth or ourHeight is 0")
                return false
            }
            if (TextUtils.isEmpty(outMimeType)) {
                em(NAME, "outMimeType is empty")
                return false
            }

            // 使用inBitmap时4.4以下inSampleSize不能为0，最小也得是1
            if (options.inSampleSize <= 0) {
                options.inSampleSize = 1
            }
            var inSampleSize = options.inSampleSize
            val imageType = ImageType.valueOfMimeType(outMimeType)
            var inBitmap: Bitmap? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                var finalWidth = calculateSamplingSize(outWidth, inSampleSize)
                var finalHeight = calculateSamplingSize(outHeight, inSampleSize)
                while (finalWidth <= 0 || finalHeight <= 0) {
                    inSampleSize /= 2
                    if (inSampleSize == 0) {
                        finalWidth = outWidth
                        finalHeight = outHeight
                    } else {
                        finalWidth = calculateSamplingSizeForRegion(outWidth, inSampleSize)
                        finalHeight = calculateSamplingSizeForRegion(outHeight, inSampleSize)
                    }
                }
                if (inSampleSize != options.inSampleSize) {
                    options.inSampleSize = inSampleSize
                }
                inBitmap = bitmapPool[finalWidth, finalHeight, options.inPreferredConfig]
            } else if (inSampleSize == 1 && (imageType == ImageType.JPEG || imageType == ImageType.PNG)) {
                inBitmap = bitmapPool[outWidth, outHeight, options.inPreferredConfig]
            }
            if (inBitmap != null && isLoggable(SLog.DEBUG)) {
                val sizeInBytes = computeByteCount(outWidth, outHeight, options.inPreferredConfig)
                dmf(
                    NAME, "setInBitmapFromPool. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                    outWidth, outHeight, options.inPreferredConfig, inSampleSize, sizeInBytes,
                    Integer.toHexString(inBitmap.hashCode()), getByteCount(inBitmap)
                )
            }
            options.inBitmap = inBitmap
            options.inMutable = true
            return inBitmap != null
        }

        /**
         * 回收bitmap，首先尝试放入bitmap pool，放不进去就回收
         *
         * @param bitmap     要处理的bitmap
         * @param bitmapPool BitmapPool 尝试放入这个池子
         * @return true：成功放入bitmap pool
         */
        @JvmStatic
        fun freeBitmapToPool(bitmap: Bitmap?, bitmapPool: BitmapPool): Boolean {
            if (bitmap == null || bitmap.isRecycled) {
                return false
            }
            val success = bitmapPool.put(bitmap)
            if (success) {
                if (isLoggable(SLog.DEBUG)) {
                    val elements = Exception().stackTrace
                    val element = if (elements.size > 1) elements[1] else elements[0]
                    dmf(
                        NAME, "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.width, bitmap.height, bitmap.config, toHexString(bitmap)!!,
                        element.className, element.methodName, element.lineNumber
                    )
                }
            } else {
                if (isLoggable(SLog.DEBUG)) {
                    val elements = Exception().stackTrace
                    val element = if (elements.size > 1) elements[1] else elements[0]
                    dmf(
                        NAME, "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.width, bitmap.height, bitmap.config, toHexString(bitmap)!!,
                        element.className, element.methodName, element.lineNumber
                    )
                }
                bitmap.recycle()
            }
            return success
        }

        /**
         * 从bitmap poo中取出可复用的Bitmap设置到inBitmap上，适用于BitmapRegionDecoder
         *
         * @param options    BitmapFactory.Options 需要用到options的inSampleSize以及inPreferredConfig属性
         * @param bitmapPool BitmapPool 从这个池子里找可复用的Bitmap
         * @return true：找到了可复用的Bitmap
         */
        @JvmStatic
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun setInBitmapFromPoolForRegionDecoder(
            options: BitmapFactory.Options,
            srcRect: Rect,
            bitmapPool: BitmapPool
        ): Boolean {
            if (!sdkSupportInBitmapForRegionDecoder()) {
                return false
            }
            var inSampleSize = if (options.inSampleSize >= 1) options.inSampleSize else 1
            val config = options.inPreferredConfig
            var finalWidth = calculateSamplingSizeForRegion(srcRect.width(), inSampleSize)
            var finalHeight = calculateSamplingSizeForRegion(srcRect.height(), inSampleSize)
            while (finalWidth <= 0 || finalHeight <= 0) {
                inSampleSize /= 2
                if (inSampleSize == 0) {
                    finalWidth = srcRect.width()
                    finalHeight = srcRect.height()
                } else {
                    finalWidth = calculateSamplingSizeForRegion(srcRect.width(), inSampleSize)
                    finalHeight = calculateSamplingSizeForRegion(srcRect.height(), inSampleSize)
                }
            }
            if (inSampleSize != options.inSampleSize) {
                options.inSampleSize = inSampleSize
            }
            var inBitmap = bitmapPool[finalWidth, finalHeight, config]
            if (inBitmap != null) {
                if (isLoggable(SLog.DEBUG)) {
                    val sizeInBytes = computeByteCount(finalWidth, finalHeight, config)
                    dmf(
                        NAME,
                        "setInBitmapFromPoolForRegionDecoder. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                        finalWidth,
                        finalHeight,
                        config,
                        inSampleSize,
                        sizeInBytes,
                        Integer.toHexString(inBitmap.hashCode()),
                        getByteCount(inBitmap)
                    )
                }
            } else {
                // 由于BitmapRegionDecoder不支持inMutable所以就自己创建Bitmap
                inBitmap = Bitmap.createBitmap(finalWidth, finalHeight, config)
            }
            options.inBitmap = inBitmap
            return inBitmap != null
        }

        /**
         * 处理bitmap，首先尝试放入bitmap pool，放不进去就回收
         *
         * @param bitmap     要处理的bitmap
         * @param bitmapPool BitmapPool 尝试放入这个池子
         * @return true：成功放入bitmap pool
         */
        @JvmStatic
        fun freeBitmapToPoolForRegionDecoder(bitmap: Bitmap?, bitmapPool: BitmapPool): Boolean {
            if (bitmap == null || bitmap.isRecycled) {
                return false
            }
            val success = sdkSupportInBitmapForRegionDecoder() && bitmapPool.put(bitmap)
            if (!success) {
                if (isLoggable(SLog.DEBUG)) {
                    val elements = Exception().stackTrace
                    val element = if (elements.size > 1) elements[1] else elements[0]
                    dmf(
                        NAME, "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.width, bitmap.height, bitmap.config, toHexString(bitmap)!!,
                        element.className, element.methodName, element.lineNumber
                    )
                }
                bitmap.recycle()
            } else {
                if (isLoggable(SLog.DEBUG)) {
                    val elements = Exception().stackTrace
                    val element = if (elements.size > 1) elements[1] else elements[0]
                    dmf(
                        NAME, "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                        bitmap.width, bitmap.height, bitmap.config, toHexString(bitmap)!!,
                        element.className, element.methodName, element.lineNumber
                    )
                }
            }
            return success
        }
    }
}