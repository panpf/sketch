package com.github.panpf.sketch.extensions.core.common.test.ability

import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_MASK_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_STROKE_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressIndicatorTest {

    @Test
    fun testConsts() {
        assertEquals(
            expected = 150,
            actual = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
        )
        assertEquals(
            expected = false,
            actual = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
        )
        assertEquals(
            expected = true,
            actual = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
        )

        assertEquals(
            expected = 0x22000000,
            actual = PROGRESS_INDICATOR_MASK_COLOR
        )

        assertEquals(
            expected = 0xFFFFFFFF.toInt(),
            actual = PROGRESS_INDICATOR_RING_COLOR
        )
        assertEquals(
            expected = 0.25f,
            actual = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT
        )
        assertEquals(
            expected = 50f,
            actual = PROGRESS_INDICATOR_RING_SIZE
        )
        assertEquals(
            expected = 0.1f,
            actual = PROGRESS_INDICATOR_RING_WIDTH_PERCENT
        )

        assertEquals(
            expected = 50f,
            actual = PROGRESS_INDICATOR_SECTOR_SIZE
        )
        assertEquals(
            expected = 0x44000000,
            actual = PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR
        )
        assertEquals(
            expected = 0xFFFFFFFF.toInt(),
            actual = PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR
        )
        assertEquals(
            expected = 0xFFFFFFFF.toInt(),
            actual = PROGRESS_INDICATOR_SECTOR_STROKE_COLOR
        )
        assertEquals(
            expected = 0.02f,
            actual = PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT
        )
    }
}