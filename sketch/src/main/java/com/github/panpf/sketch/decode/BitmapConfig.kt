package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.os.Build
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.decode.BitmapConfig.FixedBitmapConfig

fun BitmapConfig(config: Bitmap.Config): BitmapConfig = FixedBitmapConfig(config)

sealed interface BitmapConfig {
    val key: String

    fun getConfig(mimeType: String?): Bitmap.Config

    val isLowQuality: Boolean
        get() = this === LowQuality

    val isHighQuality: Boolean
        get() = this === HighQuality

    val isFixed: Boolean
        get() = this is FixedBitmapConfig

    val isDynamic: Boolean
        get() = this !is FixedBitmapConfig

    /**
     * Lower quality bitmap config are preferred.
     * Use [Bitmap.Config.RGB_565] if the image format is JPEG,
     * otherwise use [Bitmap.Config.ARGB_4444] for JELLY_BEAN_MR2 and below,
     * KITKAT and later use [Bitmap.Config.ARGB_8888]
     */
    object LowQuality : BitmapConfig {

        override val key: String
            get() = "BitmapConfig(LowQuality)"

        override fun getConfig(mimeType: String?): Bitmap.Config =
            when {
                ImageFormat.valueOfMimeType(mimeType) == ImageFormat.JPEG -> {
                    Bitmap.Config.RGB_565
                }
                Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT -> {
                    @Suppress("DEPRECATION")
                    Bitmap.Config.ARGB_4444
                }
                else -> {
                    Bitmap.Config.ARGB_8888
                }
            }

        override fun toString(): String = key
    }

    /**
     * [Bitmap.Config.RGBA_F16] is preferred, otherwise [Bitmap.Config.ARGB_8888] is used.
     */
    object HighQuality : BitmapConfig {

        override val key: String
            get() = "BitmapConfig(HighQuality)"

        override fun getConfig(mimeType: String?): Bitmap.Config =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Bitmap.Config.RGBA_F16
            } else {
                Bitmap.Config.ARGB_8888
            }

        override fun toString(): String = LowQuality.key
    }

    class FixedBitmapConfig(private val config: Bitmap.Config) : BitmapConfig {

        override val key: String by lazy {
            "BitmapConfig($config)"
        }

        override fun getConfig(mimeType: String?): Bitmap.Config = config

        override fun toString(): String = key

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FixedBitmapConfig

            if (config != other.config) return false

            return true
        }

        override fun hashCode(): Int {
            return config.hashCode()
        }
    }
}