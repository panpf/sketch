package com.github.panpf.sketch.zoom.test

import android.graphics.Rect
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSamplingSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.findSampleSize
import com.github.panpf.sketch.zoom.tile.initializeTileMap
import com.github.panpf.sketch.zoom.tile.isIntersection
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.max
import kotlin.math.min

@RunWith(AndroidJUnit4::class)
class TilesUtilsTest {

    @Test
    fun testInitializeTileMap() {
        initializeTileMap(imageSize = Size(8000, 8000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 40, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(2)!!, expectedSize = 12, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(4)!!, expectedSize = 4, imageSize = Size(8000, 8000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(8000, 8000))
        }

        initializeTileMap(imageSize = Size(8000, 3000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 16, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(2)!!, expectedSize = 4, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(8000, 3000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(8000, 3000))
        }

        initializeTileMap(imageSize = Size(3000, 8000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 15, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(2)!!, expectedSize = 6, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(3000, 8000))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(3000, 8000))
        }


        initializeTileMap(imageSize = Size(1500, 1500), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(tileList = get(1)!!, expectedSize = 2, imageSize = Size(1500, 1500))
            checkTiles(tileList = get(2)!!, expectedSize = 1, imageSize = Size(1500, 1500))
        }

        initializeTileMap(imageSize = Size(1000, 1500), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(tileList = get(1)!!, expectedSize = 1, imageSize = Size(1000, 1500))
        }

        initializeTileMap(imageSize = Size(1500, 1000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(tileList = get(1)!!, expectedSize = 2, imageSize = Size(1500, 1000))
            checkTiles(tileList = get(2)!!, expectedSize = 1, imageSize = Size(1500, 1000))
        }

        initializeTileMap(imageSize = Size(1000, 1000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(tileList = get(1)!!, expectedSize = 1, imageSize = Size(1000, 1000))
        }


        initializeTileMap(imageSize = Size(30000, 926), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(6, size)
            checkTiles(tileList = get(1)!!, expectedSize = 28, imageSize = Size(30000, 926))
            checkTiles(tileList = get(2)!!, expectedSize = 14, imageSize = Size(30000, 926))
            checkTiles(tileList = get(4)!!, expectedSize = 7, imageSize = Size(30000, 926))
            checkTiles(tileList = get(8)!!, expectedSize = 4, imageSize = Size(30000, 926))
            checkTiles(tileList = get(16)!!, expectedSize = 2, imageSize = Size(30000, 926))
            checkTiles(tileList = get(32)!!, expectedSize = 1, imageSize = Size(30000, 926))
        }

        initializeTileMap(imageSize = Size(690, 12176), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 7, imageSize = Size(690, 12176))
            checkTiles(tileList = get(2)!!, expectedSize = 4, imageSize = Size(690, 12176))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(690, 12176))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(690, 12176))
        }

        initializeTileMap(imageSize = Size(7557, 5669), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(tileList = get(1)!!, expectedSize = 21, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(2)!!, expectedSize = 8, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(4)!!, expectedSize = 2, imageSize = Size(7557, 5669))
            checkTiles(tileList = get(8)!!, expectedSize = 1, imageSize = Size(7557, 5669))
        }

        initializeTileMap(imageSize = Size(9798, 6988), tileMaxSize = Size(1080, 1920)).apply {
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

        Assert.assertEquals(16, findSampleSize(Size(800, 800), Size(50, 50), 1f))
        Assert.assertEquals(8, findSampleSize(Size(800, 800), Size(51, 51), 1f))
        Assert.assertEquals(8, findSampleSize(Size(800, 800), Size(99, 99), 1f))
        Assert.assertEquals(8, findSampleSize(Size(800, 800), Size(100, 100), 1f))
        Assert.assertEquals(4, findSampleSize(Size(800, 800), Size(101, 101), 1f))
        Assert.assertEquals(4, findSampleSize(Size(800, 800), Size(199, 199), 1f))
        Assert.assertEquals(4, findSampleSize(Size(800, 800), Size(200, 200), 1f))
        Assert.assertEquals(2, findSampleSize(Size(800, 800), Size(201, 201), 1f))
        Assert.assertEquals(2, findSampleSize(Size(800, 800), Size(399, 399), 1f))
        Assert.assertEquals(2, findSampleSize(Size(800, 800), Size(400, 400), 1f))
        Assert.assertEquals(1, findSampleSize(Size(800, 800), Size(401, 401), 1f))
        Assert.assertEquals(1, findSampleSize(Size(800, 800), Size(799, 799), 1f))
        Assert.assertEquals(1, findSampleSize(Size(800, 800), Size(800, 800), 1f))
        Assert.assertEquals(1, findSampleSize(Size(800, 800), Size(801, 801), 1f))
        Assert.assertEquals(1, findSampleSize(Size(800, 800), Size(10000, 10000), 1f))

        Assert.assertEquals(
            findSampleSize(Size(800, 800), Size(200, 200), 1f),
            findSampleSize(Size(800, 800), Size(100, 100), 2f)
        )
        Assert.assertEquals(
            findSampleSize(Size(800, 800), Size(300, 300), 1f),
            findSampleSize(Size(800, 800), Size(100, 100), 3f)
        )
        Assert.assertEquals(
            findSampleSize(Size(800, 800), Size(400, 400), 1f),
            findSampleSize(Size(800, 800), Size(100, 100), 4f)
        )
        Assert.assertEquals(
            findSampleSize(Size(800, 800), Size(700, 700), 1f),
            findSampleSize(Size(800, 800), Size(100, 100), 7f)
        )
        Assert.assertEquals(
            findSampleSize(Size(800, 800), Size(800, 800), 1f),
            findSampleSize(Size(800, 800), Size(100, 100), 8f)
        )
    }

    @Test
    fun testIsOverlap() {
        // same
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(40, 40, 60, 60)))

