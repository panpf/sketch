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
package com.github.panpf.sketch.core.android.test.cache.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.KeyMapperCache
import com.github.panpf.sketch.util.sha256String
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyMapperCacheTest {

    @Test
    fun testMaxSize() {
        KeyMapperCache(5) {
            it.sha256String()
        }.apply {
            Assert.assertEquals(5, maxSize)
        }

        KeyMapperCache(15) {
            it.sha256String()
        }.apply {
            Assert.assertEquals(15, maxSize)
        }
    }

    @Test
    fun testMapper() {
        KeyMapperCache(5) {
            it.sha256String()
        }.apply {
            Assert.assertEquals("image1".sha256String(), mapper("image1"))
            Assert.assertEquals("image2".sha256String(), mapper("image2"))
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
            it.sha256String()
        }.apply {
            Assert.assertEquals("image1".sha256String(), mapKey("image1"))
            Assert.assertEquals("image2".sha256String(), mapKey("image2"))
        }
    }
}