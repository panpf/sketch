package com.github.panpf.sketch.compose.request.internal

import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.panpf.sketch.JvmBitmapImage
import com.github.panpf.sketch.compose.ComposeBitmap
import com.github.panpf.sketch.compose.asSketchImage
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JvmBitmapToComposeBitmapRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 95

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val result = chain.proceed(request)
        val imageData = result.getOrNull()
        if (imageData != null) {
            val image = imageData.image
            if (image is JvmBitmapImage) {
                val composeBitmap: ComposeBitmap = withContext(Dispatchers.IO) {
                    image.bitmap.toComposeImageBitmap()
                }
                val newImage = composeBitmap.asSketchImage()
                return Result.success(imageData.copy(image = newImage))
            }
        }
        return result
    }

    override fun toString(): String =
        "JvmBitmapToComposeBitmapRequestInterceptor(sortWeight=$sortWeight)"

    @Suppress("RedundantOverride")
    override fun equals(other: Any?): Boolean {
        // If you add construction parameters to this class, you need to change it here
        return super.equals(other)
    }

    @Suppress("RedundantOverride")
    override fun hashCode(): Int {
        // If you add construction parameters to this class, you need to change it here
        return super.hashCode()
    }
}