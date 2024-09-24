package com.github.panpf.sketch.core.common.test.transform

import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.transform.TransformResult
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TransformResultTest {

    @Test
    fun testEqualsAndHashCode() {
        val element1 = TransformResult(
            image = FakeImage(Size(100, 100)),
            transformed = createCircleCropTransformed(CENTER_CROP)
        )
        val element11 = element1.copy()
        val element2 = element1.copy(image = FakeImage(Size(200, 200)))
        val element3 = element1.copy(transformed = createInSampledTransformed(4))

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "TransformResult(image=FakeImage(size=100x100), transformed=CircleCropTransformed(CENTER_CROP))",
            actual = TransformResult(
                image = FakeImage(Size(100, 100)),
                transformed = createCircleCropTransformed(CENTER_CROP)
            ).toString()
        )
    }
}