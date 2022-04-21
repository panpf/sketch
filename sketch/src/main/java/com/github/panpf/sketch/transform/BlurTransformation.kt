package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject

class BlurTransformation(
    /**
     * Blur radius
     */
    @IntRange(from = 0, to = 100)
    val radius: Int = 15,

    /**
     * Overlay the blurred image with a layer of color, often useful when using images as a background
     */
    @ColorInt
    val maskColor: Int? = null
) : Transformation {

    init {
        require(radius in 0..100) {
            "Radius must range from 0 to 100"
        }
    }

    override val key: String =
        "BlurTransformation(${radius}${if (maskColor != null) ",$maskColor" else ""})"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult? {
        // blur handle
        val canReuseInBitmap = input.config != null && input.isMutable
        val blurBitmap = fastGaussianBlur(input, radius, canReuseInBitmap) ?: return null

        // layer color handle
        if (maskColor != null) {
            Canvas(blurBitmap).drawColor(maskColor)
        }
        return TransformResult(blurBitmap, BlurTransformed(radius, maskColor))
    }

    companion object {
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
                if (bitmap != null && bitmap !== sentBitmap) {
                    bitmap.recycle()
                }
                null
            }
        }
    }
}

class BlurTransformed(val radius: Int, val maskColor: Int?) : Transformed {

    override val key: String by lazy { toString() }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = "BlurTransformed($radius,${maskColor ?: -1})"

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<BlurTransformed> {
        override fun toJson(t: BlurTransformed): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("radius", radius)
                    put("maskColor", maskColor)
                }
            }

        override fun fromJson(jsonObject: JSONObject): BlurTransformed =
            BlurTransformed(
                jsonObject.getInt("radius"),
                jsonObject.optInt("maskColor", -1).takeIf { it != -1 }
            )
    }
}

fun List<Transformed>.getBlurTransformed(): BlurTransformed? =
    find { it is BlurTransformed } as BlurTransformed?