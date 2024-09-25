package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.request.FixedLifecycleResolver
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.test.utils.TestLifecycle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class LifecycleResolverTest {

    @Test
    fun testLifecycleResolver() {
        val lifecycle = TestLifecycle()
        assertEquals(
            expected = FixedLifecycleResolver(lifecycle),
            actual = LifecycleResolver(lifecycle)
        )
    }

    @Test
    fun testFixedLifecycleResolver() = runTest {
        val lifecycle = TestLifecycle()

        assertSame(
            expected = lifecycle,
            actual = FixedLifecycleResolver(lifecycle).lifecycle()
        )

        val element1 = FixedLifecycleResolver(lifecycle)
        val element11 = element1.copy()
        val element2 = element1.copy(lifecycle = TestLifecycle())

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())

        assertEquals(
            expected = "FixedLifecycleResolver($lifecycle)",
            actual = FixedLifecycleResolver(lifecycle).toString()
        )
    }
}