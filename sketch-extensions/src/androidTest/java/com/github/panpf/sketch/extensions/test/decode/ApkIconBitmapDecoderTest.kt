package com.github.panpf.sketch.extensions.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ApkIconBitmapDecoder
import com.github.panpf.sketch.decode.internal.samplingByTarget
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApkIconBitmapDecoderTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = ApkIconBitmapDecoder.Factory()

        Assert.assertEquals("ApkIconBitmapDecoder", factory.toString())

        // mimeType normal
        LoadRequest(context, newAssetUri("sample.svg")).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, "sample.svg"),
                "application/vnd.android.package-archive"
            )
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // mimeType null
        LoadRequest(context, newAssetUri("sample.png")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.png"), null)
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        LoadRequest(context, newAssetUri("sample.png")).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, "sample.png"), "image/svg+xml")
            factory.create(sketch, it, RequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecode() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = ApkIconBitmapDecoder.Factory()
        val apkFilePath = context.applicationInfo.publicSourceDir
        val iconDrawable = context.applicationInfo.loadIcon(context.packageManager)

        LoadRequest(context, apkFilePath).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals(
                "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},ARGB_8888)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'application/vnd.android.package-archive')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, imageExifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, apkFilePath) {
            bitmapConfig(RGB_565)
        }.run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals(
                "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},RGB_565)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'application/vnd.android.package-archive')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, imageExifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, apkFilePath) {
            resize(100, 100, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking { fetcher.fetch() }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(), fetchResult)!!.decode()
            }
        }.apply {
            val bitmapSize = Size(
                iconDrawable.intrinsicWidth,
                iconDrawable.intrinsicHeight
            ).samplingByTarget(100, 100)
            Assert.assertEquals(
                "Bitmap(${bitmapSize.height}x${bitmapSize.height},ARGB_8888)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'application/vnd.android.package-archive')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, imageExifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                listOf(ResizeTransformed(Resize(100, 100, LESS_PIXELS, CENTER_CROP))),
                transformedList
            )
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