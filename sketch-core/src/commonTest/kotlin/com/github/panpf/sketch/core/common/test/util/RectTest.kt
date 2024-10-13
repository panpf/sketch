package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.flip
import com.github.panpf.sketch.util.rotateInSpace
import com.github.panpf.sketch.util.size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RectTest {

    @Test
    fun testSketchRect() {
        assertEquals(
            expected = Rect::class,
            actual = SketchRect::class
        )
    }

    @Test
    fun testFlip() {
        val spaceSize = Size(1000, 700)
        val rect = Rect(100, 200, 600, 400)

        assertEquals(
            Rect(400, 200, 900, 400),
            rect.flip(spaceSize, vertical = false)
        )
        assertEquals(
            Rect(100, 300, 600, 500),
            rect.flip(spaceSize, vertical = true)
        )
    }

    @Test
    fun testRotateInSpace() {
        val spaceSize = Size(1000, 700)

        listOf(0, 0 - 360, 0 + 360, 0 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Rect(100, 200, 600, 500),
                actual = Rect(100, 200, 600, 500).rotateInSpace(spaceSize, rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(90, 90 - 360, 90 + 360, 90 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Rect(200, 100, 500, 600),
                actual = Rect(100, 200, 600, 500).rotateInSpace(spaceSize, rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(180, 180 - 360, 180 + 360, 180 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Rect(400, 200, 900, 500),
                actual = Rect(100, 200, 600, 500).rotateInSpace(spaceSize, rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(270, 270 - 360, 270 + 360, 270 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Rect(200, 400, 500, 900),
                actual = Rect(100, 200, 600, 500).rotateInSpace(spaceSize, rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(360, 360 - 360, 360 + 360, 360 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Rect(100, 200, 600, 500),
                actual = Rect(100, 200, 600, 500).rotateInSpace(spaceSize, rotation),
                message = "rotation: $rotation",
            )
        }

        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, -1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 89)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 91)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 179)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 191)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 269)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 271)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 359)
        }
        assertFailsWith(IllegalArgumentException::class) {
            Rect(100, 200, 600, 500).rotateInSpace(spaceSize, 361)
        }
    }

    @Test
    fun testSize() {
        assertEquals(
            expected = Size(500, 200),
            actual = Rect(100, 200, 600, 400).size
        )
    }
}