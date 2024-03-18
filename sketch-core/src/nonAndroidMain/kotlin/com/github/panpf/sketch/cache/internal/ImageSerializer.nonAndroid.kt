package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.RequestContext
import okio.BufferedSink
import okio.buffer
import org.jetbrains.skia.EncodedImageFormat.PNG

// TODO for iOS, web
class SkiaBitmapImageSerializer : ImageSerializer {

    override fun supportImage(image: Image): Boolean {
        return image is SkiaBitmapImage
    }

    override fun compress(image: Image, sink: BufferedSink) {
        if (image is SkiaBitmapImage) {
            val skiaImage = org.jetbrains.skia.Image.makeFromBitmap(image.bitmap)
            val bytes = skiaImage.encodeToData(PNG)!!.bytes
            sink.write(bytes)
        }
    }

    override fun decode(
        requestContext: RequestContext,
        imageInfo: ImageInfo,
        dataSource: DataSource
    ): Image {
        val bytes = dataSource.openSource().use {
            it.buffer().readByteArray()
        }
        val skiaImage = org.jetbrains.skia.Image.makeFromEncoded(bytes)
        val bitmap = SkiaBitmap.makeFromImage(skiaImage)
        return bitmap.asSketchImage()
    }
}