package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.LoadRequest

// todo 融合 ResizeInterceptor 和 ExifOrientationHelperInterceptor
class SizeInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(chain.request)
        val bitmap = bitmapResult.bitmap
        val resize = chain.request.resize
//        val maxSize = chain.request.maxSize
        val bitmapPool = chain.sketch.bitmapPool
        return if (resize?.shouldUse(bitmap.width, bitmap.height) == true) {
            val newBitmap = resize(bitmap, resize, bitmapPool)
            bitmapPool.free(bitmap)
            bitmapResult.new(newBitmap) {
                addTransformed(ResizeTransformed(resize))
            }
//        } else if(maxSize != null && bitmap.width * bitmap.height > maxSize.width * maxSize.height){
            // todo 限制不超过 maxSize
        } else {
            bitmapResult
        }
    }

    private fun resize(bitmap: Bitmap, resize: Resize, bitmapPool: BitmapPool): Bitmap {
        val mapping = ResizeMapping.calculator(
            imageWidth = bitmap.width,
            imageHeight = bitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            resizeScale = resize.scale,
            exactlySize = resize.precision == Resize.Precision.EXACTLY
        )
        // todo 限制不超过 maxSize
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val resizeBitmap =
            bitmapPool.getOrCreate(mapping.newWidth, mapping.newHeight, config)
        val canvas = Canvas(resizeBitmap)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null)
        return resizeBitmap
    }

    override fun toString(): String = "SizeInterceptor"
}

class ResizeTransformed constructor(val resize: Resize): Transformed {
    override val key: String = "ResizeTransformed($resize)"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getResizeTransformed(): ResizeTransformed? =
    find { it is ResizeTransformed } as ResizeTransformed?