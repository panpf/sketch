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
package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.fixedScale
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FixedScaleDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertEquals(FixedScaleDecider(START_CROP), fixedScale(START_CROP))
        Assert.assertEquals(FixedScaleDecider(END_CROP), fixedScale(END_CROP))
        Assert.assertEquals(FixedScaleDecider(CENTER_CROP), fixedScale(CENTER_CROP))
        Assert.assertEquals(FixedScaleDecider(FILL), fixedScale(FILL))
    }

    @Test
    fun testGet() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals(START_CROP, get(100, 48, 50, 50))
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals(END_CROP, get(100, 48, 50, 50))
        }
    }

    @Test
    fun testAddExifOrientation() {
        val exifOriNormal =
            ExifOrientationHelper(androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL)
        val exifOri90 =
            ExifOrientationHelper(androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90)
        val exifOri180 =
            ExifOrientationHelper(androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180)

        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(START_CROP),
                addExifOrientation(exifOriNormal, Size(100, 48))
            )
        }
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(END_CROP),
                addExifOrientation(exifOri90, Size(100, 48))
            )
        }
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(END_CROP),
                addExifOrientation(exifOri180, Size(100, 48))
            )
        }

        FixedScaleDecider(CENTER_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(CENTER_CROP),
                addExifOrientation(exifOriNormal, Size(100, 48))
            )
        }
        FixedScaleDecider(CENTER_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(CENTER_CROP),
                addExifOrientation(exifOri90, Size(100, 48))
            )
        }
        FixedScaleDecider(CENTER_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(CENTER_CROP),
                addExifOrientation(exifOri180, Size(100, 48))
            )
        }

        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(END_CROP),
                addExifOrientation(exifOriNormal, Size(100, 48))
            )
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(START_CROP),
                addExifOrientation(exifOri90, Size(100, 48))
            )
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals(
                FixedScaleDecider(START_CROP),
                addExifOrientation(exifOri180, Size(100, 48))
            )
        }
    }

    @Test
    fun testKey() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals("Fixed(START_CROP)", key)
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals("Fixed(END_CROP)", key)
        }
    }

    @Test
    fun testToString() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(scale=START_CROP)", toString())
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(scale=END_CROP)", toString())
        }
    }

    @Test
    fun testEquals() {
        val element1 = FixedScaleDecider(START_CROP)
        val element11 = FixedScaleDecider(START_CROP)
        val element2 = FixedScaleDecider(END_CROP)
        val other = LongImageScaleDecider(END_CROP, CENTER_CROP)
        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, other)
    }

    @Test
    fun testHashCode() {
        val element1 = FixedScaleDecider(START_CROP)
        val element11 = FixedScaleDecider(START_CROP)
        val element2 = FixedScaleDecider(END_CROP)
        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
    }
}