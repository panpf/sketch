package com.github.panpf.sketch.svg.android.test.decode

import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.util.Size

class SvgDecoderTest {

//    @Test
//    fun testDecode() = runTest{
//        val (context, sketch) = getTestContextAndSketch()
//
//        val factory = SvgDecoder.Factory()
//
//        ImageRequest(context, MyImages.svg.uri).run {
//            val fetchResult = sketch.components.newFetcherOrThrow(this).fetch().getOrThrow()
//            val svgDecoder = factory.create(this@run.toRequestContext(sketch), fetchResult)!!
//            svgDecoder.decode().getOrThrow()
//        }.apply {
//            assertEquals(expected = Size(width = 842, height = 595), actual = image.size)
//            assertEquals(
//                expected = "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
//                actual = imageInfo.toShortString()
//            )
//            assertEquals(LOCAL, dataFrom)
//            assertNull(transformedList)
//        }
//
//        ImageRequest(context, MyImages.svg.uri) {
//            bitmapConfig(RGB_565)
//        }.run {
//            val fetcher = sketch.components.newFetcherOrThrow(this)
//            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
//            runBlocking {
//                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
//            }.getOrThrow()
//        }.apply {
//            assertEquals(
//                "Bitmap(842x595,RGB_565)",
//                image.getBitmapOrThrow().toShortInfoString()
//            )
//            assertEquals(
//                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
//                imageInfo.toShortString()
//            )
//            assertEquals(LOCAL, dataFrom)
//            assertNull(transformedList)
//        }
//    }
}