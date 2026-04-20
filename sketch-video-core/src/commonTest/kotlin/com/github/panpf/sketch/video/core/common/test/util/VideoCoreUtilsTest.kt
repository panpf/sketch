package com.github.panpf.sketch.video.core.common.test.util

import com.github.panpf.sketch.util.resolveRequestVideoFrameMicros
import kotlin.test.Test
import kotlin.test.assertEquals

class VideoCoreUtilsTest {

    @Test
    fun testResolveRequestVideoFrameMicros() {
        // videoFrameMicros
        assertEquals(
            expected = 20001,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = null,
                videoFrameMicros = 20001,
                videoFramePercent = null,
            )
        )
        assertEquals(
            expected = 20000,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = 20000,
                videoFrameMicros = 20001,
                videoFramePercent = null,
            )
        )
        assertEquals(
            expected = 0L,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = null,
                videoFrameMicros = -20001,
                videoFramePercent = null,
            )
        )
        assertEquals(
            expected = 0L,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = null,
                videoFrameMicros = null,
                videoFramePercent = null,
            )
        )

        // videoFramePercent
        assertEquals(
            expected = 75000,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = 100000,
                videoFrameMicros = null,
                videoFramePercent = 0.75f,
            )
        )
        assertEquals(
            expected = 100000,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = 100000,
                videoFrameMicros = null,
                videoFramePercent = 1.1f,
            )
        )
        assertEquals(
            expected = 0L,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = 100000,
                videoFrameMicros = null,
                videoFramePercent = -0.75f,
            )
        )
        assertEquals(
            expected = 0L,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = 100000,
                videoFrameMicros = null,
                videoFramePercent = null,
            )
        )
        assertEquals(
            expected = 0L,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = -100000,
                videoFrameMicros = null,
                videoFramePercent = 0.75f,
            )
        )
        assertEquals(
            expected = 0L,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = null,
                videoFrameMicros = null,
                videoFramePercent = 0.75f,
            )
        )

        // videoFrameMicros priority
        assertEquals(
            expected = 20001,
            actual = resolveRequestVideoFrameMicros(
                durationMicros = 100000,
                videoFrameMicros = 20001,
                videoFramePercent = 0.75f,
            )
        )
    }
}