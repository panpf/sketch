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

package com.github.panpf.sketch.extensions.core.android.test.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.internal.format
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ExtensionsCoreAndroidUtilsTest {

    @Test
    fun testFormat() {
        assertEquals(1.412f, 1.412412f.format(3))
        assertEquals(1.41f, 1.412412f.format(2))
        assertEquals(1.4f, 1.412412f.format(1))
        assertEquals(1f, 1.412412f.format(0))
    }

    @Test
    fun testDp2Px() {
        // TODO test
    }

    @Test
    fun testVersionCodeCompat() {
        // TODO test
    }
}