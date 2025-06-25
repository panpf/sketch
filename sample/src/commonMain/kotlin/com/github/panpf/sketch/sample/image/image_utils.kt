package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform

suspend fun ImageRequest.decode(decoder: Decoder.Factory): DecodeResult? =
    withContext(ioCoroutineDispatcher()) {
        val sketch: Sketch = KoinPlatform.getKoin().get<Sketch>()
        val requestContext = RequestContext(sketch, this@decode)
        val fetchResult = sketch.components.newFetcherOrThrow(requestContext).fetch().getOrThrow()
        decoder.create(requestContext, fetchResult)?.decode()
    }