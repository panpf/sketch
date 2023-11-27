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
package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ProgressListener

class DisplayProgressListenerSupervisor(
    private val name: String? = null,
    private val onUpdateProgress: (() -> Unit)? = null
) : ProgressListener<DisplayRequest> {

    val callbackActionList = mutableListOf<String>()

    override fun onUpdateProgress(
        request: DisplayRequest,
        totalLength: Long,
        completedLength: Long
    ) {
        callbackActionList.add(completedLength.toString() + (name?.let { ":$it" } ?: ""))
        onUpdateProgress?.invoke()
    }
}