package com.github.panpf.sketch.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.sketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchSingletonTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        Assert.assertSame(context.sketch, context.sketch)
        Assert.assertSame(targetContext.sketch, targetContext.sketch)
        Assert.assertNotSame(context, targetContext)
        Assert.assertSame(context.sketch, targetContext.sketch)
    }
}