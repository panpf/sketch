package com.github.panpf.sketch.extensions.core.common.test.ability

import com.github.panpf.sketch.ability.DATA_FROM_DEFAULT_SIZE
import com.github.panpf.sketch.ability.dataFromColor
import com.github.panpf.sketch.source.DataFrom
import kotlin.test.Test
import kotlin.test.assertEquals

class DataFromLogoTest {

    @Test
    fun testDataFromColor() {
        assertEquals(
            expected = 0x7700FF00,
            actual = dataFromColor(DataFrom.MEMORY_CACHE)
        )
        assertEquals(
            expected = 0x77008800,
            actual = dataFromColor(DataFrom.MEMORY)
        )
        assertEquals(
            expected = 0x77FFFF00,
            actual = dataFromColor(DataFrom.RESULT_CACHE)
        )
        assertEquals(
            expected = 0x77FF8800,
            actual = dataFromColor(DataFrom.DOWNLOAD_CACHE)
        )
        assertEquals(
            expected = 0x771E90FF,
            actual = dataFromColor(DataFrom.LOCAL)
        )
        assertEquals(
            expected = 0x77FF0000,
            actual = dataFromColor(DataFrom.NETWORK)
        )
    }

    @Test
    fun testDataFromDefaultSize() {
        assertEquals(
            expected = 20f,
            actual = DATA_FROM_DEFAULT_SIZE
        )
    }
}