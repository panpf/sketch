package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.samplingByTarget
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.ratio
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
            Assert.assertEquals(imageSize.samplingByTarget(displaySize), drawable.intrinsicSize)
            Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }
}