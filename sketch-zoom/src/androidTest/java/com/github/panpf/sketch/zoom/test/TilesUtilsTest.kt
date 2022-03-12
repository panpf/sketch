package com.github.panpf.sketch.zoom.test

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.tile.Tile
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
        initializeTileMap(imageSize = Size(8000, 8000), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 40, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(2)!!, expectedSize = 12, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(4)!!, expectedSize = 4, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(8000, 8000))
        }

        initializeTileMap(imageSize = Size(8000, 3000), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 16, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(2)!!, expectedSize = 4, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(8000, 3000))
        }

        initializeTileMap(imageSize = Size(3000, 8000), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 15, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(2)!!, expectedSize = 6, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(3000, 8000))
        }


        initializeTileMap(imageSize = Size(1500, 1500), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(tileList = get(1)!!, expectedSize = 2, imageSize = Size(1500, 1500))
            checkTiles(tileList = get(2)!!, expectedSize = 1, imageSize = Size(1500, 1500))
        }

        initializeTileMap(imageSize = Size(1000, 1500), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(tileList = get(1)!!, expectedSize = 1, imageSize = Size(1000, 1500))
        }

        initializeTileMap(imageSize = Size(1500, 1000), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(tileList = get(1)!!, expectedSize = 2, imageSize = Size(1500, 1000))
            checkTiles(tileList = get(2)!!, expectedSize = 1, imageSize = Size(1500, 1000))
        }

        initializeTileMap(imageSize = Size(1000, 1000), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(tileList = get(1)!!, expectedSize = 1, imageSize = Size(1000, 1000))
        }


        initializeTileMap(imageSize = Size(30000, 926), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(6, size)
            checkTiles(tileList = get(1)!!, expectedSize = 28, imageSize = Size(30000, 926))
            checkTiles(tileList = get(2)!!, expectedSize = 14, imageSize = Size(30000, 926))
            checkTiles(tileList = get(4)!!, expectedSize = 7, imageSize = Size(30000, 926))
            checkTiles(tileList = get(8)!!, expectedSize = 4, imageSize = Size(30000, 926))
            checkTiles(tileList = get(16)!!, expectedSize = 2, imageSize = Size(30000, 926))
            checkTiles(tileList = get(32)!!, expectedSize = 1, imageSize = Size(30000, 926))
        }

        initializeTileMap(imageSize = Size(690, 12176), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 7, imageSize = Size(690, 12176))
            checkTiles(tileList = get(2)!!, expectedSize = 4, imageSize = Size(690, 12176))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(690, 12176))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(690, 12176))
        }

        initializeTileMap(imageSize = Size(7557, 5669), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 21, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(2)!!, expectedSize = 8, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(7557, 5669))
        }

        initializeTileMap(imageSize = Size(9798, 6988), viewSize = Size(1080, 1920)).apply {
            Assert.assertEquals(5, size)
            checkTiles(tileList = get(1)!!, expectedSize = 40, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(2)!!, expectedSize = 10, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(4)!!, expectedSize = 3, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(8)!!, expectedSize = 2, imageSize = Size(9798, 6988))
            checkTiles(tileList = get(16)!!, expectedSize = 1, imageSize = Size(9798, 6988))
        }
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
}