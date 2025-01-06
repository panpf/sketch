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

package com.github.panpf.sketch.decode.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.GifDecoder
import com.github.panpf.sketch.util.ComponentLoader
import com.github.panpf.sketch.util.DecoderProvider

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration [GifDecoder]
 *
 * @see com.github.panpf.sketch.animated.gif.jvm.test.decode.internal.GifDecoderProviderJvmTest
 */
@Keep
actual class GifDecoderProvider : DecoderProvider {

    actual override fun factory(context: PlatformContext): GifDecoder.Factory {
        return GifDecoder.Factory()
    }
}