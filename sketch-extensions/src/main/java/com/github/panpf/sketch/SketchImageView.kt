package com.github.panpf.sketch

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.extensions.R
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    override var displayImageOptions: ImageOptions? = null

    private var srcResId: Int? = null

    init {
        parseXmlAttributes(context, attrs)
        displaySrc()
    }

    private fun displaySrc() {
        val displaySrcResId = srcResId
        if (displaySrcResId != null) {
            if (isInEditMode) {
                setImageResource(displaySrcResId)
            } else {
                post {
                    displayImage(context.newResourceUri(displaySrcResId))
                }
            }
        }
    }

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueue(request)
    }

    private fun parseXmlAttributes(context: Context, attrs: AttributeSet? = null) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SketchImageView)
        try {
            displayImageOptions = ImageOptions {
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
                        preferQualityOverSpeed(this)
                    }
                typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_bitmapConfig)
                    ?.apply {
                        bitmapConfig(parseBitmapConfigAttribute(this))
                    }
                typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_disabledReuseBitmap)
                    ?.apply {
                        disabledReuseBitmap(this)
                    }
                typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_ignoreExifOrientation)
                    ?.apply {
                        ignoreExifOrientation(this)
                    }
                val resizeWidth =
                    typedArray.getDimensionPixelSizeOrNull(R.styleable.SketchImageView_sketch_resizeWidth)
                val resizeHeight =
                    typedArray.getDimensionPixelSizeOrNull(R.styleable.SketchImageView_sketch_resizeHeight)
                if (resizeWidth != null && resizeHeight != null) {
                    resizeSize(resizeWidth, resizeHeight)
                }
                typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_resizePrecision)
                    ?.apply {
                        resizePrecision(parseResizePrecision(this))
                    }
                typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_resizeScale)
                    ?.apply {
                        resizeScale(parseResizeScale(this))
                    }
                typedArray.getBooleanOrNull(R.styleable.SketchImageView_sketch_resizeApplyToDrawable)
                    ?.apply {
                        resizeApplyToDrawable(this)
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
                            crossfade(
                                durationMillis = durationMillis
                                    ?: CrossfadeDrawable.DEFAULT_DURATION,
                                preferExactIntrinsicSize = preferExactIntrinsicSize ?: false
                            )
                        }
                    }
                typedArray.getDrawable(R.styleable.SketchImageView_sketch_placeholder)?.apply {
                    placeholder(this)
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

            srcResId =
                typedArray.getResourceId(R.styleable.SketchImageView_sketch_src, -1)
                    .takeIf { it != -1 }
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseDepthAttribute(value: Int): Depth =
        when (value) {
            1 -> Depth.NETWORK
            2 -> Depth.LOCAL
            3 -> Depth.MEMORY
            else -> throw IllegalArgumentException("Value not supported by the 'sketch_depth' attribute: $this")
        }

    private fun parseCachePolicyAttribute(value: Int, name: String): CachePolicy =
        when (value) {
            1 -> CachePolicy.ENABLED
            2 -> CachePolicy.READ_ONLY
            3 -> CachePolicy.WRITE_ONLY
            4 -> CachePolicy.DISABLED
            else -> throw IllegalArgumentException("Value not supported by the '$name' attribute: $this")
        }

    private fun parseBitmapConfigAttribute(value: Int): BitmapConfig =
        when (value) {
            1 -> BitmapConfig.LowQuality
            3 -> BitmapConfig.HighQuality
            4 -> BitmapConfig(Bitmap.Config.ALPHA_8)
            5 -> BitmapConfig(Bitmap.Config.RGB_565)
            6 -> BitmapConfig(Bitmap.Config.ARGB_4444)
            7 -> BitmapConfig(Bitmap.Config.ARGB_8888)
            8 -> if (VERSION.SDK_INT >= VERSION_CODES.O) {
                BitmapConfig(Bitmap.Config.RGBA_F16)
            } else {
                throw IllegalArgumentException("VERSION.SDK_INT < O, Does not support RGBA_F16")
            }
            9 -> if (VERSION.SDK_INT >= VERSION_CODES.O) {
                BitmapConfig(Bitmap.Config.HARDWARE)
            } else {
                throw IllegalArgumentException("VERSION.SDK_INT < O, Does not support HARDWARE")
            }
            else -> throw IllegalArgumentException("Value not supported by the 'sketch_bitmapConfig' attribute: $this")
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

    private fun parseResizePrecision(value: Int): Precision =
        when (value) {
            1 -> Precision.EXACTLY
            2 -> Precision.SAME_ASPECT_RATIO
            3 -> Precision.LESS_PIXELS
            else -> throw IllegalArgumentException("Value not supported by the 'sketch_resizePrecision' attribute: $this")
        }

    private fun parseResizeScale(value: Int): Scale =
        when (value) {
            1 -> Scale.START_CROP
            2 -> Scale.CENTER_CROP
            3 -> Scale.END_CROP
            4 -> Scale.FILL
            else -> throw IllegalArgumentException("Value not supported by the 'sketch_resizeScale' attribute: $this")
        }


    private fun parseTransformation(value: Int, typedArray: TypedArray): Transformation =
        when (value) {
            1 -> {
                val radius =
                    typedArray.getDimensionPixelSizeOrNull(R.styleable.SketchImageView_sketch_transformation_blur_radius)
                val maskColor =
                    typedArray.getColorOrNull(R.styleable.SketchImageView_sketch_transformation_blur_maskColor)
                BlurTransformation(radius = radius ?: 15, maskColor = maskColor)
            }
            2 -> {
                val degrees =
                    typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_transformation_rotate_degrees)
                        ?: throw IllegalArgumentException("Missing 'sketch_transformation_rotate_degrees' property")
                RotateTransformation(degrees)
            }
            3 -> {
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
            4 -> {
                val scale =
                    typedArray.getIntOrNull(R.styleable.SketchImageView_sketch_transformation_circleCrop_scale)
                        ?.let { it1 ->
                            when (it1) {
                                1 -> Scale.START_CROP
                                2 -> Scale.CENTER_CROP
                                3 -> Scale.END_CROP
                                4 -> Scale.FILL
                                else -> throw IllegalArgumentException("Value not supported by the 'sketch_transformation_circleCrop_scale' attribute: $this")
                            }
                        }
                CircleCropTransformation(scale ?: Scale.CENTER_CROP)
            }
            else -> throw IllegalArgumentException("Value not supported by the 'sketch_transformation' attribute: $this")
        }
}