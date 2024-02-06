@file:Suppress("DEPRECATION")

/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.internal

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.extensions.view.core.R
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.uriEmpty
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.stateimage.uriEmptyError
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition

fun parseImageXmlAttributes(context: Context, attrs: AttributeSet? = null): ImageOptions? {
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SketchImageView)
    return try {
        ImageOptions {
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_depth)?.apply {
                depth(parseDepthAttribute(this))
            }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_downloadCachePolicy)
                ?.apply {
                    downloadCachePolicy(
                        parseCachePolicyAttribute(this, "sketch_downloadCachePolicy")
                    )
                }
            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_preferQualityOverSpeed)
                ?.apply {
                    @Suppress("DEPRECATION")
                    preferQualityOverSpeed(this)
                }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_bitmapConfig)
                ?.apply {
                    bitmapConfig(parseBitmapConfigAttribute(this))
                }
            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_ignoreExifOrientation)
                ?.apply {
                    ignoreExifOrientation(this)
                }
            val sizeWidth =
                typedArray.getDimensionPixelSizeOrNull(R.styleable.SketchImageView_sketch_sizeWidth)
            val sizeHeight =
                typedArray.getDimensionPixelSizeOrNull(R.styleable.SketchImageView_sketch_sizeHeight)
            if (sizeWidth != null && sizeHeight != null) {
                size(sizeWidth, sizeHeight)
            }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_precision)
                ?.apply {
                    precision(parsePrecision(this))
                }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_scale)
                ?.apply {
                    scale(parseScale(this))
                }
            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_resizeOnDraw)
                ?.apply {
                    resizeOnDraw(this)
                }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_transformation)?.apply {
                transformations(parseTransformation(this, typedArray))
            }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_resultCachePolicy)
                ?.apply {
                    resultCachePolicy(
                        parseCachePolicyAttribute(this, "sketch_resultCachePolicy")
                    )
                }
            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_memoryCachePolicy)
                ?.apply {
                    memoryCachePolicy(
                        parseCachePolicyAttribute(this, "sketch_memoryCachePolicy")
                    )
                }
            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_crossfade)
                ?.apply {
                    if (this) {
                        val durationMillis =
                            typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_crossfadeDurationMillis)
                        val preferExactIntrinsicSize =
                            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_crossfadePreferExactIntrinsicSize)
                        val fadeStart =
                            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_crossfadeFadeStart)
                        val alwaysUse =
                            typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_crossfadeAlwaysUse)
                        crossfade(
                            durationMillis = durationMillis ?: Transition.DEFAULT_DURATION,
                            fadeStart = fadeStart ?: true,
                            preferExactIntrinsicSize = preferExactIntrinsicSize ?: false,
                            alwaysUse = alwaysUse ?: false,
                        )
                    }
                }
            typedArray.getDrawable(R.styleable.SketchImageView_sketch_placeholder)?.apply {
                placeholder(this)
            }
            typedArray.getDrawable(R.styleable.SketchImageView_sketch_uriEmpty)?.apply {
                uriEmpty(this)
            }
            typedArray.getDrawable(R.styleable.SketchImageView_sketch_error)?.apply {
                error(this) {
                    typedArray.getDrawable(R.styleable.SketchImageView_sketch_uriEmptyError)
                        ?.let {
                            uriEmptyError(it)
                        }
                }
            }
        }.takeIf { !it.isEmpty() }
    } finally {
        typedArray.recycle()
    }
}

private fun parseDepthAttribute(value: Int): Depth =
    when (value) {
        1 -> Depth.NETWORK
        2 -> Depth.LOCAL
        3 -> Depth.MEMORY
        else -> throw IllegalArgumentException("Value not supported by the 'sketch_depth' attribute: $value")
    }

private fun parseCachePolicyAttribute(value: Int, name: String): CachePolicy =
    when (value) {
        1 -> CachePolicy.ENABLED
        2 -> CachePolicy.READ_ONLY
        3 -> CachePolicy.WRITE_ONLY
        4 -> CachePolicy.DISABLED
        else -> throw IllegalArgumentException("Value not supported by the '$name' attribute: $value")
    }

@Suppress("DEPRECATION")
private fun parseBitmapConfigAttribute(value: Int): BitmapConfig =
    when (value) {
        1 -> BitmapConfig.LowQuality
        3 -> BitmapConfig.HighQuality
        4 -> BitmapConfig(ALPHA_8)
        5 -> BitmapConfig(RGB_565)
        6 -> BitmapConfig(ARGB_4444)
        7 -> BitmapConfig(ARGB_8888)
        8 -> if (VERSION.SDK_INT >= VERSION_CODES.O) {
            BitmapConfig(RGBA_F16)
        } else {
            throw IllegalArgumentException("VERSION.SDK_INT < O, Does not support RGBA_F16")
        }

        9 -> if (VERSION.SDK_INT >= VERSION_CODES.O) {
            BitmapConfig(HARDWARE)
        } else {
            throw IllegalArgumentException("VERSION.SDK_INT < O, Does not support HARDWARE")
        }

        else -> throw IllegalArgumentException("Value not supported by the 'sketch_bitmapConfig' attribute: $value")
    }

