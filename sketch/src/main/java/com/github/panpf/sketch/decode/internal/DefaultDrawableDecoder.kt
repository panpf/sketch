package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.request.internal.putCountDrawablePendingManagerKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val requestExtras: RequestExtras,
    private val fetchResult: FetchResult
) : DrawableDecoder {

    // todo support ImageDecoder gif wep animation heif animation
    @WorkerThread
    override suspend fun decode(): DrawableDecodeResult {
        val result = tryLockBitmapMemoryCache(sketch, request) { helper ->
            helper?.read() ?: decodeNewBitmap().let { result ->
                val drawable = helper?.write(result)
                    ?: SketchBitmapDrawable(
                        requestKey = request.key,
                        requestUri = request.uriString,
                        imageInfo = result.imageInfo,
                        imageExifOrientation = result.exifOrientation,
                        dataFrom = result.dataFrom,
                        transformedList = result.transformedList,
                        bitmap = result.bitmap
                    )
                DrawableDecodeResult(
                    drawable = drawable,
                    imageInfo = result.imageInfo,
                    exifOrientation = result.exifOrientation,
                    dataFrom = result.dataFrom,
                    transformedList = result.transformedList
                )
            }
        }

        val drawable = result.drawable
        if (drawable is SketchCountBitmapDrawable) {
            val key = request.key
            requestExtras.putCountDrawablePendingManagerKey(key)
            withContext(Dispatchers.Main) {
                sketch.countDrawablePendingManager.mark("DefaultDrawableDecoder", key, drawable)
            }
        }
        return result
    }

    private suspend fun decodeNewBitmap(): BitmapDecodeResult =
        BitmapDecodeInterceptorChain(
            interceptors = sketch.bitmapDecodeInterceptors,
            index = 0,
            sketch = sketch,
            request = request,
            requestExtras = requestExtras,
            fetchResult = fetchResult
        ).proceed()

    class Factory : DrawableDecoder.Factory {
        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): DrawableDecoder = DefaultDrawableDecoder(
            sketch = sketch,
            request = request,
            requestExtras = requestExtras,
            fetchResult = fetchResult
        )

        override fun toString(): String = "DefaultDrawableDecoder"
    }
}