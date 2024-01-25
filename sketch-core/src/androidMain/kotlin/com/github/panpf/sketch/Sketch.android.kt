package com.github.panpf.sketch

import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewTarget
import kotlinx.coroutines.Deferred

internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        addFetcher(ContentUriFetcher.Factory())
        addFetcher(ResourceUriFetcher.Factory())
        addFetcher(AssetUriFetcher.Factory())
        addFetcher(FileUriFetcher.Factory())

        addDecoder(DrawableDecoder.Factory())
        addDecoder(BitmapFactoryDecoder.Factory())
    }.build()
}

internal actual fun getDisposable(
    request: ImageRequest,
    job: Deferred<ImageResult>,
): Disposable {
    val target = request.target
    return if (target is ViewTarget<*>) {
        target.view?.requestManager?.getDisposable(job) ?: OneShotDisposable(job)
    } else {
        OneShotDisposable(job)
    }
}