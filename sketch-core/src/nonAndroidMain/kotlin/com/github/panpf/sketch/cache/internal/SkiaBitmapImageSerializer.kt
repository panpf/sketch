package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.RequestContext
import okio.BufferedSink
import okio.buffer
import okio.use
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.impl.use

class SkiaBitmapImageSerializer : ImageSerializer {

    override fun supportImage(image: Image): Boolean {
        return image is SkiaBitmapImage
    }

    override fun compress(image: Image, sink: BufferedSink) {
        when (image) {
            is SkiaBitmapImage -> {
                val skiaImage = SkiaImage.makeFromBitmap(image.bitmap)
                val encodedData =
                    skiaImage.encodeToData(format = EncodedImageFormat.PNG, quality = 100)
                encodedData?.use {
                    sink.write(it.bytes)
                }
            }

            else -> {
                throw IllegalArgumentException("Unsupported image type: ${image::class}")
            }
        }
    }

    override fun decode(
        requestContext: RequestContext,
        imageInfo: ImageInfo,
        dataSource: DataSource
    ): Image {
        val bytes = dataSource.openSource().buffer().use { it.readByteArray() }
        val skiaImage = SkiaImage.makeFromEncoded(bytes)
        return SkiaBitmap.makeFromImage(skiaImage).asSketchImage()
    }
}