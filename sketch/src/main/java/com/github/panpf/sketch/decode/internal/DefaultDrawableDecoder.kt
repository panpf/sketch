package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchCountDrawable
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
    override suspend fun decode(): DrawableDecodeResult {
        val helper = newBitmapMemoryCacheEditor(sketch, request)
        val result = helper?.tryLock {
            read() ?: decodeNewBitmap().run {
                val drawable = write(this) ?: SketchBitmapDrawable(
                    requestKey = request.key,
                    requestUri = request.uriString,
                    imageInfo = this.imageInfo,
                    exifOrientation = this.exifOrientation,
                    imageDataFrom = this.dataFrom,
                    transformedList = this.transformedList,
                    bitmap = this.bitmap
                )
                DrawableDecodeResult(
                    drawable = drawable,
                    imageInfo = this.imageInfo,
                    exifOrientation = this.exifOrientation,
                    dataFrom = this.dataFrom,
                    transformedList = this.transformedList
                )
            }
        } ?: decodeNewBitmap().run {
            val drawable = SketchBitmapDrawable(
                requestKey = request.key,
                requestUri = request.uriString,
                imageInfo = this.imageInfo,
                exifOrientation = this.exifOrientation,
                imageDataFrom = this.dataFrom,
                transformedList = this.transformedList,
                bitmap = this.bitmap
            )
            DrawableDecodeResult(
                drawable = drawable,
                imageInfo = this.imageInfo,
                exifOrientation = this.exifOrientation,
                dataFrom = this.dataFrom,
                transformedList = this.transformedList
            )
        }
        val drawable = result.drawable
        if (drawable is SketchCountDrawable) {
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

    override fun close() {

    }

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