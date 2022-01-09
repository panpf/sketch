package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory.Options
import android.graphics.Rect
import com.github.panpf.sketch.ImageType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.request.ImageInfo
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.supportBitmapRegionDecoder

class BitmapFactoryDecoder(
    sketch: Sketch,
    request: LoadRequest,
    dataSource: DataSource
) : AbsBitmapDecoder(sketch, request, dataSource) {

    companion object {
        const val MODULE = "BitmapFactoryDecoder"
    }

    override fun readImageInfo(): ImageInfo {
        val boundOptions = Options().apply {
            inJustDecodeBounds = true
        }
        dataSource.decodeBitmap(boundOptions)
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            throw DecodeBitmapException(
                request,
                "Invalid image size. size=${boundOptions.outWidth}x${boundOptions.outHeight}, uri=${request.uriString}"
            )
        }

        val exifOrientation: Int =
            ExifOrientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource)
        return ImageInfo(
            boundOptions.outMimeType,
            boundOptions.outWidth,
            boundOptions.outHeight,
            exifOrientation
        )
    }

    override fun canDecodeRegion(imageInfo: ImageInfo, imageType: ImageType?): Boolean =
        imageType?.supportBitmapRegionDecoder() == true

    override fun decodeRegion(imageInfo: ImageInfo, srcRect: Rect, decodeOptions: Options): Bitmap {
        if (request.disabledBitmapPool != true) {
            // todo 这里的宽高，貌似有问题，需要验证一下
            bitmapPoolHelper.setInBitmapForRegionDecoder(
                width = srcRect.width(),
                height = srcRect.height(),
                options = decodeOptions,
            )
        }

        val bitmap = try {
            dataSource.decodeRegionBitmap(srcRect, decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            when {
                inBitmap != null && isInBitmapError(throwable, true) -> {
                    val message =
                        "Bitmap region decode error. Because inBitmap. uri=${request.uriString}"
                    logger.e(MODULE, throwable, message)

                    decodeOptions.inBitmap = null
                    bitmapPoolHelper.freeBitmapToPool(inBitmap)
                    try {
                        dataSource.decodeRegionBitmap(srcRect, decodeOptions)
                    } catch (throwable2: Throwable) {
                        throw DecodeBitmapException(
                            request,
                            "Bitmap region decode error. uri=${request.uriString}",
                            throwable2
                        )
                    }
                }
                isSrcRectError(throwable, imageInfo.width, imageInfo.height, srcRect) -> {
                    throw DecodeBitmapException(
                        request,
                        "Bitmap region decode error. Because srcRect. imageInfo=${imageInfo}, resize=${request.resize}, srcRect=${srcRect}, uri=${request.uriString}",
                        throwable
                    )
                }
                else -> {
                    throw DecodeBitmapException(
                        request,
                        "Bitmap region decode error. uri=${request.uriString}",
                        throwable
                    )
                }
            }
        } ?: throw DecodeBitmapException(
            request, "Bitmap region decode return null. uri=${request.uriString}"
        )
        if (bitmap.width <= 1 || bitmap.height <= 1) {
            bitmap.recycle()
            throw DecodeBitmapException(
                request,
                "Invalid image size. size=${bitmap.width}x${bitmap.height}, uri=${request.uriString}"
            )
        }
        return bitmap
    }

    override fun decode(imageInfo: ImageInfo, decodeOptions: Options): Bitmap {
        // Set inBitmap from bitmap pool
        if (request.disabledBitmapPool != true) {
            bitmapPoolHelper.setInBitmap(
                decodeOptions, imageInfo.width, imageInfo.height, imageInfo.mimeType
            )
        }

        val bitmap: Bitmap = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable, false)) {
                val message = "Bitmap decode error. Because inBitmap. uri=%s"
                    .format(request.uriString)
                logger.e(MODULE, throwable, message)

                decodeOptions.inBitmap = null
                bitmapPoolHelper.freeBitmapToPool(inBitmap)
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    throw DecodeBitmapException(
                        request,
                        "Bitmap decode error. uri=%s".format(request.uriString),
                        throwable2
                    )
                }
            } else {
                throw DecodeBitmapException(
                    request,
                    "Bitmap decode error. uri=%s".format(request.uriString),
                    throwable
                )
            }
        } ?: throw DecodeBitmapException(
            request, "Bitmap decode return null. uri=%s".format(request.uriString)
        )
        if (bitmap.width <= 1 || bitmap.height <= 1) {
            bitmap.recycle()
            throw DecodeBitmapException(
                request,
                "Invalid image size. size=%dx%d, uri=%s"
                    .format(bitmap.width, bitmap.height, request.uriString)
            )
        }
        return bitmap
    }

    class Factory : Decoder.Factory {

        override fun create(
            sketch: Sketch,
            request: LoadRequest,
            dataSource: DataSource
        ): Decoder = BitmapFactoryDecoder(sketch, request, dataSource)
    }
}