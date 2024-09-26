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
import com.github.panpf.sketch.util.getTrimLevelName
import com.github.panpf.sketch.util.isMainThread
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CoreUtilsAndroidTest {

    @Test
    fun testIsMainThread() = runTest {
        withContext(Dispatchers.IO) {
            assertFalse(isMainThread())
        }
        withContext(Dispatchers.Main) {
            assertTrue(isMainThread())
        }
    }

    @Test
    fun testRequiredMainThread() = runTest {
        withContext(Dispatchers.IO) {
            assertFailsWith(IllegalStateException::class) {
                requiredMainThread()
            }
        }
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testRequiredWorkThread() = runTest {
        withContext(Dispatchers.IO) {
            requiredWorkThread()
        }
        withContext(Dispatchers.Main) {
            assertFailsWith(IllegalStateException::class) {
                requiredWorkThread()
            }
        }
    }

    @Test
    fun testGetMimeTypeFromUrl() {
        assertEquals("image/jpeg", getMimeTypeFromUrl("http://sample.com/sample.jpeg"))
        assertEquals(
            "image/png",
            getMimeTypeFromUrl("http://sample.com/sample.png#path?name=david")
        )
    }

    @Test
    fun testGetTrimLevelName() {
        assertEquals("COMPLETE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_COMPLETE))
        assertEquals("MODERATE", getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_MODERATE))
        assertEquals(
            "BACKGROUND",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
        )
        assertEquals(
            "UI_HIDDEN",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
        )
        assertEquals(
            "RUNNING_CRITICAL",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL)
        )
        assertEquals(
            "RUNNING_LOW",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
        )
        assertEquals(
            "RUNNING_MODERATE",
            getTrimLevelName(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE)
        )
        assertEquals("UNKNOWN", getTrimLevelName(34))
        assertEquals("UNKNOWN", getTrimLevelName(-1))
    }

    @Test
    fun testFileNameCompatibilityMultiProcess() {
        // TODO testFileNameCompatibilityMultiProcess. Not testable: Because the test method cannot be run on a non-main thread
    }

    @Test
    fun testGetProcessNameCompat() {
        // TODO testGetProcessNameCompat. Not testable: Because the test method cannot be run on a non-main thread
    }

    @Test
    fun testGetProcessNameSuffix() {
        // TODO testGetProcessNameSuffix. Not testable: Because the test method cannot be run on a non-main thread
    }
}