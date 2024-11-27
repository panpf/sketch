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

package com.github.panpf.sketch.target.internal

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.LoadState.Canceled
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.util.toHexString

/**
 * [AsyncImage] listener
 *
 * @see com.github.panpf.sketch.compose.core.common.test.target.internal.AsyncImageListenerTest
 */
class AsyncImageListener : Listener, ProgressListener {

    private val loadStateMutableState: MutableState<LoadState?> = mutableStateOf(null)
    private val resultMutableState: MutableState<ImageResult?> = mutableStateOf(null)
    private val progressMutableState: MutableState<Progress?> = mutableStateOf(null)

    val loadStateState: State<LoadState?> = loadStateMutableState
    val resultState: State<ImageResult?> = resultMutableState
    val progressState: State<Progress?> = progressMutableState

    var onLoadState: ((LoadState) -> Unit)? = null

    override fun onStart(request: ImageRequest) {
        resultMutableState.value = null
        progressMutableState.value = null
        val startState = LoadState.Started(request)
        loadStateMutableState.value = startState
        onLoadState?.invoke(startState)
    }

    override fun onSuccess(request: ImageRequest, result: Success) {
        resultMutableState.value = result
        val successState = LoadState.Success(request, result)
        loadStateMutableState.value = successState
        onLoadState?.invoke(successState)
    }

    override fun onError(request: ImageRequest, error: Error) {
        resultMutableState.value = error
        val errorState = LoadState.Error(request, error)
        loadStateMutableState.value = errorState
        onLoadState?.invoke(errorState)
    }

    override fun onCancel(request: ImageRequest) {
        val cancelState = Canceled(request)
        loadStateMutableState.value = cancelState
        onLoadState?.invoke(cancelState)
    }

    override fun onUpdateProgress(request: ImageRequest, progress: Progress) {
        progressMutableState.value = progress
    }

    override fun equals(other: Any?): Boolean = super.equals(other)

    override fun hashCode(): Int = super.hashCode()

    override fun toString(): String = "AsyncImageListener@${this.toHexString()}"
}