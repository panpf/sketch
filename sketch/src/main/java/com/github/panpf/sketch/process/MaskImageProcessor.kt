package com.github.panpf.sketch.process

import android.graphics.*
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Resize
import java.util.*

/**
 * 在图片上面个盖上一层颜色，可兼容形状不规则的透明图片
 */
class MaskImageProcessor @JvmOverloads constructor(
    /**
     * 获取遮罩颜色
     */
    val maskColor: Int, wrappedProcessor: WrappedImageProcessor? = null
) : WrappedImageProcessor(wrappedProcessor) {

    private var paint: Paint? = null

    override fun onProcess(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {
        if (bitmap.isRecycled) {
            return bitmap
        }
        val bitmapPool = sketch.configuration.bitmapPool
        var newBitmapConfig = bitmap.config
        if (newBitmapConfig == null) {
            newBitmapConfig =
                if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
        }
        val maskBitmap: Bitmap
        var isNewBitmap = false
        if (bitmap.isMutable) {
            maskBitmap = bitmap
        } else {
            maskBitmap = bitmapPool.getOrMake(bitmap.width, bitmap.height, newBitmapConfig)
            isNewBitmap = true
        }
        val canvas = Canvas(maskBitmap)
        if (isNewBitmap) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        }
        val paint = paint ?: Paint().apply {
            color = maskColor
            this@MaskImageProcessor.paint = this
        }
        paint.xfermode = null
        val src = canvas.saveLayer(
            0f,
            0f,
            bitmap.width.toFloat(),
            bitmap.height.toFloat(),
            paint,
            Canvas.ALL_SAVE_FLAG
        )
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        canvas.restoreToCount(src)
        return maskBitmap
    }

    override fun onToString(): String {
        return String.format(Locale.US, "%s(%d)", "MaskImageProcessor", maskColor)
    }

    override fun onGetKey(): String {
        return String.format(Locale.US, "%s(%d)", "Mask", maskColor)
    }
}