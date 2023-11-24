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
package com.github.panpf.sketch.core.test.cache

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.isReadAndWrite
import com.github.panpf.sketch.cache.isReadOrWrite
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CachePolicyTest {

    @Test
    fun testReadEnabled() {
        Assert.assertTrue(CachePolicy.ENABLED.readEnabled)
        Assert.assertTrue(CachePolicy.READ_ONLY.readEnabled)
        Assert.assertFalse(CachePolicy.WRITE_ONLY.readEnabled)
        Assert.assertFalse(CachePolicy.DISABLED.readEnabled)
    }

    @Test
    fun testWriteEnabled() {
        Assert.assertTrue(CachePolicy.ENABLED.writeEnabled)
        Assert.assertFalse(CachePolicy.READ_ONLY.writeEnabled)
        Assert.assertTrue(CachePolicy.WRITE_ONLY.writeEnabled)
        Assert.assertFalse(CachePolicy.DISABLED.writeEnabled)
    }

    @Test
    fun testIsReadOrWrite() {
        Assert.assertTrue(CachePolicy.ENABLED.isReadOrWrite)
        Assert.assertTrue(CachePolicy.READ_ONLY.isReadOrWrite)
        Assert.assertTrue(CachePolicy.WRITE_ONLY.isReadOrWrite)
        Assert.assertFalse(CachePolicy.DISABLED.isReadOrWrite)
    }

    @Test
    fun testIsReadAndWrite() {
        Assert.assertTrue(CachePolicy.ENABLED.isReadAndWrite)
        Assert.assertFalse(CachePolicy.READ_ONLY.isReadAndWrite)
        Assert.assertFalse(CachePolicy.WRITE_ONLY.isReadAndWrite)
        Assert.assertFalse(CachePolicy.DISABLED.isReadAndWrite)
    }
}