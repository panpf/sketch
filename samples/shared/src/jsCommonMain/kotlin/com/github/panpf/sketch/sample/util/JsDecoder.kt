package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.decode.internal.DecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import okio.Source
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.impl.use

class JsDecoder(
    requestContext: RequestContext,
    dataSource: ByteArrayDataSource
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = { JsDecodeHelper(dataSource) }
) {

    companion object {
        const val SORT_WEIGHT = SkiaDecoder.SORT_WEIGHT - 1
    }

    class Factory : Decoder.Factory {

        override val key: String = "JsDecoder"
        override val sortWeight: Int = SORT_WEIGHT

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): JsDecoder? {
            val dataSource = fetchResult.dataSource as? ByteArrayDataSource ?: return null
            return JsDecoder(requestContext, dataSource)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "JsDecoder"
    }
}


class JsDecodeHelper(
    val dataSource: ByteArrayDataSource,
) : DecodeHelper {

    private val data by lazy { dataSource.toByteArray() }
    private val _imageInfo: ImageInfo by lazy { readImageInfo(data) }

    override suspend fun getImageInfo(): ImageInfo {
        val source: Source = dataSource.openSource()
        return _imageInfo
    }

    override suspend fun isSupportRegion(): Boolean = false

    override suspend fun decode(sampleSize: Int): Image {
        val imageInfo = _imageInfo
        val targetSize = calculateSampledBitmapSize(imageInfo.size, sampleSize)
        val bitmap = decodeImageAsync(data, targetSize.width, targetSize.height)
        return bitmap.asImage()
    }

    override suspend fun decodeRegion(region: Rect, sampleSize: Int): Image =
        throw UnsupportedOperationException("JsDecodeHelper does not support decodeRegion")

    override fun close() {

    }
}

// try to read a size directly for PNG and JPEG images.
// It is faster than Image.makeFromEncoded(bytes)
internal fun readImageInfo(bytes: ByteArray): ImageInfo { // (w,h)
    val pngSize = getPngSizeOrNull(bytes)
    if (pngSize != null) {
        return ImageInfo(Size(pngSize.first, pngSize.second), "image/png")
    }

    val jpegSize = getJpegSizeOrNull(bytes)
    if (jpegSize != null) {
        return ImageInfo(Size(jpegSize.first, jpegSize.second), "image/jpeg")
    }

    val webpSize = getWebpSizeOrNull(bytes)
    if (webpSize != null) {
        return ImageInfo(Size(webpSize.first, webpSize.second), "image/webp")
    }

    // Fallback for others
    val image = org.jetbrains.skia.Image.makeFromEncoded(bytes)
    val mimeType = Codec.makeFromData(Data.makeFromBytes(bytes)).use {
        "image/${it.encodedImageFormat.name.lowercase()}"
    }
    return ImageInfo(Size(image.width, image.height), mimeType)
}

internal fun getPngSizeOrNull(bytes: ByteArray): Pair<Int, Int>? {
    if (bytes.size < 24) return null
    if (
        int32(bytes[0], bytes[1], bytes[2], bytes[3]) == 0x8950_4E47u &&
        int32(bytes[4], bytes[5], bytes[6], bytes[7]) == 0x0D0A_1A0Au
    ) {
        val width = int32(bytes[16], bytes[17], bytes[18], bytes[19])
        val height = int32(bytes[20], bytes[21], bytes[22], bytes[23])
        return width.toInt() to height.toInt()
    }
    return null
}

internal fun getJpegSizeOrNull(bytes: ByteArray): Pair<Int, Int>? {
    if (bytes.size < 10) return null
    if (int16(bytes[0], bytes[1]) == 0xFFD8u) {
        var offset = 2
        while (offset < bytes.size - 6) {
            val marker = int16(bytes[offset], bytes[offset + 1])
            offset += 2

            if (marker in 0xFFC0u..0xFFCFu && marker != 0xFFC4u && marker != 0xFFC8u && marker != 0xFFCCu) {
                val height = int16(bytes[offset + 3], bytes[offset + 4])
                val width = int16(bytes[offset + 5], bytes[offset + 6])
                return width.toInt() to height.toInt()
            }

            val segmentLength = int16(bytes[offset], bytes[offset + 1])
            offset += segmentLength.toInt()
        }
    }
    return null
}

internal fun getWebpSizeOrNull(bytes: ByteArray): Pair<Int, Int>? {
    if (bytes.size < 30) return null

    // check "RIFF" and "WEBP" signatures
    if (
        int32(bytes[0], bytes[1], bytes[2], bytes[3]) != 0x5249_4646u ||
        int32(bytes[8], bytes[9], bytes[10], bytes[11]) != 0x5745_4250u
    ) {
        return null
    }

    val chunkType = int32(bytes[12], bytes[13], bytes[14], bytes[15])

    return when (chunkType) {
        0x5650_3858u -> { // "VP8X" (Extended WebP)
            val w = int24LE(bytes[24], bytes[25], bytes[26]).toInt() + 1
            val h = int24LE(bytes[27], bytes[28], bytes[29]).toInt() + 1
            w to h
        }

        0x5650_3820u -> { // "VP8" (Lossy WebP)
            // Check Sync Code "0x9D 0x01 0x2A"
            if (bytes[23].asInt() != 0x9Du || bytes[24].asInt() != 0x01u || bytes[25].asInt() != 0x2Au) return null
            val w = (int16LE(bytes[26], bytes[27]) and 0x3FFFu).toInt()
            val h = (int16LE(bytes[28], bytes[29]) and 0x3FFFu).toInt()
            w to h
        }

        0x5650_384Cu -> { // "VP8L" (Lossless WebP)
            // Check Lossless
            if (bytes[20].asInt() != 0x2Fu) return null
            val bits = int32LE(bytes[21], bytes[22], bytes[23], bytes[24])
            val w = (bits and 0x3FFFu).toInt() + 1
            val h = ((bits shr 14) and 0x3FFFu).toInt() + 1
            w to h
        }

        else -> null
    }
}

private fun int16(b1: Byte, b2: Byte): UInt =
    (b1.asInt() shl 8) or b2.asInt()

private fun int24(b1: Byte, b2: Byte, b3: Byte): UInt =
    (b1.asInt() shl 16) or (b2.asInt() shl 8) or b3.asInt()

private fun int32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): UInt =
    (int16(b1, b2) shl 16) or int16(b3, b4)

private fun int16LE(b1: Byte, b2: Byte): UInt =
    int16(b2, b1)

private fun int24LE(b1: Byte, b2: Byte, b3: Byte): UInt =
    int24(b3, b2, b1)

private fun int32LE(b1: Byte, b2: Byte, b3: Byte, b4: Byte): UInt =
    int32(b4, b3, b2, b1)

private fun Byte.asInt() = this.toUInt() and 0xFFu