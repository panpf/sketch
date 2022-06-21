package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.resize.DefaultSizeResolver
import com.github.panpf.sketch.resize.DisplaySizeResolver
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultSizeResolverTest {

    @Test
    fun test() {
        val context = getTestContext()
        val displaySize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
        val displaySizeResolver = DisplaySizeResolver(context)

        runBlocking {
            DefaultSizeResolver(displaySizeResolver).size()
        }.apply {
            Assert.assertEquals(displaySize, this)
        }
    }

    @Test
    fun testEquals() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val resolver1 = DefaultSizeResolver(DisplaySizeResolver(context))
        val resolver11 = DefaultSizeResolver(DisplaySizeResolver(context))
        val resolver2 = DefaultSizeResolver(DisplaySizeResolver(targetContext))

        Assert.assertEquals(resolver1, resolver1)
        Assert.assertEquals(resolver1, resolver11)
        Assert.assertNotEquals(resolver1, resolver2)
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val displaySizeResolver = DisplaySizeResolver(context)
        val defaultSizeResolver = DefaultSizeResolver(displaySizeResolver)
        defaultSizeResolver.apply {
            Assert.assertEquals(
                "DefaultSizeResolver(wrapped=DisplaySizeResolver(context=${context}))",
                toString()
            )
        }
    }
}