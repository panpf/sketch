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

package com.github.panpf.sketch.core.android.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.OpenGLTextureHelper
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class OpenGLTextureHelperTest {

    @Test
    fun test() {
        val maxSize = OpenGLTextureHelper.maxSize
        assertTrue(
            arrayOf(2048, 4096, 8192, 16384).any { it == maxSize },
            "maxSize=$maxSize"
        )
    }
}