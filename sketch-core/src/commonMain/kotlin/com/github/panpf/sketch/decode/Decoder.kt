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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Key

/**
 * Decode Image from [DataSource].
 */
fun interface Decoder {

    /**
     * Decode Image from [DataSource] and wrap it as a [DecodeResult] return.
     */
    @WorkerThread
    suspend fun decode(): Result<DecodeResult>

    /**
     * [Factory] will be registered in [ComponentRegistry], and will traverse [Factory]
     * to create [Decoder] when it needs decode Image
     *
     * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
     * that is, the equals() and hashCode() methods of instances created with the same
     * construction parameters return consistent results. This is important in Compose
     */
    interface Factory : Key {

        override val key: String

        /**
         * If the current [Factory]'s [Decoder] can decode Image from the current [fetchResult],
         * create a [Decoder] and return it, otherwise return null
         */
        fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): Decoder?
    }
}