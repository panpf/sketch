//package com.github.panpf.sketch.request.internal
//
//import androidx.compose.ui.graphics.toComposeImageBitmap
//import com.github.panpf.sketch.JvmBitmapImage
//import com.github.panpf.sketch.ComposeBitmap
//import com.github.panpf.sketch.asSketchImage
//import com.github.panpf.sketch.request.ImageData
//import com.github.panpf.sketch.request.RequestInterceptor
//import com.github.panpf.sketch.request.RequestInterceptor.Chain
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class JvmBitmapToComposeBitmapRequestInterceptor : RequestInterceptor {
//
//    override val key: String? = null
//
//    override val sortWeight: Int = 95
//
//    override suspend fun intercept(chain: Chain): Result<ImageData> {
//        val request = chain.request
//        val result = chain.proceed(request)
//        val imageData = result.getOrNull()
//        if (imageData != null) {
//            val image = imageData.image
//            if (image is JvmBitmapImage) {
//                val composeBitmap: ComposeBitmap = withContext(Dispatchers.IO) {
//                    image.bitmap.toComposeImageBitmap()
//                }
//                val newImage = composeBitmap.asSketchImage()
//                return Result.success(imageData.copy(image = newImage))
//            }
//        }
//        return result
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        return other is JvmBitmapToComposeBitmapRequestInterceptor
//    }
//
//    override fun hashCode(): Int {
//        return this@JvmBitmapToComposeBitmapRequestInterceptor::class.hashCode()
//    }
//
//    override fun toString(): String =
//        "JvmBitmapToComposeBitmapRequestInterceptor(sortWeight=$sortWeight)"
//}