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

package com.github.panpf.sketch.request

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Provide the requested loading status, progress status and result status to the outside world
 *
 * @see com.github.panpf.sketch.core.common.test.request.RequestStateTest
 */
class RequestState : Listener, ProgressListener {

    private val _loadState = MutableStateFlow<LoadState?>(null)
    val loadState: StateFlow<LoadState?> = _loadState

    private val _resultState = MutableStateFlow<ImageResult?>(null)
    val resultState: StateFlow<ImageResult?> = _resultState

    private val _progressState = MutableStateFlow<Progress?>(null)
    val progressState: StateFlow<Progress?> = _progressState

    override fun onStart(request: ImageRequest) {
        _resultState.value = null
        _progressState.value = null
        _loadState.value = LoadState.Started(request)
    }

    override fun onSuccess(request: ImageRequest, result: ImageResult.Success) {
        _resultState.value = result
        _loadState.value = LoadState.Success(request, result)
    }

    override fun onError(request: ImageRequest, error: ImageResult.Error) {
        _resultState.value = error
        _loadState.value = LoadState.Error(request, error)
    }

    override fun onCancel(request: ImageRequest) {
        _loadState.value = LoadState.Canceled(request)
    }

    override fun onUpdateProgress(request: ImageRequest, progress: Progress) {
        _progressState.value = progress
    }
}