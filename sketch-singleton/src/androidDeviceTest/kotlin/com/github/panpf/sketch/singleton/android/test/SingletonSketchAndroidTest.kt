package com.github.panpf.sketch.singleton.android.test

import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.applicationSketchFactory
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class SingletonSketchAndroidTest {

    @Test
    fun testApplicationSketchFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        assertNotNull(context.applicationSketchFactory())
        assertEquals(
            expected = TestApplication::class,
            actual = context.applicationSketchFactory()!!::class
        )
    }

    @Test
    fun testContextSketch() {
        val context = getTestContext()
        assertSame(expected = SingletonSketch.get(context), actual = context.sketch)
        assertSame(expected = context.sketch, actual = context.sketch)
    }

    @Test
    fun testViewSketch() {
        val context = getTestContext()
        val view = View(context)
        assertSame(expected = SingletonSketch.get(context), actual = view.sketch)
    }
}