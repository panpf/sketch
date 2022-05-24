package com.github.panpf.sketch.video.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.VideoFrameBitmapDecoder
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileDescriptor
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class VideoFrameBitmapDecoderTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        // normal
        val request = LoadRequest(context, newAssetUri("sample.mp4"))
        val fetchResult = FetchResult(AssetDataSource(sketch, request, "sample.mp4"), null)
        Assert.assertNull(
            VideoFrameBitmapDecoder.Factory().create(sketch, request, RequestContext(), fetchResult)
        )

        val request0 = LoadRequest(context, newAssetUri("sample.mp4"))
        val fetchResult0 = FetchResult(AssetDataSource(sketch, request0, "sample.mp4"), "video/mp4")
        Assert.assertNotNull(
            VideoFrameBitmapDecoder.Factory()
                .create(sketch, request0, RequestContext(), fetchResult0)
        )

        // not mp4
        val request1 = LoadRequest(context, newAssetUri("sample.png"))
        val fetchResult1 = FetchResult(AssetDataSource(sketch, request1, "sample.png"), "image/png")
        Assert.assertNull(
            VideoFrameBitmapDecoder.Factory()
                .create(sketch, request1, RequestContext(), fetchResult1)
        )

        // external mimeType it's right
        val fetchResult2 = FetchResult(ErrorDataSource(sketch, request0, LOCAL), "video/mp4")
        Assert.assertNotNull(
            VideoFrameBitmapDecoder.Factory()
                .create(sketch, request0, RequestContext(), fetchResult2)
        )
    }

    @Test
    fun testDecode() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        val factory = VideoFrameBitmapDecoder.Factory()

        Assert.assertEquals("VideoFrameBitmapDecoder", factory.toString())

        LoadRequest(context, newAssetUri("sample.mp4")).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Assert.assertEquals("Bitmap(500x250,ARGB_8888)", bitmap.toShortInfoString())
            } else {
                Assert.assertEquals("Bitmap(500x250,RGB_565)", bitmap.toShortInfoString())
            }
            Assert.assertEquals("ImageInfo(500x250,'video/mp4')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                bitmapConfig(RGB_565)
            }.run {
                val fetcher = sketch.components.newFetcher(this)
                val fetchResult = runBlocking { fetcher.fetch() }
                runBlocking {
                    factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
                }
            }.apply {
                Assert.assertEquals("Bitmap(500x250,RGB_565)", bitmap.toShortInfoString())
                Assert.assertEquals("ImageInfo(500x250,'video/mp4')", imageInfo.toShortString())
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertNull(transformedList)
            }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                Assert.assertEquals("Bitmap(250x125,ARGB_8888)", bitmap.toShortInfoString())
                Assert.assertEquals(listOf(InSampledTransformed(2)), transformedList)
            } else {
                Assert.assertEquals("Bitmap(500x250,RGB_565)", bitmap.toShortInfoString())
                Assert.assertNull(transformedList)
            }
            Assert.assertEquals("ImageInfo(500x250,'video/mp4')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
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

    private class ErrorDataSource(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val dataFrom: DataFrom
    ) : DataSource {
        override fun length(): Long = throw UnsupportedOperationException("Unsupported length()")

        override fun newFileDescriptor(): FileDescriptor =
            throw UnsupportedOperationException("Unsupported newFileDescriptor()")

        override fun newInputStream(): InputStream =
            throw UnsupportedOperationException("Unsupported newInputStream()")
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}