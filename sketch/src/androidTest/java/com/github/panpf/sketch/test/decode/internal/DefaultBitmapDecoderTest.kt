package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.SRGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultBitmapDecoderTest {

    @Test
    fun testDecode() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        // default
        val request = LoadRequest(newAssetUri("sample.jpeg"))
        DefaultBitmapDecoder(sketch, request, AssetDataSource(sketch, request, "sample.jpeg"))
            .use { runBlocking { it.decode() } }
            .apply {
                Assert.assertEquals(1291, bitmap.width)
                Assert.assertEquals(1936, bitmap.height)
                Assert.assertEquals(ARGB_8888, bitmap.config)
                Assert.assertEquals(1291, imageInfo.width)
                Assert.assertEquals(1936, imageInfo.height)
                Assert.assertEquals("image/jpeg", imageInfo.mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
                Assert.assertEquals(DataFrom.LOCAL, dataFrom)
                Assert.assertEquals("null", transformedList.toString())
            }

        // maxSize
        val request1 = LoadRequest(newAssetUri("sample.jpeg")) {
            maxSize(800, 800)
        }
        DefaultBitmapDecoder(sketch, request1, AssetDataSource(sketch, request1, "sample.jpeg"))
            .use { runBlocking { it.decode() } }
            .apply {
                Assert.assertEquals(646, bitmap.width)
                Assert.assertEquals(968, bitmap.height)
                Assert.assertEquals(ARGB_8888, bitmap.config)
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
                }
                Assert.assertEquals(1291, imageInfo.width)
                Assert.assertEquals(1936, imageInfo.height)
                Assert.assertEquals("image/jpeg", imageInfo.mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
                Assert.assertEquals(DataFrom.LOCAL, dataFrom)
                Assert.assertEquals("[InSampledTransformed(2)]", transformedList.toString())
            }

        // bitmapConfig
        val request2 = LoadRequest(newAssetUri("sample.jpeg")) {
            bitmapConfig(RGB_565)
        }
        DefaultBitmapDecoder(sketch, request2, AssetDataSource(sketch, request2, "sample.jpeg"))
            .use { runBlocking { it.decode() } }
            .apply {
                Assert.assertEquals(1291, bitmap.width)
                Assert.assertEquals(1936, bitmap.height)
                Assert.assertEquals(RGB_565, bitmap.config)
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
                }
                Assert.assertEquals(1291, imageInfo.width)
                Assert.assertEquals(1936, imageInfo.height)
                Assert.assertEquals("image/jpeg", imageInfo.mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
                Assert.assertEquals(DataFrom.LOCAL, dataFrom)
                Assert.assertEquals("null", transformedList.toString())
            }

        // colorSpace
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val request3 = LoadRequest(newAssetUri("sample.jpeg")) {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
            DefaultBitmapDecoder(sketch, request3, AssetDataSource(sketch, request3, "sample.jpeg"))
                .use { runBlocking { it.decode() } }
                .apply {
                    Assert.assertEquals(1291, bitmap.width)
                    Assert.assertEquals(1936, bitmap.height)
                    Assert.assertEquals(ARGB_8888, bitmap.config)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
                    }
                    Assert.assertEquals(1291, imageInfo.width)
                    Assert.assertEquals(1936, imageInfo.height)
                    Assert.assertEquals("image/jpeg", imageInfo.mimeType)
                    Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
                    Assert.assertEquals(DataFrom.LOCAL, dataFrom)
                    Assert.assertEquals("null", transformedList.toString())
                }
        }

        // resize region
        val request4 = LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 500)
        }
        DefaultBitmapDecoder(sketch, request4, AssetDataSource(sketch, request4, "sample.jpeg"))
            .use { runBlocking { it.decode() } }
            .apply {
                Assert.assertEquals(322, bitmap.width)
                Assert.assertEquals(322, bitmap.height)
                Assert.assertEquals(ARGB_8888, bitmap.config)
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
                }
                Assert.assertEquals(1291, imageInfo.width)
                Assert.assertEquals(1936, imageInfo.height)
                Assert.assertEquals("image/jpeg", imageInfo.mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
                Assert.assertEquals(DataFrom.LOCAL, dataFrom)
                Assert.assertEquals(
                    "[ResizeTransformed(Resize(500x500,All,CENTER_CROP,KEEP_ASPECT_RATIO)), InSampledTransformed(4)]",
                    transformedList.toString()
                )
            }

        // resize region unusable
        val request5 = LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 500)
        }
        DefaultBitmapDecoder(sketch, request5, AssetDataSource(sketch, request5, "sample.bmp"))
            .use { runBlocking { it.decode() } }
            .apply {
                Assert.assertEquals(350, bitmap.width)
                Assert.assertEquals(506, bitmap.height)
                Assert.assertEquals(ARGB_8888, bitmap.config)
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
                }
                Assert.assertEquals(700, imageInfo.width)
                Assert.assertEquals(1012, imageInfo.height)
                Assert.assertEquals("image/bmp", imageInfo.mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
                Assert.assertEquals(DataFrom.LOCAL, dataFrom)
                Assert.assertEquals("[InSampledTransformed(2)]", transformedList.toString())
            }

        // disabledBitmapPool
        TODO("")
        // todo 增加一个有 exif 的示例图片
        // todo test exif
    }
}