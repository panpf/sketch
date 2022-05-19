package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.drawable.toSketchBitmapDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.request.internal.putCountDrawablePendingManagerKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultDrawableDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val requestExtras: RequestExtras,
    private val fetchResult: FetchResult
) : DrawableDecoder {

    @WorkerThread
    override suspend fun decode(): DrawableDecodeResult =
        tryLockMemoryCache(sketch, request) { helper ->
            val cachedResult = helper?.read()
            if (cachedResult != null) {
                cachedResult
            } else {
                val bitmapResult = BitmapDecodeInterceptorChain(
                    sketch = sketch,
                    request = request,
                    requestExtras = requestExtras,
                    fetchResult = fetchResult,
                    interceptors = sketch.bitmapDecodeInterceptors,
                    index = 0,
                ).proceed()
                val drawable = helper?.write(bitmapResult)
                    ?: bitmapResult.toSketchBitmapDrawable(request)
                DrawableDecodeResult(
                    drawable = drawable,
                    imageInfo = bitmapResult.imageInfo,
                    exifOrientation = bitmapResult.exifOrientation,
                    dataFrom = bitmapResult.dataFrom,
                    transformedList = bitmapResult.transformedList
                )
            }
        }.apply {
            if (drawable is SketchCountBitmapDrawable) {
                withContext(Dispatchers.Main) {
                    requestExtras.putCountDrawablePendingManagerKey(request.key)
                    sketch.countDrawablePendingManager
                        .mark("DefaultDrawableDecoder", request.key, drawable)
                }
            }
        }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): DrawableDecoder = DefaultDrawableDecoder(sketch, request, requestExtras, fetchResult)

        override fun toString(): String = "DefaultDrawableDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}