package com.github.panpf.sketch.decode.transform.internal

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.DecodeInterceptor
import com.github.panpf.sketch.request.LoadRequest
import java.util.LinkedList

class TransformationInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val sketch = chain.sketch
        val request = chain.request
        val result = chain.proceed(request)
        val transformations = request.transformations
        if (transformations?.isNotEmpty() != true) return result

        val oldBitmap = result.bitmap
        var transformedBitmap: Bitmap? = null
        val transformedList = LinkedList<Transformed>()
        transformations.forEach {
            val inputBitmap = transformedBitmap ?: oldBitmap
            val transformResult = it.transform(sketch, request, inputBitmap)
            if (transformResult != null) {
                sketch.bitmapPool.free(inputBitmap)
                transformedBitmap = transformResult.bitmap
                transformedList.add(transformResult.transformed)
            }
        }
        val newBitmap = transformedBitmap
        return if (newBitmap != null) {
            result.new(newBitmap) {
                transformedList.forEach {
                    addTransformed(it)
                }
            }
        } else {
            result
        }
    }

    override fun toString(): String = "TransformationInterceptor"
}