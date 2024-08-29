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

package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ProgressListenerDelegateTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val scope = CoroutineScope(SupervisorJob())
        val completedList = mutableListOf<Long>()
        val listener = ProgressListener { _, progress ->
            block(100)
            completedList.add(progress.completedLength)
        }
        val delegate = ProgressListenerDelegate(scope, listener)

        delegate.onUpdateProgress(request, 1000, 200)
        block(40)
        delegate.onUpdateProgress(request, 1000, 400)
        block(40)
        delegate.onUpdateProgress(request, 1000, 600)
        block(40)
        delegate.onUpdateProgress(request, 1000, 800)
        block(40)
        delegate.onUpdateProgress(request, 1000, 1000)
        block(150)
        assertTrue(completedList.size < 5)
    }
}