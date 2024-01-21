package com.github.panpf.sketch.compose.request

import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.compose.asSketchImage
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AwtImage2ComposeImageRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 95

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val result = chain.proceed(request)
        val imageData = result.getOrNull()
        if (imageData != null) {
            val image = imageData.image
            if (image is BufferedImageImage) {
                val imageBitmap = withContext(Dispatchers.IO) {
                    image.bufferedImage.toComposeImageBitmap()
                }
                val newImage = imageBitmap.asSketchImage()
                return Result.success(imageData.copy(image = newImage))
            }
        }
        return result
    }
}