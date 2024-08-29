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

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.createImage
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals

class ListenersTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")

        val list = listOf(
            ListenerSupervisor("2"),
            ListenerSupervisor("3"),
            ListenerSupervisor("1"),
        )
        assertEquals(listOf(), list.flatMap { it.callbackActionList })

        val listeners = Listeners(*list.toTypedArray())
        assertEquals(list, listeners.list)

        withContext(Dispatchers.Main) {
            listeners.onStart(request)
        }
        assertEquals(
            listOf("onStart:2", "onStart:3", "onStart:1"),
            list.flatMap { it.callbackActionList })

        withContext(Dispatchers.Main) {
            listeners.onCancel(request)
        }
        assertEquals(
            listOf(
                "onStart:2",
                "onCancel:2",
                "onStart:3",
                "onCancel:3",
                "onStart:1",
                "onCancel:1",
            ), list.flatMap { it.callbackActionList })

        withContext(Dispatchers.Main) {
            listeners.onError(request, ImageResult.Error(request, null, Exception("")))
        }
        assertEquals(
            listOf(
                "onStart:2",
                "onCancel:2",
                "onError:2",
                "onStart:3",
                "onCancel:3",
                "onError:3",
                "onStart:1",
                "onCancel:1",
                "onError:1",
            ),
            list.flatMap { it.callbackActionList })

        withContext(Dispatchers.Main) {
            listeners.onSuccess(
                request,
                ImageResult.Success(
                    request = request,
                    image = createImage(100, 100),
                    cacheKey = "",
                    imageInfo = ImageInfo(100, 100, "image/jpeg"),
                    dataFrom = MEMORY,
                    resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                    transformeds = null,
                    extras = null
                )
            )
        }
        assertEquals(
            listOf(
                "onStart:2",
                "onCancel:2",
                "onError:2",
                "onSuccess:2",
                "onStart:3",
                "onCancel:3",
                "onError:3",
                "onSuccess:3",
                "onStart:1",
                "onCancel:1",
                "onError:1",
                "onSuccess:1",
            ),
            list.flatMap { it.callbackActionList })
    }
}