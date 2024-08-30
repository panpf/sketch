/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.indexOf
import com.github.panpf.sketch.util.rangeEquals
import okio.ArrayIndexOutOfBoundsException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByteArraysTest {

    private val bytes = "abcdefghijklmnopqrstuvwxyz".encodeToByteArray()

    @Test
    fun testRangeEquals() {
        bytes.apply {
            assertFailsWith(IllegalArgumentException::class) {
                rangeEquals(0, byteArrayOf())
            }
            assertTrue(rangeEquals(0, "abc".encodeToByteArray()))
            assertTrue(rangeEquals(1, "bcd".encodeToByteArray()))
            assertTrue(rangeEquals(20, "uvw".encodeToByteArray()))
            assertTrue(rangeEquals(23, "xyz".encodeToByteArray()))
            assertFalse(rangeEquals(0, "abd".encodeToByteArray()))
            assertFalse(rangeEquals(1, "bdc".encodeToByteArray()))
            assertFalse(rangeEquals(20, "uwf".encodeToByteArray()))
            assertFalse(rangeEquals(26, "xyz".encodeToByteArray()))
        }
    }

    @Test
    fun testIndexOf() {
        bytes.apply {
            assertEquals(0, indexOf('a'.code.toByte(), 0, 26))
            assertEquals(0, indexOf('a'.code.toByte(), 0, 1))
            assertEquals(12, indexOf('m'.code.toByte(), 0, 26))
            assertEquals(25, indexOf('z'.code.toByte(), 0, 26))
            assertEquals(-1, indexOf('a'.code.toByte(), 0, 0))
            assertEquals(-1, indexOf('a'.code.toByte(), 1, 26))
            assertEquals(-1, indexOf('m'.code.toByte(), 13, 26))
            assertEquals(-1, indexOf('z'.code.toByte(), 23, 23))
            assertEquals(-1, indexOf('z'.code.toByte(), 26, 27))
            assertFailsWith(IllegalArgumentException::class) {
                indexOf('z'.code.toByte(), -1, 26)
            }
            assertFailsWith(IllegalArgumentException::class) {
                indexOf('z'.code.toByte(), 27, 26)
            }

            assertEquals(0, indexOf("abc".encodeToByteArray(), 0, 26))
            assertEquals(12, indexOf("mno".encodeToByteArray(), 0, 26))
            assertEquals(23, indexOf("xyz".encodeToByteArray(), 0, 26))
            assertEquals(-1, indexOf("abc".encodeToByteArray(), 10, 26))
            assertEquals(-1, indexOf("mno".encodeToByteArray(), 15, 26))
            assertEquals(-1, indexOf("mno".encodeToByteArray(), 15, 26))
            assertEquals(-1, indexOf("xyz".encodeToByteArray(), 26, 26))
            assertEquals(-1, indexOf("acc".encodeToByteArray(), 0, 26))
            assertFailsWith(IllegalArgumentException::class) {
                indexOf("abc".encodeToByteArray(), 27, 26)
            }
            assertFailsWith(IllegalArgumentException::class) {
                indexOf("abc".encodeToByteArray(), -1, 26)
            }
            assertFailsWith(IllegalArgumentException::class) {
                indexOf(byteArrayOf(), 0, 26)
            }
        }
    }

    @Test
    fun testGet() {
        bytes.apply {
            assertEquals('a'.code.toByte(), get(0))
            assertEquals('m'.code.toByte(), get(12))
            assertEquals('z'.code.toByte(), get(25))
            assertFailsWith(ArrayIndexOutOfBoundsException::class) {
                get(26)
            }
        }
    }
}