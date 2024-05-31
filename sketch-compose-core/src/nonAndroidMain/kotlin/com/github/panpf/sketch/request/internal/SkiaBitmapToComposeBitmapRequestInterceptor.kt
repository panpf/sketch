package com.github.panpf.sketch.request.internal

import androidx.compose.ui.graphics.asComposeImageBitmap
import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

object SkiaBitmapToComposeBitmapRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 95

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val result = chain.proceed(request)
        val imageData = result.getOrNull()
        if (imageData != null) {
            val image = imageData.image
            if (image is SkiaBitmapImage) {
                val composeBitmap: ComposeBitmap = image.bitmap.asComposeImageBitmap()
                val newImage = composeBitmap.asSketchImage()
                return Result.success(imageData.copy(image = newImage))
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is SkiaBitmapToComposeBitmapRequestInterceptor
    }

    override fun hashCode(): Int {
        return this@SkiaBitmapToComposeBitmapRequestInterceptor::class.hashCode()
    }

    override fun toString(): String =
        "SkiaBitmapToComposeBitmapRequestInterceptor(sortWeight=$sortWeight)"
}