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
package com.github.panpf.sketch.zoom.test.tile.internal

import android.graphics.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.internal.crossWith
import com.github.panpf.sketch.zoom.tile.internal.findSampleSize
import com.github.panpf.sketch.zoom.tile.internal.initializeTileMap
import com.github.panpf.tools4j.test.ktx.assertNoThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.max
import kotlin.math.min

@RunWith(AndroidJUnit4::class)
class TileUtilsTest {

    @Test
    fun testInitializeTileMap() {
        val checkTiles: (List<Tile>, Int, Size) -> Unit = { tileList, expectedSize, imageSize ->
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

        initializeTileMap(imageSize = Size(8000, 8000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(get(1)!!, 40, Size(8000, 8000))
            checkTiles(get(2)!!, 12, Size(8000, 8000))
            checkTiles(get(4)!!, 4, Size(8000, 8000))
            checkTiles(get(8)!!, 1, Size(8000, 8000))
        }

        initializeTileMap(imageSize = Size(8000, 3000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(get(1)!!, 16, Size(8000, 3000))
            checkTiles(get(2)!!, 4, Size(8000, 3000))
            checkTiles(get(4)!!, 2, Size(8000, 3000))
            checkTiles(get(8)!!, 1, Size(8000, 3000))
        }

        initializeTileMap(imageSize = Size(3000, 8000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(get(1)!!, 15, Size(3000, 8000))
            checkTiles(get(2)!!, 6, Size(3000, 8000))
            checkTiles(get(4)!!, 2, Size(3000, 8000))
            checkTiles(get(8)!!, 1, Size(3000, 8000))
        }


        initializeTileMap(imageSize = Size(1500, 1500), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(get(1)!!, 2, Size(1500, 1500))
            checkTiles(get(2)!!, 1, Size(1500, 1500))
        }

        initializeTileMap(imageSize = Size(1000, 1500), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(get(1)!!, 1, Size(1000, 1500))
        }

        initializeTileMap(imageSize = Size(1500, 1000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(2, size)
            checkTiles(get(1)!!, 2, Size(1500, 1000))
            checkTiles(get(2)!!, 1, Size(1500, 1000))
        }

        initializeTileMap(imageSize = Size(1000, 1000), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(1, size)
            checkTiles(get(1)!!, 1, Size(1000, 1000))
        }


        initializeTileMap(imageSize = Size(30000, 926), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(6, size)
            checkTiles(get(1)!!, 28, Size(30000, 926))
            checkTiles(get(2)!!, 14, Size(30000, 926))
            checkTiles(get(4)!!, 7, Size(30000, 926))
            checkTiles(get(8)!!, 4, Size(30000, 926))
            checkTiles(get(16)!!, 2, Size(30000, 926))
            checkTiles(get(32)!!, 1, Size(30000, 926))
        }

        initializeTileMap(imageSize = Size(690, 12176), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(get(1)!!, 7, Size(690, 12176))
            checkTiles(get(2)!!, 4, Size(690, 12176))
            checkTiles(get(4)!!, 2, Size(690, 12176))
            checkTiles(get(8)!!, 1, Size(690, 12176))
        }

        initializeTileMap(imageSize = Size(7557, 5669), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(4, size)
            checkTiles(get(1)!!, 21, Size(7557, 5669))
            checkTiles(get(2)!!, 8, Size(7557, 5669))
            checkTiles(get(4)!!, 2, Size(7557, 5669))
            checkTiles(get(8)!!, 1, Size(7557, 5669))
        }

        initializeTileMap(Size(9798, 6988), tileMaxSize = Size(1080, 1920)).apply {
            Assert.assertEquals(5, size)
            checkTiles(get(1)!!, 40, Size(9798, 6988))
            checkTiles(get(2)!!, 10, Size(9798, 6988))
            checkTiles(get(4)!!, 3, Size(9798, 6988))
            checkTiles(get(8)!!, 2, Size(9798, 6988))
            checkTiles(get(16)!!, 1, Size(9798, 6988))
        }
    }

    @Test
    fun testFindSampleSize() {
        val imageSize = Size(9798, 6988)

        val findSampleSize1: (Size, Size, Float) -> Int = { imageSize1, previewSize, scale ->
            findSampleSize(
                imageSize1.width, imageSize1.height, previewSize.width, previewSize.height, scale
            )
        }

        assertNoThrow {
            findSampleSize1(imageSize, Size(9798 / 16, 6988 / 16), 1f)
        }
        assertNoThrow {
            findSampleSize1(imageSize, Size(9798 / 16, 6988 / 15), 1f)
        }
        assertNoThrow {
            findSampleSize1(imageSize, Size(9798 / 15, 6988 / 16), 1f)
        }
        assertNoThrow {
            findSampleSize1(imageSize, Size(9798 / 15, 6988 / 15), 1f)
        }
        assertThrow(IllegalArgumentException::class) {
            findSampleSize1(imageSize, Size(9798 / 16, 6988 / 14), 1f)
        }

        Assert.assertEquals(16, findSampleSize1(Size(800, 800), Size(50, 50), 1f))
        Assert.assertEquals(8, findSampleSize1(Size(800, 800), Size(51, 51), 1f))
        Assert.assertEquals(8, findSampleSize1(Size(800, 800), Size(99, 99), 1f))
        Assert.assertEquals(8, findSampleSize1(Size(800, 800), Size(100, 100), 1f))
        Assert.assertEquals(4, findSampleSize1(Size(800, 800), Size(101, 101), 1f))
        Assert.assertEquals(4, findSampleSize1(Size(800, 800), Size(199, 199), 1f))
        Assert.assertEquals(4, findSampleSize1(Size(800, 800), Size(200, 200), 1f))
        Assert.assertEquals(2, findSampleSize1(Size(800, 800), Size(201, 201), 1f))
        Assert.assertEquals(2, findSampleSize1(Size(800, 800), Size(399, 399), 1f))
        Assert.assertEquals(2, findSampleSize1(Size(800, 800), Size(400, 400), 1f))
        Assert.assertEquals(1, findSampleSize1(Size(800, 800), Size(401, 401), 1f))
        Assert.assertEquals(1, findSampleSize1(Size(800, 800), Size(799, 799), 1f))
        Assert.assertEquals(1, findSampleSize1(Size(800, 800), Size(800, 800), 1f))
        Assert.assertEquals(1, findSampleSize1(Size(800, 800), Size(801, 801), 1f))
        Assert.assertEquals(1, findSampleSize1(Size(800, 800), Size(10000, 10000), 1f))

        Assert.assertEquals(
            findSampleSize1(Size(800, 800), Size(200, 200), 1f),
            findSampleSize1(Size(800, 800), Size(100, 100), 2f)
        )
        Assert.assertEquals(
            findSampleSize1(Size(800, 800), Size(300, 300), 1f),
            findSampleSize1(Size(800, 800), Size(100, 100), 3f)
        )
        Assert.assertEquals(
            findSampleSize1(Size(800, 800), Size(400, 400), 1f),
            findSampleSize1(Size(800, 800), Size(100, 100), 4f)
        )
        Assert.assertEquals(
            findSampleSize1(Size(800, 800), Size(700, 700), 1f),
            findSampleSize1(Size(800, 800), Size(100, 100), 7f)
        )
        Assert.assertEquals(
            findSampleSize1(Size(800, 800), Size(800, 800), 1f),
            findSampleSize1(Size(800, 800), Size(100, 100), 8f)
        )
    }

    @Test
    fun testCrossWith() {
        // same
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(40, 40, 60, 60)))

        // outside
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(20, 20, 60, 60)))

        // inside
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(40, 40, 50, 50)))

