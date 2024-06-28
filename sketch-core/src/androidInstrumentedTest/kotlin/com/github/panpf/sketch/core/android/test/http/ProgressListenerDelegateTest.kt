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
package com.github.panpf.sketch.core.android.test.http

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
import com.github.panpf.sketch.images.ResourceImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressListenerDelegateTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val scope = CoroutineScope(SupervisorJob())
        val completedList = mutableListOf<Long>()
        val listener = ProgressListener { _, progress ->
            Thread.sleep(100)
            completedList.add(progress.completedLength)
        }
        val delegate = ProgressListenerDelegate(scope, listener)

        delegate.onUpdateProgress(request, 1000, 200)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 400)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 600)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 800)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 1000)
        Thread.sleep(150)
        Assert.assertTrue(completedList.size < 5)
    }
}