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

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpStack
import java.util.ServiceLoader

actual object ComponentDetector {

    // This code is written intentionally so R8 can optimize it:
    // https://github.com/Kotlin/kotlinx.coroutines/issues/1231
    actual val fetchers by lazy {
        ServiceLoader.load(
            FetcherComponent::class.java,
            FetcherComponent::class.java.classLoader,
        ).iterator().asSequence().toList().toImmutableList()
    }

    actual val decoders by lazy {
        ServiceLoader.load(
            DecoderComponent::class.java,
            DecoderComponent::class.java.classLoader,
        ).iterator().asSequence().toList().toImmutableList()
    }

    actual val httpStacks by lazy {
        ServiceLoader.load(
            HttpStackComponent::class.java,
            HttpStackComponent::class.java.classLoader,
        ).iterator().asSequence().toList().toImmutableList()
    }

    actual fun register(fetcher: FetcherComponent) {
        throw UnsupportedOperationException()
    }

    actual fun register(decoder: DecoderComponent) {
        throw UnsupportedOperationException()
    }

    actual fun register(httpStack: HttpStackComponent) {
        throw UnsupportedOperationException()
    }
}

@Keep
actual interface FetcherComponent {
    actual fun factory(context: PlatformContext): Fetcher.Factory?
}

@Keep
actual interface DecoderComponent {
    actual fun factory(context: PlatformContext): Decoder.Factory?
}

@Keep
actual interface HttpStackComponent {
    actual fun httpStack(context: PlatformContext): HttpStack
}