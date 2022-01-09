package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Interceptor.Chain
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.decode.Resize

// todo 融合 ResizeInterceptor 和 ExifOrientationCorrectInterceptor
class ResizeInterceptor : Interceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        sketch: Sketch,
        chain: Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(sketch, chain.request)
        val bitmap = bitmapResult.bitmap
        val resize = chain.request.resize
        val bitmapPoolHelper = sketch.bitmapPoolHelper
        return if (resize != null && needResize(bitmap, resize)) {
            val newBitmap = resize(bitmap, resize, bitmapPoolHelper)
            if (newBitmap !== bitmap) {
                bitmapPoolHelper.freeBitmapToPool(bitmap)
                BitmapDecodeResult(newBitmap, bitmapResult.info, bitmapResult.from)
            } else {
                bitmapResult
            }
        } else {
            bitmapResult
        }
    }

    private fun needResize(bitmap: Bitmap, resize: Resize): Boolean = when (resize.mode) {
        Resize.Mode.EXACTLY_SAME -> {
            resize.width != bitmap.width || resize.height != bitmap.height
        }
        Resize.Mode.ASPECT_RATIO_SAME -> {
            val resizeAspectRatio =
                "%.1f".format((resize.width.toFloat() / resize.height.toFloat()))
            val bitmapAspectRatio =
                "%.1f".format((bitmap.width.toFloat() / bitmap.height.toFloat()))
            resizeAspectRatio != bitmapAspectRatio
        }
        else -> {
            false
        }
    }

    private fun resize(bitmap: Bitmap, resize: Resize, bitmapPoolHelper: BitmapPoolHelper): Bitmap {
        val mapping = ResizeMapping.calculator(
            imageWidth = bitmap.width,
            imageHeight = bitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            scaleType = resize.scaleType,
            exactlySame = resize.mode == Resize.Mode.EXACTLY_SAME
        )
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val resizeBitmap =
            bitmapPoolHelper.getOrMake(mapping.newWidth, mapping.newHeight, config)
        val canvas = Canvas(resizeBitmap)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null)
        return resizeBitmap
    }
}