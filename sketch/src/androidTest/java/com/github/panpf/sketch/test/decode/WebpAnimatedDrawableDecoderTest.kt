package com.github.panpf.sketch.test.decode

import android.os.Build
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.WebpAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ScaledAnimatedImageDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebpAnimatedDrawableDecoderTest {

    @Test
    fun testFactory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = WebpAnimatedDrawableDecoder.Factory()

        Assert.assertEquals("WebpAnimatedDrawableDecoder", factory.toString())

        // normal
        DisplayRequest(context, newAssetUri("sample_anim.webp")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.webp"), "image/webp")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        DisplayRequest(context, newAssetUri("sample_anim.webp")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.webp"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // disallowAnimatedImage true
        DisplayRequest(context, newAssetUri("sample_anim.webp")) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.webp"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // data error
        DisplayRequest(context, newAssetUri("sample.png")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.png"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.gif"), "image/webp")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        DisplayRequest(context, newAssetUri("sample_anim.webp")).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, "sample_anim.webp"),
                "image/jpeg",
            )
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecodeDrawable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = WebpAnimatedDrawableDecoder.Factory()

        val request = DisplayRequest(context, newAssetUri("sample_anim.webp"))
        val fetchResult = sketch.components.newFetcher(request).let { runBlocking { it.fetch() } }
        factory.create(sketch, request, RequestContext(), fetchResult)!!
            .let { runBlocking { it.decode() } }.apply {
                Assert.assertEquals(ImageInfo(480, 270, "image/webp"), this.imageInfo)
                Assert.assertEquals(480, this.drawable.intrinsicWidth)
                Assert.assertEquals(270, this.drawable.intrinsicHeight)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, this.exifOrientation)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val animatedImageDrawable =
                    ((this.drawable as SketchAnimatableDrawable).wrappedDrawable as ScaledAnimatedImageDrawable).child
                Assert.assertEquals(-1, animatedImageDrawable.repeatCount)
            }

        val request1 = DisplayRequest(context, newAssetUri("sample_anim.webp")) {
            repeatCount(3)
            resize(300, 300)
        }
        val fetchResult1 = sketch.components.newFetcher(request1).let { runBlocking { it.fetch() } }
        factory.create(sketch, request1, RequestContext(), fetchResult1)!!
            .let { runBlocking { it.decode() } }.apply {
                Assert.assertEquals(ImageInfo(480, 270, "image/webp"), this.imageInfo)
                Assert.assertEquals(240, this.drawable.intrinsicWidth)
                Assert.assertEquals(135, this.drawable.intrinsicHeight)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, this.exifOrientation)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertEquals(listOf(InSampledTransformed(2)), this.transformedList)
                val animatedImageDrawable =
                    ((this.drawable as SketchAnimatableDrawable).wrappedDrawable as ScaledAnimatedImageDrawable).child
                Assert.assertEquals(3, animatedImageDrawable.repeatCount)
            }
    }
}