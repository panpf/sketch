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

package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

/**
 * [ProgressListener] Delegate, which can be used to prevent the progress from being updated too frequently
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.ProgressListenerDelegateTest
 */
class ProgressListenerDelegate(
    private val coroutineScope: CoroutineScope,
    private val progressListener: ProgressListener
) {

    private var lastJob: Job? = null
    private var lastTimeMark: ValueTimeMark? = null
    private var lastUpdateProgressBytesCopied: Long? = null

    fun callbackProgress(request: ImageRequest, contentLength: Long, completedLength: Long) {
        if (contentLength <= 0) {
            return
        }

        // If the progress has not changed, do not update
        if (lastUpdateProgressBytesCopied != null && lastUpdateProgressBytesCopied == completedLength) {
            return
        }

        // If the time since the last update is less than 300 milliseconds, do not update
        // CompletedLength is the last update when it is greater than or equal to contentLength, and the time interval is not to be considered.
        val inWholeMilliseconds = lastTimeMark?.elapsedNow()?.inWholeMilliseconds
        if (completedLength < contentLength && inWholeMilliseconds != null && inWholeMilliseconds < 300) {
            return
        }

        val lastJob = this.lastJob
        if (lastJob?.isActive == true) {
            lastJob.cancel()
        }

        lastTimeMark = TimeSource.Monotonic.markNow()
        lastUpdateProgressBytesCopied = completedLength
        this.lastJob = coroutineScope.launch(Dispatchers.Main) {
            progressListener.onUpdateProgress(request, Progress(contentLength, completedLength))
        }
    }
}