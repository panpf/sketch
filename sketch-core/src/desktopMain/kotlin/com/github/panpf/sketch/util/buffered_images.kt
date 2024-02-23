package com.github.panpf.sketch.util

import com.github.panpf.sketch.resize.internal.ResizeMapping
import java.awt.AWTException
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber


fun BufferedImage.scaled(scaleFactor: Float): BufferedImage {
    val oldWidth = width
    val oldHeight = height
    val newWidth = (oldWidth * scaleFactor).toInt()
    val newHeight = (oldHeight * scaleFactor).toInt()
    val newType = colorModel.transparency
    val newImage = BufferedImage(
        /* width = */ newWidth,
        /* height = */ newHeight,
        /* imageType = */ newType
    )
    val graphics: Graphics2D = newImage.createGraphics()
//        graphics.setRenderingHint(
//            RenderingHints.KEY_INTERPOLATION,
//            RenderingHints.VALUE_INTERPOLATION_BILINEAR
//        )
    graphics.drawImage(
        /* img = */ this,
        /* dx1 = */ 0,
        /* dy1 = */ 0,
        /* dx2 = */ newWidth,
        /* dy2 = */ newHeight,
        /* sx1 = */ 0,
        /* sy1 = */ 0,
        /* sx2 = */ oldWidth,
        /* sy2 = */ oldHeight,
        /* observer = */ null
    )
    graphics.dispose()
    return newImage
}

fun BufferedImage.mapping(mapping: ResizeMapping): BufferedImage {
    val newWidth = mapping.newWidth
    val newHeight = mapping.newHeight
    val newType = colorModel.transparency
    val newImage = BufferedImage(
        /* width = */ newWidth,
        /* height = */ newHeight,
        /* imageType = */ newType
    )
    val graphics: Graphics2D = newImage.createGraphics()
//        graphics.setRenderingHint(
//            RenderingHints.KEY_INTERPOLATION,
//            RenderingHints.VALUE_INTERPOLATION_BILINEAR
//        )
    graphics.drawImage(
        /* img = */ this,
        /* dx1 = */ mapping.destRect.left,
        /* dy1 = */ mapping.destRect.top,
        /* dx2 = */ mapping.destRect.right,
        /* dy2 = */ mapping.destRect.bottom,
        /* sx1 = */ mapping.srcRect.left,
        /* sy1 = */ mapping.srcRect.top,
        /* sx2 = */ mapping.srcRect.right,
        /* sy2 = */ mapping.srcRect.bottom,
        /* observer = */ null
    )
    graphics.dispose()
    return newImage
}

fun BufferedImage.rotated(degree: Int): BufferedImage {
    val source = this
    val sourceSize = Size(source.width, source.height)
    val newSize = sourceSize.rotate(degree)
    val newImage = BufferedImage(newSize.width, newSize.height, source.type)
    val graphics: Graphics2D = newImage.createGraphics()
//        graphics.setRenderingHint(
//            RenderingHints.KEY_INTERPOLATION,
//            RenderingHints.VALUE_INTERPOLATION_BILINEAR
//        )
    graphics.translate(
        /* tx = */ (newSize.width - sourceSize.width) / 2.0,
        /* ty = */ (newSize.height - sourceSize.height) / 2.0
    )
    graphics.rotate(
        /* theta = */ Math.toRadians(degree.toDouble()),
        /* x = */ (sourceSize.width / 2).toDouble(),
        /* y = */ (sourceSize.height / 2).toDouble()
    )
    graphics.drawImage(source, 0, 0, null)
    graphics.dispose()
    return newImage
}

