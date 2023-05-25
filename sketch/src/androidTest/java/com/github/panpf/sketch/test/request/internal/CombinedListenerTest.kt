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
package com.github.panpf.sketch.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CombinedListenerTest {

    @Test
    fun test() {
        val listenerCallbackList = mutableListOf<String>()
        Assert.assertEquals(listOf<String>(), listenerCallbackList)

        val listener1 =
            object : Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error> {
                override fun onStart(request: DownloadRequest) {
                    super.onStart(request)
                    listenerCallbackList.add("onStart1")
                }

                override fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) {
                    super.onSuccess(request, result)
                    listenerCallbackList.add("onSuccess1")
                }

                override fun onError(request: DownloadRequest, result: DownloadResult.Error) {
                    super.onError(request, result)
                    listenerCallbackList.add("onError1")
                }

                override fun onCancel(request: DownloadRequest) {
                    super.onCancel(request)
                    listenerCallbackList.add("onCancel1")
                }
            }
        val listener2 =
            object : Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error> {
                override fun onStart(request: DownloadRequest) {
                    super.onStart(request)
                    listenerCallbackList.add("onStart2")
                }

                override fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) {
                    super.onSuccess(request, result)
                    listenerCallbackList.add("onSuccess2")
                }

                override fun onError(request: DownloadRequest, result: DownloadResult.Error) {
                    super.onError(request, result)
                    listenerCallbackList.add("onError2")
                }

                override fun onCancel(request: DownloadRequest) {
                    super.onCancel(request)
                    listenerCallbackList.add("onCancel2")
                }
            }

        val context = getTestContext()
        val request = DownloadRequest(context, "http://sample.com/sample.jpeg")

        val combinedListener = CombinedListener(listener1, listener2)
        Assert.assertSame(listener1, combinedListener.fromProviderListener)
        Assert.assertSame(listener2, combinedListener.fromBuilderListener)

        combinedListener.onStart(request)
        Assert.assertEquals(listOf("onStart1", "onStart2"), listenerCallbackList)

        combinedListener.onError(request, DownloadResult.Error(request, Exception("")))
        Assert.assertEquals(
            listOf("onStart1", "onStart2", "onError1", "onError2"),
            listenerCallbackList
        )

        combinedListener.onCancel(request)
        Assert.assertEquals(
            listOf(
                "onStart1",
                "onStart2",
                "onError1",
                "onError2",
                "onCancel1",
                "onCancel2"
            ), listenerCallbackList
        )

        combinedListener.onSuccess(
            request,
            DownloadResult.Success(request, DownloadData(byteArrayOf(), MEMORY))
        )
        Assert.assertEquals(
            listOf(
                "onStart1",
                "onStart2",
                "onError1",
                "onError2",
                "onCancel1",
                "onCancel2",
                "onSuccess1",
                "onSuccess2"
            ), listenerCallbackList
        )
    }
}