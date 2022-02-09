package com.github.panpf.sketch.test.decode.internal

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InSampledTransformedTest {

    @Test
    fun testKey() {
        InSampledTransformed(2).apply {
            Assert.assertEquals("InSampledTransformed(2)", key)
        }
        InSampledTransformed(4).apply {
            Assert.assertEquals("InSampledTransformed(4)", key)
        }
    }

    @Test
    fun testToString() {
        InSampledTransformed(2).apply {
            Assert.assertEquals(key, toString())
        }
        InSampledTransformed(4).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        InSampledTransformed(2).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        InSampledTransformed(4).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(InSampledTransformed(2)).apply {
            Assert.assertNotNull(getInSampledTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getInSampledTransformed())
        }
    }
}