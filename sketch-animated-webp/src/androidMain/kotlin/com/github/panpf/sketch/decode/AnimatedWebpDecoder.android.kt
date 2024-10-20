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

package com.github.panpf.sketch.decode

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

/**
 * Get the default GIF decoder factory for Android platforms
 *
 * @see com.github.panpf.sketch.animated.webp.android.test.decode.AnimatedWebpDecoderAndroidTest.testDefaultAnimatedWebpDecoderFactory
 */
actual fun defaultAnimatedWebpDecoderFactory(): Decoder.Factory? {
    return if (VERSION.SDK_INT >= VERSION_CODES.P) {
        ImageDecoderAnimatedWebpDecoder.Factory()
    } else {
        null
    }
}