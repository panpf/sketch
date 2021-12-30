package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build
import com.github.panpf.sketch.ImageType

class BitmapConfig(private val config: Bitmap.Config) {

    val cacheKey: String = "BitmapConfig(${
        when {
            this === LOW_QUALITY -> "LOW_QUALITY"
            this === MIDDEN_QUALITY -> "MIDDEN_QUALITY"
            this === HIGH_QUALITY -> "HIGH_QUALITY"
            else -> config
        }
    })"

    fun getConfigByMimeType(mimeType: String?): Bitmap.Config = when {
        this === LOW_QUALITY -> {
            when {
                ImageType.valueOfMimeType(mimeType) == ImageType.JPEG -> {
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
        this === MIDDEN_QUALITY -> {
            Bitmap.Config.ARGB_8888
        }
        this === HIGH_QUALITY -> {
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
         * 优先使用较低质量额图片配置。如果图片格式是 jpeg 格式就使用 [Bitmap.Config.RGB_565]，否则 JELLY_BEAN_MR2 及以下版本使用 [Bitmap.Config.ARGB_4444]，KITKAT 及以上版本使用 [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val LOW_QUALITY = BitmapConfig(RGB_565)

        /**
         * 始终使用 [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val MIDDEN_QUALITY = BitmapConfig(ARGB_8888)

        /**
         * 优先使用 [Bitmap.Config.RGBA_F16]，否则使用 [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val HIGH_QUALITY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BitmapConfig(RGBA_F16)
        } else {
            BitmapConfig(ARGB_8888)
        }
    }
}