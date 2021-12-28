package com.github.panpf.sketch.common.cache

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.common.ImageType
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.calculateSamplingSize
import com.github.panpf.sketch.util.calculateSamplingSizeForRegion
import com.github.panpf.sketch.util.computeByteCount
import com.github.panpf.sketch.util.toHexString

class BitmapPoolHelper(
    context: Context,
    val bitmapPool: BitmapPool
) {

    companion object {
        const val MODULE = "BitmapPoolHelper"
    }

    init {
        context.registerComponentCallbacks(object: ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration?) {
            }

            override fun onLowMemory() {
                bitmapPool.clear()
            }

            override fun onTrimMemory(level: Int) {
                bitmapPool.trimMemory(level)
            }
        })
    }

    /**
     * 从 bitmap pool 中取出可复用的 Bitmap 设置到 inBitmap 上，适用于 BitmapFactory
     *
     * @param options     BitmapFactory.Options 需要用到 inSampleSize 以及 inPreferredConfig 属性
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     * @return true：找到了可复用的 Bitmap
     */
    fun setInBitmap(
        options: BitmapFactory.Options,
        outWidth: Int,
        outHeight: Int,
        outMimeType: String?,
    ): Boolean {
        if (outWidth == 0 || outHeight == 0) {
            SLog.em(MODULE, "outWidth or ourHeight is 0")
            return false
        }
        if (TextUtils.isEmpty(outMimeType)) {
            SLog.em(MODULE, "outMimeType is empty")
            return false
        }

        // 使用 inBitmap 时4.4以下 inSampleSize 不能为 0，最小也得是 1
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
        if (inBitmap != null && SLog.isLoggable(SLog.DEBUG)) {
            val sizeInBytes = computeByteCount(outWidth, outHeight, options.inPreferredConfig)
            SLog.dmf(
                MODULE, "setInBitmapFromPool. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                outWidth, outHeight, options.inPreferredConfig, inSampleSize, sizeInBytes,
                Integer.toHexString(inBitmap.hashCode()), inBitmap.byteCountCompat
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
     * @return true：成功放入bitmap pool
     */
    fun freeBitmapToPool(bitmap: Bitmap?): Boolean {
        if (bitmap == null || bitmap.isRecycled) {
            return false
        }
        val success = bitmapPool.put(bitmap)
        if (success) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                SLog.dmf(
                    MODULE, "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                    bitmap.width, bitmap.height, bitmap.config, bitmap.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
        } else {
            if (SLog.isLoggable(SLog.DEBUG)) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                SLog.dmf(
                    MODULE, "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                    bitmap.width, bitmap.height, bitmap.config, bitmap.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
            bitmap.recycle()
        }
        return success
    }

    /**
     * 从 bitmap pool 中取出可复用的 Bitmap 设置到 inBitmap 上，适用于 BitmapRegionDecoder
     *
     * @param options    BitmapFactory.Options 需要用到 options 的 inSampleSize 以及 inPreferredConfig 属性
     * @return true：找到了可复用的 Bitmap
     */
    fun setInBitmapForRegionDecoder(
        options: BitmapFactory.Options,
        srcRect: Rect,
    ): Boolean {
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
            if (SLog.isLoggable(SLog.DEBUG)) {
                val sizeInBytes = computeByteCount(finalWidth, finalHeight, config)
                SLog.dmf(
                    MODULE,
                    "setInBitmapFromPoolForRegionDecoder. options=%dx%d,%s,%d,%d. inBitmap=%s,%d",
                    finalWidth,
                    finalHeight,
                    config,
                    inSampleSize,
                    sizeInBytes,
                    Integer.toHexString(inBitmap.hashCode()),
                    inBitmap.byteCountCompat
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
     * 回收 bitmap，首先尝试放入 bitmap pool，放不进去就回收
     *
     * @param bitmap     要处理的 bitmap
     * @param bitmapPool BitmapPool 尝试放入这个池子
     * @return true：成功放入 bitmap pool
     */
    fun freeBitmapToPoolForRegionDecoder(bitmap: Bitmap?): Boolean {
        if (bitmap == null || bitmap.isRecycled) {
            return false
        }
        val success = bitmapPool.put(bitmap)
        if (!success) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                SLog.dmf(
                    MODULE, "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                    bitmap.width, bitmap.height, bitmap.config, bitmap.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
            bitmap.recycle()
        } else {
            if (SLog.isLoggable(SLog.DEBUG)) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                SLog.dmf(
                    MODULE, "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d",
                    bitmap.width, bitmap.height, bitmap.config, bitmap.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
        }
        return success
    }
}