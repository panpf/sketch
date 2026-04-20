package com.github.panpf.sketch.video.ios.test.decode.internal

import com.github.panpf.sketch.decode.internal.frameCandidates
import kotlin.test.Test
import kotlin.test.assertEquals

class AvAssetsTest {

    @Test
    fun testDurationMicrosOrNull() {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
//        val asset = AVAsset()
//        assertFails {
//                asset.durationMicrosOrNull()
//        }
    }

    @Test
    fun testFrameCandidates() {
        assertEquals(
            expected = listOf(100001L, 0L, 100_000L, 300_000L),
            actual = frameCandidates(100001L, null)
        )

        assertEquals(
            expected = listOf(100001L, 0L, 100_000L, 300_000L),
            actual = frameCandidates(100001L, -1L)
        )

        assertEquals(
            expected = listOf(100001L, 0L, 100_000L, 300_000L, 11189465L, 16784198),
            actual = frameCandidates(100001L, 33568396L)
        )
        assertEquals(
            expected = listOf(33568395L, 0L, 100_000L, 300_000L, 11189465L, 16784198),
            actual = frameCandidates(33568397L, 33568396L)
        )
    }
}