        // outside
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(20, 20, 60, 60)))

        // inside
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(40, 40, 50, 50)))

        // no cross
        Assert.assertFalse(Rect(40, 40, 60, 60).isIntersection(Rect(40, 20, 60, 40)))
        Assert.assertFalse(Rect(40, 40, 60, 60).isIntersection(Rect(20, 40, 40, 60)))
        Assert.assertFalse(Rect(40, 40, 60, 60).isIntersection(Rect(60, 40, 80, 60)))
        Assert.assertFalse(Rect(40, 40, 60, 60).isIntersection(Rect(40, 60, 60, 80)))

        // cross
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(30, 30, 50, 50)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(50, 30, 70, 50)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(30, 50, 50, 70)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(50, 50, 70, 70)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(30, 40, 50, 60)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(40, 30, 60, 50)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(50, 40, 70, 60)))
        Assert.assertTrue(Rect(40, 40, 60, 60).isIntersection(Rect(40, 50, 60, 70)))


        /*
        * 0  20  40  60  80  100
        * 20
        * 40
        * 60
        * 80
        * 100
         */
        val tiles = initializeTileMap(imageSize = Size(100, 100), tileMaxSize = Size(20, 20))[1]!!

        // all tile
        Assert.assertEquals(
            tiles.map { it.srcRect },
            findIntersectionTilesByRect(tiles, Rect(0, 0, 100, 100)).first.map { it.srcRect }
        )

        // single tile
        Assert.assertEquals(
            listOf(Rect(0, 0, 20, 20)),
            findIntersectionTilesByRect(tiles, Rect(0, 0, 20, 20)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(80, 0, 100, 20)),
            findIntersectionTilesByRect(tiles, Rect(80, 0, 100, 20)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(0, 80, 20, 100)),
            findIntersectionTilesByRect(tiles, Rect(0, 80, 20, 100)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(80, 80, 100, 100)),
            findIntersectionTilesByRect(tiles, Rect(80, 80, 100, 100)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(40, 40, 60, 60)),
            findIntersectionTilesByRect(tiles, Rect(40, 40, 60, 60)).first.map { it.srcRect }
        )

        // multi not welt tile
        Assert.assertEquals(
            listOf(
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findIntersectionTilesByRect(tiles, Rect(30, 45, 70, 55)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(40, 20, 60, 40),
                Rect(40, 40, 60, 60),
                Rect(40, 60, 60, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(45, 30, 55, 70)).first.map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findIntersectionTilesByRect(tiles, Rect(30, 30, 70, 50)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(30, 30, 50, 70)).first.map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80), Rect(60, 60, 80, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(30, 30, 70, 70)).first.map { it.srcRect }
        )

        // multi welt tile
        Assert.assertEquals(
            listOf(
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findIntersectionTilesByRect(tiles, Rect(20, 40, 80, 60)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(40, 20, 60, 40),
                Rect(40, 40, 60, 60),
                Rect(40, 60, 60, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(40, 20, 60, 80)).first.map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findIntersectionTilesByRect(tiles, Rect(20, 20, 80, 60)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(20, 20, 60, 80)).first.map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80), Rect(60, 60, 80, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(20, 20, 80, 80)).first.map { it.srcRect }
        )

        // multi hybrid welt tile
        Assert.assertEquals(
            listOf(
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findIntersectionTilesByRect(tiles, Rect(20, 40, 70, 55)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(40, 20, 60, 40),
                Rect(40, 40, 60, 60),
                Rect(40, 60, 60, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(40, 20, 55, 70)).first.map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findIntersectionTilesByRect(tiles, Rect(30, 30, 80, 60)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(30, 30, 60, 80)).first.map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80), Rect(60, 60, 80, 80),
            ),
            findIntersectionTilesByRect(tiles, Rect(20, 30, 70, 80)).first.map { it.srcRect }
        )

        // empty
        Assert.assertEquals(
            listOf<Rect>(),
            findIntersectionTilesByRect(tiles, Rect(-10, 30, 0, 80)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf<Rect>(),
            findIntersectionTilesByRect(tiles, Rect(20, -10, 70, 0)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf<Rect>(),
            findIntersectionTilesByRect(tiles, Rect(100, 30, 110, 80)).first.map { it.srcRect }
        )
        Assert.assertEquals(
            listOf<Rect>(),
            findIntersectionTilesByRect(tiles, Rect(20, 100, 70, 110)).first.map { it.srcRect }
        )
    }

    private fun findIntersectionTilesByRect(
        tiles: List<Tile>,
        rect: Rect
    ): Pair<List<Tile>, List<Tile>> {
        return tiles.partition { tile -> tile.srcRect.isIntersection(rect) }
    }

    private fun findSampleSize(imageSize: Size, previewSize: Size, scale: Float): Int {
        return findSampleSize(
            imageSize.width, imageSize.height, previewSize.width, previewSize.height, scale
        )
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
        return Size(
            calculateSamplingSize(width, sampleSize),
            calculateSamplingSize(height, sampleSize)
        )
    }

    private fun sampledSize(width: Int, height: Int, sampleSize: Double): Size {
        return Size(
            calculateSamplingSize(width, sampleSize),
            calculateSamplingSize(height, sampleSize)
        )
    }
}