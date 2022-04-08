package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.R
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
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch

        val factory = XmlDrawableBitmapDecoder.Factory()

        Assert.assertEquals("XmlDrawableBitmapDecoder", factory.toString())

        LoadRequest(context, context.newResourceUri(R.drawable.test)).apply {
            val fetcher = sketch.componentRegistry.newFetcher(sketch, this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            Assert.assertNotNull(factory.create(sketch, this, RequestExtras(), fetchResult))
        }

        LoadRequest(context, context.newResourceUri(R.drawable.test_error)).apply {
            val fetcher = sketch.componentRegistry.newFetcher(sketch, this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            Assert.assertNotNull(factory.create(sketch, this, RequestExtras(), fetchResult))
        }

        LoadRequest(context, context.newResourceUri(R.drawable.ic_launcher)).apply {
            val fetcher = sketch.componentRegistry.newFetcher(sketch, this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            Assert.assertNull(factory.create(sketch, this, RequestExtras(), fetchResult))
        }
    }

    @Test
    fun testExecuteDecode() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch

        val factory = XmlDrawableBitmapDecoder.Factory()

        Assert.assertEquals("XmlDrawableBitmapDecoder", factory.toString())

        LoadRequest(context, context.newResourceUri(R.drawable.test)).run {
            val fetcher = sketch.componentRegistry.newFetcher(sketch, this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            runBlocking {
                factory.create(sketch, this@run, RequestExtras(), fetchResult)!!.decode()
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
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, context.newResourceUri(R.drawable.test_error)).run {
            val fetcher = sketch.componentRegistry.newFetcher(sketch, this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            assertThrow(BitmapDecodeException::class) {
                runBlocking {
                    factory.create(sketch, this@run, RequestExtras(), fetchResult)!!.decode()
                }
            }
        }
    }
}