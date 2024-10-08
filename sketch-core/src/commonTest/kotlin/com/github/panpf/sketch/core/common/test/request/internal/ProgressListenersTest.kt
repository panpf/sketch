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

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.internal.ProgressListeners
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressListenersTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")

        val list = listOf(
            ProgressListenerSupervisor("2"),
            ProgressListenerSupervisor("3"),
            ProgressListenerSupervisor("1"),
        )
        assertEquals(listOf(), list.flatMap { it.callbackActionList })

        val listeners = ProgressListeners(*list.toTypedArray())
        assertEquals(list, listeners.list)

        withContext(Dispatchers.Main) {
            listeners.onUpdateProgress(request, Progress(100, 10))
        }
        assertEquals(
            listOf("10:2", "10:3", "10:1"),
            list.flatMap { it.callbackActionList })

        withContext(Dispatchers.Main) {
            listeners.onUpdateProgress(request, Progress(100, 20))
        }
        assertEquals(
            listOf("10:2", "20:2", "10:3", "20:3", "10:1", "20:1"),
            list.flatMap { it.callbackActionList })
    }
}