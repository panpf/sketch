package com.github.panpf.sketch.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultLongImageDeciderTest {

    @Test
    fun testConstructor() {
        DefaultLongImageDecider().apply {
            Assert.assertEquals(2.5f, smallRatioMultiple, 0.0f)
            Assert.assertEquals(5.0f, bigRatioMultiple, 0.0f)
        }

        DefaultLongImageDecider(smallRatioMultiple = 3.2f, bigRatioMultiple = 6.1f).apply {
            Assert.assertEquals(3.2f, smallRatioMultiple, 0.0f)
            Assert.assertEquals(6.1f, bigRatioMultiple, 0.0f)
        }
    }

    @Test
    fun test() {
        val longImageDecider = DefaultLongImageDecider()

        /* Either one is a square */
        Assert.assertTrue(longImageDecider.isLongImage(150, 58, 50, 50))
        Assert.assertTrue(longImageDecider.isLongImage(150, 59, 50, 50))
        Assert.assertTrue(longImageDecider.isLongImage(150, 60, 50, 50))
        Assert.assertFalse(longImageDecider.isLongImage(150, 61, 50, 50))
        Assert.assertFalse(longImageDecider.isLongImage(150, 62, 50, 50))

        Assert.assertTrue(longImageDecider.isLongImage(50, 50, 150, 58))
        Assert.assertTrue(longImageDecider.isLongImage(50, 50, 150, 59))
        Assert.assertTrue(longImageDecider.isLongImage(50, 50, 150, 60))
        Assert.assertFalse(longImageDecider.isLongImage(50, 50, 150, 61))
        Assert.assertFalse(longImageDecider.isLongImage(50, 50, 150, 62))

        /* They go in the same direction */
        Assert.assertTrue(longImageDecider.isLongImage(200, 48, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(200, 49, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(200, 50, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(200, 51, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(200, 52, 80, 50))

        Assert.assertTrue(longImageDecider.isLongImage(49, 200, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(50, 200, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(51, 200, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(52, 200, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(53, 200, 50, 80))

        /* They don't go in the same direction */
        Assert.assertTrue(longImageDecider.isLongImage(200, 61, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(200, 62, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(200, 63, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(200, 64, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(200, 65, 50, 80))

        Assert.assertTrue(longImageDecider.isLongImage(63, 200, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(64, 200, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(65, 200, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(66, 200, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(67, 200, 80, 50))
    }

    @Test
    fun testKey() {
        DefaultLongImageDecider().apply {
            Assert.assertEquals("Default(2.5,5.0)", key)
        }
        DefaultLongImageDecider(4f, 10f).apply {
            Assert.assertEquals("Default(4.0,10.0)", key)
        }
    }

    @Test
    fun testToString() {
        DefaultLongImageDecider().apply {
            Assert.assertEquals("DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0)", toString())
        }
        DefaultLongImageDecider(4f, 10f).apply {
            Assert.assertEquals("DefaultLongImageDecider(smallRatioMultiple=4.0, bigRatioMultiple=10.0)", toString())
        }
    }
}