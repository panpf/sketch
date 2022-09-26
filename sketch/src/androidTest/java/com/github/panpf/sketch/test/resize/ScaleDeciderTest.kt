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
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScaleDeciderTest {

    @Test
    fun testFixedScaleDeciderCreateFunction() {
        Assert.assertEquals(FixedScaleDecider(START_CROP), FixedScaleDecider(START_CROP))
        Assert.assertEquals(FixedScaleDecider(END_CROP), FixedScaleDecider(END_CROP))
        Assert.assertEquals(FixedScaleDecider(CENTER_CROP), FixedScaleDecider(CENTER_CROP))
        Assert.assertEquals(FixedScaleDecider(FILL), FixedScaleDecider(FILL))
    }

    @Test
    fun testFixedScaleDeciderGet() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals(START_CROP, get(100, 48, 50, 50))
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals(END_CROP, get(100, 48, 50, 50))
        }
    }

    @Test
    fun testFixedScaleDeciderAddExifOrientation() {
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
    fun testFixedScaleDeciderKey() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals("Fixed(START_CROP)", key)
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals("Fixed(END_CROP)", key)
        }
    }

    @Test
    fun testFixedScaleDeciderToString() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(START_CROP)", toString())
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(END_CROP)", toString())
        }
    }

    @Test
    fun testFixedScaleDeciderEquals() {
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
    fun testFixedScaleDeciderHashCode() {
        val element1 = FixedScaleDecider(START_CROP)
        val element11 = FixedScaleDecider(START_CROP)
        val element2 = FixedScaleDecider(END_CROP)
        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testLongImageScaleDeciderCreateFunction() {
        Assert.assertEquals(
            LongImageScaleDecider(START_CROP, CENTER_CROP),
            LongImageScaleDecider(START_CROP, CENTER_CROP)
        )
        Assert.assertEquals(
            LongImageScaleDecider(END_CROP, START_CROP),
            LongImageScaleDecider(END_CROP, START_CROP)
        )
    }

    @Test
    fun testLongImageScaleDeciderGet() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            Assert.assertEquals(CENTER_CROP, get(100, 50, 50, 50))
            Assert.assertEquals(START_CROP, get(100, 40, 50, 50))
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            Assert.assertEquals(CENTER_CROP, get(100, 50, 50, 50))
            Assert.assertEquals(END_CROP, get(100, 40, 50, 50))
        }
    }

    @Test
    fun testLongImageScaleDeciderAddExifOrientation() {
        val exifOriNormal =
            ExifOrientationHelper(androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL)
        val exifOri90 =
            ExifOrientationHelper(androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90)
        val exifOri180 =
            ExifOrientationHelper(androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180)

        LongImageScaleDecider(START_CROP, END_CROP).apply {
            Assert.assertEquals(
                LongImageScaleDecider(START_CROP, END_CROP),
                addExifOrientation(exifOriNormal, Size(100, 48))
            )
        }
        LongImageScaleDecider(START_CROP, END_CROP).apply {
            Assert.assertEquals(
                LongImageScaleDecider(END_CROP, START_CROP),
                addExifOrientation(exifOri90, Size(100, 48))
            )
        }
        LongImageScaleDecider(START_CROP, END_CROP).apply {
            Assert.assertEquals(
                LongImageScaleDecider(END_CROP, START_CROP),
                addExifOrientation(exifOri180, Size(100, 48))
            )
        }
    }

    @Test
    fun testLongImageScaleDeciderKey() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            Assert.assertEquals(
                "LongImage(START_CROP,CENTER_CROP),Default(2.5,5.0))",
                key
            )
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            Assert.assertEquals(
                "LongImage(END_CROP,CENTER_CROP),Default(2.5,5.0))",
                key
            )
        }
    }

    @Test
    fun testLongImageScaleDeciderToString() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            Assert.assertEquals(
                "LongImageScaleDecider(longImage=START_CROP, otherImage=CENTER_CROP, longImageDecider=DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0))",
                toString()
            )
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            Assert.assertEquals(
                "LongImageScaleDecider(longImage=END_CROP, otherImage=CENTER_CROP, longImageDecider=DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0))",
                toString()
            )
        }
    }

    @Test
    fun testLongImageScaleDeciderEquals() {
        val element1 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element11 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element2 = LongImageScaleDecider(END_CROP, CENTER_CROP)
        val element3 = LongImageScaleDecider(START_CROP, END_CROP)
        val element4 = LongImageScaleDecider(
            START_CROP,
            CENTER_CROP,
            longImageDecider = DefaultLongImageDecider(3f, 6f)
        )

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element3, element4)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testLongImageScaleDeciderHashCode() {
        val element1 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element11 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element2 = LongImageScaleDecider(END_CROP, CENTER_CROP)
        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
    }
}