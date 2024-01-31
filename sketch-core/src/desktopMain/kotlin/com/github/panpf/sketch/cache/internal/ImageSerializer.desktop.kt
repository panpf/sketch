package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.decodeImage
import com.github.panpf.sketch.request.internal.RequestContext
import okio.BufferedSink
import javax.imageio.ImageIO

actual fun createImageSerializer(): ImageSerializer? = BufferedImageImageSerializer()

class BufferedImageImageSerializer : ImageSerializer {

    override fun supportImage(image: Image): Boolean {
        return image is BufferedImageImage
    }

    override fun compress(image: Image, sink: BufferedSink) {
        image as BufferedImageImage
        ImageIO.write(image.bufferedImage, "png", sink.outputStream())
    }

    override fun decode(
        requestContext: RequestContext,
        imageInfo: ImageInfo,
        dataSource: DataSource
    ): Image {
        val bufferedImage = dataSource.decodeImage()
        return bufferedImage.asSketchImage()
    }
}