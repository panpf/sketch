package com.github.panpf.sketch.test.decode

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.HeifAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ScaledAnimatedImageDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeifAnimatedDrawableDecoderTest {

    @Test
    fun testFactory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = HeifAnimatedDrawableDecoder.Factory()

        Assert.assertEquals("HeifAnimatedDrawableDecoder", factory.toString())

        // normal
        DisplayRequest(context, newAssetUri("sample_anim.heif")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.heif"), "image/heif")
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        DisplayRequest(context, newAssetUri("sample_anim.heif")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.heif"), null)
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // disallowAnimatedImage true
        DisplayRequest(context, newAssetUri("sample_anim.heif")) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.heif"), null)
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // data error
        DisplayRequest(context, newAssetUri("sample.png")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.png"), null)
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.gif"), "image/heif")
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        DisplayRequest(context, newAssetUri("sample_anim.heif")).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, "sample_anim.heif"),
                "image/jpeg",
            )
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecodeDrawable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = HeifAnimatedDrawableDecoder.Factory()

        val request = DisplayRequest(context, newAssetUri("sample_anim.heif"))
        val fetchResult = sketch.components.newFetcher(request).let { runBlocking { it.fetch() } }
        factory.create(sketch, request, RequestContext(request), fetchResult)!!
            .let { runBlocking { it.decode() } }.apply {
                Assert.assertEquals(ImageInfo(256, 144, "image/heif", 0), this.imageInfo)
                Assert.assertEquals(Size(256, 144), this.drawable.intrinsicSize)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val animatedImageDrawable =
                    ((this.drawable as SketchAnimatableDrawable).wrappedDrawable as ScaledAnimatedImageDrawable).child
                Assert.assertEquals(-1, animatedImageDrawable.repeatCount)
            }

        val request1 = DisplayRequest(context, newAssetUri("sample_anim.heif")) {
            repeatCount(3)
            resize(100, 100)
        }
        val fetchResult1 = sketch.components.newFetcher(request1).let { runBlocking { it.fetch() } }
        factory.create(sketch, request1, RequestContext(request1), fetchResult1)!!
            .let { runBlocking { it.decode() } }.apply {
                Assert.assertEquals(ImageInfo(256, 144, "image/heif", 0), this.imageInfo)
                Assert.assertEquals(Size(128, 72), this.drawable.intrinsicSize)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertEquals(listOf(createInSampledTransformed(2)), this.transformedList)
                val animatedImageDrawable =
                    ((this.drawable as SketchAnimatableDrawable).wrappedDrawable as ScaledAnimatedImageDrawable).child
                Assert.assertEquals(3, animatedImageDrawable.repeatCount)
            }
    }
}