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

import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.LoadState.Canceled
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.target.AsyncImageTarget
import com.github.panpf.sketch.util.toHexString

/**
 * [AsyncImage] listener
 *
 * @see com.github.panpf.sketch.compose.core.common.test.target.internal.AsyncImageListenerTest
 */
class AsyncImageListener(val asyncImageTarget: AsyncImageTarget) : Listener, ProgressListener {

    override fun onStart(request: ImageRequest) {
        val imageState = asyncImageTarget.imageState ?: return

        imageState.resultMutableState.value = null
        imageState.progressMutableState.value = null

        val startState = LoadState.Started(request)
        imageState.onLoadState?.invoke(startState)
        imageState.loadStateMutableState.value = startState
    }

    override fun onSuccess(request: ImageRequest, result: Success) {
        val imageState = asyncImageTarget.imageState ?: return

        imageState.resultMutableState.value = result

        val successState = LoadState.Success(request, result)
        imageState.onLoadState?.invoke(successState)
        imageState.loadStateMutableState.value = successState
    }

    override fun onError(request: ImageRequest, error: Error) {
        val imageState = asyncImageTarget.imageState ?: return

        imageState.resultMutableState.value = error

        val errorState = LoadState.Error(request, error)
        imageState.onLoadState?.invoke(errorState)
        imageState.loadStateMutableState.value = errorState
    }

    override fun onCancel(request: ImageRequest) {
        val imageState = asyncImageTarget.imageState ?: return

        val cancelState = Canceled(request)
        imageState.onLoadState?.invoke(cancelState)
        imageState.loadStateMutableState.value = cancelState
    }

    override fun onUpdateProgress(request: ImageRequest, progress: Progress) {
        val imageState = asyncImageTarget.imageState ?: return

        imageState.progressMutableState.value = progress
    }

    override fun toString(): String = "AsyncImageListener@${this.toHexString()}"
}