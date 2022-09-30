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
package com.github.panpf.sketch.svg.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.isFromCompose
import com.github.panpf.sketch.request.setFromCompose
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeExtensionsTest {

    @Test
    fun testSvgBackgroundColor() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isFromCompose())
        }.newDisplayRequest {
            setFromCompose(true)
        }.apply {
            Assert.assertTrue(isFromCompose())
        }.newDisplayRequest {
            setFromCompose(false)
        }.apply {
            Assert.assertFalse(isFromCompose())
        }

        ImageOptions().apply {
            Assert.assertFalse(isFromCompose())
        }.newOptions {
            setFromCompose(true)
        }.apply {
            Assert.assertTrue(isFromCompose())
        }.newOptions {
            setFromCompose(false)
        }.apply {
            Assert.assertFalse(isFromCompose())
        }
    }
}