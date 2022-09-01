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

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Intercept the execution of [Bitmap] decode, you can change the output, register to [ComponentRegistry] to take effect
 */
interface BitmapDecodeInterceptor {

    /**
     * If the current BitmapDecodeInterceptor will change the BitmapDecodeResult,
     * and may only be used for a single [ImageRequest],
     * provide a valid key to build request key and cache key
     */
    val key: String?

    @WorkerThread
    suspend fun intercept(chain: Chain): BitmapDecodeResult

    interface Chain {

        val sketch: Sketch

        val request: ImageRequest

        val requestContext: RequestContext

        val fetchResult: FetchResult?

        /**
         * Continue executing the chain.
         */
        @WorkerThread
        suspend fun proceed(): BitmapDecodeResult
    }
}