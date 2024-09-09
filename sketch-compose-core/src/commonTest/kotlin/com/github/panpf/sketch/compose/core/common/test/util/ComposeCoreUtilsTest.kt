package com.github.panpf.sketch.compose.core.common.test.util

import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.toHexString
import okio.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ComposeCoreUtilsTest {

    @Test
    fun testContentScaleToScale() {
        // TODO test
    }

    @Test
    fun testContentScaleName() {
        // TODO test
    }

    @Test
    fun testContentScaleFitScale() {
        // TODO test
    }

    @Test
    fun testSizeToIntSizeOrNull() {
        // TODO test
    }

    @Test
    fun testIntSizeIsEmpty() {
        // TODO test
    }

    @Test
    fun testIntSizeToSketchSize() {
        // TODO test
    }

    @Test
    fun testSketchSizeToIntSize() {
        // TODO test
    }

    @Test
    fun testSketchSizeToSize() {
        // TODO test
    }

    @Test
    fun testConstraintsToIntSizeOrNull() {
        // TODO test
    }

    @Test
    fun testPainterFindLeafChildPainter() {
        // TODO test
    }

    @Test
    fun testAnyAsOrNull() {
        assertNotNull(IOException().asOrNull<Exception>())
        assertNull((null as Exception?).asOrNull<Exception>())
        assertFailsWith(ClassCastException::class) {
            Throwable() as Exception
        }
        assertNull(Throwable().asOrNull<Exception>())
    }

    @Test
    fun testToHexString() {
        val any = Any()
        assertEquals(
            expected = any.hashCode().toString(16),
            actual = any.toHexString()
        )
    }
}