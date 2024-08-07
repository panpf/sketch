package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.impl.use

class SkiaDecodeHelper(val request: ImageRequest, val dataSource: DataSource) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy { readImageInfo() }
    override val supportRegion: Boolean = true

    private val bytes by lazy {
        dataSource.openSource().buffer().use { it.readByteArray() }
    }
    private val skiaImage by lazy {
        // SkiaImage.makeFromEncoded(bytes) will parse exif orientation and does not support closing
        SkiaImage.makeFromEncoded(bytes)
    }

    override fun decode(sampleSize: Int): com.github.panpf.sketch.Image {
        val skiaBitmap = skiaImage.decode(sampleSize)
        return skiaBitmap.asSketchImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): com.github.panpf.sketch.Image {
        val skiaBitmap = skiaImage.decodeRegion(region, sampleSize)
        return skiaBitmap.asSketchImage()
    }

    private fun readImageInfo(): ImageInfo {
        val encodedImageFormat = Codec.makeFromData(Data.makeFromBytes(bytes)).use {
            it.encodedImageFormat
        }
        val mimeType = "image/${encodedImageFormat.name.lowercase()}"
        return ImageInfo(
            width = skiaImage.width,
            height = skiaImage.height,
            mimeType = mimeType,
        )
    }

    override fun close() {
        skiaImage.close()
    }

    override fun toString(): String {
        return "SkiaDecodeHelper(uri='${request.uri}', imageInfo=$imageInfo, supportRegion=$supportRegion)"
    }
}