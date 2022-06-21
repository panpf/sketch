package com.github.panpf.sketch.transform.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Transformed
import java.util.LinkedList

class BitmapTransformationDecodeInterceptor : BitmapDecodeInterceptor {

    @WorkerThread
    override suspend fun intercept(
        chain: BitmapDecodeInterceptor.Chain,
    ): BitmapDecodeResult {
        val request = chain.request
        val result = chain.proceed()
        val transformations = request.transformations?.takeIf { it.isNotEmpty() } ?: return result

        val oldBitmap = result.bitmap
        val transformedList = LinkedList<Transformed>()
        val newBitmap = transformations.fold(oldBitmap) { inputBitmap, next ->
            val transformResult = next.transform(chain.sketch, request, inputBitmap)
            if (transformResult != null) {
                if (transformResult.bitmap !== inputBitmap) {
                    chain.sketch.bitmapPool.free(inputBitmap, "transform")
                }
                transformedList.add(transformResult.transformed)
                transformResult.bitmap
            } else {
                inputBitmap
            }
        }
        return if (transformedList.isNotEmpty()) {
            require(!newBitmap.isRecycled)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}