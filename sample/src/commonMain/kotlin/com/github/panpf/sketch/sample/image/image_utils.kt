package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext

suspend fun ImageRequest.decode(decoder: Decoder.Factory): DecodeResult? =
    withContext(ioCoroutineDispatcher()) {
        val requestContext = RequestContext(context.sketch, this@decode)
        val fetchResult =
            context.sketch.components.newFetcherOrThrow(requestContext).fetch().getOrThrow()
        decoder.create(requestContext, fetchResult)?.decode()
    }