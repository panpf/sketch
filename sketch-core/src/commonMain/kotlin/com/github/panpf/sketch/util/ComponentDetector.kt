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

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpStack

expect object ComponentDetector {

    val fetchers: List<FetcherComponent>

    val decoders: List<DecoderComponent>

    val httpStacks: List<HttpStackComponent>

    // Only available on non-JVM. Added these declarations to work-around a compiler bug.
    fun register(fetcher: FetcherComponent)

    // Only available on non-JVM. Added these declarations to work-around a compiler bug.
    fun register(decoder: DecoderComponent)

    // Only available on non-JVM. Added these declarations to work-around a compiler bug.
    fun register(httpStack: HttpStackComponent)
}

expect interface FetcherComponent {
    fun factory(): Fetcher.Factory?
}

expect interface DecoderComponent {
    fun factory(): Decoder.Factory?
}

expect interface HttpStackComponent {
    fun httpStack(context: PlatformContext): HttpStack
}