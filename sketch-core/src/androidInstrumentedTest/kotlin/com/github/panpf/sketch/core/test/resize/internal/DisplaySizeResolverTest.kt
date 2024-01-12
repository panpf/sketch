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
package com.github.panpf.sketch.core.test.resize.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplaySizeResolverTest {

    @Test
    fun testSize() {
        val context = getTestContext()
        DisplaySizeResolver(context).apply {
            Assert.assertEquals(
                Size(
                    context.resources.displayMetrics!!.widthPixels,
                    context.resources.displayMetrics.heightPixels
                ),
                runBlocking { size() }
            )
        }
    }

    @Test
    fun testEquals() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val resolver1 = DisplaySizeResolver(context)
        val resolver11 = DisplaySizeResolver(context)
        val resolver2 = DisplaySizeResolver(targetContext)

        Assert.assertEquals(resolver1, resolver1)
        Assert.assertEquals(resolver1, resolver11)
        Assert.assertNotEquals(resolver1, resolver2)
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        DisplaySizeResolver(context).apply {
            Assert.assertEquals("DisplaySizeResolver(${context})", toString())
        }
    }
}