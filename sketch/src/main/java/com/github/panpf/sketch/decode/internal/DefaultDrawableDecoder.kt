package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DisplayRequest

class DefaultDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val fetchResult: FetchResult
) : DrawableDecoder {

    // todo support ImageDecoder gif wep animation heif animation
    override suspend fun decode(): DrawableDecodeResult {
        val helper = newBitmapMemoryCacheEditor(sketch, request)
        return helper?.tryLock {
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
    }

    private suspend fun decodeNewBitmap(): BitmapDecodeResult =
        BitmapDecodeInterceptorChain(
            initialRequest = request,
            interceptors = sketch.bitmapDecodeInterceptors,
            index = 0,
            sketch = sketch,
            request = request,
            fetchResult = fetchResult
        ).proceed(request)

    override fun close() {

    }

    class Factory : DrawableDecoder.Factory {
        override fun create(
            sketch: Sketch, request: DisplayRequest, fetchResult: FetchResult
        ): DrawableDecoder = DefaultDrawableDecoder(sketch, request, fetchResult)

        override fun toString(): String = "DefaultDrawableDecoder"
    }

}