package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor


/**
 * Set the resize size
 */
fun ImageOptions.Builder.sizeWithDisplay(context: Context): ImageOptions.Builder =
    apply {
        val displayMetrics = context.resources.displayMetrics
        size(width = displayMetrics.widthPixels, height = displayMetrics.heightPixels)
    }


/**
 * Set Drawable placeholder image when loading
 */
fun ImageOptions.Builder.placeholder(drawable: DrawableEqualizer): ImageOptions.Builder =
    placeholder(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when loading
 */
fun ImageOptions.Builder.placeholder(@DrawableRes resId: Int): ImageOptions.Builder =
    placeholder(DrawableStateImage(resId))

/**
 * Set Color placeholder image when loading
 */
fun ImageOptions.Builder.placeholder(color: IntColor): ImageOptions.Builder =
    placeholder(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when loading
 */
fun ImageOptions.Builder.placeholder(color: ResColor): ImageOptions.Builder =
    placeholder(ColorDrawableStateImage(color))

/**
 * Set Drawable placeholder image when uri is invalid
 */
fun ImageOptions.Builder.fallback(drawable: DrawableEqualizer): ImageOptions.Builder =
    fallback(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when uri is invalid
 */
fun ImageOptions.Builder.fallback(@DrawableRes resId: Int): ImageOptions.Builder =
    fallback(DrawableStateImage(resId))

/**
 * Set Color placeholder image when uri is invalid
 */
fun ImageOptions.Builder.fallback(color: IntColor): ImageOptions.Builder =
    fallback(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 */
fun ImageOptions.Builder.fallback(color: ResColor): ImageOptions.Builder =
    fallback(ColorDrawableStateImage(color))

/**
 * Set Drawable image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageOptions.Builder.error(
    defaultDrawable: DrawableEqualizer,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(DrawableStateImage(defaultDrawable), configBlock)

/**
 * Set Drawable res image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageOptions.Builder.error(
    @DrawableRes defaultResId: Int,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(DrawableStateImage(defaultResId), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageOptions.Builder.error(
    color: IntColor,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ColorDrawableStateImage(color), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
fun ImageOptions.Builder.error(
    color: ResColor,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageOptions.Builder = error(ColorDrawableStateImage(color), configBlock)

/**
 * Set [Bitmap.Config] to use when creating the bitmap.
 * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
 */
fun ImageOptions.Builder.bitmapConfig(bitmapConfig: BitmapConfig?): ImageOptions.Builder = apply {
    if (bitmapConfig != null) {
        setExtra(key = BITMAP_CONFIG_KEY, value = bitmapConfig.value)
    } else {
        removeExtra(BITMAP_CONFIG_KEY)
    }
}

/**
 * Set [Bitmap.Config] to use when creating the bitmap.
 * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
 */
fun ImageOptions.Builder.bitmapConfig(bitmapConfig: Bitmap.Config): ImageOptions.Builder =
    bitmapConfig(BitmapConfig(bitmapConfig))

/**
 * Specify [Bitmap.Config] to use when creating the bitmap.
 * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
 */
val ImageOptions.bitmapConfig: BitmapConfig?
    get() = BitmapConfig.valueOf(extras?.value<String>(BITMAP_CONFIG_KEY))


/**
 * Set preferred [Bitmap]'s [ColorSpace]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ImageOptions.Builder.colorSpace(named: ColorSpace.Named?): ImageOptions.Builder = apply {
    if (named != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        setExtra(key = COLOR_SPACE_NAMED_KEY, value = named.name)
    } else {
        removeExtra(COLOR_SPACE_NAMED_KEY)
    }
}

/**
 * [Bitmap]'s [ColorSpace]
 *
 * Applied to [android.graphics.BitmapFactory.Options.inPreferredColorSpace]
 */
@get:RequiresApi(Build.VERSION_CODES.O)
val ImageOptions.colorSpace: ColorSpace?
    get() = extras?.value<String>(COLOR_SPACE_NAMED_KEY)
        ?.let { ColorSpace.get(ColorSpace.Named.valueOf(it)) }

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
fun ImageOptions.Builder.preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): ImageOptions.Builder =
    apply {
        if (inPreferQualityOverSpeed == true) {
            setExtra(key = PREFER_QUALITY_OVER_SPEED_KEY, value = true.toString())
        } else {
            removeExtra(PREFER_QUALITY_OVER_SPEED_KEY)
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
val ImageOptions.preferQualityOverSpeed: Boolean?
    get() = extras?.value<String>(PREFER_QUALITY_OVER_SPEED_KEY)?.toBoolean()