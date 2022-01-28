package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.request.LoadRequest

// todo 融合 ResizeInterceptor 和 ExifOrientationCorrectInterceptor
class SizeInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(chain.request)
        val bitmap = bitmapResult.bitmap
        val resize = chain.request.resize
//        val maxSize = chain.request.maxSize
        val bitmapPoolHelper = chain.sketch.bitmapPoolHelper
        return if (resize != null && needResize(bitmap, resize)) {
            val newBitmap = resize(bitmap, resize, bitmapPoolHelper)
            if (newBitmap !== bitmap) {
                bitmapPoolHelper.freeBitmapToPool(bitmap)
                bitmapResult.new(newBitmap) {
                    addTransformed(ResizeTransformed(resize))
                }
            } else {
                bitmapResult
            }
//        } else if(maxSize != null && bitmap.width * bitmap.height > maxSize.width * maxSize.height){
            // todo 限制不超过 maxSize
        } else {
            bitmapResult
        }
    }

    private fun needResize(bitmap: Bitmap, resize: Resize): Boolean =
        if (resize.scope is Resize.Scope.All) {
            when (resize.precision) {
                Resize.Precision.EXACTLY -> {
                    resize.width != bitmap.width || resize.height != bitmap.height
                }
                Resize.Precision.KEEP_ASPECT_RATIO -> {
                    val resizeAspectRatio =
                        "%.1f".format((resize.width.toFloat() / resize.height.toFloat()))
                    val bitmapAspectRatio =
                        "%.1f".format((bitmap.width.toFloat() / bitmap.height.toFloat()))
                    resizeAspectRatio != bitmapAspectRatio
                }
            }
        } else {
            false
        }

    private fun resize(bitmap: Bitmap, resize: Resize, bitmapPoolHelper: BitmapPoolHelper): Bitmap {
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
            bitmapPoolHelper.getOrMake(mapping.newWidth, mapping.newHeight, config)
        val canvas = Canvas(resizeBitmap)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null)
        return resizeBitmap
    }
}