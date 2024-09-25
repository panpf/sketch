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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestManager
import kotlinx.coroutines.Deferred

/**
 * Represents the work of an *Request that has been executed by an [Sketch].
 *
 * @see com.github.panpf.sketch.core.common.test.request.DisposableTest.testReusableDisposable
 * @see com.github.panpf.sketch.core.common.test.request.DisposableTest.testOneShotDisposable
 */
interface Disposable {

    /**
     * The most recent image request job.
     * This field is **not immutable** and can change if the request is replayed.
     */
    val job: Deferred<ImageResult>

    /**
     * Returns 'true' if this disposable's work is complete or cancelling.
     */
    val isDisposed: Boolean

    /**
     * Cancels this disposable's work and releases any held resources.
     */
    fun dispose()
}

/**
 * A disposable for reusable image requests.
 *
 * @see com.github.panpf.sketch.core.common.test.request.DisposableTest.testReusableDisposable
 */
class ReusableDisposable(
    val requestManager: RequestManager,
    override var job: Deferred<ImageResult>
) : Disposable {

    override val isDisposed: Boolean
        get() = requestManager.isDisposed(this)

    override fun dispose() {
        if (!isDisposed) {
            requestManager.dispose()
        }
    }
}

/**
 * A disposable for one-shot image requests.
 *
 * @see com.github.panpf.sketch.core.common.test.request.DisposableTest.testOneShotDisposable
 */
class OneShotDisposable(
    override val job: Deferred<ImageResult>
) : Disposable {

    override val isDisposed: Boolean
        get() = !job.isActive

    override fun dispose() {
        if (!isDisposed) {
            job.cancel()
        }
    }
}