package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.util.DrawableEqualizer


/**
 * Set Drawable placeholder image when loading
 */
fun ImageRequest.Builder.placeholder(drawable: DrawableEqualizer): ImageRequest.Builder =
    placeholder(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when loading
 */
fun ImageRequest.Builder.placeholder(@DrawableRes resId: Int): ImageRequest.Builder =
    placeholder(DrawableStateImage(resId))

/**
 * Set Drawable placeholder image when uri is empty
 */
fun ImageRequest.Builder.uriEmpty(drawable: DrawableEqualizer): ImageRequest.Builder =
    uriEmpty(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when uri is empty
 */
fun ImageRequest.Builder.uriEmpty(@DrawableRes resId: Int): ImageRequest.Builder =
    uriEmpty(DrawableStateImage(resId))

/**
 * Set Drawable image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageRequest.Builder.error(
    defaultDrawable: DrawableEqualizer,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(DrawableStateImage(defaultDrawable), configBlock)

/**
 * Set Drawable res image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageRequest.Builder.error(
    defaultResId: Int,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(DrawableStateImage(defaultResId), configBlock)


const val BITMAP_CONFIG_KEY = "sketch#bitmap_config"

/**
 * Set [Bitmap.Config] to use when creating the bitmap.
 * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
 */
fun ImageRequest.Builder.bitmapConfig(bitmapConfig: BitmapConfig?): ImageRequest.Builder = apply {
    if (bitmapConfig != null) {
        setParameter(
            key = BITMAP_CONFIG_KEY,
            value = bitmapConfig.value,
            cacheKey = bitmapConfig.value
        )
    } else {
        removeParameter(BITMAP_CONFIG_KEY)
    }
}

/**
 * Set [Bitmap.Config] to use when creating the bitmap.
 * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
 */
fun ImageRequest.Builder.bitmapConfig(bitmapConfig: Bitmap.Config): ImageRequest.Builder =
    bitmapConfig(BitmapConfig(bitmapConfig))

/**
 * Specify [Bitmap.Config] to use when creating the bitmap.
 * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
 */
val ImageRequest.bitmapConfig: BitmapConfig?
    get() = BitmapConfig.valueOf(parameters?.value<String>(BITMAP_CONFIG_KEY))


const val COLOR_SPACE_NAMED_KEY = "sketch#color_space_named"

/**
 * Set preferred [Bitmap]'s [ColorSpace]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ImageRequest.Builder.colorSpace(named: ColorSpace.Named?): ImageRequest.Builder = apply {
    if (named != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        setParameter(
            key = COLOR_SPACE_NAMED_KEY,
            value = named.name,
            cacheKey = named.name
        )
    } else {
        removeParameter(COLOR_SPACE_NAMED_KEY)
    }
}

/**
 * [Bitmap]'s [ColorSpace]
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferredColorSpace]
 */
@get:RequiresApi(Build.VERSION_CODES.O)
val ImageRequest.colorSpace: ColorSpace?
    get() = parameters?.value<String>(COLOR_SPACE_NAMED_KEY)
        ?.let { ColorSpace.get(ColorSpace.Named.valueOf(it)) }

const val PREFER_QUALITY_OVER_SPEED_KEY = "sketch#prefer_quality_over_speed"

/**
 * From Android N (API 24), this is ignored.  The output will always be high quality.
 *
 * In [android.os.Build.VERSION_CODES.M] and below, if
 * inPreferQualityOverSpeed is set to true, the decoder will try to
 * decode the reconstructed image to a higher quality even at the
 * expense of the decoding speed. Currently the field only affects JPEG
 * decode, in the case of which a more accurate, but slightly slower,
 * IDCT method will be used instead.
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
 */
@Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
fun ImageRequest.Builder.preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): ImageRequest.Builder =
    apply {
        if (inPreferQualityOverSpeed == true) {
            setParameter(
                key = PREFER_QUALITY_OVER_SPEED_KEY,
                value = inPreferQualityOverSpeed.toString(),
                cacheKey = inPreferQualityOverSpeed.toString()
            )
        } else {
            removeParameter(PREFER_QUALITY_OVER_SPEED_KEY)
        }
    }

/**
 * From Android N (API 24), this is ignored.  The output will always be high quality.
 *
 * In [android.os.Build.VERSION_CODES.M] and below, if
 * inPreferQualityOverSpeed is set to true, the decoder will try to
 * decode the reconstructed image to a higher quality even at the
 * expense of the decoding speed. Currently the field only affects JPEG
 * decode, in the case of which a more accurate, but slightly slower,
 * IDCT method will be used instead.
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
 */
@Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
val ImageRequest.preferQualityOverSpeed: Boolean
    get() = parameters?.value<String>(PREFER_QUALITY_OVER_SPEED_KEY)?.toBoolean() == true