package com.github.panpf.sketch.core.common.test.decode.internal

import com.github.panpf.sketch.decode.internal.checkSampledBitmapSize
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class DecodesTest {

    @Test
    fun testCheckSampledBitmapSize() {
        data class Item(
            val sampledBitmapSize: Size,
            val targetSize: Size,
            val smallerSizeMode: Boolean,
            val maxBitmapSize: Size? = null
        )

        listOf(
            Item(Size(300, 500), Size.Empty, false, null) to true,
            Item(Size(300, 500), Size(0, 500), false, null) to true,
            Item(Size(300, 500), Size(300, 0), false, null) to true,
            Item(Size(300, 500), Size(0, 499), false, null) to false,
            Item(Size(300, 499), Size(0, 499), false, null) to true,
            Item(Size(300, 500), Size(299, 0), false, null) to false,
            Item(Size(299, 500), Size(299, 0), false, null) to true,

            Item(Size(300, 500), Size.Empty, true, null) to true,
            Item(Size(300, 500), Size(0, 500), true, null) to true,
            Item(Size(300, 500), Size(300, 0), true, null) to true,
            Item(Size(300, 500), Size(0, 499), true, null) to false,
            Item(Size(300, 499), Size(0, 499), true, null) to true,
            Item(Size(300, 500), Size(299, 0), true, null) to false,
            Item(Size(299, 500), Size(299, 0), true, null) to true,

            Item(Size(300, 500), Size.Empty, false, Size(200, 200)) to false,
            Item(Size(300, 500), Size(0, 500), false, Size(200, 200)) to false,
            Item(Size(300, 500), Size(300, 0), false, Size(200, 200)) to false,
            Item(Size(300, 500), Size(0, 499), false, Size(200, 200)) to false,
            Item(Size(300, 499), Size(0, 499), false, Size(200, 200)) to false,
            Item(Size(300, 500), Size(299, 0), false, Size(200, 200)) to false,
            Item(Size(299, 500), Size(299, 0), false, Size(200, 200)) to false,
            Item(Size(201, 201), Size.Empty, false, Size(200, 200)) to false,
            Item(Size(201, 200), Size.Empty, false, Size(200, 200)) to false,
            Item(Size(200, 201), Size.Empty, false, Size(200, 200)) to false,
            Item(Size(200, 200), Size.Empty, false, Size(200, 200)) to true,

            Item(Size(300, 500), Size.Empty, true, Size(200, 200)) to false,
            Item(Size(300, 500), Size(0, 500), true, Size(200, 200)) to false,
            Item(Size(300, 500), Size(300, 0), true, Size(200, 200)) to false,
            Item(Size(300, 500), Size(0, 499), true, Size(200, 200)) to false,
            Item(Size(300, 499), Size(0, 499), true, Size(200, 200)) to false,
            Item(Size(300, 500), Size(299, 0), true, Size(200, 200)) to false,
            Item(Size(299, 500), Size(299, 0), true, Size(200, 200)) to false,
            Item(Size(201, 201), Size.Empty, true, Size(200, 200)) to false,
            Item(Size(201, 200), Size.Empty, true, Size(200, 200)) to false,
            Item(Size(200, 201), Size.Empty, true, Size(200, 200)) to false,
            Item(Size(200, 200), Size.Empty, true, Size(200, 200)) to true,

            Item(Size(300, 500), Size(300, 400), false, null) to false,
            Item(Size(300, 500), Size(400, 400), false, null) to true,
            Item(Size(300, 500), Size(400, 400), true, null) to false,
            Item(Size(300, 399), Size(400, 400), true, null) to true,

            Item(Size(300, 500), Size(300, 400), false, Size(200, 200)) to false,
            Item(Size(300, 500), Size(400, 400), false, Size(200, 200)) to false,
            Item(Size(300, 500), Size(400, 400), true, Size(200, 200)) to false,
            Item(Size(300, 399), Size(400, 400), true, Size(200, 200)) to false,
            Item(Size(201, 201), Size(400, 400), false, Size(200, 200)) to false,
            Item(Size(201, 200), Size(400, 400), false, Size(200, 200)) to false,
            Item(Size(200, 201), Size(400, 400), false, Size(200, 200)) to false,
            Item(Size(200, 200), Size(400, 400), false, Size(200, 200)) to true,
            Item(Size(201, 201), Size(400, 400), true, Size(200, 200)) to false,
            Item(Size(201, 200), Size(400, 400), true, Size(200, 200)) to false,
            Item(Size(200, 201), Size(400, 400), true, Size(200, 200)) to false,
            Item(Size(200, 200), Size(400, 400), true, Size(200, 200)) to true,
        ).forEachIndexed { index, pair ->
            val (item, expected) = pair
            assertEquals(
                expected = expected,
                actual = checkSampledBitmapSize(
                    sampledBitmapSize = item.sampledBitmapSize,
                    targetSize = item.targetSize,
                    smallerSizeMode = item.smallerSizeMode,
                    maxBitmapSize = item.maxBitmapSize
                ),
                message = "index=$index, item=$item, expected=$expected"
            )
        }
    }
}