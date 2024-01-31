package com.github.panpf.sketch.cache.internal

import android.graphics.Bitmap.CompressFormat
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.decode.internal.newDecodeConfigByQualityParams
import com.github.panpf.sketch.request.internal.RequestContext
import okio.BufferedSink


actual fun createImageSerializer(): ImageSerializer? {
    return BitmapImageImageSerializer()
}

class BitmapImageImageSerializer : ImageSerializer {

    override fun supportImage(image: Image): Boolean {
        return image is BitmapImage
    }

    override fun compress(image: Image, sink: BufferedSink) {
        image as BitmapImage
        image.bitmap.compress(CompressFormat.PNG, 100, sink.outputStream())
    }

    override fun decode(
        requestContext: RequestContext,
        imageInfo: ImageInfo,
        dataSource: DataSource
    ): Image {
        val decodeOptions = requestContext.request
            .newDecodeConfigByQualityParams(imageInfo.mimeType)
            .toBitmapOptions()
        val bitmap = dataSource.decodeBitmap(decodeOptions)
            ?: throw DecodeException("Decode bitmap return null. '${requestContext.logKey}'")
        return bitmap.asSketchImage()
    }
}