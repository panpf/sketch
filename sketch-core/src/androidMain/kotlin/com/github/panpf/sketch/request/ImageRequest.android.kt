/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
 * Use the screen size as the resize size
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testSizeWithDisplay
 */
fun ImageRequest.Builder.sizeWithDisplay(context: Context): ImageRequest.Builder =
    apply {
        val displayMetrics = context.resources.displayMetrics
        size(width = displayMetrics.widthPixels, height = displayMetrics.heightPixels)
    }


/**
 * Set Drawable placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPlaceholder
 */
fun ImageRequest.Builder.placeholder(drawable: DrawableEqualizer): ImageRequest.Builder =
    placeholder(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPlaceholder
 */
fun ImageRequest.Builder.placeholder(@DrawableRes resId: Int): ImageRequest.Builder =
    placeholder(DrawableStateImage(resId))

/**
 * Set Color placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPlaceholder
 */
fun ImageRequest.Builder.placeholder(color: IntColor): ImageRequest.Builder =
    placeholder(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPlaceholder
 */
fun ImageRequest.Builder.placeholder(color: ResColor): ImageRequest.Builder =
    placeholder(ColorDrawableStateImage(color))


/**
 * Set Drawable placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testFallback
 */
fun ImageRequest.Builder.fallback(drawable: DrawableEqualizer): ImageRequest.Builder =
    fallback(DrawableStateImage(drawable))

/**
 * Set Drawable res placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testFallback
 */
fun ImageRequest.Builder.fallback(@DrawableRes resId: Int): ImageRequest.Builder =
    fallback(DrawableStateImage(resId))

/**
 * Set Color placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testFallback
 */
fun ImageRequest.Builder.fallback(color: IntColor): ImageRequest.Builder =
    fallback(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testFallback
 */
fun ImageRequest.Builder.fallback(color: ResColor): ImageRequest.Builder =
    fallback(ColorDrawableStateImage(color))


/**
 * Set Drawable image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(
    defaultDrawable: DrawableEqualizer,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(DrawableStateImage(defaultDrawable), configBlock)

/**
 * Set Drawable res image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(
    @DrawableRes defaultResId: Int,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(DrawableStateImage(defaultResId), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(
    color: IntColor,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(ColorDrawableStateImage(color), configBlock)

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(
    color: ResColor,
    configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
): ImageRequest.Builder = error(ColorDrawableStateImage(color), configBlock)

/**
 * Configure bitmap quality
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testBitmapConfig
 */
fun ImageRequest.Builder.bitmapConfig(config: Bitmap.Config): ImageRequest.Builder =
    bitmapConfig(BitmapConfig(config))


const val COLOR_SPACE_NAMED_KEY = "sketch#color_space_named"

/**
 * Set preferred [Bitmap]'s [ColorSpace]
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testColorSpace
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ImageRequest.Builder.colorSpace(named: ColorSpace.Named?): ImageRequest.Builder = apply {
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
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testColorSpace
 */
@get:RequiresApi(Build.VERSION_CODES.O)
val ImageRequest.colorSpace: ColorSpace?
    get() = extras?.value<String>(COLOR_SPACE_NAMED_KEY)
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
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPreferQualityOverSpeed
 */
@Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
fun ImageRequest.Builder.preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): ImageRequest.Builder =
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
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPreferQualityOverSpeed
 */
@Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
val ImageRequest.preferQualityOverSpeed: Boolean
    get() = extras?.value<String>(PREFER_QUALITY_OVER_SPEED_KEY)?.toBoolean() == true