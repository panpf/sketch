package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.internal.DecodeHelper
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource

suspend fun ImageRequest.decode(sketch: Sketch, factory: Decoder.Factory? = null): DecodeResult {
    val fetchResult = fetch(sketch)
    val requestContext = this.toRequestContext(sketch)
    val decoder =
        factory?.create(requestContext, fetchResult)
            ?: sketch.components.newDecoderOrThrow(requestContext, fetchResult)
    return decoder.decode().getOrThrow()
}

expect fun createDecodeHelper(request: ImageRequest, dataSource: DataSource): DecodeHelper