private fun TypedArray.getIntOrNull(index: Int): Int? = getInt(index, -1).takeIf { it != -1 }

private fun TypedArray.getBooleanOrNull(index: Int): Boolean? =
    when {
        getBoolean(index, false) -> true
        !getBoolean(index, true) -> false
        else -> null
    }

private fun TypedArray.getDimensionPixelSizeOrNull(index: Int): Int? =
    getDimensionPixelSize(index, -1).takeIf { it != -1 }

private fun TypedArray.getDimensionOrNull(index: Int): Float? =
    getDimension(index, -1f).takeIf { it != -1f }

private fun TypedArray.getColorOrNull(index: Int): Int? =
    getColor(index, Int.MIN_VALUE).takeIf { it != Int.MIN_VALUE }

private fun parsePrecision(value: Int): Precision =
    when (value) {
        1 -> Precision.EXACTLY
        2 -> Precision.SAME_ASPECT_RATIO
        3 -> Precision.LESS_PIXELS
        4 -> Precision.SMALLER_SIZE
        else -> throw IllegalArgumentException("Value not supported by the 'sketch_precision' attribute: $value")
    }

private fun parseScale(value: Int): Scale =
    when (value) {
        1 -> Scale.START_CROP
        2 -> Scale.CENTER_CROP
        3 -> Scale.END_CROP
        4 -> Scale.FILL
        else -> throw IllegalArgumentException("Value not supported by the 'sketch_scale' attribute: $value")
    }


private fun parseTransformation(value: Int, typedArray: TypedArray): Transformation =
    when (value) {
        1 -> {
            val degrees =
                typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_transformation_rotate_degrees)
                    ?: throw IllegalArgumentException("Missing 'sketch_transformation_rotate_degrees' property")
            RotateTransformation(degrees)
        }

        2 -> {
            val radius =
                typedArray.getDimensionOrNull(R.styleable.SketchImageView_sketch_transformation_roundedCorners_radius)
            val radiusTopLeft =
                typedArray.getDimensionOrNull(R.styleable.SketchImageView_sketch_transformation_roundedCorners_radiusTopLeft)
            val radiusTopRight =
                typedArray.getDimensionOrNull(R.styleable.SketchImageView_sketch_transformation_roundedCorners_radiusTopRight)
            val radiusBottomLeft =
                typedArray.getDimensionOrNull(R.styleable.SketchImageView_sketch_transformation_roundedCorners_radiusBottomLeft)
            val radiusBottomRight =
                typedArray.getDimensionOrNull(R.styleable.SketchImageView_sketch_transformation_roundedCorners_radiusBottomRight)
            RoundedCornersTransformation(
                topLeft = radiusTopLeft ?: radius ?: 0f,
                topRight = radiusTopRight ?: radius ?: 0f,
                bottomLeft = radiusBottomLeft ?: radius ?: 0f,
                bottomRight = radiusBottomRight ?: radius ?: 0f,
            )
        }

        3 -> {
            val scale =
                typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_transformation_circleCrop_scale)
                    ?.let { it1 ->
                        when (it1) {
                            1 -> Scale.START_CROP
                            2 -> Scale.CENTER_CROP
                            3 -> Scale.END_CROP
                            4 -> Scale.FILL
                            else -> throw IllegalArgumentException("Value not supported by the 'sketch_transformation_circleCrop_scale' attribute: $value")
                        }
                    }
            CircleCropTransformation(scale)
        }

        4 -> {
            val radius =
                typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_transformation_blur_radius)
            val hasAlphaBitmapBgColor =
                typedArray.getColorOrNull(R.styleable.SketchImageView_sketch_transformation_blur_hasAlphaBitmapBgColor)
            val maskColor =
                typedArray.getColorOrNull(R.styleable.SketchImageView_sketch_transformation_blur_maskColor)
            BlurTransformation(
                radius = radius ?: 15,
                hasAlphaBitmapBgColor = hasAlphaBitmapBgColor ?: Color.BLACK,
                maskColor = maskColor
            )
        }

        5 -> {
            val maskColor =
                typedArray.getColorOrNull(R.styleable.SketchImageView_sketch_transformation_mask_maskColor)
            MaskTransformation(maskColor = maskColor ?: Color.TRANSPARENT)
        }

        else -> throw IllegalArgumentException("Value not supported by the 'sketch_transformation' attribute: $value")
    }