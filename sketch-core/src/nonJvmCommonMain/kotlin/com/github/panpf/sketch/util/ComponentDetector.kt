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
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

actual object ComponentDetector {

    private val lock = SynchronizedObject()
    private val _fetchers = mutableListOf<FetcherComponent>()
    private val _decoders = mutableListOf<DecoderComponent>()
    private val _httpStacks = mutableListOf<HttpStackComponent>()

    actual val fetchers: List<FetcherComponent>
        get() = synchronized(lock) { _fetchers.toImmutableList() }

    actual val decoders: List<DecoderComponent>
        get() = synchronized(lock) { _decoders.toImmutableList() }

    actual val httpStacks: List<HttpStackComponent>
        get() = synchronized(lock) { _httpStacks.toImmutableList() }

    actual fun register(fetcher: FetcherComponent) = synchronized(lock) {
        _fetchers += fetcher
    }

    actual fun register(decoder: DecoderComponent) = synchronized(lock) {
        _decoders += decoder
    }

    actual fun register(httpStack: HttpStackComponent) = synchronized(lock) {
        _httpStacks += httpStack
    }
}

actual interface FetcherComponent {
    actual fun factory(context: PlatformContext): Fetcher.Factory?
}

actual interface DecoderComponent {
    actual fun factory(context: PlatformContext): Decoder.Factory?
}

actual interface HttpStackComponent {
    actual fun httpStack(context: PlatformContext): HttpStack
}