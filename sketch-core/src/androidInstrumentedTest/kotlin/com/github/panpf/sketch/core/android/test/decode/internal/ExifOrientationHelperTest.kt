/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.core.android.test.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.addToResize
import com.github.panpf.sketch.decode.internal.readExifOrientation
import com.github.panpf.sketch.decode.internal.readExifOrientationWithMimeType
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.cornerA
import com.github.panpf.sketch.test.utils.cornerB
import com.github.panpf.sketch.test.utils.cornerC
import com.github.panpf.sketch.test.utils.cornerD
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import okio.Path.Companion.toOkioPath
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExifOrientationHelperTest {

    @Test
    fun testConstructor() {
        ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).apply {
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_270, exifOrientation)
        }
    }

    @Test
    fun testReadExifOrientation() {
        val (context, sketch) = getTestContextAndNewSketch()

        Assert.assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            AssetDataSource(
                sketch, ImageRequest(context, MyImages.jpeg.uri), MyImages.jpeg.fileName
            ).readExifOrientation()
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch, ImageRequest(context, MyImages.webp.uri), MyImages.webp.fileName
            ).readExifOrientation()
        )

        ExifOrientationTestFileHelper(
            context,
            MyImages.clockHor.fileName
        ).files()
            .forEach {
                Assert.assertEquals(
                    it.exifOrientation,
                    FileDataSource(
                        sketch,
                        ImageRequest(context, it.file.path),
                        it.file.toOkioPath()
                    ).readExifOrientation()
                )
            }

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            ResourceDataSource(
                sketch,
                ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.test.utils.core.R.xml.network_security_config)
                ),
                packageName = context.packageName,
                context.resources,
                com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
            ).readExifOrientation()
        )
    }

    @Test
    fun testReadExifOrientationWithMimeType() {
        val (context, sketch) = getTestContextAndNewSketch()

        Assert.assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            AssetDataSource(
                sketch,
                ImageRequest(context, MyImages.jpeg.uri), MyImages.jpeg.fileName
            ).readExifOrientationWithMimeType("image/jpeg")
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch,
                ImageRequest(context, MyImages.jpeg.uri), MyImages.jpeg.fileName
            ).readExifOrientationWithMimeType("image/bmp")
        )

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            AssetDataSource(
                sketch,
                ImageRequest(context, MyImages.webp.uri), MyImages.webp.fileName
            ).readExifOrientationWithMimeType("image/webp")
        )

        ExifOrientationTestFileHelper(
            context,
            MyImages.clockHor.fileName
        ).files()
            .forEach {
                Assert.assertEquals(
                    it.exifOrientation,
                    FileDataSource(
                        sketch,
                        ImageRequest(context, it.file.path),
                        it.file.toOkioPath()
                    )
                        .readExifOrientationWithMimeType("image/jpeg")
                )
                Assert.assertEquals(
                    ExifInterface.ORIENTATION_UNDEFINED,
                    FileDataSource(
                        sketch,
                        ImageRequest(context, it.file.path),
                        it.file.toOkioPath()
                    )
                        .readExifOrientationWithMimeType("image/bmp")
                )
            }

        Assert.assertEquals(
            ExifInterface.ORIENTATION_UNDEFINED,
            ResourceDataSource(
                sketch,
                ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.test.utils.core.R.xml.network_security_config)
                ),
                packageName = context.packageName,
                context.resources,
                com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
            ).readExifOrientationWithMimeType("image/jpeg")
        )
    }

    @Test
    fun testExifOrientationName() {
        Assert.assertEquals("ROTATE_90", ExifOrientation.name(ExifInterface.ORIENTATION_ROTATE_90))
        Assert.assertEquals("TRANSPOSE", ExifOrientation.name(ExifInterface.ORIENTATION_TRANSPOSE))
        Assert.assertEquals(
            "ROTATE_180",
            ExifOrientation.name(ExifInterface.ORIENTATION_ROTATE_180)
        )
        Assert.assertEquals(
            "FLIP_VERTICAL",
            ExifOrientation.name(ExifInterface.ORIENTATION_FLIP_VERTICAL)
        )
        Assert.assertEquals(
            "ROTATE_270",
            ExifOrientation.name(ExifInterface.ORIENTATION_ROTATE_270)
        )
        Assert.assertEquals(
            "TRANSVERSE",
            ExifOrientation.name(ExifInterface.ORIENTATION_TRANSVERSE)
        )
        Assert.assertEquals(
            "FLIP_HORIZONTAL",
            ExifOrientation.name(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
        )
        Assert.assertEquals("UNDEFINED", ExifOrientation.name(ExifInterface.ORIENTATION_UNDEFINED))
        Assert.assertEquals("NORMAL", ExifOrientation.name(ExifInterface.ORIENTATION_NORMAL))
        Assert.assertEquals("-1", ExifOrientation.name(-1))
        Assert.assertEquals("100", ExifOrientation.name(100))
    }

    @Test
    fun testIsFlipped() {
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).isFlipped())
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).isFlipped())
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).isFlipped())
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).isFlipped())
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).isFlipped())
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).isFlipped())
        Assert.assertTrue(ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).isFlipped())
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).isFlipped())
        Assert.assertFalse(ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).isFlipped())
        Assert.assertFalse(ExifOrientationHelper(-1).isFlipped())
        Assert.assertFalse(ExifOrientationHelper(100).isFlipped())
    }

    @Test
    fun testRotationDegrees() {
        Assert.assertEquals(
            90,
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).getRotationDegrees()
        )
        Assert.assertEquals(
            270,
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).getRotationDegrees()
        )
        Assert.assertEquals(
            180,
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).getRotationDegrees()
        )
        Assert.assertEquals(
            180,
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).getRotationDegrees()
        )
        Assert.assertEquals(
            270,
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).getRotationDegrees()
        )
        Assert.assertEquals(
            90,
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).getRotationDegrees()
        )
        Assert.assertEquals(
            0,
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).getRotationDegrees()
        )
        Assert.assertEquals(
            0,
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).getRotationDegrees()
        )
        Assert.assertEquals(
            0,
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).getRotationDegrees()
        )
        Assert.assertEquals(0, ExifOrientationHelper(-1).getRotationDegrees())
        Assert.assertEquals(0, ExifOrientationHelper(100).getRotationDegrees())
    }

    @Test
    fun testApplyToBitmap() {
        val context = getTestContext()
        val inBitmap = context.assets.open(MyImages.jpeg.fileName).use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_90
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_180
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_270
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .applyToBitmap(inBitmap, false)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }.toString(),
                )
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .applyToBitmap(inBitmap, false)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .applyToBitmap(inBitmap, false)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1).applyToBitmap(inBitmap, false)
        )
        Assert.assertNull(
            ExifOrientationHelper(100).applyToBitmap(inBitmap, false)
        )
    }

    @Test
    fun testAddToBitmap() {
        val context = getTestContext()
        val inBitmap = context.assets.open(MyImages.jpeg.fileName).use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_90
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_180
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_270
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }.toString(),
                )
            }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
            .applyToBitmap(inBitmap, reverse = true)!!.let { outBitmap ->
                Assert.assertEquals(
                    inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                    inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }.toString(),
                )
            }
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .applyToBitmap(inBitmap, reverse = true)
        )
        Assert.assertNull(
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .applyToBitmap(inBitmap, reverse = true)
        )
        Assert.assertNull(
            ExifOrientationHelper(-1).applyToBitmap(inBitmap, reverse = true)
        )
        Assert.assertNull(
            ExifOrientationHelper(100).applyToBitmap(inBitmap, reverse = true)
        )
    }

    @Test
    fun testAddAndApplyToBitmap() {
        val context = getTestContext()
        val inBitmap = context.assets.open(MyImages.jpeg.fileName).use {
            BitmapFactory.decodeStream(it)
        }
        Assert.assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_90)
                .applyToBitmap(inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .applyToBitmap(inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .applyToBitmap(inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .applyToBitmap(inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .applyToBitmap(inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .applyToBitmap(inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            Assert.assertEquals(
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
            )
        }

        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).applyToBitmap(
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .applyToBitmap(inBitmap, reverse = true)!!, false
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
            Assert.assertEquals(Size(50, 100), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE).apply {
            Assert.assertEquals(Size(50, 100), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals(Size(50, 100), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE).apply {
            Assert.assertEquals(Size(50, 100), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(-1).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
        }
        ExifOrientationHelper(100).apply {
            Assert.assertEquals(Size(100, 50), applyToSize(Size(100, 50), reverse = true))
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
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(20, 50, 40, 60),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSVERSE)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(50, 20, 60, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_180)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(40, 20, 50, 40),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_VERTICAL)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(20, 40, 40, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_ROTATE_270)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(10, 40, 30, 50),
            ExifOrientationHelper(ExifInterface.ORIENTATION_TRANSPOSE)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(50, 10, 60, 30),
            ExifOrientationHelper(ExifInterface.ORIENTATION_FLIP_HORIZONTAL)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(ExifInterface.ORIENTATION_NORMAL)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(-1)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
        Assert.assertEquals(
            Rect(40, 10, 50, 30),
            ExifOrientationHelper(100)
                .applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
        )
    }

    private fun ExifOrientationHelper.applyToBitmap(
        bitmap: Bitmap,
        reverse: Boolean = false
    ): Bitmap? {
        return applyToImage(bitmap.asSketchImage(), reverse)?.getBitmapOrThrow()
    }
}