        // no cross
        Assert.assertFalse(Rect(40, 40, 60, 60).crossWith(Rect(40, 20, 60, 40)))
        Assert.assertFalse(Rect(40, 40, 60, 60).crossWith(Rect(20, 40, 40, 60)))
        Assert.assertFalse(Rect(40, 40, 60, 60).crossWith(Rect(60, 40, 80, 60)))
        Assert.assertFalse(Rect(40, 40, 60, 60).crossWith(Rect(40, 60, 60, 80)))

        // cross
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(30, 30, 50, 50)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(50, 30, 70, 50)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(30, 50, 50, 70)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(50, 50, 70, 70)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(30, 40, 50, 60)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(40, 30, 60, 50)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(50, 40, 70, 60)))
        Assert.assertTrue(Rect(40, 40, 60, 60).crossWith(Rect(40, 50, 60, 70)))


        /*
        * 0  20  40  60  80  100
        * 20
        * 40
        * 60
        * 80
        * 100
         */
        val tiles = initializeTileMap(imageSize = Size(100, 100), tileMaxSize = Size(20, 20))[1]!!

        val findCrossTilesBy: (Rect) -> List<Tile> = {
            tiles.filter { tile -> tile.srcRect.crossWith(it) }
        }

        // all tile
        Assert.assertEquals(
            tiles.map { it.srcRect },
            findCrossTilesBy(Rect(0, 0, 100, 100)).map { it.srcRect }
        )

        // single tile
        Assert.assertEquals(
            listOf(Rect(0, 0, 20, 20)),
            findCrossTilesBy(Rect(0, 0, 20, 20)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(80, 0, 100, 20)),
            findCrossTilesBy(Rect(80, 0, 100, 20)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(0, 80, 20, 100)),
            findCrossTilesBy(Rect(0, 80, 20, 100)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(80, 80, 100, 100)),
            findCrossTilesBy(Rect(80, 80, 100, 100)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(Rect(40, 40, 60, 60)),
            findCrossTilesBy(Rect(40, 40, 60, 60)).map { it.srcRect }
        )

        // multi not welt tile
        Assert.assertEquals(
            listOf(
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findCrossTilesBy(Rect(30, 45, 70, 55)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(40, 20, 60, 40),
                Rect(40, 40, 60, 60),
                Rect(40, 60, 60, 80),
            ),
            findCrossTilesBy(Rect(45, 30, 55, 70)).map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findCrossTilesBy(Rect(30, 30, 70, 50)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80),
            ),
            findCrossTilesBy(Rect(30, 30, 50, 70)).map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80), Rect(60, 60, 80, 80),
            ),
            findCrossTilesBy(Rect(30, 30, 70, 70)).map { it.srcRect }
        )

        // multi welt tile
        Assert.assertEquals(
            listOf(
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findCrossTilesBy(Rect(20, 40, 80, 60)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(40, 20, 60, 40),
                Rect(40, 40, 60, 60),
                Rect(40, 60, 60, 80),
            ),
            findCrossTilesBy(Rect(40, 20, 60, 80)).map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findCrossTilesBy(Rect(20, 20, 80, 60)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80),
            ),
            findCrossTilesBy(Rect(20, 20, 60, 80)).map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80), Rect(60, 60, 80, 80),
            ),
            findCrossTilesBy(Rect(20, 20, 80, 80)).map { it.srcRect }
        )

        // multi hybrid welt tile
        Assert.assertEquals(
            listOf(
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findCrossTilesBy(Rect(20, 40, 70, 55)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(40, 20, 60, 40),
                Rect(40, 40, 60, 60),
                Rect(40, 60, 60, 80),
            ),
            findCrossTilesBy(Rect(40, 20, 55, 70)).map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
            ),
            findCrossTilesBy(Rect(30, 30, 80, 60)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80),
            ),
            findCrossTilesBy(Rect(30, 30, 60, 80)).map { it.srcRect }
        )

        Assert.assertEquals(
            listOf(
                Rect(20, 20, 40, 40), Rect(40, 20, 60, 40), Rect(60, 20, 80, 40),
                Rect(20, 40, 40, 60), Rect(40, 40, 60, 60), Rect(60, 40, 80, 60),
                Rect(20, 60, 40, 80), Rect(40, 60, 60, 80), Rect(60, 60, 80, 80),
            ),
            findCrossTilesBy(Rect(20, 30, 70, 80)).map { it.srcRect }
        )

        // empty
        Assert.assertEquals(
            listOf<Rect>(),
            findCrossTilesBy(Rect(-10, 30, 0, 80)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf<Rect>(),
            findCrossTilesBy(Rect(20, -10, 70, 0)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf<Rect>(),
            findCrossTilesBy(Rect(100, 30, 110, 80)).map { it.srcRect }
        )
        Assert.assertEquals(
            listOf<Rect>(),
            findCrossTilesBy(Rect(20, 100, 70, 110)).map { it.srcRect }
        )
    }
}