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

    fun onUpdateProgress(request: ImageRequest, totalLength: Long, completedLength: Long) {
        val lastJob = this.lastJob
        if (lastJob?.isActive == true) {
            lastJob.cancel()
        }
        this.lastJob = coroutineScope.launch(Dispatchers.Main) {
            progressListener.onUpdateProgress(request, Progress(totalLength, completedLength))
        }
    }
}