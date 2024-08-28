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

package com.github.panpf.sketch.core.android.test.util

import android.content.ComponentCallbacks2
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.MimeTypeMap.getMimeTypeFromUrl
import com.github.panpf.sketch.util.computeScaleMultiplierWithFit
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.getTrimLevelName
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.intMerged
import com.github.panpf.sketch.util.intSplit
import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    @Test
    fun testIfOrNull() {
        Assert.assertEquals("yes", ifOrNull(true) { "yes" })
        Assert.assertEquals(null, ifOrNull(false) { "yes" })
    }

    @Test
    fun testIsMainThread() {
        Assert.assertFalse(isMainThread())
        Assert.assertTrue(runBlocking(Dispatchers.Main) {
            isMainThread()
        })
    }

    @Test
    fun testRequiredMainThread() {
        assertThrow(IllegalStateException::class) {
            requiredMainThread()
        }
        runBlocking(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testRequiredWorkThread() {
        requiredWorkThread()

        assertThrow(IllegalStateException::class) {
            runBlocking(Dispatchers.Main) {
                requiredWorkThread()
            }
        }
    }

    @Test
    fun testGetMimeTypeFromUrl() {
        Assert.assertEquals("image/jpeg", getMimeTypeFromUrl("http://sample.com/sample.jpeg"))
        Assert.assertEquals(
            "image/png",
            getMimeTypeFromUrl("http://sample.com/sample.png#path?name=david")
        )
    }

    @Test
    fun testGetTrimLevelName() {
        Assert.assertEquals("COMPLETE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_COMPLETE))
        Assert.assertEquals("MODERATE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_MODERATE))
        Assert.assertEquals(
            "BACKGROUND",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
        )
        Assert.assertEquals(
            "UI_HIDDEN",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        )
        Assert.assertEquals(
            "RUNNING_CRITICAL",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL)
        )
        Assert.assertEquals(
            "RUNNING_LOW",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
        )
        Assert.assertEquals(
            "RUNNING_MODERATE",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE)
        )
        Assert.assertEquals("UNKNOWN", getTrimLevelName(34))
        Assert.assertEquals("UNKNOWN", getTrimLevelName(-1))
    }

    @Test
    fun testIntMergedAndIntSplit() {
        intSplit(intMerged(39, 25)).apply {
            Assert.assertEquals(39, first)
            Assert.assertEquals(25, second)
        }
        intSplit(intMerged(7, 43)).apply {
            Assert.assertEquals(7, first)
            Assert.assertEquals(43, second)
        }

        assertThrow(IllegalArgumentException::class) {
            intMerged(-1, 25)
        }
        assertThrow(IllegalArgumentException::class) {
            intMerged(Short.MAX_VALUE + 1, 25)
        }
        assertThrow(IllegalArgumentException::class) {
            intMerged(25, -1)
        }
        assertThrow(IllegalArgumentException::class) {
            intMerged(25, Short.MAX_VALUE + 1)
        }
    }

    // TODO Test calculateBounds

    @Test
    fun testComputeScaleMultiplierWithFit() {
        Assert.assertEquals(0.2, computeScaleMultiplierWithFit(1000, 600, 200, 400, true), 0.1)
        Assert.assertEquals(0.6, computeScaleMultiplierWithFit(1000, 600, 200, 400, false), 0.1)
        Assert.assertEquals(0.3, computeScaleMultiplierWithFit(1000, 600, 400, 200, true), 0.1)
        Assert.assertEquals(0.4, computeScaleMultiplierWithFit(1000, 600, 400, 200, false), 0.1)

        Assert.assertEquals(0.6, computeScaleMultiplierWithFit(1000, 600, 2000, 400, true), 0.1)
        Assert.assertEquals(2.0, computeScaleMultiplierWithFit(1000, 600, 2000, 400, false), 0.1)
        Assert.assertEquals(0.4, computeScaleMultiplierWithFit(1000, 600, 400, 2000, true), 0.1)
        Assert.assertEquals(3.3, computeScaleMultiplierWithFit(1000, 600, 400, 2000, false), 0.1)

        Assert.assertEquals(2.0, computeScaleMultiplierWithFit(1000, 600, 2000, 4000, true), 0.1)
        Assert.assertEquals(6.6, computeScaleMultiplierWithFit(1000, 600, 2000, 4000, false), 0.1)
        Assert.assertEquals(3.3, computeScaleMultiplierWithFit(1000, 600, 4000, 2000, true), 0.1)
        Assert.assertEquals(4.0, computeScaleMultiplierWithFit(1000, 600, 4000, 2000, false), 0.1)
    }

    // TODO computeScaleMultiplierWithOneSide

    @Test
    fun testFloatFormat() {
        listOf(
            FormatItem(number = 6.2517f, newScale = 3, expected = 6.252f),
            FormatItem(number = 6.2517f, newScale = 2, expected = 6.25f),
            FormatItem(number = 6.2517f, newScale = 1, expected = 6.3f),
            FormatItem(number = 6.251f, newScale = 2, expected = 6.25f),
            FormatItem(number = 6.251f, newScale = 1, expected = 6.3f),

            FormatItem(number = 0.6253f, newScale = 3, expected = 0.625f),
            FormatItem(number = 0.6253f, newScale = 2, expected = 0.63f),
            FormatItem(number = 0.6253f, newScale = 1, expected = 0.6f),
            FormatItem(number = 0.625f, newScale = 2, expected = 0.62f),
            FormatItem(number = 0.625f, newScale = 1, expected = 0.6f),
        ).forEach {
            assertEquals(
                expected = it.expected,
                actual = it.number.format(it.newScale),
                absoluteTolerance = 0f,
                message = "format. number=${it.number}, newScale=${it.newScale}"
            )
        }
    }

    @Test
    fun testDoubleFormat() {
        listOf(
            FormatItem(number = 6.2517, newScale = 3, expected = 6.252),
            FormatItem(number = 6.2517, newScale = 2, expected = 6.25),
            FormatItem(number = 6.2517, newScale = 1, expected = 6.3),
            FormatItem(number = 6.251, newScale = 2, expected = 6.25),
            FormatItem(number = 6.251, newScale = 1, expected = 6.3),

            FormatItem(number = 0.6253, newScale = 3, expected = 0.625),
            FormatItem(number = 0.6253, newScale = 2, expected = 0.63),
            FormatItem(number = 0.6253, newScale = 1, expected = 0.6),
            FormatItem(number = 0.625, newScale = 2, expected = 0.62),
            FormatItem(number = 0.625, newScale = 1, expected = 0.6),
        ).forEach {
            assertEquals(
                expected = it.expected,
                actual = it.number.format(it.newScale),
                absoluteTolerance = 0.0,
                message = "format. number=${it.number}, newScale=${it.newScale}"
            )
        }
    }

    class FormatItem<T>(val number: T, val newScale: Int, val expected: T)
}