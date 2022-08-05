/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.zoom.tile.internal

import android.graphics.Rect
import com.github.panpf.sketch.decode.internal.maxBitmapSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.internal.format
import com.github.panpf.sketch.zoom.tile.Tile
import kotlin.math.abs
import kotlin.math.ceil

internal fun initializeTileMap(imageSize: Size, tileMaxSize: Size): Map<Int, List<Tile>> {
    /* The core rules are: The size of each tile does not exceed tileMaxSize */
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
    require(shouldUseTiles(imageWidth, imageHeight, previewWidth, previewHeight)) {
        "imageSize(${imageWidth}x${imageHeight}} and previewSize(${previewWidth}x${previewHeight}) must have the same aspect ratio)"
    }

    val scaledWidthRatio = (imageWidth / (previewWidth * scale))
    var sampleSize = 1
    while (scaledWidthRatio >= sampleSize * 2) {
        sampleSize *= 2
    }
    return sampleSize
}

internal fun Rect.crossWith(other: Rect): Boolean {
    return this.left < other.right
            && this.right > other.left
            && this.top < other.bottom
            && this.bottom > other.top
}

internal fun shouldUseTiles(
    imageWidth: Int, imageHeight: Int, previewWidth: Int, previewHeight: Int
): Boolean {
    val imageRatio = (imageWidth / imageHeight.toFloat()).format(1)
    val previewRatio = (previewWidth / previewHeight.toFloat()).format(1)
    return abs(imageRatio - previewRatio).format(1) <= 0.1f
}