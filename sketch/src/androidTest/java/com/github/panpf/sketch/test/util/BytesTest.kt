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
package com.github.panpf.sketch.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.Bytes
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BytesTest {

    private val bytes = "abcdefghijklmnopqrstuvwxyz".toByteArray()

    @Test
    fun testRangeEquals() {
        Bytes(bytes).apply {
            assertThrow(IllegalArgumentException::class) {
                rangeEquals(0, byteArrayOf())
            }
            Assert.assertTrue(rangeEquals(0, "abc".toByteArray()))
            Assert.assertTrue(rangeEquals(1, "bcd".toByteArray()))
            Assert.assertTrue(rangeEquals(20, "uvw".toByteArray()))
            Assert.assertTrue(rangeEquals(23, "xyz".toByteArray()))
            Assert.assertFalse(rangeEquals(0, "abd".toByteArray()))
            Assert.assertFalse(rangeEquals(1, "bdc".toByteArray()))
            Assert.assertFalse(rangeEquals(20, "uwf".toByteArray()))
            Assert.assertFalse(rangeEquals(26, "xyz".toByteArray()))
        }
    }

    @Test
    fun testIndexOf() {
        Bytes(bytes).apply {
            Assert.assertEquals(0, indexOf('a'.code.toByte(), 0, 26))
            Assert.assertEquals(0, indexOf('a'.code.toByte(), 0, 1))
            Assert.assertEquals(12, indexOf('m'.code.toByte(), 0, 26))
            Assert.assertEquals(25, indexOf('z'.code.toByte(), 0, 26))
            Assert.assertEquals(-1, indexOf('a'.code.toByte(), 0, 0))
            Assert.assertEquals(-1, indexOf('a'.code.toByte(), 1, 26))
            Assert.assertEquals(-1, indexOf('m'.code.toByte(), 13, 26))
            Assert.assertEquals(-1, indexOf('z'.code.toByte(), 23, 23))
            Assert.assertEquals(-1, indexOf('z'.code.toByte(), 26, 27))
            assertThrow(IllegalArgumentException::class) {
                indexOf('z'.code.toByte(), -1, 26)
            }
            assertThrow(IllegalArgumentException::class) {
                indexOf('z'.code.toByte(), 27, 26)
            }

            Assert.assertEquals(0, indexOf("abc".toByteArray(), 0, 26))
            Assert.assertEquals(12, indexOf("mno".toByteArray(), 0, 26))
            Assert.assertEquals(23, indexOf("xyz".toByteArray(), 0, 26))
            Assert.assertEquals(-1, indexOf("abc".toByteArray(), 10, 26))
            Assert.assertEquals(-1, indexOf("mno".toByteArray(), 15, 26))
            Assert.assertEquals(-1, indexOf("mno".toByteArray(), 15, 26))
            Assert.assertEquals(-1, indexOf("xyz".toByteArray(), 26, 26))
            Assert.assertEquals(-1, indexOf("acc".toByteArray(), 0, 26))
            assertThrow(IllegalArgumentException::class) {
                indexOf("abc".toByteArray(), 27, 26)
            }
            assertThrow(IllegalArgumentException::class) {
                indexOf("abc".toByteArray(), -1, 26)
            }
            assertThrow(IllegalArgumentException::class) {
                indexOf(byteArrayOf(), 0, 26)
            }
        }
    }

    @Test
    fun testGet() {
        Bytes(bytes).apply {
            Assert.assertEquals('a'.code.toByte(), get(0))
            Assert.assertEquals('m'.code.toByte(), get(12))
            Assert.assertEquals('z'.code.toByte(), get(25))
            assertThrow(ArrayIndexOutOfBoundsException::class) {
                get(26)
            }
        }
    }
}