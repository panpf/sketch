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

package com.github.panpf.sketch.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.darwin.DISPATCH_MEMORYPRESSURE_CRITICAL
import platform.darwin.DISPATCH_MEMORYPRESSURE_NORMAL
import platform.darwin.DISPATCH_MEMORYPRESSURE_WARN
import platform.darwin.DISPATCH_SOURCE_TYPE_MEMORYPRESSURE
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_resume
import platform.darwin.dispatch_source_cancel
import platform.darwin.dispatch_source_create
import platform.darwin.dispatch_source_get_data
import platform.darwin.dispatch_source_set_event_handler

@OptIn(ExperimentalForeignApi::class)
internal class IosMemoryPressureObserver() {

    enum class MemoryPressure {
        CRITICAL, WARN, NORMAL
    }

    val flow: Flow<MemoryPressure> = callbackFlow {
        val queue = dispatch_get_main_queue()
        val memoryPressureMask = DISPATCH_MEMORYPRESSURE_NORMAL.toULong() or
                DISPATCH_MEMORYPRESSURE_WARN.toULong() or
                DISPATCH_MEMORYPRESSURE_CRITICAL.toULong()
        val source = dispatch_source_create(
            type = DISPATCH_SOURCE_TYPE_MEMORYPRESSURE,
            handle = 0u,
            mask = memoryPressureMask,
            queue = queue
        )
        dispatch_source_set_event_handler(source) {
            val data = dispatch_source_get_data(source).toLong()
            val memoryPressure = when {
                (data.toUInt() and DISPATCH_MEMORYPRESSURE_CRITICAL.toUInt()) != 0u -> MemoryPressure.CRITICAL
                (data.toUInt() and DISPATCH_MEMORYPRESSURE_WARN.toUInt()) != 0u -> MemoryPressure.WARN
                else -> MemoryPressure.NORMAL
            }
            trySend(memoryPressure)
        }
        dispatch_resume(source)

        awaitClose {
            dispatch_source_cancel(source)
        }
    }
}
