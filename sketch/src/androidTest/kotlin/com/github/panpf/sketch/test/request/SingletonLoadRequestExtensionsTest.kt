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
package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.enqueue
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.getTestContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingletonLoadRequestExtensionsTest {

    @Test
    fun testExecuteAndEnqueue() {
        val context = getTestContext()

        LoadRequest(context, AssetImages.jpeg.uri).let { request ->
            runBlocking { request.execute() }
        }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }

        LoadRequest(context, AssetImages.jpeg.uri).let { request ->
            runBlocking { request.enqueue().job.await() }
        }.apply {
            Assert.assertTrue(this is LoadResult.Success)
        }
    }
}