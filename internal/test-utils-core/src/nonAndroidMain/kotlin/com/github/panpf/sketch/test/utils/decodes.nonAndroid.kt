package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.decode.internal.DecodeHelper
import com.github.panpf.sketch.decode.internal.SkiaDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource

actual fun createDecodeHelper(request: ImageRequest, dataSource: DataSource): DecodeHelper {
    return SkiaDecodeHelper(request, dataSource)
}

actual suspend fun ImageRequest.createDecoderOrDefault(
    sketch: Sketch,
    factory: Decoder.Factory?,
    fetchResultMap: ((FetchResult) -> FetchResult)?
): Decoder {
    val request = this@createDecoderOrDefault
    val requestContext = request.toRequestContext(sketch)
    val fetcher = sketch.components.newFetcherOrThrow(requestContext)
    val fetchResult = fetcher.fetch().getOrThrow()
        .let { fetchResultMap?.invoke(it) ?: it }
    val decoder = factory?.create(requestContext, fetchResult)
        ?: SkiaDecoder(requestContext, fetchResult.dataSource.asOrThrow())
    return decoder
}

actual suspend fun ImageRequest.decode(sketch: Sketch, factory: Decoder.Factory?): DecodeResult {
    return createDecoderOrDefault(sketch, factory).decode()
}