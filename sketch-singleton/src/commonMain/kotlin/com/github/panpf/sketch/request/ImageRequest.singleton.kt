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

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch


/**
 * Execute current ImageRequest asynchronously.
 *
 * Note: The request will not start executing until Lifecycle state is STARTED
 * reaches [Lifecycle.State.STARTED] state and View is attached to window
 *
 * @return A [Disposable] which can be used to cancel or check the status of the request.
 *
 * @see com.github.panpf.sketch.singleton.common.test.request.ImageRequestSingletonTest.testEnqueue
 */
fun ImageRequest.enqueue(sketch: Sketch = SingletonSketch.get(context)): Disposable {
    return sketch.enqueue(this)
}

/**
 * Execute current ImageRequest synchronously in the current coroutine scope.
 *
 * Note: The request will not start executing until Lifecycle state is STARTED
 * reaches [Lifecycle.State.STARTED] state and View is attached to window
 *
 * @return A [ImageResult.Success] if the request completes successfully. Else, returns an [ImageResult.Error].
 *
 * @see com.github.panpf.sketch.singleton.common.test.request.ImageRequestSingletonTest.testExecute
 */
suspend fun ImageRequest.execute(sketch: Sketch = SingletonSketch.get(context)): ImageResult {
    return sketch.execute(this)
}