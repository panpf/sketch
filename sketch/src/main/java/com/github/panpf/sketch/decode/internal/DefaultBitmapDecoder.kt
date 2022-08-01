package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
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
import com.github.panpf.sketch.util.Size

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
        val canDecodeRegion = ImageFormat.parseMimeType(imageInfo.mimeType)
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
        ).applyExifOrientation(sketch).applyResize(sketch, request.resize)
    }

    private fun realDecodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap {
        val decodeOptions = decodeConfig.toBitmapOptions()

        // Set inBitmap from bitmap pool
        if (!request.disallowReuseBitmap) {
            setInBitmap(
                bitmapPool = sketch.bitmapPool,
                logger = sketch.logger,
                options = decodeOptions,
                imageSize = Size(imageInfo.width, imageInfo.height),
                imageMimeType = imageInfo.mimeType
            )
        }

        Log.e("LruBitmapPool", "${decodeOptions.inBitmap != null}. $request")

        val bitmap: Bitmap = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                val message = "Bitmap decode error. Because inBitmap. uri=${request.uriString}"
                logger.e(MODULE, throwable, message)

                decodeOptions.inBitmap = null
                freeBitmap(sketch.bitmapPool, sketch.logger, inBitmap, "decode:error")
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    throw BitmapDecodeException("Bitmap decode error2: $throwable", throwable2)
                }
            } else {
                throw BitmapDecodeException("Bitmap decode error: $throwable", throwable)
            }
        } ?: throw BitmapDecodeException("Bitmap decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            bitmap.recycle()
            throw BitmapDecodeException("Invalid image, size=${bitmap.width}x${bitmap.height}")
        }
        return bitmap
    }

    private fun realDecodeRegion(
        imageInfo: ImageInfo, srcRect: Rect, decodeConfig: DecodeConfig
    ): Bitmap {
        val decodeOptions = decodeConfig.toBitmapOptions()
        if (!request.disallowReuseBitmap) {
            setInBitmapForRegion(
                bitmapPool = sketch.bitmapPool,
                logger = sketch.logger,
                options = decodeOptions,
                regionSize = Size(srcRect.width(), srcRect.height()),
                imageMimeType = imageInfo.mimeType,
                imageSize = Size(imageInfo.width, imageInfo.height)
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
                    freeBitmap(sketch.bitmapPool, sketch.logger, inBitmap, "decodeRegion:error")
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