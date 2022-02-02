package com.github.panpf.sketch.test.decode.internal

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.internal.decodeBitmapWithBitmapFactory
import com.github.panpf.sketch.decode.internal.decodeRegionBitmap
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactory
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.R
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DecodeUtilsTest {

    @Test
    fun testReadImageInfoWithBitmapFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp")
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                Assert.assertEquals("image/webp", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }

        val rotateImageFile = getRotateImageFile(context, sketch)
        FileDataSource(sketch, LoadRequest(rotateImageFile.path), rotateImageFile)
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_90, exifOrientation)
            }

        ResourceDataSource(
            sketch,
            LoadRequest(context.newResourceUri(R.xml.network_security_config)),
            context.resources,
            R.xml.network_security_config
        )
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(-1, width)
                Assert.assertEquals(-1, height)
                Assert.assertEquals("", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrThrow() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp")
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                Assert.assertEquals("image/webp", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }

        val rotateImageFile = getRotateImageFile(context, sketch)
        FileDataSource(sketch, LoadRequest(rotateImageFile.path), rotateImageFile)
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_90, exifOrientation)
            }

        assertThrow(Exception::class) {
            ResourceDataSource(
                sketch,
                LoadRequest(context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrThrow()
        }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrNull() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp")
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                Assert.assertEquals("image/webp", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }

        val rotateImageFile = getRotateImageFile(context, sketch)
        FileDataSource(sketch, LoadRequest(rotateImageFile.path), rotateImageFile)
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_90, exifOrientation)
            }

        Assert.assertNull(
            ResourceDataSource(
                sketch,
                LoadRequest(context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrNull()
        )
    }

    @Test
    fun testDecodeBitmapWithBitmapFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeBitmapWithBitmapFactory()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeBitmapWithBitmapFactory(BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                Assert.assertEquals(646, width)
                Assert.assertEquals(968, height)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp")
            .decodeBitmapWithBitmapFactory()!!.apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
            }

        Assert.assertNull(
            ResourceDataSource(
                sketch,
                LoadRequest(context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).decodeBitmapWithBitmapFactory()
        )
    }

    @Test
    fun testSupportBitmapRegionDecoder() {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            Assert.assertTrue(ImageFormat.HEIF.supportBitmapRegionDecoder())
        } else {
            Assert.assertFalse(ImageFormat.HEIF.supportBitmapRegionDecoder())
        }
        Assert.assertFalse(ImageFormat.BMP.supportBitmapRegionDecoder())
        Assert.assertFalse(ImageFormat.GIF.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.JPEG.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.PNG.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.WEBP.supportBitmapRegionDecoder())
    }

    @Test
    fun testDecodeRegionBitmap() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeRegionBitmap(Rect(500, 500, 600, 600))!!.apply {
                Assert.assertEquals(100, width)
                Assert.assertEquals(100, height)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeRegionBitmap(
                Rect(500, 500, 600, 600),
                BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                Assert.assertEquals(50, width)
                Assert.assertEquals(50, height)
            }

        AssetDataSource(sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp")
            .decodeRegionBitmap(Rect(500, 500, 700, 700))!!.apply {
                Assert.assertEquals(200, width)
                Assert.assertEquals(200, height)
            }

        assertThrow(IOException::class) {
            ResourceDataSource(
                sketch,
                LoadRequest(context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).decodeRegionBitmap(Rect(500, 500, 600, 600))
        }
    }

    @Test
    fun testIsInBitmapError() {
        Assert.assertTrue(
            isInBitmapError(IllegalArgumentException("Problem decoding into existing bitmap"))
        )
        Assert.assertTrue(
            isInBitmapError(IllegalArgumentException("bitmap"))
        )

        Assert.assertFalse(
            isInBitmapError(IllegalArgumentException("Problem decoding"))
        )
        Assert.assertFalse(
            isInBitmapError(IllegalStateException("Problem decoding into existing bitmap"))
        )
    }

    @Test
    fun testIsSrcRectError() {
        Assert.assertTrue(
            isSrcRectError(IllegalArgumentException("rectangle is outside the image srcRect"))
        )
        Assert.assertTrue(
            isSrcRectError(IllegalArgumentException("srcRect"))
        )

        Assert.assertFalse(
            isSrcRectError(IllegalStateException("rectangle is outside the image srcRect"))
        )
        Assert.assertFalse(
            isSrcRectError(IllegalArgumentException(""))
        )
    }

    private fun getRotateImageFile(context: Context, sketch: Sketch): File {
        val file = File(context.externalCacheDir ?: context.cacheDir, "sample_rotate.jpeg")
        if (file.exists()) {
            return file
        }

        val source = AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
        file.outputStream().use { out ->
            source.newInputStream().use { input ->
                input.copyTo(out)
            }
        }

        val exifInterface = ExifInterface(file)
        exifInterface.rotate(90)
        exifInterface.saveAttributes()
        return file
    }
}