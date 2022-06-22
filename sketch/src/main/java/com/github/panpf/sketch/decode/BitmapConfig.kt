package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.github.panpf.sketch.decode.BitmapConfig.FixedBitmapConfig
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.mimeTypeToImageFormat

/**
 * Build a [BitmapConfig] with the specified [config]
 */
fun BitmapConfig(config: Bitmap.Config): BitmapConfig = FixedBitmapConfig(config)

/**
 * Adapt the appropriate [Bitmap.Config] according to the mimeType and set it to the [BitmapFactory.Options.inPreferredConfig] parameter
 */
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
                mimeTypeToImageFormat(mimeType) == ImageFormat.JPEG -> {
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

    /**
     * Fixed Bitmap Config, whatever mimeTye is will return the specified [Bitmap.Config]
     */
    class FixedBitmapConfig(private val config: Bitmap.Config) : BitmapConfig {

        override val key: String by lazy {
            "BitmapConfig($config)"
        }

        override fun getConfig(mimeType: String?): Bitmap.Config = config

        override fun toString(): String = key

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FixedBitmapConfig) return false

            if (config != other.config) return false

            return true
        }

        override fun hashCode(): Int {
            return config.hashCode()
        }
    }
}