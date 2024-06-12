package com.github.panpf.sketch.sample.ui.components.zoomimage.core

import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateRotatedSize
import com.github.panpf.sketch.util.fastGaussianBlur
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.color.ColorSpace
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import kotlin.math.min

typealias BufferedBitmap = BufferedImage

internal fun BufferedBitmap.copied(): BufferedBitmap {
    val newImage = BufferedBitmap(width, height, type)
    val graphics = newImage.createGraphics()
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()
    return newImage
}

internal fun BufferedBitmap.hasAlpha(): Boolean {
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

internal fun BufferedBitmap.readPixels(region: Rect? = null): IntArray {
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

internal fun BufferedBitmap.toLogString(): String {
    return "BufferedBitmap@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},${colorModel.colorSpace.typeName})"
}

internal fun BufferedBitmap.backgrounded(color: Int): BufferedBitmap {
    val inputBitmap = this
    val outBitmap = BufferedBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.type)
    val graphics = outBitmap.createGraphics()
    graphics.color = Color(color)
    graphics.fillRect(0, 0, inputBitmap.width, inputBitmap.height)
    graphics.color = null
    graphics.drawImage(inputBitmap, 0, 0, null)
    graphics.dispose()
    return outBitmap
}

internal fun BufferedBitmap.blur(radius: Int): Boolean {
    val imageWidth = this.width
    val imageHeight = this.height
    val pixels: IntArray = readPixels()
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

internal fun BufferedBitmap.circleCropped(scale: Scale): BufferedBitmap {
    val inputBitmap = this
    val newImageSize = min(inputBitmap.width, inputBitmap.height)
    val outBitmap = BufferedBitmap(
        /* width = */ newImageSize,
        /* height = */ newImageSize,
        /* imageType = */ BufferedBitmap.TYPE_INT_ARGB
    )
    val graphics = outBitmap.createGraphics().apply {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.fillRoundRect(
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ outBitmap.width,
        /* height = */ outBitmap.height,
        /* arcWidth = */ outBitmap.width,
        /* arcHeight = */ outBitmap.height,
    )
    graphics.composite = AlphaComposite.SrcIn
    val mapping = calculateResizeMapping(
        imageWidth = inputBitmap.width,
        imageHeight = inputBitmap.height,
        resizeWidth = outBitmap.width,
        resizeHeight = outBitmap.height,
        precision = Precision.EXACTLY,
        scale = scale,
    )!!
    graphics.drawImage(
        /* img = */ inputBitmap,
        /* dx1 = */ 0,
        /* dy1 = */ 0,
        /* dx2 = */ outBitmap.width,
        /* dy2 = */ outBitmap.height,
        /* sx1 = */ mapping.srcRect.left,
        /* sy1 = */ mapping.srcRect.top,
        /* sx2 = */ mapping.srcRect.right,
        /* sy2 = */ mapping.srcRect.bottom,
        /* observer = */ null,
    )
    graphics.dispose()
    return outBitmap
}

internal fun BufferedBitmap.flipped(horizontal: Boolean = true): BufferedBitmap {
    val inputBitmap = this
    val outBitmap = BufferedBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.type)
    val graphics = outBitmap.createGraphics()
    val transform = if (horizontal) {
        AffineTransform.getTranslateInstance(inputBitmap.width.toDouble(), 0.0)
    } else {
        AffineTransform.getTranslateInstance(0.0, inputBitmap.height.toDouble())
    }.apply {
        val flip = if (horizontal) {
            AffineTransform.getScaleInstance(-1.0, 1.0)
        } else {
            AffineTransform.getScaleInstance(1.0, -1.0)
        }
        concatenate(flip)
    }
    graphics.transform = transform
    graphics.drawImage(inputBitmap, 0, 0, null)
    graphics.dispose()
    return outBitmap
}

internal fun BufferedBitmap.mapping(mapping: ResizeMapping): BufferedBitmap {
    val inputBitmap = this
    val newWidth = mapping.newWidth
    val newHeight = mapping.newHeight
    val newType = inputBitmap.colorModel.transparency
    val outBitmap = BufferedBitmap(
        /* width = */ newWidth,
        /* height = */ newHeight,
        /* imageType = */ newType
    )
    val graphics: Graphics2D = outBitmap.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
    }
    graphics.drawImage(
        /* img = */ inputBitmap,
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
    return outBitmap
}

internal fun BufferedBitmap.mask(color: Int) {
    val graphics = this@mask.createGraphics()
    val alpha = color ushr 24
    graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 255f)
    val withoutAlpha = color and 0x00FFFFFF
    graphics.color = Color(withoutAlpha)
    graphics.fillRect(0, 0, this@mask.width, this@mask.height)
    graphics.dispose()
}

internal fun BufferedBitmap.roundedCornered(cornerRadii: FloatArray): BufferedBitmap {
    val inputBitmap = this
    val outBitmap = BufferedBitmap(
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* imageType = */ BufferedBitmap.TYPE_INT_ARGB
    )
    val graphics = outBitmap.createGraphics().apply {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.fill(createRoundedCornersShape(inputBitmap, cornerRadii))
    graphics.composite = AlphaComposite.SrcIn
    graphics.drawImage(
        /* img = */ inputBitmap,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* observer = */ null,
    )
    graphics.dispose()
    return outBitmap
}

internal fun BufferedBitmap.rotated(angle: Int): BufferedBitmap {
    val inputBitmap = this
    val inputSize = Size(inputBitmap.width, inputBitmap.height)
    val finalAngle = (angle % 360).let { if (it < 0) 360 + it else it }
    val outSize = calculateRotatedSize(size = inputSize, angle = finalAngle.toDouble())
    val outBitmap = BufferedBitmap(
        /* width = */ outSize.width,
        /* height = */ outSize.height,
        /* imageType = */ BufferedBitmap.TYPE_INT_ARGB
    )
    val graphics: Graphics2D = outBitmap.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.translate(
        /* tx = */ (outSize.width - inputSize.width) / 2.0,
        /* ty = */ (outSize.height - inputSize.height) / 2.0
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
        /* x = */ (inputSize.width / 2).toDouble(),
        /* y = */ (inputSize.height / 2).toDouble()
    )
    graphics.apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }
    graphics.drawImage(/* img = */ inputBitmap, /* x = */ 0, /* y = */ 0, /* observer = */ null)
    graphics.dispose()
    return outBitmap
}

internal fun BufferedBitmap.scaled(scaleFactor: Float): BufferedBitmap {
    val inputBitmap = this
    val oldWidth = inputBitmap.width
    val oldHeight = inputBitmap.height
    val newWidth = (oldWidth * scaleFactor).toInt()
    val newHeight = (oldHeight * scaleFactor).toInt()
    val newType = inputBitmap.colorModel.transparency
    val outBitmap = BufferedBitmap(
        /* width = */ newWidth,
        /* height = */ newHeight,
        /* imageType = */ newType
    )
    val graphics: Graphics2D = outBitmap.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
    }
    graphics.drawImage(
        /* img = */ inputBitmap,
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
    return outBitmap
}

private fun createRoundedCornersShape(
    inputBitmap: BufferedBitmap,
    cornerRadii: FloatArray
): Shape = Area().apply {
    /* Use four rounded rectangles with different degrees to overlap each other, obtain a rounded rectangle with four different angles */
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ 0.0,
                /* y = */ 0.0,
                /* w = */ inputBitmap.width * 0.75,
                /* h = */ inputBitmap.height * 0.75,
                /* arcw = */ cornerRadii[0].toDouble(),
                /* arch = */ cornerRadii[1].toDouble()
            )
        )
    )
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ inputBitmap.width * 0.25,
                /* y = */ 0.0,
                /* w = */ inputBitmap.width * 0.75,
                /* h = */ inputBitmap.height * 0.75,
                /* arcw = */ cornerRadii[2].toDouble(),
                /* arch = */ cornerRadii[3].toDouble()
            )
        )
    )
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ inputBitmap.width * 0.25,
                /* y = */ inputBitmap.height * 0.25,
                /* w = */ inputBitmap.width * 0.75,
                /* h = */ inputBitmap.height * 0.75,
                /* arcw = */ cornerRadii[4].toDouble(),
                /* arch = */ cornerRadii[5].toDouble()
            )
        )
    )
    add(
        Area(
            RoundRectangle2D.Double(
                /* x = */ 0.0,
                /* y = */ inputBitmap.height * 0.25,
                /* w = */ inputBitmap.width * 0.75,
                /* h = */ inputBitmap.height * 0.75,
                /* arcw = */ cornerRadii[6].toDouble(),
                /* arch = */ cornerRadii[7].toDouble()
            )
        )
    )
}

