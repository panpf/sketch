package com.github.panpf.sketch.common.decode

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.datasource.DataSource

class BitmapFactoryDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val extras: RequestExtras<ImageRequest, ImageResult>?,
    private val source: DataSource,
) : Decoder {

    override suspend fun decode(): DecodeResult? {
        TODO("Not yet implemented")
    }

    class Factory : Decoder.Factory {
        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            extras: RequestExtras<ImageRequest, ImageResult>?,
            source: DataSource,
        ): Decoder = BitmapFactoryDecoder(sketch, request, extras, source)
    }

    companion object {
        const val MIME_TYPE_JPEG = "image/jpeg"
    }
}