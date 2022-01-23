package com.github.panpf.sketch.test.fetch.internal

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.fetch.internal.HeaderBytes
import com.github.panpf.sketch.fetch.internal.isAnimatedHeif
import com.github.panpf.sketch.fetch.internal.isAnimatedWebP
import com.github.panpf.sketch.fetch.internal.isHeif
import com.github.panpf.sketch.fetch.internal.isWebP
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeaderBytesTest {

    val bytes = "abcdefghijklmnopqrstuvwxyz".toByteArray()

    @Test
    fun testRangeEquals() {
        HeaderBytes(bytes).apply {
            Assert.assertTrue(rangeEquals(0, "abc".toByteArray()))
            Assert.assertTrue(rangeEquals(1, "bcd".toByteArray()))
            Assert.assertTrue(rangeEquals(20, "uvw".toByteArray()))
            Assert.assertTrue(rangeEquals(23, "xyz".toByteArray()))
            Assert.assertFalse(rangeEquals(0, "abd".toByteArray()))
            Assert.assertFalse(rangeEquals(1, "bdc".toByteArray()))
            Assert.assertFalse(rangeEquals(20, "uwf".toByteArray()))
        }
    }

    @Test
    fun testIndexOf() {
        HeaderBytes(bytes).apply {
            Assert.assertEquals(0, indexOf('a'.code.toByte(), 0, 26))
            Assert.assertEquals(0, indexOf('a'.code.toByte(), 0, 1))
            Assert.assertEquals(12, indexOf('m'.code.toByte(), 0, 26))
            Assert.assertEquals(25, indexOf('z'.code.toByte(), 0, 26))
            Assert.assertEquals(-1, indexOf('a'.code.toByte(), 0, 0))
            Assert.assertEquals(-1, indexOf('a'.code.toByte(), 1, 26))
            Assert.assertEquals(-1, indexOf('m'.code.toByte(), 13, 26))
            assertThrow(IllegalArgumentException::class) {
                indexOf('z'.code.toByte(), 28, 26)
            }

            Assert.assertEquals(0, indexOf("abc".toByteArray(), 0, 26))
            Assert.assertEquals(12, indexOf("mno".toByteArray(), 0, 26))
            Assert.assertEquals(23, indexOf("xyz".toByteArray(), 0, 26))
            Assert.assertEquals(-1, indexOf("abc".toByteArray(), 10, 26))
            Assert.assertEquals(-1, indexOf("mno".toByteArray(), 15, 26))
            Assert.assertEquals(-1, indexOf("xyz".toByteArray(), 25, 26))
        }
    }

    @Test
    fun testGet() {
        HeaderBytes(bytes).apply {
            Assert.assertEquals('a'.code.toByte(), get(0))
            Assert.assertEquals('m'.code.toByte(), get(12))
            Assert.assertEquals('z'.code.toByte(), get(25))
            assertThrow(ArrayIndexOutOfBoundsException::class) {
                get(26)
            }
        }
    }

    @Test
    fun testIsWebP() {
        val context = InstrumentationRegistry.getContext()

        HeaderBytes(context.assets.open("sample.webp").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isWebP())
        }
        HeaderBytes(context.assets.open("sample_anim.webp").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isWebP())
        }
        HeaderBytes(context.assets.open("sample.jpeg").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isWebP())
        }
    }

    @Test
    fun testIsAnimatedWebP() {
        val context = InstrumentationRegistry.getContext()

        HeaderBytes(context.assets.open("sample_anim.webp").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isAnimatedWebP())
        }
        HeaderBytes(context.assets.open("sample.webp").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }
        HeaderBytes(context.assets.open("sample.jpeg").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }
    }

    @Test
    fun testIsHeif() {
        val context = InstrumentationRegistry.getContext()

        HeaderBytes(context.assets.open("sample.heic").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isHeif())
        }
        HeaderBytes(context.assets.open("sample_anim.webp").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isHeif())
        }
        HeaderBytes(context.assets.open("sample.jpeg").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isHeif())
        }
    }

    @Test
    fun testIsAnimatedHeif() {
        val context = InstrumentationRegistry.getContext()

        HeaderBytes(context.assets.open("sample.heic").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedHeif())
        }
        HeaderBytes(context.assets.open("sample_anim.webp").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedHeif())
        }
        HeaderBytes(context.assets.open("sample.jpeg").use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedHeif())
        }
    }
}