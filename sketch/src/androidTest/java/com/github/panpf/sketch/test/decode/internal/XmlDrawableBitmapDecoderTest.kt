package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.tools4a.dimen.ktx.dp2px
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class XmlDrawableBitmapDecoderTest {

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val factory = XmlDrawableBitmapDecoder.Factory()

        Assert.assertEquals("XmlDrawableBitmapDecoder", factory.toString())

        LoadRequest(context, newResourceUri(R.drawable.test)).let {
            val fetcher = sketch.components.newFetcher(it)
            val fetchResult = runBlocking { fetcher.fetch() }
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        LoadRequest(context, newResourceUri(R.drawable.test_error)).let {
            val fetcher = sketch.components.newFetcher(it)
            val fetchResult = runBlocking { fetcher.fetch() }
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        LoadRequest(context, newResourceUri(R.drawable.ic_launcher)).let {
            val fetcher = sketch.components.newFetcher(it)
            val fetchResult = runBlocking { fetcher.fetch() }
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecode() {
        val (context, sketch) = getTestContextAndNewSketch()
        val factory = XmlDrawableBitmapDecoder.Factory()

        LoadRequest(context, newResourceUri(R.drawable.test)).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals(
                "Bitmap(${50.dp2px}x${40.dp2px},ARGB_8888)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${50.dp2px}x${40.dp2px},'image/android-xml')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, imageExifOrientation)
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newResourceUri(R.drawable.test_error)).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            assertThrow(BitmapDecodeException::class) {
                runBlocking {
                    factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!.decode()
                }
            }
        }
    }
}