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

package com.github.panpf.sketch.request

actual object VideoFrameOptions {
    // Align with android.media.MediaMetadataRetriever constants
    actual val PREVIOUS_SYNC: Int = 0
    actual val NEXT_SYNC: Int = 1
    actual val CLOSEST_SYNC: Int = 2
    actual val CLOSEST: Int = 3

    actual fun isValid(option: Int): Boolean =
        option == PREVIOUS_SYNC || option == NEXT_SYNC || option == CLOSEST_SYNC || option == CLOSEST

    actual fun nameOf(option: Int): String = when (option) {
        PREVIOUS_SYNC -> "PREVIOUS_SYNC"
        NEXT_SYNC -> "NEXT_SYNC"
        CLOSEST_SYNC -> "CLOSEST_SYNC"
        CLOSEST -> "CLOSEST"
        else -> "Unknown($option)"
    }
}
