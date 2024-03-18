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
package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.os.Build

internal fun Bitmap.toLogString(): String =
    "Bitmap@${hashCode().toString(16)}(${width}x${height},$config)"

/**
 * Convert null and [Bitmap.Config.HARDWARE] configs to [Bitmap.Config.ARGB_8888].
 */
internal fun Bitmap.Config?.toSoftware(): Bitmap.Config {
    return if (this == null || Build.VERSION.SDK_INT >= 26 && this == Bitmap.Config.HARDWARE)
        Bitmap.Config.ARGB_8888 else this
}