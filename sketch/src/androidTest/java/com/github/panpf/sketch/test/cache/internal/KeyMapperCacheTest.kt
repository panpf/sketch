package com.github.panpf.sketch.test.cache.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.KeyMapperCache
import com.github.panpf.sketch.util.md5
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyMapperCacheTest {

    @Test
    fun testMaxSize() {
        KeyMapperCache(5) {
            md5(it)
        }.apply {
            Assert.assertEquals(5, maxSize)
        }

        KeyMapperCache(15) {
            md5(it)
        }.apply {
            Assert.assertEquals(15, maxSize)
        }
    }

    @Test
    fun testMapper() {
        KeyMapperCache(5) {
            md5(it)
        }.apply {
            Assert.assertEquals(md5("image1"), mapper("image1"))
            Assert.assertEquals(md5("image2"), mapper("image2"))
        }

        KeyMapperCache(5) {
            it
        }.apply {
            Assert.assertEquals("image1", mapper("image1"))
            Assert.assertEquals("image2", mapper("image2"))
        }
    }

    @Test
    fun testMapKey() {
        KeyMapperCache(5) {
            md5(it)
        }.apply {
            Assert.assertEquals(md5("image1"), mapKey("image1"))
            Assert.assertEquals(md5("image2"), mapKey("image2"))
        }
    }
}