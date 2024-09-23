package com.github.panpf.sketch.singleton.nonandroid.test

import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.applicationSketchFactory
import com.github.panpf.sketch.get
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class SingletonSketchNonAndroidTest {

    @Test
    fun testApplicationSketchFactory() {
        val context = getTestContext()
        assertNull(actual = context.applicationSketchFactory())
    }

    @Test
    fun testContextSketch() {
        val context = getTestContext()
        assertSame(expected = SingletonSketch.get(context), actual = context.sketch)
        assertSame(expected = context.sketch, actual = context.sketch)
    }

    @Test
    fun testGet() {
        val context = getTestContext()
        assertSame(expected = SingletonSketch.get(context), actual = SingletonSketch.get())
    }
}