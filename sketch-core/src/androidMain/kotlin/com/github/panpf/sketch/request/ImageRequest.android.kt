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
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher

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
fun ImageRequest.Builder.placeholder(drawable: EquitableDrawable): ImageRequest.Builder =
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
fun ImageRequest.Builder.placeholder(color: IntColorFetcher): ImageRequest.Builder =
    placeholder(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when loading
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testPlaceholder
 */
fun ImageRequest.Builder.placeholder(color: ResColorFetcher): ImageRequest.Builder =
    placeholder(ColorDrawableStateImage(color))


/**
 * Set Drawable placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testFallback
 */
fun ImageRequest.Builder.fallback(drawable: EquitableDrawable): ImageRequest.Builder =
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
fun ImageRequest.Builder.fallback(color: IntColorFetcher): ImageRequest.Builder =
    fallback(ColorDrawableStateImage(color))

/**
 * Set Color placeholder image when uri is invalid
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testFallback
 */
fun ImageRequest.Builder.fallback(color: ResColorFetcher): ImageRequest.Builder =
    fallback(ColorDrawableStateImage(color))


/**
 * Set Drawable image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(drawable: EquitableDrawable): ImageRequest.Builder =
    error(DrawableStateImage(drawable))

/**
 * Set Drawable res image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(@DrawableRes resId: Int): ImageRequest.Builder =
    error(DrawableStateImage(resId))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(color: IntColorFetcher): ImageRequest.Builder =
    error(ColorDrawableStateImage(color))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testError
 */
fun ImageRequest.Builder.error(color: ResColorFetcher): ImageRequest.Builder =
    error(ColorDrawableStateImage(color))


/**
 * Set [ColorType] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testColorType
 */
fun ImageRequest.Builder.colorType(colorType: ColorType?): ImageRequest.Builder =
    colorType(colorType?.let { BitmapColorType(it) })


/**
 * Set preferred [Bitmap]'s [ColorSpace]
 *
 * @see com.github.panpf.sketch.core.android.test.request.ImageRequestAndroidTest.testColorSpace
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ImageRequest.Builder.colorSpace(colorSpace: ColorSpace.Named?): ImageRequest.Builder =
    colorSpace(colorSpace?.let { BitmapColorSpace(it) })


const val PREFER_QUALITY_OVER_SPEED_KEY = "sketch#prefer_quality_over_speed"

/**
 * From Android N (API 24), this is ignored.  The output will always be high quality.
 *
 * In [android.os.Build.VERSION_CODES.M] and below, if
 * preferQualityOverSpeed is set to true, the decoder will try to
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
fun ImageRequest.Builder.preferQualityOverSpeed(preferQualityOverSpeed: Boolean? = true): ImageRequest.Builder =
    apply {
        if (preferQualityOverSpeed == true) {
            setExtra(key = PREFER_QUALITY_OVER_SPEED_KEY, value = true.toString())
        } else {
            removeExtra(PREFER_QUALITY_OVER_SPEED_KEY)
        }
    }

/**
 * From Android N (API 24), this is ignored.  The output will always be high quality.
 *
 * In [android.os.Build.VERSION_CODES.M] and below, if
 * preferQualityOverSpeed is set to true, the decoder will try to
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