fun BufferedImage.flipped(horizontal: Boolean): BufferedImage {
    val source = this
    val flipped = BufferedImage(source.width, source.height, source.type)
    val graphics = flipped.createGraphics()
    val transform = if (horizontal) {
        AffineTransform.getTranslateInstance(source.width.toDouble(), 0.0)
    } else {
        AffineTransform.getTranslateInstance(0.0, source.height.toDouble())
    }.apply {
        val flip = if (horizontal) {
            AffineTransform.getScaleInstance(-1.0, 1.0)
        } else {
            AffineTransform.getScaleInstance(1.0, -1.0)
        }
        concatenate(flip)
    }
    graphics.transform = transform
    graphics.drawImage(source, 0, 0, null)
    graphics.dispose()
    return flipped
}

fun BufferedImage.horizontalFlipped(): BufferedImage = flipped(horizontal = true)

fun BufferedImage.verticalFlipped(): BufferedImage = flipped(horizontal = false)

fun BufferedImage.backgrounded(color: Int): BufferedImage {
    val source = this
    val newImage = BufferedImage(source.width, source.height, source.type)
    val graphics = newImage.createGraphics()
    graphics.color = Color(color)
    graphics.fillRect(0, 0, source.width, source.height)
    graphics.color = null
    graphics.drawImage(source, 0, 0, null)
    graphics.dispose()
    return newImage
}

fun BufferedImage.mask(color: Int) {
    val graphics = this@mask.createGraphics()
    val alpha = color ushr 24
    graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 255f)
    val withoutAlpha = color and 0x00FFFFFF
    graphics.color = Color(withoutAlpha)
    graphics.fillRect(0, 0, this@mask.width, this@mask.height)
    graphics.dispose()
}

fun BufferedImage.copied(): BufferedImage {
    val newImage = BufferedImage(width, height, type)
    val graphics = newImage.createGraphics()
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()
    return newImage
}

fun BufferedImage.hasAlpha(): Boolean {
    val height = this.height
    val width = this.width
    var hasAlpha = false
    for (i in 0 until width) {
        for (j in 0 until height) {
            val pixelAlpha = this.getRGB(i, j) shr 24
            if (pixelAlpha in 0..254) {
                hasAlpha = true
                break
            }
        }
    }
    return hasAlpha
}

fun BufferedImage.blur(radius: Int): Boolean {
    val imageWidth = this.width
    val imageHeight = this.height
    val values: IntArray = try {
        getPixels().apply { doBlur(this, imageWidth, imageHeight, radius) }
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    this.setRGB(0, 0, imageWidth, imageHeight, values, 0, imageWidth)
    return true
}

private fun BufferedImage.getPixels(getWidth: Int = width, getHeight: Int = height): IntArray {
    val pixels = IntArray(getWidth * getHeight)
    val pixelGrabber = PixelGrabber(
        /* img = */ this,
        /* x = */ 0,
        /* y = */ 0,
        /* w = */ getWidth,
        /* h = */ getHeight,
        /* pix = */ pixels,
        /* off = */ 0,
        /* scansize = */ getWidth
    )
    if (!pixelGrabber.grabPixels()) {
        throw AWTException("pg error" + pixelGrabber.status())
    }
    return pixels
}

private fun doBlur(pixels: IntArray, width: Int, height: Int, radius: Int) {
    val wm = width - 1
    val hm = height - 1
    val wh = width * height
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
    val vmin = IntArray(Math.max(width, height))
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
    val stack = Array(div) {
        IntArray(
            3
        )
    }
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
    while (y < height) {
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
            p = pixels[yi + Math.min(wm, Math.max(i, 0))]
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
        while (x < width) {
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
            p = pixels[yw + vmin[x]]
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
        yw += width
        y++
    }
    x = 0
    while (x < width) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        yp = -radius * width
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
                yp += width
            }
            i++
        }
        yi = x
        stackpointer = radius
        y = 0
        while (y < height) {

            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
            pixels[yi] = (-0x1000000 and pixels[yi] or (dv[rsum] shl 16)
                    or (dv[gsum] shl 8) or dv[bsum])
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (x == 0) {
                vmin[y] = Math.min(y + r1, hm) * width
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
            yi += width
            y++
        }
        x++
    }
}