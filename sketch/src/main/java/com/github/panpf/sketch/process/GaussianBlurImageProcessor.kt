package com.github.panpf.sketch.process

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Resize
import java.util.*

/**
 * 高斯模糊图片处理器
 */
class GaussianBlurImageProcessor private constructor(
    /**
     * 获取模糊半径
     */
    val radius // 模糊半径，取值为0到100
    : Int,
    /**
     * 获取图层颜色
     */
    val maskColor // 图层颜色，在模糊后的图片上加一层颜色
    : Int, wrappedImageProcessor: WrappedImageProcessor?
) : WrappedImageProcessor(wrappedImageProcessor) {

    override fun onProcess(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {
        if (bitmap.isRecycled) {
            return bitmap
        }

        // blur handle
        val blurBitmap = fastGaussianBlur(
            bitmap,
            radius,
            bitmap.config != null && bitmap.isMutable
        ) ?: return bitmap

        // layer color handle
        if (maskColor != NO_LAYER_COLOR) {
            val canvas = Canvas(blurBitmap)
            canvas.drawColor(maskColor)
        }
        return blurBitmap
    }

    override fun onToString(): String {
        return String.format(
            Locale.US,
            "%s(radius=%d,maskColor=%d)",
            "GaussianBlurImageProcessor",
            radius,
            maskColor
        )
    }

    override fun onGetKey(): String {
        return String.format(
            Locale.US,
            "%s(radius=%d,maskColor=%d)",
            "GaussianBlur",
            radius,
            maskColor
        )
    }

    companion object {
        private const val NO_LAYER_COLOR = -1
        private const val DEFAULT_RADIUS = 15

        /**
         * 创建一个指定半径和图层颜色的高斯模糊图片处理器
         *
         * @param radius                模糊半径，取值为0到100
         * @param layerColor            图层颜色，在模糊后的图片上加一层颜色
         * @param wrappedImageProcessor 嵌套一个图片处理器
         * @return GaussianBlurImageProcessor
         */
        fun make(
            radius: Int,
            layerColor: Int,
            wrappedImageProcessor: WrappedImageProcessor?
        ): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(radius, layerColor, wrappedImageProcessor)
        }

        /**
         * 创建一个指定半径和图层颜色的高斯模糊图片处理器
         *
         * @param radius     模糊半径，取值为0到100
         * @param layerColor 图层颜色，在模糊后的图片上加一层颜色
         * @return GaussianBlurImageProcessor
         */
        fun make(radius: Int, layerColor: Int): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(radius, layerColor, null)
        }

        /**
         * 创建一个图层颜色的高斯模糊图片处理器
         *
         * @param layerColor            图层颜色，在模糊后的图片上加一层颜色
         * @param wrappedImageProcessor 嵌套一个图片处理器
         * @return GaussianBlurImageProcessor
         */
        fun makeLayerColor(
            layerColor: Int,
            wrappedImageProcessor: WrappedImageProcessor?
        ): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(DEFAULT_RADIUS, layerColor, wrappedImageProcessor)
        }

        /**
         * 创建一个图层颜色的高斯模糊图片处理器
         *
         * @param layerColor 图层颜色，在模糊后的图片上加一层颜色
         * @return GaussianBlurImageProcessor
         */
        fun makeLayerColor(layerColor: Int): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(DEFAULT_RADIUS, layerColor, null)
        }

        /**
         * 创建一个指定半径的高斯模糊图片处理器
         *
         * @param radius                模糊半径，取值为 0 到 100
         * @param wrappedImageProcessor 嵌套一个图片处理器
         * @return GaussianBlurImageProcessor
         */
        fun makeRadius(
            radius: Int,
            wrappedImageProcessor: WrappedImageProcessor?
        ): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(radius, NO_LAYER_COLOR, wrappedImageProcessor)
        }

        /**
         * 创建一个指定半径的高斯模糊图片处理器
         *
         * @param radius 模糊半径，取值为 0 到 100
         * @return GaussianBlurImageProcessor
         */
        fun makeRadius(radius: Int): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(radius, NO_LAYER_COLOR, null)
        }

        /**
         * 创建一个半径为15的高斯模糊图片处理器
         *
         * @param wrappedImageProcessor 嵌套一个图片处理器
         * @return GaussianBlurImageProcessor
         */
        fun make(wrappedImageProcessor: WrappedImageProcessor?): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(DEFAULT_RADIUS, NO_LAYER_COLOR, wrappedImageProcessor)
        }

        /**
         * 创建一个半径为 15 的高斯模糊图片处理器
         *
         * @return GaussianBlurImageProcessor
         */
        fun make(): GaussianBlurImageProcessor {
            return GaussianBlurImageProcessor(DEFAULT_RADIUS, NO_LAYER_COLOR, null)
        }

        /**
         * 快速高斯模糊
         */
        fun fastGaussianBlur(sentBitmap: Bitmap, radius: Int, canReuseInBitmap: Boolean): Bitmap? {
            val bitmap: Bitmap? = if (canReuseInBitmap) {
                sentBitmap
            } else {
                sentBitmap.copy(
                    if (sentBitmap.config != null) sentBitmap.config else Bitmap.Config.ARGB_8888,
                    true
                )
            }
            return try {
                if (radius < 1) {
                    return null
                }
                val w = bitmap!!.width
                val h = bitmap.height
                val pix = IntArray(w * h)
                bitmap.getPixels(pix, 0, w, 0, 0, w, h)
                val wm = w - 1
                val hm = h - 1
                val wh = w * h
                val div = radius + radius + 1
                val r = IntArray(wh)
                val g = IntArray(wh)
                val b = IntArray(wh)
                var rsum: Int
                var gsum: Int
                var bsum: Int
                var x: Int
                var y: Int
                var i: Int
                var p: Int
                var yp: Int
                var yi: Int
                var yw: Int
                val vmin = IntArray(Math.max(w, h))
                var divsum = div + 1 shr 1
                divsum *= divsum
                val dv = IntArray(256 * divsum)
                i = 0
                while (i < 256 * divsum) {
                    dv[i] = i / divsum
                    i++
                }
                yi = 0
                yw = yi
                val stack = Array(div) { IntArray(3) }
                var stackpointer: Int
                var stackstart: Int
                var sir: IntArray
                var rbs: Int
                val r1 = radius + 1
                var routsum: Int
                var goutsum: Int
                var boutsum: Int
                var rinsum: Int
                var ginsum: Int
                var binsum: Int
                y = 0
                while (y < h) {
                    bsum = 0
                    gsum = bsum
                    rsum = gsum
                    boutsum = rsum
                    goutsum = boutsum
                    routsum = goutsum
                    binsum = routsum
                    ginsum = binsum
                    rinsum = ginsum
                    i = -radius
                    while (i <= radius) {
                        p = pix[yi + Math.min(wm, Math.max(i, 0))]
                        sir = stack[i + radius]
                        sir[0] = p and 0xff0000 shr 16
                        sir[1] = p and 0x00ff00 shr 8
                        sir[2] = p and 0x0000ff
                        rbs = r1 - Math.abs(i)
                        rsum += sir[0] * rbs
                        gsum += sir[1] * rbs
                        bsum += sir[2] * rbs
                        if (i > 0) {
                            rinsum += sir[0]
                            ginsum += sir[1]
                            binsum += sir[2]
                        } else {
                            routsum += sir[0]
                            goutsum += sir[1]
                            boutsum += sir[2]
                        }
                        i++
                    }
                    stackpointer = radius
                    x = 0
                    while (x < w) {
                        r[yi] = dv[rsum]
                        g[yi] = dv[gsum]
                        b[yi] = dv[bsum]
                        rsum -= routsum
                        gsum -= goutsum
                        bsum -= boutsum
                        stackstart = stackpointer - radius + div
                        sir = stack[stackstart % div]
                        routsum -= sir[0]
                        goutsum -= sir[1]
                        boutsum -= sir[2]
                        if (y == 0) {
                            vmin[x] = Math.min(x + radius + 1, wm)
                        }
                        p = pix[yw + vmin[x]]
                        sir[0] = p and 0xff0000 shr 16
                        sir[1] = p and 0x00ff00 shr 8
                        sir[2] = p and 0x0000ff
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                        rsum += rinsum
                        gsum += ginsum
                        bsum += binsum
                        stackpointer = (stackpointer + 1) % div
                        sir = stack[stackpointer % div]
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                        rinsum -= sir[0]
                        ginsum -= sir[1]
                        binsum -= sir[2]
                        yi++
                        x++
                    }
                    yw += w
                    y++
                }
                x = 0
                while (x < w) {
                    bsum = 0
                    gsum = bsum
                    rsum = gsum
                    boutsum = rsum
                    goutsum = boutsum
                    routsum = goutsum
                    binsum = routsum
                    ginsum = binsum
                    rinsum = ginsum
                    yp = -radius * w
                    i = -radius
                    while (i <= radius) {
                        yi = Math.max(0, yp) + x
                        sir = stack[i + radius]
                        sir[0] = r[yi]
                        sir[1] = g[yi]
                        sir[2] = b[yi]
                        rbs = r1 - Math.abs(i)
                        rsum += r[yi] * rbs
                        gsum += g[yi] * rbs
                        bsum += b[yi] * rbs
                        if (i > 0) {
                            rinsum += sir[0]
                            ginsum += sir[1]
                            binsum += sir[2]
                        } else {
                            routsum += sir[0]
                            goutsum += sir[1]
                            boutsum += sir[2]
                        }
                        if (i < hm) {
                            yp += w
                        }
                        i++
                    }
                    yi = x
                    stackpointer = radius
                    y = 0
                    while (y < h) {

                        // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                        pix[yi] =
                            -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                        rsum -= routsum
                        gsum -= goutsum
                        bsum -= boutsum
                        stackstart = stackpointer - radius + div
                        sir = stack[stackstart % div]
                        routsum -= sir[0]
                        goutsum -= sir[1]
                        boutsum -= sir[2]
                        if (x == 0) {
                            vmin[y] = Math.min(y + r1, hm) * w
                        }
                        p = x + vmin[y]
                        sir[0] = r[p]
                        sir[1] = g[p]
                        sir[2] = b[p]
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                        rsum += rinsum
                        gsum += ginsum
                        bsum += binsum
                        stackpointer = (stackpointer + 1) % div
                        sir = stack[stackpointer]
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                        rinsum -= sir[0]
                        ginsum -= sir[1]
                        binsum -= sir[2]
                        yi += w
                        y++
                    }
                    x++
                }
                bitmap.setPixels(pix, 0, w, 0, 0, w, h)
                bitmap
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                if (bitmap != null && bitmap != sentBitmap) {
                    bitmap.recycle()
                }
                null
            }
        }
    }
}