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

/**
 * The current state of the [ImageRequest].
 *
 * @see com.github.panpf.sketch.core.common.test.request.LoadStateTest
 */
sealed interface LoadState {

    /**
     * The [ImageRequest] that this state is for.
     */
    val request: ImageRequest

    /**
     * The request has been started.
     */
    data class Started(override val request: ImageRequest) : LoadState

    /**
     * The request has completed successfully.
     */
    data class Success(
        override val request: ImageRequest,
        val result: ImageResult.Success
    ) : LoadState

    /**
     * The request has failed.
     */
    data class Error(
        override val request: ImageRequest,
        val result: ImageResult.Error
    ) : LoadState

    /**
     * The request has been canceled.
     */
    data class Canceled(override val request: ImageRequest) : LoadState
}

/**
 * Return the name of the [LoadState]
 *
 * @see com.github.panpf.sketch.core.common.test.request.LoadStateTest.testName
 */
val LoadState.name: String
    get() = when (this) {
        is LoadState.Started -> "Started"
        is LoadState.Success -> "Success"
        is LoadState.Error -> "Error"
        is LoadState.Canceled -> "Canceled"
    }