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
package com.github.panpf.sketch.core.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.core.test.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CombinedProgressListenerTest {

    @Test
    fun test() {
        val listenerCallbackList = mutableListOf<String>()
        Assert.assertEquals(listOf<String>(), listenerCallbackList)

        val listener1 = ProgressListener<DownloadRequest> { _, _, _ ->
            listenerCallbackList.add("onUpdateProgress1")
        }
        val listener2 = ProgressListener<DownloadRequest> { _, _, _ ->
            listenerCallbackList.add("onUpdateProgress2")
        }
        val listener3 = ProgressListener<DownloadRequest> { _, _, _ ->
            listenerCallbackList.add("onUpdateProgress3")
        }

        val context = getTestContext()
        val request = DownloadRequest(context, "http://sample.com/sample.jpeg")

        val combinedProgressListener = CombinedProgressListener(
            fromProviderProgressListener = listener1,
            fromBuilderProgressListener = listener2,
            fromBuilderProgressListeners = listOf(listener3)
        )
        Assert.assertSame(listener1, combinedProgressListener.fromProviderProgressListener)
        Assert.assertSame(listener2, combinedProgressListener.fromBuilderProgressListener)
        Assert.assertSame(listener3, combinedProgressListener.fromBuilderProgressListeners!!.first())

        combinedProgressListener.onUpdateProgress(request, 10000, 2000)
        Assert.assertEquals(listOf("onUpdateProgress1", "onUpdateProgress2", "onUpdateProgress3"), listenerCallbackList)
    }
}