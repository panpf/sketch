package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.drawable.toSketchBitmapDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultDrawableDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val requestContext: RequestContext,
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
                    requestContext = requestContext,
                    fetchResult = fetchResult,
                    interceptors = sketch.components.bitmapDecodeInterceptorList,
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
                    requestContext.pendingCountDrawable(drawable, "decodeAfter")
                }
            }
        }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): DrawableDecoder = DefaultDrawableDecoder(sketch, request, requestContext, fetchResult)

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