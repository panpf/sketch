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
package com.github.panpf.sketch.test.singleton.request

import androidx.annotation.AnyThread
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.test.singleton.sketch


/**
 * Execute current LoadRequest asynchronously.
 *
 * Note: The request will not start executing until Lifecycle state is STARTED
 * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
 *
 * @return A [Disposable] which can be used to cancel or check the status of the request.
 */
@AnyThread
fun LoadRequest.enqueue(sketch: Sketch = context.sketch): Disposable<LoadResult> {
    return sketch.enqueue(this)
}

/**
 * Execute current LoadRequest synchronously in the current coroutine scope.
 *
 * Note: The request will not start executing until Lifecycle state is STARTED
 * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
 *
 * @return A [LoadResult.Success] if the request completes successfully. Else, returns an [LoadResult.Error].
 */
suspend fun LoadRequest.execute(sketch: Sketch = context.sketch): LoadResult {
    return sketch.execute(this)
}