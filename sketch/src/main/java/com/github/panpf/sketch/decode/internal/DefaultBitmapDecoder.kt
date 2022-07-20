package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Decode image files using BitmapFactory
 */
open class DefaultBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
) : BitmapDecoder {

    companion object {
        const val MODULE = "DefaultBitmapDecoder"
    }

    private val bitmapPool = sketch.bitmapPool
    private val logger = sketch.logger

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val imageInfo =
            dataSource.readImageInfoWithBitmapFactoryOrThrow(request.ignoreExifOrientation)
        val canDecodeRegion = mimeTypeToImageFormat(imageInfo.mimeType)
            ?.supportBitmapRegionDecoder() == true
        return realDecode(
            request = request,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            decodeFull = { decodeConfig ->
                realDecodeFull(imageInfo, decodeConfig)
            },
            decodeRegion = if (canDecodeRegion) { srcRect, decodeConfig ->
                realDecodeRegion(imageInfo, srcRect, decodeConfig)
            } else null
        ).applyExifOrientation(bitmapPool)
            .applyResize(sketch, request.resize)
    }

    private fun realDecodeRegion(
        imageInfo: ImageInfo, srcRect: Rect, decodeConfig: DecodeConfig
    ): Bitmap {
        val decodeOptions = decodeConfig.toBitmapOptions()
        if (!request.disallowReuseBitmap) {
            bitmapPool.setInBitmapForRegion(
                options = decodeOptions,
                imageWidth = srcRect.width(),
                imageHeight = srcRect.height(),
            )
        }

        val bitmap = try {
            dataSource.decodeRegionBitmap(srcRect, decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            when {
                inBitmap != null && isInBitmapError(throwable) -> {
                    val message =
                        "Bitmap decode region error. Because inBitmap. uri=${request.uriString}"
                    logger.e(MODULE, throwable, message)

                    decodeOptions.inBitmap = null
                    bitmapPool.free(inBitmap, "decodeRegion:error")
                    try {
                        dataSource.decodeRegionBitmap(srcRect, decodeOptions)
                    } catch (throwable2: Throwable) {
                        val message2 = "Bitmap region decode error"
                        throw BitmapDecodeException(message2, throwable2)
                    }
                }
                isSrcRectError(throwable) -> {
                    val message =
                        "Bitmap region decode error. Because srcRect. imageInfo=${imageInfo}, resize=${request.resize}, srcRect=${srcRect}"
                    throw BitmapDecodeException(message, throwable)
                }
                else -> {
                    throw BitmapDecodeException("Bitmap region decode error", throwable)
                }
            }
        } ?: throw BitmapDecodeException("Bitmap region decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            bitmap.recycle()
            throw BitmapDecodeException("Invalid image, size=${bitmap.width}x${bitmap.height}")
        }
        return bitmap
    }

    private fun realDecodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap {
        val decodeOptions = decodeConfig.toBitmapOptions()

        // Set inBitmap from bitmap pool
        if (!request.disallowReuseBitmap) {
            bitmapPool.setInBitmap(
                decodeOptions, imageInfo.width, imageInfo.height, imageInfo.mimeType
            )
        }

        val bitmap: Bitmap = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                val message = "Bitmap decode error. Because inBitmap. uri=${request.uriString}"
                logger.e(MODULE, throwable, message)

                decodeOptions.inBitmap = null
                bitmapPool.free(inBitmap, "decode:error")
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    throw BitmapDecodeException("Bitmap decode error", throwable2)
                }
            } else {
                throw BitmapDecodeException("Bitmap decode error", throwable)
            }
        } ?: throw BitmapDecodeException("Bitmap decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            bitmap.recycle()
            throw BitmapDecodeException("Invalid image, size=${bitmap.width}x${bitmap.height}")
        }
        return bitmap
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder = DefaultBitmapDecoder(sketch, request, fetchResult.dataSource)

        override fun toString(): String = "DefaultBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}