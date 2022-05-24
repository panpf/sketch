package com.github.panpf.sketch.video.ffmpeg.test.decode

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.FFmpegVideoFrameBitmapDecoder
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FFmpegVideoFrameBitmapDecoderTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = FFmpegVideoFrameBitmapDecoder.Factory()

        Assert.assertEquals("FFmpegVideoFrameBitmapDecoder", factory.toString())

        // normal
        LoadRequest(context, newAssetUri("sample.mp4")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.mp4"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        LoadRequest(context, newAssetUri("sample.mp4")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.mp4"), "video/mp4")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // data error
        LoadRequest(context, newAssetUri("sample.png")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.png"), "video/mp4")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // mimeType error
        LoadRequest(context, newAssetUri("sample.mp4")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.mp4"), "image/png")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecode() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = FFmpegVideoFrameBitmapDecoder.Factory()

        LoadRequest(context, newAssetUri("sample.mp4")).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals("Bitmap(500x250,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(500x250,'video/mp4')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newAssetUri("sample.mp4")) {
            resize(300, 300, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals("Bitmap(250x125,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(500x250,'video/mp4')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(InSampledTransformed(2)), transformedList)
        }

        LoadRequest(context, newAssetUri("sample.png")).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(NullPointerException::class) {
                runBlocking {
                    factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
                }
            }
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}