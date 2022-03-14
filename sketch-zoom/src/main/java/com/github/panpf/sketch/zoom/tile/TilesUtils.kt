package com.github.panpf.sketch.zoom.tile

import android.graphics.Rect
import com.github.panpf.sketch.decode.internal.maxBitmapSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import kotlin.math.ceil
import kotlin.math.roundToInt

internal fun initializeTileMap(imageSize: Size, sampleTileSize: Size): Map<Int, List<Tile>> {
    /* The core rules are: The size of each tile does not exceed viewSize */
    val maximumBitmapSize = maxBitmapSize
    val sampleTileMaxWith = sampleTileSize.width.coerceAtMost(maximumBitmapSize.width)
    val sampleTileMaxHeight = sampleTileSize.height.coerceAtMost(maximumBitmapSize.height)
    val tileMap = HashMap<Int, List<Tile>>()

    var sampleSize = 1
    while (true) {
        var xTiles = 0
        var sourceTileWidth: Int
        var sampleTileWidth: Int
        do {
            xTiles += 1
            sourceTileWidth = ceil(imageSize.width / xTiles.toFloat()).toInt()
            sampleTileWidth = ceil(sourceTileWidth / sampleSize.toFloat()).toInt()
        } while (sampleTileWidth > sampleTileMaxWith)

        var yTiles = 0
        var sourceTileHeight: Int
        var sampleTileHeight: Int
        do {
            yTiles += 1
            sourceTileHeight = ceil(imageSize.height / yTiles.toFloat()).toInt()
            sampleTileHeight = ceil(sourceTileHeight / sampleSize.toFloat()).toInt()
        } while (sampleTileHeight > sampleTileMaxHeight)

        val tileList = ArrayList<Tile>(xTiles * yTiles)
        var left = 0
        var top = 0
        while (true) {
            val right = (left + sourceTileWidth).coerceAtMost(imageSize.width)
            val bottom = (top + sourceTileHeight).coerceAtMost(imageSize.height)
            tileList.add(Tile(Rect(left, top, right, bottom), sampleSize))
            if (right >= imageSize.width && bottom >= imageSize.height) {
                break
            } else if (right >= imageSize.width) {
                left = 0
                top += sourceTileHeight
            } else {
                left += sourceTileWidth
            }
        }
        tileMap[sampleSize] = tileList

        if (tileList.size == 1) {
            break
        } else {
            sampleSize *= 2
        }
    }
    return tileMap
}

internal fun findSampleSize(imageSize: Size, previewSize: Size, scale: Float): Int {
    val widthRatio = (imageSize.width / previewSize.width.toFloat()).format(1)
    val heightRatio = (imageSize.height / previewSize.height.toFloat()).format(1)
    require(widthRatio == heightRatio) {
        "imageSize(${imageSize}} and previewSize(${previewSize}) must have the same aspect ratio)"
    }

    val scaledPreviewWidth = (previewSize.width * scale)
    val scaledWidthRatio = (imageSize.width / scaledPreviewWidth)

    var sampleSize = 1
    while (scaledWidthRatio > sampleSize) {
        sampleSize *= 2
    }
    return sampleSize
}