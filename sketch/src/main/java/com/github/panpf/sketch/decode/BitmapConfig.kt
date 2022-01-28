package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.os.Build
import com.github.panpf.sketch.ImageFormat

class BitmapConfig(private val config: Bitmap.Config) {

    val cacheKey: String = "BitmapConfig(${
        when {
            this === LOW_QUALITY -> "LOW_QUALITY"
            this === MIDDEN_QUALITY -> "MIDDEN_QUALITY"
            this === HIGH_QUALITY -> "HIGH_QUALITY"
            else -> config
        }
    })"

    val isLowQuality: Boolean
        get() = this === LOW_QUALITY

    val isMiddenQuality: Boolean
        get() = this === MIDDEN_QUALITY

    val isHighQuality: Boolean
        get() = this === HIGH_QUALITY

    fun getConfigByMimeType(mimeType: String?): Bitmap.Config = when {
        isLowQuality -> {
            when {
                ImageFormat.valueOfMimeType(mimeType) == ImageFormat.JPEG -> {
                    Bitmap.Config.RGB_565
                }
                Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT -> {
                    Bitmap.Config.ARGB_4444
                }
                else -> {
                    Bitmap.Config.ARGB_8888
                }
            }
        }
        isMiddenQuality -> {
            Bitmap.Config.ARGB_8888
        }
        isHighQuality -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Bitmap.Config.RGBA_F16
            } else {
                Bitmap.Config.ARGB_8888
            }
        }
        else -> {
            config
        }
    }

    companion object {
        /**
         * 优先使用较低质量的图片配置。如果图片格式是 jpeg 格式就使用 [Bitmap.Config.RGB_565]，否则 JELLY_BEAN_MR2 及以下版本使用 [Bitmap.Config.ARGB_4444]，KITKAT 及以上版本使用 [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val LOW_QUALITY = BitmapConfig(Bitmap.Config.RGB_565)

        /**
         * 始终使用 [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val MIDDEN_QUALITY = BitmapConfig(Bitmap.Config.ARGB_8888)

        /**
         * 优先使用 [Bitmap.Config.RGBA_F16]，否则使用 [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val HIGH_QUALITY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BitmapConfig(Bitmap.Config.RGBA_F16)
        } else {
            BitmapConfig(Bitmap.Config.ARGB_8888)
        }
    }
}