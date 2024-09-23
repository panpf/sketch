package com.github.panpf.sketch.singleton.common.test

import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SingletonSketchTest {

    @Test
    fun test() {
        val context = getTestContext()

        val mySketch1 = Sketch.Builder(context).build()
        val mySketch2 = Sketch.Builder(context).build()
        val mySketch3 = Sketch.Builder(context).build()
        assertFalse(mySketch1.isShutdown)
        assertFalse(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)

        assertNotSame(
            illegal = mySketch1,
            actual = SingletonSketch.get(context)
        )
        assertSame(
            expected = SingletonSketch.get(context),
            actual = SingletonSketch.get(context)
        )
        val sketch0 = SingletonSketch.get(context)
        assertFalse(sketch0.isShutdown)

        assertFailsWith(IllegalStateException::class) {
            SingletonSketch.setSafe { mySketch1 }
        }

        SingletonSketch.reset()
        assertTrue(sketch0.isShutdown)
        assertFalse(mySketch1.isShutdown)
        assertFalse(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)

        SingletonSketch.setSafe { mySketch1 }
        SingletonSketch.setSafe { mySketch1 }
        assertSame(
            expected = mySketch1,
            actual = SingletonSketch.get(context)
        )
        assertTrue(sketch0.isShutdown)
        assertFalse(mySketch1.isShutdown)
        assertFalse(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)

        SingletonSketch.setSafe { mySketch1 }
        assertSame(
            expected = mySketch1,
            actual = SingletonSketch.get(context)
        )
        assertTrue(sketch0.isShutdown)
        assertFalse(mySketch1.isShutdown)
        assertFalse(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)

        SingletonSketch.setUnsafe(mySketch1)
        assertSame(
            expected = mySketch1,
            actual = SingletonSketch.get(context)
        )
        assertTrue(sketch0.isShutdown)
        assertFalse(mySketch1.isShutdown)
        assertFalse(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)

        SingletonSketch.setUnsafe(mySketch2)
        assertSame(
            expected = mySketch2,
            actual = SingletonSketch.get(context)
        )
        assertTrue(sketch0.isShutdown)
        assertTrue(mySketch1.isShutdown)
        assertFalse(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)

        SingletonSketch.setUnsafe { mySketch3 }
        assertSame(
            expected = mySketch3,
            actual = SingletonSketch.get(context)
        )
        assertTrue(sketch0.isShutdown)
        assertTrue(mySketch1.isShutdown)
        assertTrue(mySketch2.isShutdown)
        assertFalse(mySketch3.isShutdown)
    }
}