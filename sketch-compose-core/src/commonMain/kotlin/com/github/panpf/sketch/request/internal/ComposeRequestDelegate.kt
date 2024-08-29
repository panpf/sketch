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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ComposeTarget
import kotlinx.coroutines.Job

/**
 * A request delegate for restartable requests with a [ComposeTarget].
 *
 * @see com.github.panpf.sketch.compose.core.common.test.request.internal.ComposeRequestDelegateTest
 */
class ComposeRequestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    target: ComposeTarget,
    job: Job
) : BaseRequestDelegate(sketch, initialRequest, target, job) {

    override fun assertActive() {
        // AsyncImageState will only execute the request when it is remembered, so there is no need to judge whether it has been remembered.
    }

    override fun finish() {
        // Monitoring of Lifecycle cannot be removed here.
        // Because GenericComposeTarget needs to stop or start animation by listening to Lifecycle Image
    }
}