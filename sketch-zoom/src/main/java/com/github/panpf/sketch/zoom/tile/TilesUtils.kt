package com.github.panpf.sketch.zoom.tile

import android.graphics.Rect
import com.github.panpf.sketch.decode.internal.maxBitmapSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import kotlin.math.ceil

internal fun initializeTileMap(imageSize: Size, tileMaxSize: Size): Map<Int, List<Tile>> {
    /* The core rules are: The size of each tile does not exceed viewSize */
    val maxBitmapSize = maxBitmapSize
    val tileMaxWith = tileMaxSize.width.coerceAtMost(maxBitmapSize.width)
    val tileMaxHeight = tileMaxSize.height.coerceAtMost(maxBitmapSize.height)
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
        } while (sampleTileWidth > tileMaxWith)

        var yTiles = 0
        var sourceTileHeight: Int
        var sampleTileHeight: Int
        do {
            yTiles += 1
            sourceTileHeight = ceil(imageSize.height / yTiles.toFloat()).toInt()
            sampleTileHeight = ceil(sourceTileHeight / sampleSize.toFloat()).toInt()
        } while (sampleTileHeight > tileMaxHeight)

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

internal fun findSampleSize(
    imageWidth: Int,
    imageHeight: Int,
    previewWidth: Int,
    previewHeight: Int,
    scale: Float
): Int {
    val widthRatio = (imageWidth / previewWidth.toFloat()).format(1)
    val heightRatio = (imageHeight / previewHeight.toFloat()).format(1)
    require(widthRatio == heightRatio) {
        "imageSize(${imageWidth}x${imageHeight}} and previewSize(${previewWidth}x${previewHeight}) must have the same aspect ratio)"
    }

    val scaledWidthRatio = (imageWidth / (previewWidth * scale))
    var sampleSize = 1
    while (scaledWidthRatio > sampleSize) {
        sampleSize *= 2
    }
    return sampleSize
}

internal fun findIntersectionTilesByRect(tiles: List<Tile>, rect: Rect): Pair<List<Tile>, List<Tile>> {
    return tiles.partition { tile -> tile.srcRect.isIntersection(rect) }
}

/**
 * Returns true if the current Rect has an intersection with [otherRect]. Support boundary overlap
 */
internal fun Rect.isIntersection(otherRect: Rect): Boolean {
    val thisRect = this
    when {
        otherRect.right <= thisRect.left || otherRect.bottom <= thisRect.top || otherRect.left >= thisRect.right || otherRect.top >= thisRect.bottom -> {
            // No intersection
            return false
        }
        otherRect == thisRect -> {
            // same
            return true
        }
        otherRect.left <= thisRect.left && otherRect.top <= thisRect.top && otherRect.right >= thisRect.right && otherRect.bottom >= thisRect.bottom -> {
            // outside
            return true
        }
        otherRect.left >= thisRect.left && otherRect.top >= thisRect.top && otherRect.right <= thisRect.right && otherRect.bottom <= thisRect.bottom -> {
            // inside
            return true
        }
        (otherRect.left >= thisRect.left && otherRect.left < thisRect.right) || (otherRect.right >= thisRect.left && otherRect.right < thisRect.right) -> {
            when {
                otherRect.top <= thisRect.top && otherRect.bottom >= thisRect.bottom -> {
                    return true
                }
                otherRect.top >= thisRect.top && otherRect.bottom <= thisRect.bottom -> {
                    return true
                }
                otherRect.top >= thisRect.top && otherRect.top < thisRect.bottom -> {
                    return true
                }
                otherRect.bottom >= thisRect.top && otherRect.bottom < thisRect.bottom -> {
                    return true
                }
                else -> {
                    return false
                }
            }
        }
        (otherRect.top >= thisRect.top && otherRect.top < thisRect.bottom) || (otherRect.bottom >= thisRect.top && otherRect.bottom < thisRect.bottom) -> {
            return when {
                otherRect.left <= thisRect.left && otherRect.right >= thisRect.right -> {
                    true
                }
                otherRect.left >= thisRect.left && otherRect.right <= thisRect.right -> {
                    true
                }
                otherRect.left >= thisRect.left && otherRect.left < thisRect.right -> {
                    true
                }
                otherRect.right >= thisRect.left && otherRect.right < thisRect.right -> {
                    true
                }
                else -> {
                    false
                }
            }
        }
        else -> {
            return false
        }
    }
}