package com.github.panpf.sketch.transform.internal

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.Transformed
import java.util.LinkedList

class BitmapTransformationDecodeInterceptor : DecodeInterceptor<BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val request = chain.request
        val result = chain.proceed()
        val transformations = request.transformations
        if (transformations?.isNotEmpty() != true) return result

        val oldBitmap = result.bitmap
        var transformedBitmap: Bitmap? = null
        val transformedList = LinkedList<Transformed>()
        transformations.forEach {
            val inputBitmap = transformedBitmap ?: oldBitmap
            val transformResult = it.transform(chain.sketch, request, inputBitmap)
            if (transformResult != null) {
                chain.sketch.bitmapPool.free(inputBitmap)
                transformedBitmap = transformResult.bitmap
                transformedList.add(transformResult.transformed)
            }
        }
        val newBitmap = transformedBitmap
        return if (newBitmap != null) {
            result.newResult(newBitmap) {
                transformedList.forEach {
                    addTransformed(it)
                }
            }
        } else {
            result
        }
    }

    override fun toString(): String = "BitmapTransformationDecodeInterceptor"
}