/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readExifOrientation
import com.github.panpf.sketch.decode.internal.readExifOrientationWithMimeType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.utils.cornerA
import com.github.panpf.sketch.test.utils.cornerB
import com.github.panpf.sketch.test.utils.cornerC
import com.github.panpf.sketch.test.utils.cornerD
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ExifOrientationHelperTest {

    @Test
    fun testReadExifOrientation() {
        val context = getTestContext()

        assertEquals(
            ExifOrientationHelper.NORMAL,
            AssetDataSource(context, ResourceImages.jpeg.resourceName).readExifOrientation()
        )

        assertEquals(
            ExifOrientationHelper.UNDEFINED,
            AssetDataSource(context, ResourceImages.webp.resourceName).readExifOrientation()
        )

        ResourceImages.clockExifs.forEach {
            assertEquals(
                expected = it.exifOrientation,
                actual = it.toDataSource(context).readExifOrientation(),
                message = "imageFile: ${it.uri}"
            )
        }

        assertEquals(
            ExifOrientationHelper.UNDEFINED,
            ResourceDataSource(
                context.resources,
                packageName = context.packageName,
                com.github.panpf.sketch.test.R.xml.network_security_config
            ).readExifOrientation()
        )
    }

    @Test
    fun testReadExifOrientationWithMimeType() {
        val context = getTestContext()

        assertEquals(
            ExifOrientationHelper.NORMAL,
            AssetDataSource(
                context,
                ResourceImages.jpeg.resourceName
            ).readExifOrientationWithMimeType("image/jpeg")
        )

        assertEquals(
            ExifOrientationHelper.UNDEFINED,
            AssetDataSource(
                context,
                ResourceImages.jpeg.resourceName
            ).readExifOrientationWithMimeType("image/bmp")
        )

        assertEquals(
            ExifOrientationHelper.UNDEFINED,
            AssetDataSource(
                context,
                ResourceImages.webp.resourceName
            ).readExifOrientationWithMimeType("image/webp")
        )

        ResourceImages.clockExifs.forEach {
            assertEquals(
                expected = it.exifOrientation,
                actual = it.toDataSource(context).readExifOrientationWithMimeType("image/jpeg"),
                message = "imageFile: ${it.uri}"
            )
            assertEquals(
                expected = ExifOrientationHelper.UNDEFINED,
                actual = it.toDataSource(context).readExifOrientationWithMimeType("image/bmp"),
                message = "imageFile: ${it.uri}"
            )
        }

        assertEquals(
            ExifOrientationHelper.UNDEFINED,
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = com.github.panpf.sketch.test.R.xml.network_security_config
            ).readExifOrientationWithMimeType("image/jpeg")
        )
    }

    @Test
    fun testName() {
        listOf(
            ExifOrientationHelper.UNDEFINED to "UNDEFINED",
            ExifOrientationHelper.NORMAL to "NORMAL",
            ExifOrientationHelper.FLIP_HORIZONTAL to "FLIP_HORIZONTAL",
            ExifOrientationHelper.ROTATE_180 to "ROTATE_180",
            ExifOrientationHelper.FLIP_VERTICAL to "FLIP_VERTICAL",
            ExifOrientationHelper.TRANSPOSE to "TRANSPOSE",
            ExifOrientationHelper.ROTATE_90 to "ROTATE_90",
            ExifOrientationHelper.TRANSVERSE to "TRANSVERSE",
            ExifOrientationHelper.ROTATE_270 to "ROTATE_270",
        ).forEach { (orientation, expected) ->
            assertEquals(
                expected = expected,
                actual = ExifOrientationHelper.name(orientation),
                message = "orientation=${ExifOrientationHelper.name(orientation)},"
            )
        }

        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.name(-1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.name(-2)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.name(9)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.name(10)
        }
    }

    @Test
    fun testValueOf() {
        listOf(
            "UNDEFINED" to ExifOrientationHelper.UNDEFINED,
            "NORMAL" to ExifOrientationHelper.NORMAL,
            "FLIP_HORIZONTAL" to ExifOrientationHelper.FLIP_HORIZONTAL,
            "ROTATE_180" to ExifOrientationHelper.ROTATE_180,
            "FLIP_VERTICAL" to ExifOrientationHelper.FLIP_VERTICAL,
            "TRANSPOSE" to ExifOrientationHelper.TRANSPOSE,
            "ROTATE_90" to ExifOrientationHelper.ROTATE_90,
            "TRANSVERSE" to ExifOrientationHelper.TRANSVERSE,
            "ROTATE_270" to ExifOrientationHelper.ROTATE_270,
        ).forEach { (name, expected) ->
            assertEquals(
                expected = expected,
                actual = ExifOrientationHelper.valueOf(name),
                message = "name=${name},"
            )
        }

        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.valueOf("-1")
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.valueOf("-2")
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.valueOf("9")
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper.valueOf("10")
        }
    }

    @Test
    fun testConstructor() {
        ExifOrientationHelper(ExifOrientationHelper.UNDEFINED).apply {
            assertEquals(ExifOrientationHelper.UNDEFINED, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.NORMAL).apply {
            assertEquals(ExifOrientationHelper.NORMAL, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL).apply {
            assertEquals(ExifOrientationHelper.FLIP_HORIZONTAL, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_180).apply {
            assertEquals(ExifOrientationHelper.ROTATE_180, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL).apply {
            assertEquals(ExifOrientationHelper.FLIP_VERTICAL, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE).apply {
            assertEquals(ExifOrientationHelper.TRANSPOSE, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_90).apply {
            assertEquals(ExifOrientationHelper.ROTATE_90, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE).apply {
            assertEquals(ExifOrientationHelper.TRANSVERSE, exifOrientation)
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_270).apply {
            assertEquals(ExifOrientationHelper.ROTATE_270, exifOrientation)
        }

        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper(-1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper(-2)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper(9)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ExifOrientationHelper(10)
        }
    }

    @Test
    fun testIsFlipHorizontally() {
        listOf(
            ExifOrientationHelper.UNDEFINED to false,
            ExifOrientationHelper.NORMAL to false,
            ExifOrientationHelper.FLIP_HORIZONTAL to true,
            ExifOrientationHelper.ROTATE_180 to false,
            ExifOrientationHelper.FLIP_VERTICAL to true,
            ExifOrientationHelper.TRANSPOSE to true,
            ExifOrientationHelper.ROTATE_90 to false,
            ExifOrientationHelper.TRANSVERSE to true,
            ExifOrientationHelper.ROTATE_270 to false,
        ).forEach { (orientation, expected) ->
            assertEquals(
                expected = expected,
                actual = ExifOrientationHelper(orientation).isFlipHorizontally,
                message = "orientation=${ExifOrientationHelper.name(orientation)},"
            )
        }
    }

    @Test
    fun testRotationDegrees() {
        assertEquals(
            expected = 90,
            actual = ExifOrientationHelper(ExifOrientationHelper.ROTATE_90).rotationDegrees
        )
        assertEquals(
            expected = 270,
            actual = ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE).rotationDegrees
        )
        assertEquals(
            expected = 180,
            actual = ExifOrientationHelper(ExifOrientationHelper.ROTATE_180).rotationDegrees
        )
        assertEquals(
            expected = 180,
            actual = ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL).rotationDegrees
        )
        assertEquals(
            expected = 270,
            actual = ExifOrientationHelper(ExifOrientationHelper.ROTATE_270).rotationDegrees
        )
        assertEquals(
            expected = 90,
            actual = ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE).rotationDegrees
        )
        assertEquals(
            expected = 0,
            actual = ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL).rotationDegrees
        )
        assertEquals(
            expected = 0,
            actual = ExifOrientationHelper(ExifOrientationHelper.UNDEFINED).rotationDegrees
        )
        assertEquals(
            expected = 0,
            actual = ExifOrientationHelper(ExifOrientationHelper.NORMAL).rotationDegrees
        )
    }

    @Test
    fun testApplyToSize() {
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_90).apply {
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE).apply {
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_180).apply {
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL).apply {
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_270).apply {
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE).apply {
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(50, 100),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.UNDEFINED).apply {
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.NORMAL).apply {
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
        ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL).apply {
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Size(100, 50),
                actual = applyToSize(Size(100, 50), reverse = true)
            )
        }
    }

    @Test
    fun testApplyToRect() {
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_90).apply {
            assertEquals(
                expected = Rect(20, 40, 40, 50),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(10, 50, 30, 60),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE).apply {
            assertEquals(
                expected = Rect(20, 50, 40, 60),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(20, 50, 40, 60),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_180).apply {
            assertEquals(
                expected = Rect(50, 20, 60, 40),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(50, 20, 60, 40),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL).apply {
            assertEquals(
                expected = Rect(40, 20, 50, 40),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(40, 20, 50, 40),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_270).apply {
            assertEquals(
                expected = Rect(10, 50, 30, 60),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(20, 40, 40, 50),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE).apply {
            assertEquals(
                expected = Rect(10, 40, 30, 50),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(10, 40, 30, 50),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL).apply {
            assertEquals(
                expected = Rect(50, 10, 60, 30),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )

            assertEquals(
                expected = Rect(50, 10, 60, 30),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.UNDEFINED).apply {
            assertEquals(
                expected = Rect(40, 10, 50, 30),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(40, 10, 50, 30),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.NORMAL).apply {
            assertEquals(
                expected = Rect(40, 10, 50, 30),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = false)
            )
            assertEquals(
                expected = Rect(40, 10, 50, 30),
                actual = applyToRect(Rect(40, 10, 50, 30), Size(100, 50), reverse = true)
            )
        }
    }

    @Test
    fun testApplyToBitmap() {
        val context = getTestContext()
        val inBitmap = context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it)
        }
        assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_90)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_90
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_180)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_180
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_270)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                // Flip horizontally and apply ORIENTATION_ROTATE_270
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL)
            .applyToBitmap(bitmap = inBitmap, reverse = false)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        assertNull(
            ExifOrientationHelper(ExifOrientationHelper.UNDEFINED)
                .applyToBitmap(bitmap = inBitmap, reverse = false)
        )
        assertNull(
            ExifOrientationHelper(ExifOrientationHelper.NORMAL)
                .applyToBitmap(bitmap = inBitmap, reverse = false)
        )
    }

    @Test
    fun testAddToBitmap() {
        val context = getTestContext()
        val inBitmap = context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it)
        }
        assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_90)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerB, cornerC, cornerD, cornerA) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_90
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerC, cornerB, cornerA, cornerD) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_180)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerC, cornerD, cornerA, cornerB) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_180
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerD, cornerC, cornerB, cornerA) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.ROTATE_270)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerD, cornerA, cornerB, cornerC) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                // Flip horizontally based on ORIENTATION_ROTATE_270
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerA, cornerD, cornerC, cornerB) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL)
            .applyToBitmap(bitmap = inBitmap, reverse = true)!!.let { outBitmap ->
                assertEquals(
                    expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                    actual = inBitmap.corners { listOf(cornerB, cornerA, cornerD, cornerC) }
                        .toString(),
                    message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                        .toString(),
                )
            }
        assertNull(
            ExifOrientationHelper(ExifOrientationHelper.UNDEFINED)
                .applyToBitmap(bitmap = inBitmap, reverse = true)
        )
        assertNull(
            ExifOrientationHelper(ExifOrientationHelper.NORMAL)
                .applyToBitmap(bitmap = inBitmap, reverse = true)
        )
    }

    @Test
    fun testAddAndApplyToBitmap() {
        val context = getTestContext()
        val inBitmap = context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it)
        }
        assertTrue(
            inBitmap.cornerA != inBitmap.cornerB
                    && inBitmap.cornerA != inBitmap.cornerC
                    && inBitmap.cornerA != inBitmap.cornerD
        )

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_90).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.ROTATE_90)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.TRANSVERSE)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_180).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.ROTATE_180)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.FLIP_VERTICAL)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.ROTATE_270).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.ROTATE_270)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.TRANSPOSE)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }

        ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL).applyToBitmap(
            ExifOrientationHelper(ExifOrientationHelper.FLIP_HORIZONTAL)
                .applyToBitmap(bitmap = inBitmap, reverse = true)!!, false
        )!!.let { outBitmap ->
            assertEquals(
                expected = outBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
                actual = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }.toString(),
                message = inBitmap.corners { listOf(cornerA, cornerB, cornerC, cornerD) }
                    .toString(),
            )
        }
    }
}