private val ColorSpace.typeName: String
    get() = when (type) {
        ColorSpace.TYPE_XYZ -> "XYZ"
        ColorSpace.TYPE_Lab -> "Lab"
        ColorSpace.TYPE_Luv -> "Luv"
        ColorSpace.TYPE_YCbCr -> "YCbCr"
        ColorSpace.TYPE_Yxy -> "Yxy"
        ColorSpace.TYPE_RGB -> "RGB"
        ColorSpace.TYPE_GRAY -> "GRAY"
        ColorSpace.TYPE_HSV -> "HSV"
        ColorSpace.TYPE_HLS -> "HLS"
        ColorSpace.TYPE_CMYK -> "CMYK"
        ColorSpace.TYPE_CMY -> "CMY"
        ColorSpace.TYPE_2CLR -> "2CLR"
        ColorSpace.TYPE_3CLR -> "3CLR"
        ColorSpace.TYPE_4CLR -> "4CLR"
        ColorSpace.TYPE_5CLR -> "5CLR"
        ColorSpace.TYPE_6CLR -> "6CLR"
        ColorSpace.TYPE_7CLR -> "7CLR"
        ColorSpace.TYPE_8CLR -> "8CLR"
        ColorSpace.TYPE_9CLR -> "9CLR"
        ColorSpace.TYPE_ACLR -> "ACLR"
        ColorSpace.TYPE_BCLR -> "BCLR"
        ColorSpace.TYPE_CCLR -> "CCLR"
        ColorSpace.TYPE_DCLR -> "DCLR"
        ColorSpace.TYPE_ECLR -> "ECLR"
        ColorSpace.TYPE_FCLR -> "FCLR"
        else -> "Unknown($type)"
    }