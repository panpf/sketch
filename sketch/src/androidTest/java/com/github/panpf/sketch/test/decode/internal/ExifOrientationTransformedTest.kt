package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.ExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExifOrientationTransformedTest {

    @Test
    fun testExifOrientation() {
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_270, exifOrientation)
        }
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90).apply {
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_90, exifOrientation)
        }
    }

    @Test
    fun testKey() {
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals("ExifOrientationTransformed(ROTATE_270)", key)
        }
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90).apply {
            Assert.assertEquals("ExifOrientationTransformed(ROTATE_90)", key)
        }
    }

    @Test
    fun testCacheResultToDisk() {
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testEquals() {
        val transformed1 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)
        val transformed11 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)

        val transformed2 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180)
        val transformed21 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180)

        val transformed3 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270)
        val transformed31 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270)

        Assert.assertNotSame(transformed1, transformed11)
        Assert.assertNotSame(transformed2, transformed21)
        Assert.assertNotSame(transformed3, transformed31)

        Assert.assertEquals(transformed1, transformed1)
        Assert.assertEquals(transformed1, transformed11)
        Assert.assertEquals(transformed2, transformed21)
        Assert.assertEquals(transformed3, transformed31)

        Assert.assertNotEquals(transformed1, transformed2)
        Assert.assertNotEquals(transformed1, transformed3)
        Assert.assertNotEquals(transformed2, transformed3)

        Assert.assertNotEquals(transformed2, null)
        Assert.assertNotEquals(transformed2, Any())
    }

    @Test
    fun testHashCode() {
        val transformed1 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)
        val transformed11 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)

        val transformed2 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180)
        val transformed21 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180)

        val transformed3 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270)
        val transformed31 = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270)

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
    }

    @Test
    fun testToString() {
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90).apply {
            Assert.assertEquals("ExifOrientationTransformed(ROTATE_90)", toString())
        }
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals("ExifOrientationTransformed(ROTATE_180)", toString())
        }
        ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_270).apply {
            Assert.assertEquals("ExifOrientationTransformed(ROTATE_270)", toString())
        }
    }

    @Test
    fun testGetResizeTransformed() {
        listOf(ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)).apply {
            Assert.assertNotNull(getExifOrientationTransformed())
        }
        listOf<Transformed>(CircleCropTransformed(END_CROP)).apply {
            Assert.assertNull(getExifOrientationTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getExifOrientationTransformed())
        }
    }

    @Test
    fun testSerializer() {
        val transformed = ExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}