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
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
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
        val sketch = newSketch()
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals(START_CROP, get(sketch, 100, 48, 50, 50))
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals(END_CROP, get(sketch, 100, 48, 50, 50))
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
            Assert.assertEquals("FixedScaleDecider(START_CROP)", key)
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(END_CROP)", key)
        }
    }

    @Test
    fun testToString() {
        FixedScaleDecider(START_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(START_CROP)", toString())
        }
        FixedScaleDecider(END_CROP).apply {
            Assert.assertEquals("FixedScaleDecider(END_CROP)", toString())
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

    @Test
    fun testSerializer() {
        val scaleDecider = FixedScaleDecider(START_CROP)

        val serializer =
            scaleDecider.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(scaleDecider))

        Assert.assertNotSame(scaleDecider, transformed1)
        Assert.assertEquals(scaleDecider, transformed1)
    }
}