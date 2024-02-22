package com.github.panpf.sketch.sample.ui.components.zoomimage.core

import com.github.panpf.zoomimage.subsampling.CacheTileBitmap
import com.github.panpf.zoomimage.subsampling.DesktopTileBitmap
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage


class BufferedImageCacheTileBitmap constructor(
    override val key: String,
    override val bufferedImage: BufferedImage,
) : CacheTileBitmap, DesktopTileBitmap {

    override val width: Int = bufferedImage.width

    override val height: Int = bufferedImage.height

    override val byteCount: Int = width * height * (bufferedImage.colorModel.pixelSize / 8)

    override val isRecycled: Boolean = false


    override fun recycle() {

    }

    override fun setIsDisplayed(displayed: Boolean) {

    }

    override fun toString(): String {
        return "SketchTileBitmap(${bufferedImage.toLogString()})"
    }

    internal fun BufferedImage.toLogString(): String {
        return "BufferedImage(${width.toFloat()}x${height.toFloat()}," +
                "${colorModel.colorSpace.typeName})@${hashCode().toString(16)}"
    }

    internal val ColorSpace.typeName: String
        get() = when (type) {
            ColorSpace.TYPE_CMYK -> "CMYK"
            ColorSpace.TYPE_GRAY -> "GRAY"
            ColorSpace.TYPE_RGB -> "RGB"
            ColorSpace.TYPE_HLS -> "HLS"
            else -> "Unknown"
        }
}