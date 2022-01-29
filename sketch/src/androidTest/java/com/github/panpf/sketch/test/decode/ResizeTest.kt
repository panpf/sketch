package com.github.panpf.sketch.test.decode

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.Resize.Precision.EXACTLY
import com.github.panpf.sketch.decode.Resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.decode.Resize.Scale.CENTER_CROP
import com.github.panpf.sketch.decode.Resize.Scale.END_CROP
import com.github.panpf.sketch.decode.Resize.Scale.FILL
import com.github.panpf.sketch.decode.Resize.Scale.START_CROP
import com.github.panpf.sketch.decode.Resize.Scope.All
import com.github.panpf.sketch.decode.Resize.Scope.OnlyLongImage
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeTest {

    @Test
    fun testConstructor() {
        Resize(100, 30).apply {
            Assert.assertEquals(100, width)
            Assert.assertEquals(30, height)
        }
        Resize(10, 20).apply {
            Assert.assertEquals(10, width)
            Assert.assertEquals(20, height)
        }

        Resize(100, 30).apply {
            Assert.assertEquals(KEEP_ASPECT_RATIO, precision)
        }
        Resize(100, 30, precision = EXACTLY).apply {
            Assert.assertEquals(EXACTLY, precision)
        }

        Resize(100, 100).apply {
            Assert.assertEquals(CENTER_CROP, scale)
        }
        Resize(100, 100, scale = START_CROP).apply {
            Assert.assertEquals(START_CROP, scale)
        }
        Resize(100, 100, scale = END_CROP).apply {
            Assert.assertEquals(END_CROP, scale)
        }
        Resize(100, 100, scale = FILL).apply {
            Assert.assertEquals(FILL, scale)
        }

        Resize(100, 100).apply {
            Assert.assertEquals(Resize.Scope.All, scope)
        }
        Resize(100, 100, scope = OnlyLongImage()).apply {
            Assert.assertEquals(OnlyLongImage(), scope)
        }
        Resize(100, 100, scope = OnlyLongImage(2f)).apply {
            Assert.assertEquals(OnlyLongImage(2f), scope)
        }
    }

    @Test
    fun testCacheKey() {
        Resize(100, 100).apply {
            Assert.assertEquals("Resize(100x100,All,CENTER_CROP,KEEP_ASPECT_RATIO)", cacheKey)
        }
        Resize(100, 100, scope = OnlyLongImage(), scale = FILL, precision = EXACTLY).apply {
            Assert.assertEquals(
                "Resize(100x100,OnlyLongImage(1.5),FILL,EXACTLY)",
                cacheKey
            )
        }
    }

    @Test
    fun testShouldUse() {
        Resize(100, 100).apply {
            Assert.assertTrue(shouldUse(100, 50))
            Assert.assertTrue(shouldUse(100, 150))
            Assert.assertTrue(shouldUse(50, 100))
            Assert.assertTrue(shouldUse(150, 100))
            Assert.assertFalse(shouldUse(100, 100))
            Assert.assertFalse(shouldUse(50, 50))
            Assert.assertFalse(shouldUse(150, 150))
        }

        Resize(100, 100, precision = EXACTLY).apply {
            Assert.assertTrue(shouldUse(100, 50))
            Assert.assertTrue(shouldUse(100, 150))
            Assert.assertTrue(shouldUse(50, 100))
            Assert.assertTrue(shouldUse(150, 100))
            Assert.assertFalse(shouldUse(100, 100))
            Assert.assertTrue(shouldUse(50, 50))
            Assert.assertTrue(shouldUse(150, 150))
        }

        Resize(100, 100, scope = OnlyLongImage()).apply {
            Assert.assertTrue(shouldUse(100, 50))
            Assert.assertFalse(shouldUse(100, 150))
            Assert.assertTrue(shouldUse(50, 100))
            Assert.assertFalse(shouldUse(150, 100))
            Assert.assertFalse(shouldUse(100, 100))
            Assert.assertFalse(shouldUse(50, 50))
            Assert.assertFalse(shouldUse(150, 150))
        }
    }

    @Test
    fun testScope() {
        All.apply {
            Assert.assertTrue(accept(100, 200, 50, 50))
            Assert.assertTrue(accept(700, 200, 50, 50))
            Assert.assertTrue(accept(700, 900, 50, 10))
            Assert.assertTrue(accept(700, 900, 10, 10))
        }

        OnlyLongImage().apply {
            Assert.assertTrue(accept(100, 200, 50, 50))
            Assert.assertTrue(accept(700, 200, 50, 50))
            Assert.assertTrue(accept(700, 900, 50, 10))
            Assert.assertFalse(accept(700, 900, 10, 10))
        }
    }
}