package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthHolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class DepthTest {

    @Test
    fun testDepth() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "NETWORK, LOCAL, MEMORY",
            actual = Depth.values().joinToString()
        )
    }

    @Test
    fun testDepthHolder() {
        DepthHolder(depth = Depth.LOCAL)
        DepthHolder(depth = Depth.LOCAL, from = "list")
        DepthHolder(Depth.LOCAL, "detail")

        assertEquals(expected = DepthHolder(depth = Depth.NETWORK), actual = DepthHolder.Default)
        assertSame(expected = DepthHolder.Default, actual = DepthHolder.Default)

        assertEquals(
            expected = "DepthHolder(LOCAL)",
            actual = DepthHolder(depth = Depth.LOCAL).key
        )
        assertEquals(
            expected = "DepthHolder(depth=LOCAL,from='list')",
            actual = DepthHolder(depth = Depth.LOCAL, from = "list").key
        )

        assertEquals(
            expected = "DepthHolder(depth=LOCAL, from='null')",
            actual = DepthHolder(depth = Depth.LOCAL).toString()
        )
        assertEquals(
            expected = "DepthHolder(depth=LOCAL, from='list')",
            actual = DepthHolder(depth = Depth.LOCAL, from = "list").toString()
        )

        val element1 = DepthHolder(depth = Depth.LOCAL)
        val element11 = element1.copy()
        val element2 = element1.copy(depth = Depth.MEMORY)
        val element3 = element1.copy(from = "detail")

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
}