package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.decode.internal.readExifOrientation
import com.github.panpf.sketch.decode.internal.readExifOrientationWithMimeType
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExifOrientationHelperTest {

    @Test
    fun testReadExifOrientation() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        Assert.assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            AssetDataSource(
                sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg"
            ).readExifOrientation()
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp"
            ).readExifOrientation()
        )

        ExifOrientationTestFileHelper(context).files().forEach {
            Assert.assertEquals(
                it.exifOrientation,
                FileDataSource(sketch, LoadRequest(it.file.path), it.file).readExifOrientation()
            )
        }

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            ResourceDataSource(
                sketch,
                LoadRequest(context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readExifOrientation()
        )
    }

    @Test
    fun testReadExifOrientationWithMimeType() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        Assert.assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            AssetDataSource(
                sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg"
            ).readExifOrientationWithMimeType("image/jpeg")
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg"
            ).readExifOrientationWithMimeType("image/bmp")
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch, LoadRequest(newAssetUri("sample.webp")), "sample.webp"
            ).readExifOrientationWithMimeType("image/webp")
        )

        ExifOrientationTestFileHelper(context).files().forEach {
            Assert.assertEquals(
                it.exifOrientation,
                FileDataSource(sketch, LoadRequest(it.file.path), it.file)
                    .readExifOrientationWithMimeType("image/jpeg")
            )
            Assert.assertEquals(
                ExifInterface.ORIENTATION_UNDEFINED,
                FileDataSource(sketch, LoadRequest(it.file.path), it.file)
                    .readExifOrientationWithMimeType("image/bmp")
            )
        }

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            ResourceDataSource(
                sketch,
                LoadRequest(context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readExifOrientationWithMimeType("image/jpeg")
        )
    }

    @Test
    fun testExifOrientationName() {
        Assert.assertEquals("ROTATE_90", exifOrientationName(ExifInterface.ORIENTATION_ROTATE_90))
        Assert.assertEquals("TRANSPOSE", exifOrientationName(ExifInterface.ORIENTATION_TRANSPOSE))
        Assert.assertEquals("ROTATE_180", exifOrientationName(ExifInterface.ORIENTATION_ROTATE_180))
        Assert.assertEquals(
            "FLIP_VERTICAL",
            exifOrientationName(ExifInterface.ORIENTATION_FLIP_VERTICAL)
        )
        Assert.assertEquals("ROTATE_270", exifOrientationName(ExifInterface.ORIENTATION_ROTATE_270))
        Assert.assertEquals("TRANSVERSE", exifOrientationName(ExifInterface.ORIENTATION_TRANSVERSE))
        Assert.assertEquals(
            "FLIP_HORIZONTAL",
            exifOrientationName(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
        )
        Assert.assertEquals("UNDEFINED", exifOrientationName(ExifInterface.ORIENTATION_UNDEFINED))
        Assert.assertEquals("NORMAL", exifOrientationName(ExifInterface.ORIENTATION_NORMAL))
        Assert.assertEquals("-1", exifOrientationName(-1))
        Assert.assertEquals("100", exifOrientationName(100))
    }

    @Test
    fun testIsFlipped() {
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).isFlipped)
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).isFlipped)
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).isFlipped)
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).isFlipped)
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).isFlipped)
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).isFlipped)
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).isFlipped)
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).isFlipped)
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).isFlipped)
        Assert.assertFalse(ExifOrientationHelper(-1).isFlipped)
        Assert.assertFalse(ExifOrientationHelper(100).isFlipped)
    }

    @Test
    fun testRotationDegrees() {
        Assert.assertEquals(
            90,
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).rotationDegrees
        )
        Assert.assertEquals(
            270,
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).rotationDegrees
        )
        Assert.assertEquals(
            180,
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).rotationDegrees
        )
        Assert.assertEquals(
            180,
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).rotationDegrees
        )
        Assert.assertEquals(
            270,
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).rotationDegrees
        )
        Assert.assertEquals(
            90,
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).rotationDegrees
        )
        Assert.assertEquals(
            0,
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).rotationDegrees
        )
        Assert.assertEquals(
            0,
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).rotationDegrees
        )
        Assert.assertEquals(
            0,
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).rotationDegrees
        )
        Assert.assertEquals(0, ExifOrientationHelper(-1).rotationDegrees)
        Assert.assertEquals(0, ExifOrientationHelper(100).rotationDegrees)
    }

    @Test
    fun testApplyOrientation() {
        val context = InstrumentationRegistry.getContext()
        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        val bitmapPool = LruBitmapPool(Logger(), 100 * 10124 * 1024)

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                Assert.assertEquals(inBitmap.leftTopPixel, rightTopPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, leftBottomPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, leftTopPixel)
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                // flip horizontal
//                Assert.assertEquals(inBitmap.leftTopPixel, rightTopPixel)
//                Assert.assertEquals(inBitmap.rightTopPixel, leftTopPixel)
//                Assert.assertEquals(inBitmap.rightBottomPixel, leftBottomPixel)
//                Assert.assertEquals(inBitmap.leftBottomPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.leftTopPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, rightTopPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, leftTopPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, leftBottomPixel)
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                Assert.assertEquals(inBitmap.leftTopPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, leftBottomPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, leftTopPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, rightTopPixel)
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                // flip horizontal
//                Assert.assertEquals(inBitmap.leftTopPixel, rightTopPixel)
//                Assert.assertEquals(inBitmap.rightTopPixel, leftTopPixel)
//                Assert.assertEquals(inBitmap.rightBottomPixel, leftBottomPixel)
//                Assert.assertEquals(inBitmap.leftBottomPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.leftTopPixel, leftBottomPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, rightTopPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, leftTopPixel)
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                Assert.assertEquals(inBitmap.leftTopPixel, leftBottomPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, leftTopPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, rightTopPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, rightBottomPixel)
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                // flip horizontal
//                Assert.assertEquals(inBitmap.leftTopPixel, rightTopPixel)
//                Assert.assertEquals(inBitmap.rightTopPixel, leftTopPixel)
//                Assert.assertEquals(inBitmap.rightBottomPixel, leftBottomPixel)
//                Assert.assertEquals(inBitmap.leftBottomPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.leftTopPixel, leftTopPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, leftBottomPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, rightBottomPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, rightTopPixel)
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .applyOrientation(inBitmap, bitmapPool)!!.apply {
                Assert.assertEquals(inBitmap.leftTopPixel, rightTopPixel)
                Assert.assertEquals(inBitmap.rightTopPixel, leftTopPixel)
                Assert.assertEquals(inBitmap.rightBottomPixel, leftBottomPixel)
                Assert.assertEquals(inBitmap.leftBottomPixel, rightBottomPixel)
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .applyOrientation(inBitmap, bitmapPool)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .applyOrientation(inBitmap, bitmapPool)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1)
                .applyOrientation(inBitmap, bitmapPool)
        )
        Assert.assertNull(
            ExifOrientationHelper(100)
                .applyOrientation(inBitmap, bitmapPool)
        )
    }

    @Test
    fun testRotateSize() {
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(-1).rotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(100).rotateSize(Size(100, 50))
        )
    }

    @Test
    fun testReverseRotateSize() {
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(-1).reverseRotateSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(100).reverseRotateSize(Size(100, 50))
        )
    }

    @Test
    fun testReverseRotateRect() {
        Assert.assertEquals(
            Rect(10, 50, 40, 60),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(10, 50, 40, 60),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(50, 10, 60, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(50, 10, 60, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(10, 40, 40, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(10, 40, 40, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 40),
            ExifOrientationHelper(-1)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 40),
            ExifOrientationHelper(100)
                .reverseRotateRect(Rect(40, 10, 50, 40), 100, 50)
        )
    }

//    private fun getRotateImageFile(context: Context, sketch: Sketch): File {
//        val file = File(context.externalCacheDir ?: context.cacheDir, "sample_rotate.jpeg")
//        if (file.exists()) {
//            return file
//        }
//
//        val source = AssetDataSource(sketch, LoadRequest(newAssetUri("sample.jpeg")), "sample.jpeg")
//        file.outputStream().use { out ->
//            source.newInputStream().use { input ->
//                input.copyTo(out)
//            }
//        }
//
//        val exifInterface = ExifInterface(file)
//        exifInterface.rotate(90)
//        exifInterface.saveAttributes()
//        return file
//    }

    private val Bitmap.leftTopPixel: Int
        get() = getPixel(0, 0)
    private val Bitmap.leftBottomPixel: Int
        get() = getPixel(0, height - 1)
    private val Bitmap.rightTopPixel: Int
        get() = getPixel(width - 1, 0)
    private val Bitmap.rightBottomPixel: Int
        get() = getPixel(width - 1, height - 1)
}