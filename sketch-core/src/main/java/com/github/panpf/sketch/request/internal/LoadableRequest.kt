package com.github.panpf.sketch.request.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.BitmapConfig
import com.github.panpf.sketch.request.MaxSize
import com.github.panpf.sketch.request.Resize
import com.github.panpf.sketch.transform.Transformation

interface LoadableRequest : DownloadableRequest {

    /**
     * What is resultDiskCache. To speed up image load, cache the final bitmap to disk if you set [maxSize], [resize], [transformations] parameters (see [newQualityKey]). So that it can be used directly after the next read
     */
    val resultDiskCacheKey: String?

    /**
     * resultDiskCache policy configuration
     * @see resultDiskCacheKey
     */
    val resultDiskCachePolicy: CachePolicy

    /**
     * Limit the maximum size of the bitmap on decode, default value is [MaxSize.SCREEN_SIZE]
     *
     * Applied to [android.graphics.BitmapFactory.Options.inSampleSize]
     */
    val maxSize: MaxSize?

    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    val bitmapConfig: BitmapConfig?

    @get:RequiresApi(26)
    val colorSpace: ColorSpace?

    /**
     * From Android N (API 24), this is ignored.  The output will always be high quality.
     *
     * In {@link android.os.Build.VERSION_CODES#M} and below, if
     * inPreferQualityOverSpeed is set to true, the decoder will try to
     * decode the reconstructed image to a higher quality even at the
     * expense of the decoding speed. Currently the field only affects JPEG
     * decode, in the case of which a more accurate, but slightly slower,
     * IDCT method will be used instead.
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
     */
    @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
    val preferQualityOverSpeed: Boolean?

    /**
     * The size of the desired bitmap
     */
    val resize: Resize?

    /**
     * The list of [Transformation]s to be applied to this request.
     */
    val transformations: List<Transformation>?

    /**
     * Disabled reuse of Bitmap from [BitmapPool]
     */
    val disabledBitmapPool: Boolean?

    /**
     * Disabled correcting the image orientation based on 'exifOrientation'
     */
    val disabledCorrectExifOrientation: Boolean?

    fun newDecodeOptionsByQualityParams(mimeType: String): BitmapFactory.Options

    val qualityKey: String?

    companion object {

        fun newDecodeOptionsByQualityParams(
            request: LoadableRequest,
            mimeType: String
        ): BitmapFactory.Options =
            BitmapFactory.Options().apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M && request.preferQualityOverSpeed == true) {
                    inPreferQualityOverSpeed = true
                }

                val newConfig = request.bitmapConfig?.getConfigByMimeType(mimeType)
                if (newConfig != null) {
                    inPreferredConfig = newConfig
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && request.colorSpace != null) {
                    inPreferredColorSpace = request.colorSpace
                }
            }

        fun newQualityKey(request: LoadableRequest): String? = buildString {
            val parameters = request.parameters
            if (parameters != null) {
                if (length > 0) append("_")
                append(parameters.cacheKey)
            }

            val maxSize = request.maxSize
            if (maxSize != null) {
                if (length > 0) append("_")
                append(maxSize.cacheKey)
            }

            val bitmapConfig = request.bitmapConfig
            if (bitmapConfig != null) {
                if (length > 0) append("_")
                append(bitmapConfig.cacheKey)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val colorSpace = request.colorSpace
                if (colorSpace != null) {
                    if (length > 0) append("_")
                    append("ColorSpace(${colorSpace.name.replace(" ", "")}")
                }
            }

            val preferQualityOverSpeed = request.preferQualityOverSpeed
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && preferQualityOverSpeed == true) {
                if (length > 0) append("_")
                append("PreferQualityOverSpeed")
            }

            val resize = request.resize
            if (resize != null) {
                if (length > 0) append("_")
                append(resize.cacheKey)
            }

            val transformations = request.transformations
            if (transformations?.isNotEmpty() == true) {
                if (length > 0) append("_")
                append("Transformations(${transformations.joinToString(separator = ",")})")
            }

            val disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            if (disabledCorrectExifOrientation != true) {
                if (length > 0) append("_")
                append("CorrectExifOrientation")
            }
        }.takeIf { it.isNotEmpty() }
    }
}