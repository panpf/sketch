package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.longImageScale
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongImageScaleDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertEquals(
            LongImageScaleDecider(START_CROP, CENTER_CROP),
            longImageScale(START_CROP, CENTER_CROP)
        )
        Assert.assertEquals(
            LongImageScaleDecider(END_CROP, START_CROP),
            longImageScale(END_CROP, START_CROP)
        )
    }

    @Test
    fun testGet() {
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
    fun testAddExifOrientation() {
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
    fun testKey() {
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
    fun testToString() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            Assert.assertEquals(
                "LongImageScaleDecider(longImage=START_CROP, otherImage=CENTER_CROP, longImageDecider=DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0))",
                toString()
            )
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            Assert.assertEquals(
                "LongImageScaleDecider(longImage=END_CROP, otherImage=CENTER_CROP, longImageDecider=DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0))",
                toString()
            )
        }
    }

    @Test
    fun testEquals() {
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
    fun testHashCode() {
        val element1 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element11 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element2 = LongImageScaleDecider(END_CROP, CENTER_CROP)
        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
    }
}