package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.util.SketchException

sealed interface DisplayResult : ImageResult {

    val drawable: Drawable?

    data class Success constructor(
        override val request: ImageRequest,
        override val drawable: Drawable,
        val imageInfo: ImageInfo,
        /**
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSPOSE
         * @see androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSVERSE
         */
        val imageExifOrientation: Int,
        val dataFrom: DataFrom,
        val transformedList: List<Transformed>?,
    ) : DisplayResult, ImageResult.Success

    data class Error constructor(
        override val request: ImageRequest,
        override val drawable: Drawable?,
        override val exception: SketchException,
    ) : DisplayResult, ImageResult.Error
}