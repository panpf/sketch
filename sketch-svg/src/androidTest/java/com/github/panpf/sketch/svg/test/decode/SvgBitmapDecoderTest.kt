package com.github.panpf.sketch.svg.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.SvgBitmapDecoder
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
class SvgBitmapDecoderTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = SvgBitmapDecoder.Factory(false)

        Assert.assertEquals("SvgBitmapDecoder", factory.toString())

        // normal
        LoadRequest(context, newAssetUri("sample.svg")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.svg"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // data error
        LoadRequest(context, newAssetUri("sample.png")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.png"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        LoadRequest(context, newAssetUri("sample.svg")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.svg"), "image/svg")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }
    }

    @Test
    fun testDecode() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        val factory = SvgBitmapDecoder.Factory()

        LoadRequest(context, newAssetUri("sample.svg")).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals("Bitmap(841x595,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(841x595,'image/svg+xml')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newAssetUri("sample.svg")) {
            bitmapConfig(RGB_565)
        }.run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals("Bitmap(841x595,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(841x595,'image/svg+xml')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newAssetUri("sample.svg")) {
            resize(600, 600, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals("Bitmap(421x298,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(listOf(InSampledTransformed(2)), transformedList)
            Assert.assertEquals("ImageInfo(841x595,'image/svg+xml')", imageInfo.toShortString())
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

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}