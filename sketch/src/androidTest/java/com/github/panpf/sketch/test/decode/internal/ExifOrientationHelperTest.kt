package com.github.panpf.sketch.test.decode.internal

import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.utils.getContextAndSketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.cornerA
import com.github.panpf.sketch.test.utils.cornerB
import com.github.panpf.sketch.test.utils.cornerC
import com.github.panpf.sketch.test.utils.cornerD
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExifOrientationHelperTest {

    @Test
    fun testReadExifOrientation() {
        val (context, sketch) = getContextAndSketch()

        Assert.assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            AssetDataSource(
                sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg"
            ).readExifOrientation()
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch, LoadRequest(context, newAssetUri("sample.webp")), "sample.webp"
            ).readExifOrientation()
        )

        ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg").files().forEach {
            Assert.assertEquals(
                it.exifOrientation,
                FileDataSource(
                    sketch,
                    LoadRequest(context, it.file.path),
                    it.file
                ).readExifOrientation()
            )
        }

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            ResourceDataSource(
                sketch,
                LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readExifOrientation()
        )
    }

    @Test
    fun testReadExifOrientationWithMimeType() {
        val (context, sketch) = getContextAndSketch()

        Assert.assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            AssetDataSource(
                sketch,
                LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg"
            ).readExifOrientationWithMimeType("image/jpeg")
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch,
                LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg"
            ).readExifOrientationWithMimeType("image/bmp")
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch,
                LoadRequest(context, newAssetUri("sample.webp")), "sample.webp"
            ).readExifOrientationWithMimeType("image/webp")
        )

        ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg").files().forEach {
            Assert.assertEquals(
                it.exifOrientation,
                FileDataSource(sketch, LoadRequest(context, it.file.path), it.file)
                    .readExifOrientationWithMimeType("image/jpeg")
            )
            Assert.assertEquals(
                ExifInterface.ORIENTATION_UNDEFINED,
                FileDataSource(sketch, LoadRequest(context, it.file.path), it.file)
                    .readExifOrientationWithMimeType("image/bmp")
            )
        }

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            ResourceDataSource(
                sketch,
                LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
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
    fun testApplyToBitmap() {
        val (context, _) = getContextAndSketch()
        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_90
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_180
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_270
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .applyToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }.toString(),
                )
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).applyToBitmap(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).applyToBitmap(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1).applyToBitmap(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(100).applyToBitmap(inBitmap)
        )
    }

    @Test
    fun testAddToBitmap() {
        val (context, _) = getContextAndSketch()
        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_90
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_180
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_270
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .addToBitmap(inBitmap)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }.toString(),
                )
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).addToBitmap(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).addToBitmap(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1).addToBitmap(inBitmap)
        )
        Assert.assertNull(
            ExifOrientationHelper(100).addToBitmap(inBitmap)
        )
    }

    @Test
    fun testAddAndApplyToBitmap() {
        val (context, sketch) = getContextAndSketch()
        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .addToBitmap(inBitmap)!!
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }
    }

    @Test
    fun testApplyToSize() {
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(50, 100),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(-1).applyToSize(Size(100, 50))
        )
        Assert.assertEquals(
            Size(100, 50),
            ExifOrientationHelper(100).applyToSize(Size(100, 50))
        )
    }

    @Test
    fun testAddToSize() {
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).apply {
            Assert.assertEquals(Size(50, 100), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).apply {
            Assert.assertEquals(Size(50, 100), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals(Size(50, 100), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).apply {
            Assert.assertEquals(Size(50, 100), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(-1).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
        ExifOrientationHelper(100).apply {
            Assert.assertEquals(Size(100, 50), addToSize(Size(100, 50)))
        }
    }

    @Test
    fun testAddToResize() {
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).apply {
            Assert.assertEquals(Resize(5, 10), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).apply {
            Assert.assertEquals(Resize(5, 10), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals(Resize(5, 10), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).apply {
            Assert.assertEquals(Resize(5, 10), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(5, 10, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(5, 10, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(-1).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
        ExifOrientationHelper(10).apply {
            Assert.assertEquals(Resize(10, 5), addToResize(Resize(10, 5), Size(100, 50)))
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(100, 50))
            )
            Assert.assertEquals(
                Resize(10, 5, START_CROP),
                addToResize(Resize(10, 5, START_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, CENTER_CROP),
                addToResize(Resize(10, 5, CENTER_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, END_CROP),
                addToResize(Resize(10, 5, END_CROP), Size(50, 100))
            )
            Assert.assertEquals(
                Resize(10, 5, FILL),
                addToResize(Resize(10, 5, FILL), Size(50, 100))
            )
        }
    }

    @Test
    fun testAddToRect() {
        Assert.assertEquals(
            Rect(10, 50, 30, 60),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(20, 50, 40, 60),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(50, 20, 60, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(40, 20, 50, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(20, 40, 40, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(10, 40, 30, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(50, 10, 60, 30),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(-1)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(100)
                .addToRect(Rect(40, 10, 50, 30), Size(100, 50))
        )
    }
}