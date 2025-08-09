package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.BlurhashUtil
import com.github.panpf.sketch.fetch.parseQueryParameters
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.installPixels

class BlurhashDecodeHelper(
    val request: ImageRequest,
    val dataSource: BlurhashDataSource,
    private val fallbackSize: Size = Size(100, 100)
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy {
        val uriString = request.uri.toString()
        val size = if (uriString.contains('&')) {
            val queryStart = uriString.indexOf('&')
            val queryString = uriString.substring(queryStart + 1)
            parseQueryParameters(queryString) ?: fallbackSize
        } else {
            fallbackSize
        }
        ImageInfo(size, "")
    }
    override val supportRegion: Boolean = false

    override fun decode(sampleSize: Int): Image {
        val pixelData = try {
            BlurhashUtil.decodeByte(dataSource.blurhash, imageInfo.width, imageInfo.height)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException()
        }

        val bitmap = createBlurhashBitmap(imageInfo.width, imageInfo.height)
        bitmap.installPixels(pixelData)
        return bitmap.asImage()
    }

    override fun decodeRegion(
        region: Rect,
        sampleSize: Int
    ): Image {
        throw UnsupportedOperationException("Decoding not implemented yet.")
    }

    override fun close() {

    }
}

expect fun createBlurhashBitmap(width: Int, height: Int): Bitmap