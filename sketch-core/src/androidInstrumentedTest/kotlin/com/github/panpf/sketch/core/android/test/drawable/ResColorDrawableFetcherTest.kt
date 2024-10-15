package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.ResColorDrawableFetcher
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ResColorDrawableFetcherTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "ResColorDrawable(${com.github.panpf.sketch.test.R.color.colorPrimary})",
            actual = ResColorDrawableFetcher(com.github.panpf.sketch.test.R.color.colorPrimary).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        ResColorDrawableFetcher(com.github.panpf.sketch.test.R.color.colorPrimary).apply {
            assertEquals(
                expected = context.resources.getColor(com.github.panpf.sketch.test.R.color.colorPrimary),
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 =
            ResColorDrawableFetcher(com.github.panpf.sketch.test.R.color.colorPrimary)
        val element11 =
            ResColorDrawableFetcher(com.github.panpf.sketch.test.R.color.colorPrimary)
        val element2 =
            ResColorDrawableFetcher(com.github.panpf.sketch.test.R.color.colorAccent)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ResColorDrawableFetcher(resId=${com.github.panpf.sketch.test.R.color.colorPrimary})",
            actual = ResColorDrawableFetcher(com.github.panpf.sketch.test.R.color.colorPrimary).toString()
        )
    }
}