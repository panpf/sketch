package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
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
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_90
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_180
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_270
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .applyOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }.toString(),
                )
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).applyOrientation(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).applyOrientation(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1).applyOrientation(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(100).applyOrientation(inBitmap)
        )
    }

    @Test
    fun testAddOrientation() {
        val context = InstrumentationRegistry.getContext()
        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_90
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_180
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_270
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .addOrientation(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }.toString(),
                )
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).addOrientation(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).addOrientation(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1).addOrientation(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(100).addOrientation(inBitmap)
        )
    }

    @Test
    fun testAddAndApplyOrientation() {
        val context = InstrumentationRegistry.getContext()
        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).applyOrientation(
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .addOrientation(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }
    }

    @Test
    fun testApplyRotationSize() {
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(-1).applyRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(100).applyRotationSize(Size(100, 50))
        )
    }

    @Test
    fun testAddRotationSize() {
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(-1).addRotationSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(100).addRotationSize(Size(100, 50))
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

    private val Bitmap.cornerA: Int
        get() = getPixel(0, 0)
    private val Bitmap.cornerB: Int
        get() = getPixel(width - 1, 0)
    private val Bitmap.cornerC: Int
        get() = getPixel(width - 1, height - 1)
    private val Bitmap.cornerD: Int
        get() = getPixel(0, height - 1)

    private fun Bitmap.corners(block: Bitmap.() -> List<Int>): List<Int> {
        return block(this)
    }
}