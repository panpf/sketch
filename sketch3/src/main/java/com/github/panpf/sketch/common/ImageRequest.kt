package com.github.panpf.sketch.common

import android.graphics.Bitmap
import android.net.Uri
import com.github.panpf.sketch.common.cache.CachePolicy
import com.github.panpf.sketch.load.BitmapConfig
import com.github.panpf.sketch.load.MaxSize
import com.github.panpf.sketch.load.Resize
import com.github.panpf.sketch.load.transform.Transformation

interface ImageRequest {
    val uri: Uri
}

interface DownloadableRequest : ImageRequest {
    val diskCacheKey: String
    val diskCachePolicy: CachePolicy
}

interface LoadableRequest : DownloadableRequest {

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
    @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
    val inPreferQualityOverSpeed: Boolean?

    /**
     * The size of the desired bitmap
     */
    val resize: Resize?

//    /**
//     * Thumbnail mode, together with the [resize] property, gives a sharper thumbnail
//     */
//    val thumbnailMode: Boolean?

    /**
     * The list of [Transformation]s to be applied to this request.
     */
    val transformations: List<Transformation>?

    /**
     * In order to speed up the loading speed, the processed result of [transformations] is cached to disk, and it can be read directly next time
     */
    val cacheTransformationsResultInDisk: Boolean?

    /**
     * Disabled reuse of Bitmap from [BitmapPool]
     */
    val disabledBitmapPool: Boolean?

    /**
     * Disabled correcting the image orientation based on 'exifOrientation'
     */
    val disabledCorrectExifOrientation: Boolean?
}