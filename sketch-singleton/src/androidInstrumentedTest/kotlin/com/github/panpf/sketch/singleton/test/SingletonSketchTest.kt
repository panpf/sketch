package com.github.panpf.sketch.singleton.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.sketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingletonSketchTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        Assert.assertSame(context.sketch, context.sketch)
        Assert.assertSame(targetContext.sketch, targetContext.sketch)
        Assert.assertNotSame(context, targetContext)
        Assert.assertSame(context.sketch, targetContext.sketch)
    }

    // TODO refactor
    @Test
    fun testSetAndReset() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch1 = context.sketch
        val sketch2 = context.sketch
        Assert.assertSame(sketch1, sketch2)

        SingletonSketch.reset()
        val sketch3 = context.sketch
        Assert.assertNotSame(sketch1, sketch3)

        val sketch4 = Builder(context).build()
        SingletonSketch.setUnsafe(sketch4)
        val sketch5 = context.sketch
        Assert.assertSame(sketch4, sketch5)

        val sketch6 = Builder(context).build()
        SingletonSketch.setUnsafe { sketch6 }
        val sketch7 = context.sketch
        Assert.assertSame(sketch6, sketch7)

        SingletonSketch.reset()
        val sketch8 = context.sketch
        Assert.assertNotSame(sketch8, sketch1)
        Assert.assertNotSame(sketch8, sketch2)
        Assert.assertNotSame(sketch8, sketch3)
        Assert.assertNotSame(sketch8, sketch4)
        Assert.assertNotSame(sketch8, sketch5)
        Assert.assertNotSame(sketch8, sketch6)
        Assert.assertNotSame(sketch8, sketch7)
    }
}