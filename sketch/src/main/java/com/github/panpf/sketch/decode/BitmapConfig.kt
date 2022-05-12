package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.os.Build
import com.github.panpf.sketch.ImageFormat

class BitmapConfig(private val config: Bitmap.Config) {

    val key: String by lazy {
        val configName = when {
            this === LOW_QUALITY -> "LOW_QUALITY"
            this === MIDDEN_QUALITY -> "MIDDEN_QUALITY"
            this === HIGH_QUALITY -> "HIGH_QUALITY"
            else -> config
        }
        "BitmapConfig($configName)"
    }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BitmapConfig
        return when {
            this === LOW_QUALITY -> other === LOW_QUALITY
            this === MIDDEN_QUALITY -> other === MIDDEN_QUALITY
            this === HIGH_QUALITY -> other === HIGH_QUALITY
            else -> this.config == other.config
        }
    }

    override fun hashCode(): Int = when {
        this === LOW_QUALITY -> super.hashCode()
        this === MIDDEN_QUALITY -> super.hashCode()
        this === HIGH_QUALITY -> super.hashCode()
        else -> config.hashCode()
    }

    override fun toString(): String = key


    companion object {
        /**
         * Lower quality bitmap config are preferred.
         * Use [Bitmap.Config.RGB_565] if the image format is JPEG,
         * otherwise use [Bitmap.Config.ARGB_4444] for JELLY_BEAN_MR2 and below,
         * KITKAT and later use [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val LOW_QUALITY = BitmapConfig(Bitmap.Config.RGB_565)

        /**
         * Always use [Bitmap.Config.ARGB_8888]
         */
        @JvmStatic
        val MIDDEN_QUALITY = BitmapConfig(Bitmap.Config.ARGB_8888)

        /**
         * [Bitmap.Config.RGBA_F16] is preferred, otherwise [Bitmap.Config.ARGB_8888] is used.
         */
        @JvmStatic
        val HIGH_QUALITY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BitmapConfig(Bitmap.Config.RGBA_F16)
        } else {
            BitmapConfig(Bitmap.Config.ARGB_8888)
        }
    }
}