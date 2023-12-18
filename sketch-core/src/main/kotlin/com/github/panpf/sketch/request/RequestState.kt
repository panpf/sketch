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
package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.LoadState.Canceled
import com.github.panpf.sketch.request.LoadState.Started
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DisplayRequestState :
    Listener<DisplayRequest, Success, Error>,
    ProgressListener<DisplayRequest> {

    private val _loadState = MutableStateFlow<LoadState?>(null)
    val loadState: StateFlow<LoadState?> = _loadState

    private val _resultState = MutableStateFlow<DisplayResult?>(null)
    val resultState: StateFlow<DisplayResult?> = _resultState

    private val _progressState = MutableStateFlow<Progress?>(null)
    val progressState: StateFlow<Progress?> = _progressState

    override fun onStart(request: DisplayRequest) {
        _resultState.value = null
        _progressState.value = null
        _loadState.value = Started(request)
    }

    override fun onSuccess(request: DisplayRequest, result: Success) {
        _resultState.value = result
        _loadState.value = LoadState.Success(request, result)
    }

    override fun onError(request: DisplayRequest, result: Error) {
        _resultState.value = result
        _loadState.value = LoadState.Error(request, result)
    }

    override fun onCancel(request: DisplayRequest) {
        _loadState.value = Canceled(request)
    }

    override fun onUpdateProgress(
        request: DisplayRequest,
        totalLength: Long,
        completedLength: Long
    ) {
        _progressState.value =
            Progress(totalLength = totalLength, completedLength = completedLength)
    }
}