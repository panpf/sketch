package com.github.panpf.sketch.gif.movie.test.decode

import android.os.Build
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.GifMovieDrawableDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.transform.PixelOpacity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifMovieDrawableDecoderTest {

    @Test
    fun testFactory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifMovieDrawableDecoder.Factory()

        Assert.assertEquals("GifMovieDrawableDecoder", factory.toString())

        // normal
        DisplayRequest(context, newAssetUri("sample_anim.gif")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.gif"), "image/gif")
            Assert.assertNotNull(factory.create(sketch, it, RequestContext(it), fetchResult))
        }.apply {
            Assert.assertNotNull(this)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.gif"), null)
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // disallowAnimatedImage true
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample_anim.gif"), null)
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

        // mimeType error
        DisplayRequest(context, newAssetUri("sample_anim.gif")).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, "sample_anim.gif"),
                "image/jpeg",
            )
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecodeDrawable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifMovieDrawableDecoder.Factory()

        val request = DisplayRequest(context, newAssetUri("sample_anim.gif"))
        val fetchResult = sketch.components.newFetcher(request).let { runBlocking { it.fetch() } }
        factory.create(sketch, request, RequestContext(request), fetchResult)!!
            .let { runBlocking { it.decode() } }.apply {
                Assert.assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
                Assert.assertEquals(480, this.drawable.intrinsicWidth)
                Assert.assertEquals(480, this.drawable.intrinsicHeight)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, this.imageExifOrientation)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val movieDrawable =
                    (this.drawable as SketchAnimatableDrawable).wrappedDrawable as MovieDrawable
                Assert.assertEquals(-1, movieDrawable.getRepeatCount())
                Assert.assertNull(movieDrawable.getAnimatedTransformation())
            }

        val request1 = DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            repeatCount(3)
            animatedTransformation { PixelOpacity.TRANSLUCENT }
            resize(300, 300)
        }
        val fetchResult1 = sketch.components.newFetcher(request1).let { runBlocking { it.fetch() } }
        factory.create(sketch, request1, RequestContext(request1), fetchResult1)!!
            .let { runBlocking { it.decode() } }.apply {
                Assert.assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
                Assert.assertEquals(480, this.drawable.intrinsicWidth)
                Assert.assertEquals(480, this.drawable.intrinsicHeight)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, this.imageExifOrientation)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val movieDrawable =
                    (this.drawable as SketchAnimatableDrawable).wrappedDrawable as MovieDrawable
                Assert.assertEquals(3, movieDrawable.getRepeatCount())
                Assert.assertNotNull(movieDrawable.getAnimatedTransformation())
            }
    }
}