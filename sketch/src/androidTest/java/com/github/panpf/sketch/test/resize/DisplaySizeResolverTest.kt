package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.resize.DisplaySizeResolver
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplaySizeResolverTest {

    @Test
    fun testSize() {
        val context = getTestContext()
        DisplaySizeResolver(context).apply {
            Assert.assertEquals(
                Size(
                    context.resources.displayMetrics!!.widthPixels,
                    context.resources.displayMetrics.heightPixels
                ),
                runBlocking { size() }
            )
        }
    }

    @Test
    fun testEquals() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val resolver1 = DisplaySizeResolver(context)
        val resolver11 = DisplaySizeResolver(context)
        val resolver2 = DisplaySizeResolver(targetContext)

        Assert.assertEquals(resolver1, resolver1)
        Assert.assertEquals(resolver1, resolver11)
        Assert.assertNotEquals(resolver1, resolver2)
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        DisplaySizeResolver(context).apply {
            Assert.assertEquals("DisplaySizeResolver(context=${context})", toString())
        }
    }
}