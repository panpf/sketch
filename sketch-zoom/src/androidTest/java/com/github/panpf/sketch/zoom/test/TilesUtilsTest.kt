package com.github.panpf.sketch.zoom.test

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSamplingSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.findSampleSize
import com.github.panpf.sketch.zoom.tile.initializeTileMap
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.max
import kotlin.math.min

@RunWith(AndroidJUnit4::class)
class TilesUtilsTest {

    @Test
    fun testInitializeTileMap() {
        initializeTileMap(imageSize = Size(8000, 8000), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 40, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(2)!!, expectedSize = 12, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(4)!!, expectedSize = 4, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(8000, 8000))
        }

        initializeTileMap(imageSize = Size(8000, 3000), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 16, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(2)!!, expectedSize = 4, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(8000, 3000))
        }

        initializeTileMap(imageSize = Size(3000, 8000), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 15, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(2)!!, expectedSize = 6, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(3000, 8000))
        }


        initializeTileMap(imageSize = Size(1500, 1500), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(tileList = get(1)!!, expectedSize = 2, imageSize = Size(1500, 1500))
            checkTiles(tileList = get(2)!!, expectedSize = 1, imageSize = Size(1500, 1500))
        }

        initializeTileMap(imageSize = Size(1000, 1500), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(tileList = get(1)!!, expectedSize = 1, imageSize = Size(1000, 1500))
        }

        initializeTileMap(imageSize = Size(1500, 1000), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(tileList = get(1)!!, expectedSize = 2, imageSize = Size(1500, 1000))
            checkTiles(tileList = get(2)!!, expectedSize = 1, imageSize = Size(1500, 1000))
        }

        initializeTileMap(imageSize = Size(1000, 1000), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(tileList = get(1)!!, expectedSize = 1, imageSize = Size(1000, 1000))
        }


        initializeTileMap(imageSize = Size(30000, 926), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(6, size)
            checkTiles(tileList = get(1)!!, expectedSize = 28, imageSize = Size(30000, 926))
            checkTiles(tileList = get(2)!!, expectedSize = 14, imageSize = Size(30000, 926))
            checkTiles(tileList = get(4)!!, expectedSize = 7, imageSize = Size(30000, 926))
            checkTiles(tileList = get(8)!!, expectedSize = 4, imageSize = Size(30000, 926))
            checkTiles(tileList = get(16)!!, expectedSize = 2, imageSize = Size(30000, 926))
            checkTiles(tileList = get(32)!!, expectedSize = 1, imageSize = Size(30000, 926))
        }

        initializeTileMap(imageSize = Size(690, 12176), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 7, imageSize = Size(690, 12176))
            checkTiles(tileList = get(2)!!, expectedSize = 4, imageSize = Size(690, 12176))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(690, 12176))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(690, 12176))
        }

        initializeTileMap(imageSize = Size(7557, 5669), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 21, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(2)!!, expectedSize = 8, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(7557, 5669))
        }

        initializeTileMap(imageSize = Size(9798, 6988), sampleTileSize = Size(1080, 1920)).apply {
            Assert.assertEquals(5, size)
            checkTiles(tileList = get(1)!!, expectedSize = 40, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(2)!!, expectedSize = 10, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(4)!!, expectedSize = 3, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(8)!!, expectedSize = 2, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(16)!!, expectedSize = 1, imageSize = Size(9798, 6988))
        }
    }

    @Test
    fun testFindSampleSize() {
        val imageSize = Size(9798, 6988)
        val errorPreviewSize = Size(9798 / 16, 6988 / 15)
        val error1PreviewSize = Size(9798 / 15, 6988 / 16)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            findSampleSize(imageSize, errorPreviewSize, scale = 1f)
        }
        Assert.assertThrows(IllegalArgumentException::class.java) {
            findSampleSize(imageSize, error1PreviewSize, scale = 1f)
        }

        Assert.assertEquals(32, findSampleSize(imageSize, sampledSize(9798, 6988, 17), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 16), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 15), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988,  14), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 13), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 12), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 11), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 10), scale = 1f))
        Assert.assertEquals(16, findSampleSize(imageSize, sampledSize(9798, 6988, 9), scale = 1f))
        Assert.assertEquals(8, findSampleSize(imageSize, sampledSize(9798, 6988, 8), scale = 1f))
        Assert.assertEquals(8, findSampleSize(imageSize, sampledSize(9798, 6988, 7), scale = 1f))
        Assert.assertEquals(8, findSampleSize(imageSize, sampledSize(9798, 6988, 6), scale = 1f))
        Assert.assertEquals(8, findSampleSize(imageSize, sampledSize(9798, 6988, 5), scale = 1f))
        Assert.assertEquals(4, findSampleSize(imageSize, sampledSize(9798, 6988, 4), scale = 1f))
        Assert.assertEquals(4, findSampleSize(imageSize, sampledSize(9798, 6988, 3), scale = 1f))
        Assert.assertEquals(2, findSampleSize(imageSize, sampledSize(9798, 6988, 2), scale = 1f))
        Assert.assertEquals(1, findSampleSize(imageSize, sampledSize(9798, 6988, 1), scale = 1f))
        Assert.assertEquals(1, findSampleSize(imageSize, sampledSize(9798, 6988, 0.9), scale = 1f))
        Assert.assertEquals(1, findSampleSize(imageSize, sampledSize(9798, 6988, 0.1), scale = 1f))

        val previewSize = sampledSize(9798, 6988, 16)
        Assert.assertEquals(256, findSampleSize(imageSize, previewSize, scale = 0.1f))
        Assert.assertEquals(32, findSampleSize(imageSize, previewSize, scale = 0.9f))
        Assert.assertEquals(16, findSampleSize(imageSize, previewSize, scale = 1f))
        Assert.assertEquals(8, findSampleSize(imageSize, previewSize, scale = 2f))
        Assert.assertEquals(8, findSampleSize(imageSize, previewSize, scale = 3f))
        Assert.assertEquals(4, findSampleSize(imageSize, previewSize, scale = 4f))
        Assert.assertEquals(4, findSampleSize(imageSize, previewSize, scale = 5f))
        Assert.assertEquals(4, findSampleSize(imageSize, previewSize, scale = 6f))
        Assert.assertEquals(4, findSampleSize(imageSize, previewSize, scale = 7f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 8f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 9f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 10f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 11f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 12f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 13f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 14f))
        Assert.assertEquals(2, findSampleSize(imageSize, previewSize, scale = 15f))
        Assert.assertEquals(1, findSampleSize(imageSize, previewSize, scale = 16f))
        Assert.assertEquals(1, findSampleSize(imageSize, previewSize, scale = 17f))
    }

    private fun checkTiles(tileList: List<Tile>, expectedSize: Int, imageSize: Size) {
        Assert.assertEquals(expectedSize, tileList.size)
        var minLeft = 0
        var minTop = 0
        var maxRight = 0
        var maxBottom = 0
        var lastTop = 0
        var lastRight = 0
        tileList.forEachIndexed { index, tile ->
            if (index == 0) {
                Assert.assertEquals(0, tile.srcRect.left)
                Assert.assertEquals(0, tile.srcRect.top)
            } else if (index == tileList.lastIndex) {
                Assert.assertEquals(imageSize.width, tile.srcRect.right)
                Assert.assertEquals(imageSize.height, tile.srcRect.bottom)
            }

            Assert.assertEquals(lastRight, tile.srcRect.left)
            Assert.assertEquals(lastTop, tile.srcRect.top)
            if (tile.srcRect.right >= imageSize.width) {
                lastTop = tile.srcRect.bottom
                lastRight = 0
            } else {
                lastRight = tile.srcRect.right
            }

            minLeft = min(minLeft, tile.srcRect.left)
            minTop = min(minTop, tile.srcRect.top)
            maxRight = max(maxRight, tile.srcRect.right)
            maxBottom = max(maxBottom, tile.srcRect.bottom)
        }
        Assert.assertEquals(0, minLeft)
        Assert.assertEquals(0, minTop)
        Assert.assertEquals(imageSize.width, maxRight)
        Assert.assertEquals(imageSize.height, maxBottom)
    }

    private fun sampledSize(width: Int, height: Int, sampleSize: Int): Size {
        return Size(calculateSamplingSize(width, sampleSize), calculateSamplingSize(height, sampleSize))
    }

    private fun sampledSize(width: Int, height: Int, sampleSize: Double): Size {
        return Size(calculateSamplingSize(width, sampleSize), calculateSamplingSize(height, sampleSize))
    }
}