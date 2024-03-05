package com.github.panpf.sketch.util

import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import kotlin.math.min

internal fun BufferedImage.copied(): BufferedImage {
    val newImage = BufferedImage(width, height, type)
    val graphics = newImage.createGraphics()
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()
    return newImage
}

internal fun BufferedImage.hasAlpha(): Boolean {
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

internal fun BufferedImage.getPixels(region: Rect? = null): IntArray {
    val targetPixels = if (region != null) {
        region.width() * region.height()
    } else {
        width * height
    }
    val pixels = IntArray(targetPixels)
    getRGB(
        /* startX = */ region?.left ?: 0,
        /* startY = */ region?.top ?: 0,
        /* w = */ region?.width() ?: width,
        /* h = */ region?.height() ?: height,
        /* rgbArray = */ pixels,
        /* offset = */ 0,
        /* scansize = */ region?.width() ?: width
    )
    return pixels
}


internal fun BufferedImage.scaled(scaleFactor: Float): BufferedImage {
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
    val graphics: Graphics2D = newImage.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
    }
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

internal fun BufferedImage.mapping(mapping: ResizeMapping): BufferedImage {
    val newWidth = mapping.newWidth
    val newHeight = mapping.newHeight
    val newType = colorModel.transparency
    val newImage = BufferedImage(
        /* width = */ newWidth,
        /* height = */ newHeight,
        /* imageType = */ newType
    )
    val graphics: Graphics2D = newImage.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
    }
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

internal fun BufferedImage.rotated(angle: Int): BufferedImage {
    val finalAngle = (angle % 360).let { if (it < 0) 360 + it else it }
    val source = this
    val sourceSize = Size(source.width, source.height)
    val newSize = calculateRotatedSize(size = sourceSize, angle = finalAngle.toDouble())
    val newImage = BufferedImage(
        /* width = */ newSize.width,
        /* height = */ newSize.height,
        /* imageType = */ BufferedImage.TYPE_INT_ARGB
    )
    val graphics: Graphics2D = newImage.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.translate(
        /* tx = */ (newSize.width - sourceSize.width) / 2.0,
        /* ty = */ (newSize.height - sourceSize.height) / 2.0
    )
    graphics.apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.rotate(
        /* theta = */ Math.toRadians(finalAngle.toDouble()),
        /* x = */ (sourceSize.width / 2).toDouble(),
        /* y = */ (sourceSize.height / 2).toDouble()
    )
    graphics.apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.drawImage(/* img = */ source, /* x = */ 0, /* y = */ 0, /* observer = */ null)
    graphics.dispose()
    return newImage
}

internal fun BufferedImage.flipped(horizontal: Boolean): BufferedImage {
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

internal fun BufferedImage.horizontalFlipped(): BufferedImage = flipped(horizontal = true)

internal fun BufferedImage.verticalFlipped(): BufferedImage = flipped(horizontal = false)

internal fun BufferedImage.backgrounded(color: Int): BufferedImage {
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

internal fun BufferedImage.mask(color: Int) {
    val graphics = this@mask.createGraphics()
    val alpha = color ushr 24
    graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 255f)
    val withoutAlpha = color and 0x00FFFFFF
    graphics.color = Color(withoutAlpha)
    graphics.fillRect(0, 0, this@mask.width, this@mask.height)
    graphics.dispose()
}

internal fun BufferedImage.blur(radius: Int): Boolean {
    val imageWidth = this.width
    val imageHeight = this.height
    val pixels: IntArray = getPixels()
    fastGaussianBlur(pixels, imageWidth, imageHeight, radius)
    this.setRGB(
        /* startX = */ 0,
        /* startY = */ 0,
        /* w = */ imageWidth,
        /* h = */ imageHeight,
        /* rgbArray = */ pixels,
        /* offset = */ 0,
        /* scansize = */ imageWidth
    )
    return true
}

internal fun BufferedImage.roundedCornered(cornerRadii: FloatArray): BufferedImage {
    val sourceImage = this
    val newImage = BufferedImage(
        /* width = */ sourceImage.width,
        /* height = */ sourceImage.height,
        /* imageType = */ BufferedImage.TYPE_INT_ARGB
    )
    val graphics = newImage.createGraphics().apply {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.fill(createRoundedCornersShape(sourceImage, cornerRadii))
    graphics.composite = AlphaComposite.SrcIn
    graphics.drawImage(
        /* img = */ sourceImage,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ sourceImage.width,
        /* height = */ sourceImage.height,
        /* observer = */ null,
    )
    graphics.dispose()
    return newImage
}

internal fun BufferedImage.circleCropped(scale: Scale): BufferedImage {
    val sourceImage = this
    val newImageSize = min(sourceImage.width, sourceImage.height)
    val newImage = BufferedImage(
        /* width = */ newImageSize,
        /* height = */ newImageSize,
        /* imageType = */ BufferedImage.TYPE_INT_ARGB
    )
    val graphics = newImage.createGraphics().apply {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.fillRoundRect(
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ newImage.width,
        /* height = */ newImage.height,
        /* arcWidth = */ newImage.width,
        /* arcHeight = */ newImage.height,
    )
    graphics.composite = AlphaComposite.SrcIn
    val mapping = calculateResizeMapping(
        imageWidth = sourceImage.width,
        imageHeight = sourceImage.height,
        resizeWidth = newImage.width,
        resizeHeight = newImage.height,
        precision = EXACTLY,
        scale = scale,
    )
    graphics.drawImage(
        /* img = */ sourceImage,
        /* dx1 = */ 0,
        /* dy1 = */ 0,
        /* dx2 = */ newImage.width,
        /* dy2 = */ newImage.height,
        /* sx1 = */ mapping.srcRect.left,
        /* sy1 = */ mapping.srcRect.top,
        /* sx2 = */ mapping.srcRect.right,
        /* sy2 = */ mapping.srcRect.bottom,
        /* observer = */ null,
    )
    graphics.dispose()
    return newImage
}


private fun createRoundedCornersShape(
    sourceImage: BufferedImage,
    cornerRadii: FloatArray
): Shape = Area().apply {
    /* Use four rounded rectangles with different degrees to overlap each other, obtain a rounded rectangle with four different angles */
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ 0.0,
                /* y = */ 0.0,
                /* w = */ sourceImage.width * 0.75,
                /* h = */ sourceImage.height * 0.75,
                /* arcw = */ cornerRadii[0].toDouble(),
                /* arch = */ cornerRadii[1].toDouble()
            )
        )
    )
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ sourceImage.width * 0.25,
                /* y = */ 0.0,
                /* w = */ sourceImage.width * 0.75,
                /* h = */ sourceImage.height * 0.75,
                /* arcw = */ cornerRadii[2].toDouble(),
                /* arch = */ cornerRadii[3].toDouble()
            )
        )
    )
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ sourceImage.width * 0.25,
                /* y = */ sourceImage.height * 0.25,
                /* w = */ sourceImage.width * 0.75,
                /* h = */ sourceImage.height * 0.75,
                /* arcw = */ cornerRadii[4].toDouble(),
                /* arch = */ cornerRadii[5].toDouble()
            )
        )
    )
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ 0.0,
                /* y = */ sourceImage.height * 0.25,
                /* w = */ sourceImage.width * 0.75,
                /* h = */ sourceImage.height * 0.75,
                /* arcw = */ cornerRadii[6].toDouble(),
                /* arch = */ cornerRadii[7].toDouble()
            )
        )
    )
}

//private fun calculateRotatedImageSize(width: Int, height: Int, angle: Double): Pair<Int, Int> {
//    val radians = Math.toRadians(angle)
//
//    val transform = AffineTransform.getRotateInstance(radians)
//
//    val corners = arrayOf(
//        Point2D.Double(0.0, 0.0),
//        Point2D.Double(width.toDouble(), 0.0),
//        Point2D.Double(0.0, height.toDouble()),
//        Point2D.Double(width.toDouble(), height.toDouble())
//    )
//
//    var minX = Double.MAX_VALUE
//    var minY = Double.MAX_VALUE
//    var maxX = Double.MIN_VALUE
//    var maxY = Double.MIN_VALUE
//
//    for (corner in corners) {
//        val result = Point2D.Double()
//        transform.transform(corner, result)
//        minX = min(minX, result.x)
//        minY = min(minY, result.y)
//        maxX = max(maxX, result.x)
//        maxY = max(maxY, result.y)
//    }
//
//    val newWidth = abs(maxX - minX).toInt()
//    val newHeight = abs(maxY - minY).toInt()
//
//    return Pair(newWidth, newHeight)
//}