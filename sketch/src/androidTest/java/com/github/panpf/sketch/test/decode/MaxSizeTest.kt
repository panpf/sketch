package com.github.panpf.sketch.test.decode

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.MaxSize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaxSizeTest {
    @Test
    fun testConstructor() {
        MaxSize(100, 40).apply {
            Assert.assertEquals(100, width)
            Assert.assertEquals(40, height)
        }

        MaxSize(500, 440).apply {
            Assert.assertEquals(500, width)
            Assert.assertEquals(440, height)
        }
    }

    @Test
    fun testCacheKey() {
        MaxSize(100, 40).apply {
            Assert.assertEquals("MaxSize(100x40)", cacheKey)
        }
    }
}