package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.samplingByTarget
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultDrawableDecoderTest {

    @Test
    fun testDecode() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageSize = Size(1291, 1936)
        val displaySize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            resize(displaySize, LESS_PIXELS)
        }

        request.let {
            runBlocking {
                val fetchResult = sketch.components.newFetcher(it).fetch()
                DefaultDrawableDecoder.Factory()
                    .create(sketch, it, RequestContext(it), fetchResult)
                    .decode()
            }
        }.apply {
            Assert.assertEquals(imageSize, imageInfo.size)
            Assert.assertEquals(samplingByTarget(imageSize, displaySize), drawable.intrinsicSize)
            Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = DefaultDrawableDecoder.Factory()
        val element11 = DefaultDrawableDecoder.Factory()
        val element2 = DefaultDrawableDecoder.Factory()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element2)
        Assert.assertEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element2.hashCode())
        Assert.assertEquals(element2.hashCode(), element11.hashCode())
    }
}