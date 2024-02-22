package com.github.panpf.sketch.util

import com.github.panpf.sketch.resize.internal.ResizeMapping
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

fun BufferedImage.scale(scaleFactor: Float): BufferedImage {
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

fun BufferedImage.rotate(degree: Int): BufferedImage {
    val source = this
    val sourceSize = Size(source.width, source.height)
    val newSize = sourceSize.rotate(degree)
    val type = source.colorModel.transparency
    val newImage = BufferedImage(newSize.width, newSize.height, type)
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

fun BufferedImage.flip(horizontal: Boolean): BufferedImage {
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

fun BufferedImage.horizontalFlip(): BufferedImage = flip(horizontal = true)

fun BufferedImage.verticalFlip(): BufferedImage = flip(horizontal = false)